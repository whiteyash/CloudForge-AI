"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Users, Plus, UserPlus, Shield, CheckCircle2, AlertCircle, X, Trash2 } from "lucide-react";
import { api, TeamResponse, MemberResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function TeamsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [orgId, setOrgId] = useState<string>("");
  const [orgMembers, setOrgMembers] = useState<MemberResponse[]>([]);
  const [teamName, setTeamName] = useState("");
  const [teamDescription, setTeamDescription] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState<TeamResponse | null>(null);
  const [selectedUserIdToAdd, setSelectedUserIdToAdd] = useState<string>("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const resolveTargetOrg = async (): Promise<string> => {
    let resolved = "";
    try {
      const me = await api.me();
      if (me.organizations && me.organizations.length > 0) {
        const stored = typeof window !== "undefined" ? localStorage.getItem("cf_active_org_id") : null;
        const validStored = me.organizations.find((o: any) => o.id === stored);
        resolved = validStored ? validStored.id : me.organizations[0].id;
      }
    } catch {
      // Fallback if me() fails
    }

    if (!resolved) {
      try {
        const orgs = await api.request<any[]>("/orgs");
        if (orgs && orgs.length > 0) {
          resolved = orgs[0].id;
        }
      } catch {
        // Ignore fallback error
      }
    }

    if (resolved && typeof window !== "undefined") {
      localStorage.setItem("cf_active_org_id", resolved);
    }
    return resolved;
  };

  const loadTeamsAndMembers = useCallback(async (targetOrgId: string) => {
    if (!targetOrgId) return;
    setLoading(true);
    try {
      const [teamsData, membersData] = await Promise.all([
        api.getTeams(targetOrgId),
        api.getMembers(targetOrgId).catch(() => []),
      ]);
      setTeams(teamsData);
      setOrgMembers(membersData);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to load teams from server.");
    } finally {
      setLoading(false);
    }
  }, [environment]);

  useEffect(() => {
    let isMounted = true;
    resolveTargetOrg().then((resolved) => {
      if (isMounted && resolved) {
        setOrgId(resolved);
        loadTeamsAndMembers(resolved);
      }
    });
    return () => {
      isMounted = false;
    };
  }, [loadTeamsAndMembers, environment]);

  const handleCreateTeam = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setMessage(null);

    if (!orgId) {
      setError("No valid organization selected.");
      return;
    }

    try {
      const created = await api.createTeam(orgId, { name: teamName, description: teamDescription });
      setTeams((prev) => [...prev, created]);
      setMessage(`Team "${created.name}" created successfully.`);
      setTeamName("");
      setTeamDescription("");
      setShowCreateModal(false);
      loadTeamsAndMembers(orgId);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to create team");
    }
  };

  const handleAddMember = async (teamId: string) => {
    if (!selectedUserIdToAdd || !orgId) return;
    setError(null);
    setMessage(null);
    try {
      const updatedTeam = await api.addTeamMember(orgId, teamId, selectedUserIdToAdd);
      setTeams((prev) => prev.map((t) => (t.id === teamId ? updatedTeam : t)));
      setSelectedTeam(updatedTeam);
      setSelectedUserIdToAdd("");
      setMessage("Team member added successfully.");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to add team member");
    }
  };

  const handleRemoveMember = async (teamId: string, userId: string) => {
    if (!orgId) return;
    setError(null);
    setMessage(null);
    try {
      const updatedTeam = await api.removeTeamMember(orgId, teamId, userId);
      setTeams((prev) => prev.map((t) => (t.id === teamId ? updatedTeam : t)));
      setSelectedTeam(updatedTeam);
      setMessage("Team member removed successfully.");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to remove team member");
    }
  };

  const handleDeleteTeam = async (teamId: string) => {
    if (!orgId) return;
    if (!confirm("Are you sure you want to delete/archive this team?")) return;
    setError(null);
    setMessage(null);
    try {
      await api.deleteTeam(orgId, teamId);
      setTeams((prev) => prev.filter((t) => t.id !== teamId));
      setSelectedTeam(null);
      setMessage("Team deleted successfully.");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to delete team");
    }
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Team Workspace Management
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Organize engineers into functional teams and delegate project access for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>

            <button
              onClick={() => setShowCreateModal(true)}
              className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
            >
              <Plus className="w-4 h-4 stroke-[2.5]" />
              Create Team
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Teams Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {teams.map((team) => (
              <div
                key={team.id}
                className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-3">
                      <div className="p-2.5 rounded-xl bg-[#0A1020] border border-[#3DD9C4]/40 text-[#3DD9C4] shadow-[0_0_10px_rgba(61,217,196,0.2)]">
                        <Users className="w-5 h-5" />
                      </div>
                      <h3 className="text-base font-heading font-bold text-[#E7EDF7]">{team.name}</h3>
                    </div>
                    <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#0A1020] text-[#3DD9C4] border border-[#3DD9C4]/30 font-bold">
                      {team.members ? team.members.length : 0} MEMBERS
                    </span>
                  </div>

                  <p className="text-xs text-[#8B99B8] mt-2">{team.description || "No description provided."}</p>
                </div>

                <div className="mt-6 pt-4 border-t border-[#22314D]/60 flex items-center justify-between text-xs font-mono">
                  <div className="flex items-center gap-1.5 text-[#8B99B8]">
                    <Shield className="w-3.5 h-3.5 text-[#3DD9C4]" />
                    <span>RBAC Enforced</span>
                  </div>
                  <button
                    onClick={() => setSelectedTeam(team)}
                    className="text-[#3DD9C4] font-bold hover:underline flex items-center gap-1 cursor-pointer"
                  >
                    <UserPlus className="w-3.5 h-3.5" />
                    Manage Members
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Create Team Modal */}
          {showCreateModal && (
            <div className="fixed inset-0 bg-[#060A14]/80 backdrop-blur-md flex items-center justify-center p-4 z-50">
              <div className="bg-[#050F25] border border-[#3DD9C4]/40 rounded-3xl p-6 w-full max-w-md shadow-[0_0_50px_rgba(61,217,196,0.2)]">
                <h3 className="text-lg font-heading font-bold text-[#E7EDF7] mb-1">Create New Team</h3>
                <p className="text-xs text-[#8B99B8] mb-4">Define a new functional group inside your organization</p>

                <form onSubmit={handleCreateTeam} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Team Name</label>
                    <input
                      type="text"
                      required
                      value={teamName}
                      onChange={(e) => setTeamName(e.target.value)}
                      placeholder="e.g. SRE / Operations"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Description</label>
                    <textarea
                      rows={3}
                      value={teamDescription}
                      onChange={(e) => setTeamDescription(e.target.value)}
                      placeholder="Brief team purpose..."
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div className="flex items-center justify-end gap-2 pt-2">
                    <button
                      type="button"
                      onClick={() => setShowCreateModal(false)}
                      className="px-4 py-2 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-bold cursor-pointer"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] cursor-pointer shadow-[0_0_12px_rgba(61,217,196,0.3)]"
                    >
                      Create Team
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {/* Manage Members Modal */}
          {selectedTeam && (
            <div className="fixed inset-0 bg-[#060A14]/80 backdrop-blur-md flex items-center justify-center p-4 z-50">
              <div className="bg-[#050F25] border border-[#3DD9C4]/40 rounded-3xl p-6 w-full max-w-lg shadow-[0_0_50px_rgba(61,217,196,0.2)] flex flex-col gap-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-lg font-heading font-bold text-[#E7EDF7]">{selectedTeam.name}</h3>
                    <p className="text-xs text-[#8B99B8]">Manage team members and assigned roles</p>
                  </div>
                  <button
                    onClick={() => setSelectedTeam(null)}
                    className="p-1.5 rounded-lg bg-[#0A1020] text-[#8B99B8] hover:text-[#E7EDF7]"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>

                {/* Add Member Form */}
                <div className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex flex-col sm:flex-row items-center gap-2">
                  <select
                    value={selectedUserIdToAdd}
                    onChange={(e) => setSelectedUserIdToAdd(e.target.value)}
                    className="flex-1 bg-[#050F25] border border-[#22314D] rounded-xl px-3 py-2 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] w-full"
                  >
                    <option value="">Select Organization Member...</option>
                    {orgMembers
                      .filter((m) => !selectedTeam.members.some((tm) => tm.userId === m.userId))
                      .map((m) => (
                        <option key={m.userId} value={m.userId}>
                          {m.fullName || m.email} ({m.role})
                        </option>
                      ))}
                  </select>
                  <button
                    onClick={() => handleAddMember(selectedTeam.id)}
                    disabled={!selectedUserIdToAdd}
                    className="w-full sm:w-auto px-3.5 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] disabled:opacity-50 cursor-pointer"
                  >
                    Add Member
                  </button>
                </div>

                {/* Members List */}
                <div className="space-y-2 max-h-60 overflow-y-auto">
                  <h4 className="text-xs font-mono text-[#8B99B8] uppercase">Team Members ({selectedTeam.members ? selectedTeam.members.length : 0})</h4>
                  {selectedTeam.members && selectedTeam.members.length > 0 ? (
                    selectedTeam.members.map((m) => (
                      <div key={m.userId} className="p-3 rounded-xl bg-[#0A1020]/60 border border-[#22314D] flex items-center justify-between text-xs">
                        <div>
                          <p className="font-bold text-[#E7EDF7]">{m.fullName || m.email}</p>
                          <p className="text-[10px] font-mono text-[#8B99B8]">{m.email} • <span className="text-[#3DD9C4]">{m.role || "MEMBER"}</span></p>
                        </div>
                        <button
                          onClick={() => handleRemoveMember(selectedTeam.id, m.userId)}
                          className="p-1.5 text-[#F87171] hover:bg-[#F87171]/10 rounded-lg cursor-pointer"
                          title="Remove Member"
                        >
                          <X className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    ))
                  ) : (
                    <p className="text-xs text-[#8B99B8] italic p-2">No team members assigned yet.</p>
                  )}
                </div>

                {/* Footer Action */}
                <div className="pt-2 border-t border-[#22314D] flex items-center justify-between">
                  <button
                    onClick={() => handleDeleteTeam(selectedTeam.id)}
                    className="text-xs text-[#F87171] font-mono flex items-center gap-1 hover:underline cursor-pointer"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                    Delete Team
                  </button>
                  <button
                    onClick={() => setSelectedTeam(null)}
                    className="px-4 py-2 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-bold cursor-pointer"
                  >
                    Close
                  </button>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
