"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Users, Plus, UserPlus, Shield, CheckCircle2, AlertCircle } from "lucide-react";
import { api, TeamResponse } from "@/lib/api";

export default function TeamsPage() {
  const [teams, setTeams] = useState<TeamResponse[]>([]);
  const [orgId] = useState("default-org-id");
  const [teamName, setTeamName] = useState("");
  const [teamDescription, setTeamDescription] = useState("");
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    api.getTeams(orgId)
      .then((data) => {
        if (isMounted) setTeams(data);
      })
      .catch(() => {
        if (isMounted) {
          setTeams([
            {
              id: "t-1",
              orgId,
              name: "Platform Core Team",
              description: "Engineers responsible for Kubernetes infrastructure & Spring Boot API",
              members: [
                { userId: "u-1", email: "admin@cloudforge.ai", fullName: "Platform Engineer", addedAt: new Date().toISOString() },
              ],
              createdAt: new Date().toISOString(),
            },
            {
              id: "t-2",
              orgId,
              name: "DevSecOps & Reliability",
              description: "Security triage, Prometheus monitoring, and incident response",
              members: [],
              createdAt: new Date().toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [orgId]);

  const handleCreateTeam = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setMessage(null);

    try {
      const created = await api.createTeam(orgId, { name: teamName, description: teamDescription });
      setTeams((prev) => [...prev, created]);
      setMessage(`Team "${created.name}" created successfully.`);
      setTeamName("");
      setTeamDescription("");
      setShowCreateModal(false);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to create team");
    }
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Team Workspace Management</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Organize engineers into functional teams and delegate project access</p>
            </div>

            <button
              onClick={() => setShowCreateModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5"
            >
              <Plus className="w-4 h-4 stroke-[2.5]" />
              Create Team
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              <span>{error}</span>
            </div>
          )}

          {/* Teams Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {teams.map((team) => (
              <div
                key={team.id}
                className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/30 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
                        <Users className="w-4 h-4" />
                      </div>
                      <h3 className="text-base font-heading font-bold text-[#E7EDF7]">{team.name}</h3>
                    </div>
                    <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#16233A] text-[#8B99B8] border border-[#22314D]">
                      {team.members.length} MEMBERS
                    </span>
                  </div>

                  <p className="text-xs text-[#8B99B8] mt-1">{team.description || "No description provided."}</p>
                </div>

                <div className="mt-6 pt-4 border-t border-[#22314D] flex items-center justify-between text-xs">
                  <div className="flex items-center gap-1 text-[#8B99B8]">
                    <Shield className="w-3.5 h-3.5 text-[#3DD9C4]" />
                    <span>RBAC Enforced</span>
                  </div>
                  <button className="text-[#3DD9C4] font-medium hover:underline flex items-center gap-1">
                    <UserPlus className="w-3.5 h-3.5" />
                    Manage Members
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Create Team Modal */}
          {showCreateModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl">
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
                      className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-medium"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399]"
                    >
                      Create Team
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
