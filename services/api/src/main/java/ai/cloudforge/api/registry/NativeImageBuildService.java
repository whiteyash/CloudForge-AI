package ai.cloudforge.api.registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@Service
public class NativeImageBuildService {

    private static final Logger log = LoggerFactory.getLogger(NativeImageBuildService.class);
    private static final Pattern REPO_TAG_PATTERN = Pattern.compile("^[a-z0-9_./-]+$");

    private final NativeImageBuildRepository buildRepository;
    private final ContainerRegistryRepository registryRepository;
    private final ContainerImageRepositoryRepository imageRepositoryRepository;
    private final ContainerImageTagRepository imageTagRepository;
    private final ProjectRepository projectRepository;
    private final RbacService rbacService;

    // Process Registry for Process-Backed Cancellation & Process Lifecycle Controls
    private final Map<UUID, Process> activeProcesses = new ConcurrentHashMap<>();

    public NativeImageBuildService(NativeImageBuildRepository buildRepository,
                                  ContainerRegistryRepository registryRepository,
                                  ContainerImageRepositoryRepository imageRepositoryRepository,
                                  ContainerImageTagRepository imageTagRepository,
                                  ProjectRepository projectRepository,
                                  RbacService rbacService) {
        this.buildRepository = buildRepository;
        this.registryRepository = registryRepository;
        this.imageRepositoryRepository = imageRepositoryRepository;
        this.imageTagRepository = imageTagRepository;
        this.projectRepository = projectRepository;
        this.rbacService = rbacService;
    }

    @Transactional
    public NativeImageBuildDto triggerBuild(UUID projectId, TriggerBuildRequest request, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "IMAGE_BUILD");

