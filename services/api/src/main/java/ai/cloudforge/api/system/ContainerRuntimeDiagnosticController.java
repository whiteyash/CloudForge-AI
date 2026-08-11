package ai.cloudforge.api.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class ContainerRuntimeDiagnosticController {

    @GetMapping("/container-runtime")
    public ResponseEntity<Map<String, Object>> getContainerRuntimeDiagnostics() {
        Map<String, Object> result = new HashMap<>();

        String dockerVersionOutput = getCommandOutput("docker", "--version");
        boolean dockerInstalled = !dockerVersionOutput.isBlank() && dockerVersionOutput.startsWith("Docker version");

        String dockerInfoOutput = getCommandOutput("docker", "info");
        boolean daemonAvailable = dockerInstalled && !dockerInfoOutput.isBlank() && !dockerInfoOutput.contains("failed to connect to the docker API");

        String buildxOutput = getCommandOutput("docker", "buildx", "version");
        boolean buildxAvailable = dockerInstalled && !buildxOutput.isBlank() && buildxOutput.contains("buildx");

        result.put("dockerInstalled", dockerInstalled);
        result.put("dockerDaemonAvailable", daemonAvailable);
        result.put("dockerVersion", dockerInstalled ? dockerVersionOutput : "NOT_INSTALLED");
        result.put("buildxAvailable", buildxAvailable);
        result.put("buildKitAvailable", daemonAvailable);
        result.put("registryProviders", List.of(
                "DOCKER_HUB", "AWS_ECR", "GOOGLE_GAR", "GITHUB_GHCR", "AZURE_ACR", "HARBOR_PRIVATE", "GENERIC_OCI"
        ));

        return ResponseEntity.ok(result);
    }

    private String getCommandOutput(String... command) {
        try {
            Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            p.waitFor(3, TimeUnit.SECONDS);
            return sb.toString().trim();
        } catch (Exception ex) {
            return "";
        }
    }
}
