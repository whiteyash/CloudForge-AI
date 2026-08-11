package ai.cloudforge.api.aiops;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.aiops.IncidentDtos.AcknowledgeIncidentRequest;
import ai.cloudforge.api.aiops.IncidentDtos.AlertChannelDto;
import ai.cloudforge.api.aiops.IncidentDtos.CreateAlertChannelRequest;
import ai.cloudforge.api.aiops.IncidentDtos.CreateOnCallScheduleRequest;
import ai.cloudforge.api.aiops.IncidentDtos.IncidentResponseDto;
import ai.cloudforge.api.aiops.IncidentDtos.IncidentSummaryDto;
import ai.cloudforge.api.aiops.IncidentDtos.OnCallScheduleDto;
import ai.cloudforge.api.aiops.IncidentDtos.TriggerIncidentRequest;
import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;

@Service
public class IncidentResponseService {

    private static final Logger log = LoggerFactory.getLogger(IncidentResponseService.class);
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final IncidentRepository incidentRepository;
    private final OnCallScheduleRepository onCallScheduleRepository;
    private final AlertIntegrationChannelRepository alertChannelRepository;
    private final ProjectRepository projectRepository;
    private final RbacService rbacService;
    private final byte[] encryptionKey;

    public IncidentResponseService(
            IncidentRepository incidentRepository,
            OnCallScheduleRepository onCallScheduleRepository,
            AlertIntegrationChannelRepository alertChannelRepository,
            ProjectRepository projectRepository,
            RbacService rbacService,
            @Value("${cloudforge.k8s.encryption-key:CloudForgeDefaultSecretEncryption32B!}") String secretKey
    ) {
        this.incidentRepository = incidentRepository;
        this.onCallScheduleRepository = onCallScheduleRepository;
        this.alertChannelRepository = alertChannelRepository;
        this.projectRepository = projectRepository;
        this.rbacService = rbacService;
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] fixed32 = new byte[32];
        System.arraycopy(keyBytes, 0, fixed32, 0, Math.min(keyBytes.length, 32));
        this.encryptionKey = fixed32;
    }

    private Project validateProjectAndAuth(UUID projectId, UUID userId, String requiredPermission) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, requiredPermission);
        return project;
    }

    @Transactional
    public IncidentResponseDto triggerIncident(UUID projectId, TriggerIncidentRequest request, UUID userId) {
        Project project = validateProjectAndAuth(projectId, userId, "INCIDENT_MANAGE");

        Incident incident = new Incident(
                projectId,
                project.getOrganization().getId(),
                request.title(),
                request.description(),
                request.severity(),
                request.incidentSource()
        );

        Incident saved = incidentRepository.save(incident);
        log.info("INCIDENT_AUDIT | project={} | user={} | action=INCIDENT_TRIGGERED | severity={} | title={}", projectId, userId, saved.getSeverity(), saved.getTitle());
        return toIncidentDto(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDto> listIncidents(UUID projectId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "INCIDENT_VIEW");
        return incidentRepository.findByProjectId(projectId).stream()
                .map(this::toIncidentDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentResponseDto getIncidentDetails(UUID projectId, UUID incidentId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "INCIDENT_VIEW");
        Incident incident = incidentRepository.findByProjectIdAndId(projectId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));
        return toIncidentDto(incident);
    }

    @Transactional
    public IncidentResponseDto acknowledgeIncident(UUID projectId, UUID incidentId, AcknowledgeIncidentRequest request, UUID userId) {
        validateProjectAndAuth(projectId, userId, "INCIDENT_MANAGE");

        Incident incident = incidentRepository.findByProjectIdAndId(projectId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        incident.setStatus("ACKNOWLEDGED");
        incident.setAcknowledgedAt(Instant.now());
        if (request != null && request.assigneeUserId() != null) {
            incident.setAssigneeUserId(request.assigneeUserId());
        } else {
            incident.setAssigneeUserId(userId);
        }

        Incident saved = incidentRepository.save(incident);
        log.info("INCIDENT_AUDIT | project={} | user={} | action=INCIDENT_ACKNOWLEDGED | incident={}", projectId, userId, incidentId);
        return toIncidentDto(saved);
    }

    @Transactional
    public IncidentResponseDto resolveIncident(UUID projectId, UUID incidentId, UUID userId) {
        validateProjectAndAuth(projectId, userId, "INCIDENT_MANAGE");

        Incident incident = incidentRepository.findByProjectIdAndId(projectId, incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found: " + incidentId));

        incident.setStatus("RESOLVED");
        incident.setResolvedAt(Instant.now());
        if (incident.getAcknowledgedAt() == null) {
            incident.setAcknowledgedAt(Instant.now());
        }

        Incident saved = incidentRepository.save(incident);
        log.info("INCIDENT_AUDIT | project={} | user={} | action=INCIDENT_RESOLVED | incident={}", projectId, userId, incidentId);
        return toIncidentDto(saved);
    }

    @Transactional
    public OnCallScheduleDto createOnCallSchedule(UUID orgId, CreateOnCallScheduleRequest request, UUID userId) {
        if (userId == null) throw new ForbiddenException("Unauthenticated request");
        rbacService.requirePermission(userId, orgId, "ONCALL_MANAGE");

        OnCallSchedule schedule = new OnCallSchedule(
                orgId,
                request.name(),
                request.timeZone(),
                request.rotationType(),
                request.activeUserId()
        );

        OnCallSchedule saved = onCallScheduleRepository.save(schedule);
        log.info("INCIDENT_AUDIT | org={} | user={} | action=ONCALL_SCHEDULE_CREATED | name={}", orgId, userId, request.name());
        return toOnCallDto(saved);
    }

    @Transactional(readOnly = true)
    public List<OnCallScheduleDto> listOnCallSchedules(UUID orgId, UUID userId) {
        if (userId == null) throw new ForbiddenException("Unauthenticated request");
        rbacService.requirePermission(userId, orgId, "INCIDENT_VIEW");

        return onCallScheduleRepository.findByOrganizationId(orgId).stream()
                .map(this::toOnCallDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlertChannelDto createAlertChannel(UUID orgId, CreateAlertChannelRequest request, UUID userId) {
        if (userId == null) throw new ForbiddenException("Unauthenticated request");
        rbacService.requirePermission(userId, orgId, "ALERT_CHANNEL_MANAGE");

        String encryptedKey = (request.apiKey() != null && !request.apiKey().isBlank())
                ? encryptSecret(request.apiKey())
                : null;

        AlertIntegrationChannel channel = new AlertIntegrationChannel(
                orgId,
                request.channelName(),
                request.channelType(),
                request.webhookUrl(),
                encryptedKey
        );

        AlertIntegrationChannel saved = alertChannelRepository.save(channel);
        log.info("INCIDENT_AUDIT | org={} | user={} | action=ALERT_CHANNEL_CREATED | type={}", orgId, userId, request.channelType());
        return toAlertChannelDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AlertChannelDto> listAlertChannels(UUID orgId, UUID userId) {
        if (userId == null) throw new ForbiddenException("Unauthenticated request");
        rbacService.requirePermission(userId, orgId, "INCIDENT_VIEW");

        return alertChannelRepository.findByOrganizationId(orgId).stream()
                .map(this::toAlertChannelDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentSummaryDto getIncidentSummary(UUID projectId, UUID userId) {
        if (userId != null && projectId != null) {
            validateProjectAndAuth(projectId, userId, "INCIDENT_VIEW");
        }

        List<Incident> incidents = (projectId != null) ? incidentRepository.findByProjectId(projectId) : incidentRepository.findAll();

        int total = incidents.size();
        List<Incident> openIncidents = incidents.stream()
                .filter(i -> !"RESOLVED".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());

        int sev1 = (int) incidents.stream().filter(i -> "SEV_1_CRITICAL".equalsIgnoreCase(i.getSeverity())).count();
        int sev2 = (int) incidents.stream().filter(i -> "SEV_2_HIGH".equalsIgnoreCase(i.getSeverity())).count();
        int sev3 = (int) incidents.stream().filter(i -> "SEV_3_MEDIUM".equalsIgnoreCase(i.getSeverity())).count();
        int sev4 = (int) incidents.stream().filter(i -> "SEV_4_LOW".equalsIgnoreCase(i.getSeverity())).count();

        double totalAckTime = 0;
        int ackCount = 0;
        double totalResTime = 0;
        int resCount = 0;

        for (Incident inc : incidents) {
            if (inc.getAcknowledgedAt() != null) {
                totalAckTime += Duration.between(inc.getTriggeredAt(), inc.getAcknowledgedAt()).toMinutes();
                ackCount++;
            }
            if (inc.getResolvedAt() != null) {
                totalResTime += Duration.between(inc.getTriggeredAt(), inc.getResolvedAt()).toMinutes();
                resCount++;
            }
        }

        double avgMtta = ackCount > 0 ? (totalAckTime / ackCount) : 0.0;
        double avgMttr = resCount > 0 ? (totalResTime / resCount) : 0.0;

        List<IncidentResponseDto> recentIncidents = incidents.stream()
                .sorted((a, b) -> b.getTriggeredAt().compareTo(a.getTriggeredAt()))
                .limit(5)
                .map(this::toIncidentDto)
                .collect(Collectors.toList());

        List<OnCallScheduleDto> schedules = onCallScheduleRepository.findAll().stream()
                .map(this::toOnCallDto)
                .collect(Collectors.toList());

        return new IncidentSummaryDto(
                total,
                openIncidents.size(),
                sev1,
                sev2,
                sev3,
                sev4,
                avgMtta,
                avgMttr,
                recentIncidents,
                schedules
        );
    }

    public String encryptSecret(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = new SecretKeySpec(encryptionKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, encryptedWithIv, GCM_IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt alert key", e);
        }
    }

    private IncidentResponseDto toIncidentDto(Incident i) {
        return new IncidentResponseDto(
                i.getId(),
                i.getProjectId(),
                i.getOrganizationId(),
                i.getTitle(),
                i.getDescription(),
                i.getSeverity(),
                i.getStatus(),
                i.getAssigneeUserId(),
                i.getIncidentSource(),
                i.getTriggeredAt(),
                i.getAcknowledgedAt(),
                i.getResolvedAt()
        );
    }

    private OnCallScheduleDto toOnCallDto(OnCallSchedule s) {
        return new OnCallScheduleDto(
                s.getId(),
                s.getOrganizationId(),
                s.getName(),
                s.getTimeZone(),
                s.getRotationType(),
                s.getActiveUserId(),
                s.getCreatedAt()
        );
    }

    private AlertChannelDto toAlertChannelDto(AlertIntegrationChannel c) {
        return new AlertChannelDto(
                c.getId(),
                c.getOrganizationId(),
                c.getChannelName(),
                c.getChannelType(),
                c.getWebhookUrl(),
                c.isEnabled(),
                c.getCreatedAt()
        );
    }
}