        ContainerRegistry registry = registryRepository.findByIdAndProjectId(request.getRegistryId(), projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Target container registry not found: " + request.getRegistryId()));

        NativeImageBuild build = new NativeImageBuild(
                projectId,
                registry.getId(),
                request.getRepositoryName(),
                request.getTagName(),
                request.getDockerfilePath()
        );

        Instant now = Instant.now();
        build.setStartedAt(now);
        build.setStatus("BUILDING");

        StringBuilder logBuffer = new StringBuilder();
        logBuffer.append(String.format("[INFO] [%s] Starting Native Image Build Execution...\n", Instant.now()));
        logBuffer.append(String.format("[INFO] Target Registry: %s (%s)\n", registry.getName(), registry.getRegistryUrl()));
        logBuffer.append(String.format("[INFO] Repository Name: %s\n", request.getRepositoryName()));
        logBuffer.append(String.format("[INFO] Tag Name: %s\n", request.getTagName()));
        logBuffer.append(String.format("[INFO] Dockerfile Path: %s\n", request.getDockerfilePath()));

        // Security Audit: Command Injection & Path Traversal Safeguards
        String repoName = request.getRepositoryName() != null ? request.getRepositoryName().toLowerCase() : "";
        String tagName = request.getTagName() != null ? request.getTagName().toLowerCase() : "";
        if (!REPO_TAG_PATTERN.matcher(repoName).matches() || !REPO_TAG_PATTERN.matcher(tagName).matches()) {
            logBuffer.append("[ERROR] Security Validation Error: Invalid characters in repository or tag name.\n");
            build.setStatus("FAILED");
            build.setLogOutput(logBuffer.toString());
            build.setCompletedAt(Instant.now());
            NativeImageBuild failedBuild = buildRepository.save(build);
            return new NativeImageBuildDto(failedBuild);
        }

        String pathStr = request.getDockerfilePath();
        if (pathStr.contains("..") || pathStr.startsWith("/etc") || pathStr.startsWith("/proc") || pathStr.startsWith("/sys")) {
            logBuffer.append(String.format("[ERROR] Path Traversal Rejection: Dockerfile path '%s' contains forbidden path sequence.\n", pathStr));
            build.setStatus("FAILED");
            build.setLogOutput(logBuffer.toString());
            build.setCompletedAt(Instant.now());
            NativeImageBuild failedBuild = buildRepository.save(build);
            return new NativeImageBuildDto(failedBuild);
        }

        boolean dockerAvailable = checkDockerExecutable();
        File dockerfile = new File(request.getDockerfilePath());

        if (!dockerAvailable) {
            logBuffer.append("[ERROR] Host Environment Exception: Docker daemon / BuildKit executable is not available on system PATH.\n");
            logBuffer.append("[ERROR] Native image compilation aborted. Docker binary 'docker' not found or socket inaccessible.\n");
            build.setStatus("FAILED");
            build.setLogOutput(logBuffer.toString());
            build.setCompletedAt(Instant.now());

            NativeImageBuild failedBuild = buildRepository.save(build);
            log.warn("BUILD_ENGINE | project={} | user={} | status=FAILED | reason=Docker binary unavailable", projectId, userId);
            return new NativeImageBuildDto(failedBuild);
        }

        if (!dockerfile.exists()) {
            logBuffer.append(String.format("[ERROR] Source Validation Error: Dockerfile not found at specified path '%s'.\n", request.getDockerfilePath()));
            logBuffer.append("[ERROR] Native image build failed: Cannot locate build context Dockerfile.\n");
            build.setStatus("FAILED");
            build.setLogOutput(logBuffer.toString());
            build.setCompletedAt(Instant.now());

            NativeImageBuild failedBuild = buildRepository.save(build);
            log.warn("BUILD_ENGINE | project={} | user={} | status=FAILED | reason=Dockerfile missing", projectId, userId);
            return new NativeImageBuildDto(failedBuild);
        }

        // Save initial BUILDING state
        NativeImageBuild currentBuild = buildRepository.save(build);
        UUID buildId = currentBuild.getId();

        // Execute real Docker build process
        String fullTag = repoName + ":" + tagName;
        try {
            logBuffer.append(String.format("[EXEC] Executing: docker build -f %s -t %s .\n", request.getDockerfilePath(), fullTag));
            ProcessBuilder pb = new ProcessBuilder("docker", "build", "-f", request.getDockerfilePath(), "-t", fullTag, ".");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            activeProcesses.put(buildId, process);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logBuffer.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            activeProcesses.remove(buildId);

            if (!finished || process.exitValue() != 0) {
                logBuffer.append(String.format("[ERROR] docker build exited with failure code %d.\n", finished ? process.exitValue() : -1));
                currentBuild.setStatus("FAILED");
                currentBuild.setLogOutput(logBuffer.toString());
                currentBuild.setCompletedAt(Instant.now());

                NativeImageBuild failedBuild = buildRepository.save(currentBuild);
                return new NativeImageBuildDto(failedBuild);
            }

            // Execute real docker push
            logBuffer.append(String.format("[EXEC] Executing: docker push %s\n", fullTag));
            ProcessBuilder pushPb = new ProcessBuilder("docker", "push", fullTag);
            pushPb.redirectErrorStream(true);
            Process pushProcess = pushPb.start();
            activeProcesses.put(buildId, pushProcess);

            try (BufferedReader pushReader = new BufferedReader(new InputStreamReader(pushProcess.getInputStream(), StandardCharsets.UTF_8))) {
                String pushLine;
                while ((pushLine = pushReader.readLine()) != null) {
                    logBuffer.append(pushLine).append("\n");
                }
            }

            boolean pushFinished = pushProcess.waitFor(120, TimeUnit.SECONDS);
            activeProcesses.remove(buildId);

            if (!pushFinished || pushProcess.exitValue() != 0) {
                logBuffer.append(String.format("[ERROR] docker push exited with failure code %d.\n", pushFinished ? pushProcess.exitValue() : -1));
                currentBuild.setStatus("FAILED");
                currentBuild.setLogOutput(logBuffer.toString());
                currentBuild.setCompletedAt(Instant.now());

                NativeImageBuild failedBuild = buildRepository.save(currentBuild);
                return new NativeImageBuildDto(failedBuild);
            }

            String realDigest = extractImageDigest(fullTag);
            logBuffer.append(String.format("[SUCCESS] Image pushed successfully. Verified Digest: %s\n", realDigest));
            currentBuild.setStatus("PUSHED");
            currentBuild.setLogOutput(logBuffer.toString());
            currentBuild.setCompletedAt(Instant.now());

            NativeImageBuild savedBuild = buildRepository.save(currentBuild);

            // Register real pushed tag in database
            ContainerImageRepository repo = imageRepositoryRepository.findByRegistryIdAndRepositoryName(registry.getId(), request.getRepositoryName())
                    .orElseGet(() -> imageRepositoryRepository.save(new ContainerImageRepository(registry.getId(), projectId, request.getRepositoryName())));

            imageTagRepository.findByRepositoryIdAndTagName(repo.getId(), request.getTagName())
                    .ifPresentOrElse(existingTag -> {
                        existingTag.setDigestSha256(realDigest);
                        existingTag.setPushedAt(Instant.now());
                        imageTagRepository.save(existingTag);
                    }, () -> {
                        ContainerImageTag newTag = new ContainerImageTag(
                                repo.getId(),
                                request.getTagName(),
                                realDigest,
                                0L,
                                "linux/amd64",
                                false
                        );
                        imageTagRepository.save(newTag);
                        repo.setImageCount(repo.getImageCount() + 1);
                        imageRepositoryRepository.save(repo);
                    });

            return new NativeImageBuildDto(savedBuild);

        } catch (Exception ex) {
            activeProcesses.remove(buildId);
            logBuffer.append(String.format("[ERROR] Build Process Exception: %s\n", ex.getMessage()));
            currentBuild.setStatus("FAILED");
            currentBuild.setLogOutput(logBuffer.toString());
            currentBuild.setCompletedAt(Instant.now());

            NativeImageBuild failedBuild = buildRepository.save(currentBuild);
            return new NativeImageBuildDto(failedBuild);
        }
    }

