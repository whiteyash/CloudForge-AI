export interface UserSummary {
  id: string;
  email: string;
  fullName: string;
}

export interface OrganizationSummary {
  id: string;
  name: string;
  slug: string;
  role: 'owner' | 'admin' | 'developer' | 'devops' | 'security' | 'viewer';
}

export interface AuthResponse {
  accessToken: string;
  expiresInSeconds: number;
  user: UserSummary;
  organizations: OrganizationSummary[];
}

export interface ProjectResponse {
  id: string;
  orgId: string;
  name: string;
  repoUrl?: string;
  k8sNamespace?: string;
  createdAt: string;
}

export interface AuditLogResponse {
  id: string;
  orgId?: string;
  userId?: string;
  action: string;
  target: string;
  createdAt: string;
}

export interface MemberResponse {
  membershipId: string;
  userId: string;
  email: string;
  fullName: string;
  role: string;
  createdAt: string;
}

export interface TeamMemberSummary {
  userId: string;
  email: string;
  fullName: string;
  addedAt: string;
}

export interface TeamResponse {
  id: string;
  orgId: string;
  name: string;
  description?: string;
  members: TeamMemberSummary[];
  createdAt: string;
}

export interface NotificationResponse {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: 'INFO' | 'WARNING' | 'CRITICAL' | 'INVITATION';
  status: 'UNREAD' | 'READ' | 'ARCHIVED';
  linkUrl?: string;
  createdAt: string;
}

export interface UserProfileResponse {
  id: string;
  email: string;
  fullName: string;
  createdAt: string;
}

export interface ActiveSessionResponse {
  id: string;
  deviceType: string;
  browser: string;
  operatingSystem: string;
  ipAddress: string;
  isCurrent: boolean;
  lastActiveAt: string;
}

export interface OrgDetailResponse {
  id: string;
  name: string;
  slug: string;
  description?: string;
  logoUrl?: string;
  websiteUrl?: string;
  timezone: string;
  status: string;
  primaryColor: string;
  createdAt: string;
}

export interface InvitationResponse {
  id: string;
  orgId: string;
  email: string;
  role: string;
  token: string;
  status: string;
  attemptsCount: number;
  expiresAt: string;
  createdAt: string;
}

export interface OrgDashboardSummaryResponse {
  id: string;
  name: string;
  slug: string;
  projectsCount: number;
  membersCount: number;
  teamsCount: number;
  pendingInvitations: number;
  status: string;
  createdAt: string;
}

export interface PermissionCatalogResponse {
  id: string;
  code: string;
  module: string;
  description: string;
}

export interface RolePermissionMappingResponse {
  role: string;
  permissionCode: string;
}

export interface UserPreferencesResponse {
  id: string;
  userId: string;
  language: string;
  timezone: string;
  dateFormat: string;
  timeFormat: string;
  theme: string;
  accentColor: string;
  density: string;
  defaultWorkspaceId?: string;
  defaultLandingPage: string;
  sidebarCollapsed: boolean;
}

export interface NotificationPreferencesResponse {
  id: string;
  userId: string;
  emailSecurityAlerts: boolean;
  emailOrgEvents: boolean;
  emailInvitations: boolean;
  emailRoleChanges: boolean;
  inappSecurityAlerts: boolean;
  inappOrgEvents: boolean;
  inappInvitations: boolean;
  inappRoleChanges: boolean;
}

export interface SecurityOverview {
  mfaEnabled: boolean;
  activeSessionsCount: number;
  securityScore: number;
  favoriteWorkspaces: string[];
}

export interface Incident {
  id: string;
  title: string;
  severity: string;
  status: string;
  rootCause?: string;
  confidenceScore?: number;
  createdAt: string;
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8000";

class ApiClient {
  private token: string | null = null;

  constructor() {
    if (typeof window !== "undefined") {
      this.token = localStorage.getItem("cf_access_token");
    }
  }

  setToken(token: string | null) {
    this.token = token;
    if (typeof window !== "undefined") {
      if (token) {
        localStorage.setItem("cf_access_token", token);
      } else {
        localStorage.removeItem("cf_access_token");
      }
    }
  }

  getToken(): string | null {
    if (!this.token && typeof window !== "undefined") {
      this.token = localStorage.getItem("cf_access_token");
    }
    return this.token;
  }

  async request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
      ...(options.headers as Record<string, string>),
    };

