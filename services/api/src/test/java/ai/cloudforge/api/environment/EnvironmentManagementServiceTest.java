package ai.cloudforge.api.environment;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class EnvironmentManagementServiceTest {

    private EnvironmentProfileRepository profileRepository;
    private EnvironmentVariableRepository variableRepository;
    private EnvironmentTargetRepository targetRepository;
    private EventPublisher eventPublisher;
    private EnvironmentManagementService service;

    @BeforeEach
    void setUp() {
        profileRepository = Mockito.mock(EnvironmentProfileRepository.class);
        variableRepository = Mockito.mock(EnvironmentVariableRepository.class);
        targetRepository = Mockito.mock(EnvironmentTargetRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);

        service = new EnvironmentManagementService(profileRepository, variableRepository, targetRepository, eventPublisher);
    }

    @Test
    void testCreateEnvironmentAndTargetBinding() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(profileRepository.save(any(EnvironmentProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        EnvironmentManagementService.EnvironmentResponse response = service.createEnvironment(
                orgId, userId, projectId, "Staging-US", "STAGING", "Staging environment for QA testing", false
        );

        assertNotNull(response);
        assertEquals("Staging-US", response.name());
        assertEquals("STAGING", response.environmentType());
        verify(targetRepository).save(any(EnvironmentTarget.class));
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testMaintenanceAndFreezeToggles() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID envId = UUID.randomUUID();
        EnvironmentProfile profile = new EnvironmentProfile(UUID.randomUUID(), "Production", "PRODUCTION", "Production env", true);

        when(profileRepository.findById(envId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(EnvironmentProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        EnvironmentManagementService.EnvironmentResponse maintResponse = service.setMaintenanceMode(orgId, userId, envId, true);
        assertTrue(maintResponse.isMaintenanceMode());
        assertEquals("MAINTENANCE", maintResponse.status());

        EnvironmentManagementService.EnvironmentResponse freezeResponse = service.setFrozenStatus(orgId, userId, envId, true);
        assertTrue(freezeResponse.isFrozen());
    }
}