    @Transactional
    public NativeImageBuildDto cancelBuild(UUID projectId, UUID buildId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "IMAGE_BUILD");

        NativeImageBuild build = buildRepository.findByIdAndProjectId(buildId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Native image build execution not found: " + buildId));

        if ("QUEUED".equals(build.getStatus()) || "BUILDING".equals(build.getStatus())) {
            // Forcibly terminate active OS process if running
            Process activeProc = activeProcesses.remove(buildId);
            if (activeProc != null && activeProc.isAlive()) {
                activeProc.destroyForcibly();
                try {
                    activeProc.waitFor(5, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            build.setStatus("CANCELLED");
            String existingLogs = build.getLogOutput() != null ? build.getLogOutput() : "";
            build.setLogOutput(existingLogs + "\n[INFO] Build process cancelled by user request. OS Process terminated forcibly.\n");
            build.setCompletedAt(Instant.now());
            NativeImageBuild saved = buildRepository.save(build);
            log.info("BUILD_ENGINE | project={} | user={} | action=BUILD_CANCELLED | buildId={}", projectId, userId, buildId);
            return new NativeImageBuildDto(saved);
        }

        return new NativeImageBuildDto(build);
    }

    @Transactional(readOnly = true)
    public List<NativeImageBuildDto> getBuildsByProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        return buildRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(NativeImageBuildDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public NativeImageBuildDto getBuildById(UUID projectId, UUID id, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        NativeImageBuild build = buildRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Native image build execution not found: " + id));
        return new NativeImageBuildDto(build);
    }

    private boolean checkDockerExecutable() {
        try {
            Process pVersion = new ProcessBuilder("docker", "--version").start();
            boolean versionOk = pVersion.waitFor(3, TimeUnit.SECONDS) && pVersion.exitValue() == 0;
            if (!versionOk) return false;

            Process pInfo = new ProcessBuilder("docker", "info").start();
            return pInfo.waitFor(5, TimeUnit.SECONDS) && pInfo.exitValue() == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    private String extractImageDigest(String imageTag) {
        try {
            Process p = new ProcessBuilder("docker", "inspect", "--format={{index .RepoDigests 0}}", imageTag).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                if (line != null && !line.isBlank() && line.contains("@sha256:")) {
                    return line.substring(line.indexOf("@sha256:") + 1).trim();
                } else if (line != null && line.startsWith("sha256:")) {
                    return line.trim();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