    const token = this.getToken();
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers,
      credentials: "omit",
    });

    if (!res.ok) {
      const errorText = await res.text();
      let errorMessage = `API Error ${res.status}`;
      try {
        const parsed = JSON.parse(errorText);
        errorMessage = parsed.message || parsed.error || errorMessage;
      } catch {
        if (errorText) errorMessage = errorText;
      }
      throw new Error(errorMessage);
    }

    if (res.status === 204) return {} as T;
    return res.json();
  }

  async register(data: { email: string; password: string; fullName: string; organizationName: string }): Promise<AuthResponse> {
    const res = await this.request<AuthResponse>("/auth/register", {
      method: "POST",
      body: JSON.stringify(data),
    });
    this.setToken(res.accessToken);
    return res;
  }

  async login(data: { email: string; password: string }): Promise<AuthResponse> {
    const res = await this.request<AuthResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify(data),
    });
    this.setToken(res.accessToken);
    return res;
  }

  async me(): Promise<AuthResponse> {
    return this.request<AuthResponse>("/auth/me");
  }

  async logout(): Promise<void> {
    try {
      await this.request<void>("/auth/logout", { method: "POST" });
    } finally {
      this.setToken(null);
    }
  }

  async logoutAll(): Promise<void> {
    try {
      await this.request<void>("/auth/logout-all", { method: "POST" });
    } finally {
      this.setToken(null);
    }
  }

  async switchWorkspace(targetOrgId: string): Promise<void> {
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_active_org_id", targetOrgId);
    }
    return this.request<void>("/auth/switch-workspace", {
      method: "POST",
      body: JSON.stringify({ targetOrgId }),
    });
  }

  async getUserPreferences(): Promise<UserPreferencesResponse> {
    return this.request<UserPreferencesResponse>("/profile/preferences");
  }

  async updateUserPreferences(data: Partial<UserPreferencesResponse>): Promise<UserPreferencesResponse> {
    return this.request<UserPreferencesResponse>("/profile/preferences", {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  }

  async getNotificationPreferences(): Promise<NotificationPreferencesResponse> {
    return this.request<NotificationPreferencesResponse>("/profile/notification-preferences");
  }

  async updateNotificationPreferences(data: Partial<NotificationPreferencesResponse>): Promise<NotificationPreferencesResponse> {
    return this.request<NotificationPreferencesResponse>("/profile/notification-preferences", {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  }

  async getPersonalAuditTrail(): Promise<AuditLogResponse[]> {
    return this.request<AuditLogResponse[]>("/profile/personal-audit");
  }

  async getPermissionsCatalog(): Promise<PermissionCatalogResponse[]> {
    return this.request<PermissionCatalogResponse[]>("/permissions/catalog");
  }

  async getPermissionsMatrix(): Promise<RolePermissionMappingResponse[]> {
    return this.request<RolePermissionMappingResponse[]>("/permissions/matrix");
  }

  async getMyPermissions(orgId: string): Promise<string[]> {
    return this.request<string[]>(`/permissions/orgs/${orgId}/my-permissions`);
  }

  async getDashboardSummary(orgId: string): Promise<OrgDashboardSummaryResponse> {
    return this.request<OrgDashboardSummaryResponse>(`/orgs/${orgId}/dashboard-summary`);
  }

  async getActivityTimeline(orgId: string): Promise<AuditLogResponse[]> {
    return this.request<AuditLogResponse[]>(`/orgs/${orgId}/activity-timeline`);
  }

  async getSessions(): Promise<ActiveSessionResponse[]> {
    return this.request<ActiveSessionResponse[]>("/auth/sessions");
  }

  async terminateSession(sessionId: string): Promise<void> {
    return this.request<void>(`/auth/sessions/${sessionId}`, { method: "DELETE" });
  }

  async getOrg(orgId: string): Promise<OrgDetailResponse> {
    return this.request<OrgDetailResponse>(`/orgs/${orgId}`);
  }

  async updateOrg(orgId: string, data: { name?: string; description?: string; websiteUrl?: string; timezone?: string; primaryColor?: string }): Promise<OrgDetailResponse> {
    return this.request<OrgDetailResponse>(`/orgs/${orgId}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  }

  async archiveOrg(orgId: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}/archive`, { method: "POST" });
  }

  async restoreOrg(orgId: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}/restore`, { method: "POST" });
  }

  async deleteOrg(orgId: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}`, { method: "DELETE" });
  }

  async getProjects(orgId: string): Promise<ProjectResponse[]> {
    return this.request<ProjectResponse[]>(`/orgs/${orgId}/projects`);
  }

  async createProject(orgId: string, data: { name: string; repoUrl?: string; k8sNamespace?: string }): Promise<ProjectResponse> {
    return this.request<ProjectResponse>(`/orgs/${orgId}/projects`, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async getMembers(orgId: string): Promise<MemberResponse[]> {
    return this.request<MemberResponse[]>(`/orgs/${orgId}/members`);
  }

  async inviteMember(orgId: string, data: { email: string; role: string }): Promise<InvitationResponse> {
    return this.request<InvitationResponse>(`/orgs/${orgId}/invitations`, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async acceptInvitation(token: string): Promise<void> {
    return this.request<void>(`/invitations/${token}/accept`, { method: "POST" });
  }

  async rejectInvitation(token: string): Promise<void> {
    return this.request<void>(`/invitations/${token}/reject`, { method: "POST" });
  }

  async updateMemberRole(orgId: string, userId: string, newRole: string): Promise<MemberResponse> {
    return this.request<MemberResponse>(`/orgs/${orgId}/members/${userId}/role`, {
      method: "PATCH",
      body: JSON.stringify({ newRole }),
    });
  }

  async removeMember(orgId: string, userId: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}/members/${userId}`, { method: "DELETE" });
  }

  async transferOwnership(orgId: string, targetUserId: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}/transfer-ownership`, {
      method: "POST",
      body: JSON.stringify({ targetUserId }),
    });
  }

  async listInvitations(orgId: string): Promise<InvitationResponse[]> {
    return this.request<InvitationResponse[]>(`/orgs/${orgId}/invitations`);
  }

  async resendInvitation(orgId: string, id: string): Promise<InvitationResponse> {
    return this.request<InvitationResponse>(`/orgs/${orgId}/invitations/${id}/resend`, { method: "POST" });
  }

  async cancelInvitation(orgId: string, id: string): Promise<void> {
    return this.request<void>(`/orgs/${orgId}/invitations/${id}`, { method: "DELETE" });
  }

  async getAuditLogs(orgId: string): Promise<AuditLogResponse[]> {
    return this.request<AuditLogResponse[]>(`/orgs/${orgId}/audit-logs`);
  }

  async getTeams(orgId: string): Promise<TeamResponse[]> {
    return this.request<TeamResponse[]>(`/orgs/${orgId}/teams`);
  }

  async createTeam(orgId: string, data: { name: string; description?: string }): Promise<TeamResponse> {
    return this.request<TeamResponse>(`/orgs/${orgId}/teams`, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async getNotifications(): Promise<NotificationResponse[]> {
    return this.request<NotificationResponse[]>("/notifications");
  }

  async getUnreadNotificationCount(): Promise<{ unreadCount: number }> {
    return this.request<{ unreadCount: number }>("/notifications/unread-count");
  }

  async markNotificationAsRead(id: string): Promise<NotificationResponse> {
    return this.request<NotificationResponse>(`/notifications/${id}/read`, {
      method: "PATCH",
    });
  }

  async markAllNotificationsAsRead(): Promise<void> {
    return this.request<void>("/notifications/mark-all-read", {
      method: "POST",
    });
  }

  async getProfile(): Promise<UserProfileResponse> {
    return this.request<UserProfileResponse>("/profile/me");
  }

  async updateProfile(data: { fullName?: string }): Promise<UserProfileResponse> {
    return this.request<UserProfileResponse>("/profile/me", {
      method: "PATCH",
      body: JSON.stringify(data),
    });
  }

  async changePassword(data: { oldPassword: string; newPassword: string }): Promise<void> {
    return this.request<void>("/profile/change-password", {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async processCopilotChat(projectId: string, orgId: string, prompt: string, conversationId?: string): Promise<any> {
    let url = `/projects/${projectId}/copilot/chat?orgId=${encodeURIComponent(orgId)}&prompt=${encodeURIComponent(prompt)}`;
    if (conversationId) {
      url += `&conversationId=${encodeURIComponent(conversationId)}`;
    }
    return this.request<any>(url, {
      method: "POST",
    });
  }

  async getSecurityOverview(): Promise<SecurityOverview> {
    return this.request<SecurityOverview>("/profile/security-overview");
  }

  async getIncidents(projectId: string): Promise<Incident[]> {
    return this.request<Incident[]>(`/projects/${projectId}/incidents`);
  }

  async createIncident(projectId: string, orgId: string, data: { title: string; severity: string; rootCause?: string; confidenceScore?: number }): Promise<Incident> {
    return this.request<Incident>(`/projects/${projectId}/incidents?orgId=${encodeURIComponent(orgId)}`, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  async resolveIncident(incidentId: string, orgId: string): Promise<Incident> {
    return this.request<Incident>(`/incidents/${incidentId}/resolve?orgId=${encodeURIComponent(orgId)}`, {
      method: "POST",
    });
  }

  async getAnalyticsOverview(projectId: string): Promise<any> {
    return this.request<any>(`/projects/${projectId}/analytics/overview`);
  }

  async getDoraMetrics(projectId: string): Promise<any> {
    return this.request<any>(`/projects/${projectId}/analytics/dora`);
  }

  async getEnvironments(projectId: string): Promise<any[]> {
    return this.request<any[]>(`/projects/${projectId}/environments`);
  }

  async getRunners(projectId: string): Promise<any[]> {
    return this.request<any[]>(`/projects/${projectId}/runners`);
  }
}

export const api = new ApiClient();
