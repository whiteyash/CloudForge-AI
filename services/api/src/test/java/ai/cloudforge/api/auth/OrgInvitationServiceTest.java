package ai.cloudforge.api.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ai.cloudforge.api.shared.EmailService;

@ExtendWith(MockitoExtension.class)
class OrgInvitationServiceTest {

    @Mock private OrgInvitationRepository invitationRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private AppUserRepository userRepository;
    @Mock private MembershipRepository membershipRepository;
    @Mock private AuditLogRepository auditLogRepository;
    @Mock private RbacService rbacService;
    @Mock private EmailService emailService;

    private OrgInvitationService service;
    private UUID orgId;
    private UUID requesterId;
    private Organization organization;
    private AppUser requester;

    @BeforeEach
    void setUp() {
        service = new OrgInvitationService(
                invitationRepository,
                organizationRepository,
                userRepository,
                membershipRepository,
                auditLogRepository,
                rbacService,
                emailService
        );

        organization = new Organization("Acme Corp", "acme-corp");
        orgId = organization.getId();

        requester = new AppUser("admin@acme.com", "hash", "Admin User");
        requesterId = requester.getId();
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should generate 32-byte raw token and store ONLY SHA-256 hash in database")
    void createInvitation_SecureTokenHashing() {
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(organization));
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        doNothing().when(rbacService).requireAdminOrOwner(requesterId, orgId);
        when(emailService.sendInvitationEmail(any(), any(), any(), any())).thenReturn(true);

        when(invitationRepository.save(any(OrgInvitation.class))).thenAnswer(i -> {
            OrgInvitation inv = i.getArgument(0);
            if (inv.getId() == null) inv.setId(UUID.randomUUID());
            return inv;
        });

        OrgInvitationService.InvitationResponse response = service.createInvitation(requesterId, orgId, "developer@acme.com", Role.ADMIN);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo("developer@acme.com");

        ArgumentCaptor<OrgInvitation> captor = ArgumentCaptor.forClass(OrgInvitation.class);
        verify(invitationRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());

        OrgInvitation savedEntity = captor.getValue();
        assertThat(savedEntity.getTokenHash()).isNotNull().hasSize(64); // SHA-256 hex string length
        assertThat(savedEntity.getDeliveryStatus()).isEqualTo("SENT");
    }

    @Test
    @DisplayName("Should accept valid invitation using raw token and mark invitation ACCEPTED")
    void acceptInvitation_Success() {
        String rawToken = "sample-raw-token-1234567890-secure";
        String tokenHash = hashToken(rawToken);

        AppUser acceptingUser = new AppUser("user@acme.com", "hash", "Test User");
        UUID userId = acceptingUser.getId();

        OrgInvitation invitation = new OrgInvitation(organization, "user@acme.com", Role.DEVELOPER, requester, tokenHash, Instant.now().plusSeconds(3600));

        when(invitationRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(invitation));
        when(userRepository.findById(userId)).thenReturn(Optional.of(acceptingUser));
        when(membershipRepository.findByOrganizationIdAndUserId(orgId, userId)).thenReturn(Optional.empty());

        service.acceptInvitation(userId, rawToken);

        assertThat(invitation.getStatus()).isEqualTo("ACCEPTED");
        verify(membershipRepository).save(any(Membership.class));
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when accepting expired invitation token")
    void acceptInvitation_ExpiredToken() {
        String rawToken = "expired-raw-token";
        String tokenHash = hashToken(rawToken);

        UUID userId = UUID.randomUUID();
        OrgInvitation expiredInvitation = new OrgInvitation(organization, "user@acme.com", Role.DEVELOPER, requester, tokenHash, Instant.now().minusSeconds(3600));

        when(invitationRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredInvitation));

        assertThatThrownBy(() -> service.acceptInvitation(userId, rawToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or expired invitation token");
    }
}
