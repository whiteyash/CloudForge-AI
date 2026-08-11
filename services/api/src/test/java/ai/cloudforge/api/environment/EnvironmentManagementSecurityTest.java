package ai.cloudforge.api.environment;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class EnvironmentManagementSecurityTest {

    private EnvironmentProfileRepository profileRepository;
    private EnvironmentManagementService service;

    @BeforeEach
    void setUp() {
        profileRepository = Mockito.mock(EnvironmentProfileRepository.class);
        EnvironmentVariableRepository variableRepository = Mockito.mock(EnvironmentVariableRepository.class);
        EnvironmentTargetRepository targetRepository = Mockito.mock(EnvironmentTargetRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new EnvironmentManagementService(profileRepository, variableRepository, targetRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedEnvironmentAccessThrowsNotFound() {
        UUID envId = UUID.randomUUID();
        when(profileRepository.findById(envId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getEnvironmentById(envId);
        });
    }
}
