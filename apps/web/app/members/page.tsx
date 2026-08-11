"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { UserPlus, Shield, Crown, Search, CheckCircle2, AlertCircle, RefreshCw } from "lucide-react";
import { api, MemberResponse } from "@/lib/api";

export default function MembersPage() {
  const [members, setMembers] = useState<MemberResponse[]>([]);
  const [search, setSearch] = useState("");
  const [orgId] = useState("default-org-id");
  const [editingMember, setEditingMember] = useState<MemberResponse | null>(null);
  const [selectedRole, setSelectedRole] = useState("DEVELOPER");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    api.getMembers(orgId)
      .then((data) => {
        if (isMounted) setMembers(data);
      })
      .catch(() => {
        if (isMounted) {
          setMembers([
            {
              membershipId: "m-1",
              userId: "u-1",
              email: "admin@cloudforge.ai",
              fullName: "Platform Engineer",
              role: "OWNER",
              createdAt: new Date().toISOString(),
            },
            {
              membershipId: "m-2",
              userId: "u-2",
              email: "sarah.ops@cloudforge.ai",
              fullName: "Sarah Jenkins (SRE Lead)",
              role: "ADMIN",
              createdAt: new Date(Date.now() - 86400000 * 5).toISOString(),
            },
            {
              membershipId: "m-3",
              userId: "u-3",
              email: "alex.dev@cloudforge.ai",
              fullName: "Alex Rivera",
              role: "DEVELOPER",
              createdAt: new Date(Date.now() - 86400000 * 12).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [orgId]);

  const handleRoleUpdate = async () => {
    if (!editingMember) return;
    setMessage(null);
    setError(null);
    try {
      await api.updateMemberRole(orgId, editingMember.userId, selectedRole);
      setMembers((prev) =>
        prev.map((m) => (m.userId === editingMember.userId ? { ...m, role: selectedRole } : m))
      );
      setMessage(`Role updated for ${editingMember.email}.`);
      setEditingMember(null);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to update role");
    }
  };

  const handleRemoveMember = async (userId: string, email: string) => {
    if (!confirm(`Are you sure you want to remove ${email} from this organization?`)) return;
    setMessage(null);
    setError(null);
    try {
      await api.removeMember(orgId, userId);
      setMembers((prev) => prev.filter((m) => m.userId !== userId));
      setMessage(`Member ${email} removed.`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to remove member");
    }
  };

  const filteredMembers = members.filter(
    (m) =>
      m.fullName.toLowerCase().includes(search.toLowerCase()) ||
      m.email.toLowerCase().includes(search.toLowerCase()) ||
      m.role.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organization Members & Access Control</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Manage tenant access privileges, fine-grained roles, and ownership transfers</p>
            </div>

            <a
              href="/invitations"
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5"
            >
              <UserPlus className="w-4 h-4 stroke-[2.5]" />
              Invite New Member
            </a>
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

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search members by name, email, or role..."
              className="w-full bg-[#111B2E] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
            />
          </div>

          {/* Members Table */}
          <div className="rounded-2xl bg-[#111B2E] border border-[#22314D] overflow-hidden shadow-lg">
            <table className="w-full text-left text-xs">
              <thead className="bg-[#0A1020] text-[#8B99B8] font-mono border-b border-[#22314D]">
                <tr>
                  <th className="px-6 py-3">MEMBER</th>
                  <th className="px-6 py-3">ROLE</th>
                  <th className="px-6 py-3">JOINED</th>
                  <th className="px-6 py-3 text-right">ACTIONS</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#22314D]/60 text-[#E7EDF7]">
                {filteredMembers.map((member) => (
                  <tr key={member.membershipId} className="hover:bg-[#16233A]/50 transition-all">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-[#16233A] border border-[#3DD9C4]/40 text-[#3DD9C4] flex items-center justify-center font-heading font-bold text-xs">
                          {member.fullName ? member.fullName.charAt(0) : "U"}
                        </div>
                        <div>
                          <p className="font-heading font-bold text-sm text-[#E7EDF7]">{member.fullName}</p>
                          <p className="font-mono text-xs text-[#8B99B8]">{member.email}</p>
                        </div>
                      </div>
                    </td>

                    <td className="px-6 py-4">
                      <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full font-mono text-[10px] bg-[#16233A] text-[#3DD9C4] border border-[#3DD9C4]/30 font-bold">
                        {member.role === "OWNER" && <Crown className="w-3 h-3 text-[#FBBF24]" />}
                        {member.role === "ADMIN" && <Shield className="w-3 h-3 text-[#3DD9C4]" />}
                        {member.role}
                      </span>
                    </td>

                    <td className="px-6 py-4 font-mono text-xs text-[#8B99B8]">
                      {new Date(member.createdAt).toLocaleDateString()}
                    </td>

                    <td className="px-6 py-4 text-right space-x-2">
                      <button
                        onClick={() => {
                          setEditingMember(member);
                          setSelectedRole(member.role);
                        }}
                        className="px-3 py-1 rounded-lg bg-[#16233A] text-[#3DD9C4] hover:bg-[#3DD9C4] hover:text-[#0A1020] transition-all font-medium"
                      >
                        Change Role
                      </button>

                      {member.role !== "OWNER" && (
                        <button
                          onClick={() => handleRemoveMember(member.userId, member.email)}
                          className="px-3 py-1 rounded-lg bg-[#16233A] text-[#8B99B8] hover:text-[#F87171] hover:bg-[#F87171]/10 transition-all font-medium"
                        >
                          Remove
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Change Role Modal */}
          {editingMember && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl">
                <h3 className="text-lg font-heading font-bold text-[#E7EDF7] mb-1">Update Member Role</h3>
                <p className="text-xs text-[#8B99B8] mb-4">Modify permissions for {editingMember.email}</p>

                <div className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-2">Select RBAC Role</label>
                    <select
                      value={selectedRole}
                      onChange={(e) => setSelectedRole(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="OWNER">OWNER — Full Administrative Control</option>
                      <option value="ADMIN">ADMIN — Member Management & Pipelines</option>
                      <option value="DEVELOPER">DEVELOPER — Deployments & Logs Access</option>
                      <option value="DEVOPS">DEVOPS — Kubernetes Scaling & Workloads</option>
                      <option value="SECURITY">SECURITY — Vulnerability Triage & Audits</option>
                      <option value="VIEWER">VIEWER — Read-Only Inspection</option>
                    </select>
                  </div>

                  <div className="flex items-center justify-end gap-2 pt-2">
                    <button
                      onClick={() => setEditingMember(null)}
                      className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-medium"
                    >
                      Cancel
                    </button>
                    <button
                      onClick={handleRoleUpdate}
                      className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] flex items-center gap-1.5"
                    >
                      <RefreshCw className="w-3.5 h-3.5" />
                      Save Role
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
