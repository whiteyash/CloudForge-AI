package ai.cloudforge.api.git;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class RepositoryGovernanceServiceTest {

    private RepositoryGovernancePolicyRepository repository;
    private EventPublisher eventPublisher;
    private RepositoryGovernanceService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RepositoryGovernancePolicyRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RepositoryGovernanceService(repository, eventPublisher);
    }

    @Test
    void testGetGovernancePolicyDefaults() {
        UUID repoId = UUID.randomUUID();
        when(repository.findByRepositoryId(repoId)).thenReturn(Optional.empty());

        RepositoryGovernanceService.GovernanceResponse response = service.getGovernancePolicy(repoId);

        assertNotNull(response);
        assertEquals(100, response.complianceScore());
        assertEquals(0, response.riskScore());
    }

    @Test
    void testUpdateGovernancePolicyRecalculatesScores() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID repoId = UUID.randomUUID();

        when(repository.findByRepositoryId(repoId)).thenReturn(Optional.empty());
        when(repository.save(any(RepositoryGovernancePolicy.class))).thenAnswer(inv -> inv.getArgument(0));

        RepositoryGovernanceService.GovernanceResponse response = service.updateGovernancePolicy(
                orgId, userId, repoId, false, 0, false, false, false, false
        );

        assertNotNull(response);
        assertEquals(0, response.complianceScore());
        assertEquals(100, response.riskScore());
        assertEquals(5, response.violationCount());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
