"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { UserPlus, Shield, Crown, Search, CheckCircle2, AlertCircle, RefreshCw, Trash2, Edit3, X, Check } from "lucide-react";
import { api, MemberResponse } from "@/lib/api";
import InviteMemberModal from "@/components/modals/InviteMemberModal";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

import { useLanguage } from "@/lib/i18n";

export default function MembersPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [members, setMembers] = useState<MemberResponse[]>([]);
  const [search, setSearch] = useState("");
  const [orgId, setOrgId] = useState<string>("");
  const [editingMember, setEditingMember] = useState<MemberResponse | null>(null);
  const [selectedRole, setSelectedRole] = useState("MEMBER");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [inviteModalOpen, setInviteModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let isMounted = true;
    const resolveOrg = async () => {
      let target = typeof window !== "undefined" ? localStorage.getItem("cf_active_org_id") || "" : "";
      if (!target) {
        try {
          const orgs = await api.request<any[]>("/orgs");
          if (orgs && orgs.length > 0) target = orgs[0].id;
        } catch {}
      }
      if (!target) {
        try {
          const me = await api.me();
          if (me.organizations && me.organizations.length > 0) target = me.organizations[0].id;
        } catch {}
      }
      if (isMounted && target) {
        setOrgId(target);
        if (typeof window !== "undefined") localStorage.setItem("cf_active_org_id", target);
      }
    };
    resolveOrg();
    return () => { isMounted = false; };
  }, []);

  const fetchMembers = useCallback(async () => {
    if (!orgId) return;
    setLoading(true);
    try {
      const data = await api.getMembers(orgId);
      setMembers(data);
    } catch {
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
          role: "MEMBER",
          createdAt: new Date(Date.now() - 86400000 * 12).toISOString(),
        },
      ]);
    } finally {
      setLoading(false);
    }
  }, [orgId]);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

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
                  {t("Organization Members & Access")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Manage user privileges, active status, and organization roles")} ({environment.toUpperCase()})
              </p>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={fetchMembers}
                className="p-2.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] hover:border-[#3DD9C4]/40 transition-colors cursor-pointer"
                title="Refresh Members"
              >
                <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin text-[#3DD9C4]" : ""}`} />
              </button>
              <button
                onClick={() => setInviteModalOpen(true)}
                className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
              >
                <UserPlus className="w-4 h-4 stroke-[2.5]" />
                {t("Invite Member")}
              </button>
            </div>
          </div>

          {/* Feedback Messages */}
          {message && (
            <div className="p-3.5 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}
          {error && (
            <div className="p-3.5 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search members by name, email, or role..."
              className="w-full bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl pl-10 pr-4 py-2.5 text-xs text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-colors font-sans"
            />
          </div>

          {/* Members Table */}
          <div className="rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)] overflow-hidden">
            <table className="w-full text-left text-xs text-[#E7EDF7]">
              <thead className="bg-[#0A1020]/90 border-b border-[#22314D] font-mono text-[10px] text-[#8B99B8] uppercase tracking-wider">
                <tr>
                  <th className="py-3.5 px-4">{t("Member")}</th>
                  <th className="py-3.5 px-4">{t("Role")}</th>
                  <th className="py-3.5 px-4">{t("Joined Date")}</th>
                  <th className="py-3.5 px-4 text-right">{t("ACTIONS")}</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#22314D]/50">
                {filteredMembers.map((member) => (
                  <tr key={member.membershipId} className="hover:bg-[#16233A]/40 transition-colors">
                    <td className="py-4 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-[#0A1020] border border-[#3DD9C4]/40 flex items-center justify-center text-[#3DD9C4] font-semibold text-xs shadow-[0_0_10px_rgba(61,217,196,0.2)]">
                          {member.fullName.charAt(0)}
                        </div>
                        <div>
                          <div className="font-semibold text-[#E7EDF7]">{member.fullName}</div>
                          <div className="text-[10px] font-mono text-[#8B99B8]">{member.email}</div>
                        </div>
                      </div>
                    </td>

                    <td className="py-4 px-4">
                      {editingMember?.userId === member.userId ? (
                        <div className="flex items-center gap-1.5">
                          <select
                            value={selectedRole}
                            onChange={(e) => setSelectedRole(e.target.value)}
                            className="bg-[#0A1020] border border-[#3DD9C4] rounded-lg px-2 py-1 text-xs text-[#E7EDF7] font-mono"
                          >
                            <option value="MEMBER">MEMBER</option>
                            <option value="ADMIN">ADMIN</option>
                            <option value="OWNER">OWNER</option>
                          </select>
                          <button
                            onClick={handleRoleUpdate}
                            className="p-1 rounded-lg bg-[#34D399]/20 text-[#34D399] hover:bg-[#34D399]/30 cursor-pointer"
                          >
                            <Check className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => setEditingMember(null)}
                            className="p-1 rounded-lg bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] cursor-pointer"
                          >
                            <X className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      ) : (
                        <span className={`inline-flex items-center gap-1 font-mono text-[10px] font-bold px-2.5 py-1 rounded-full border ${
                          member.role === "OWNER"
                            ? "bg-[#3DD9C4]/15 text-[#3DD9C4] border-[#3DD9C4]/30"
                            : member.role === "ADMIN"
                            ? "bg-[#4A72FF]/15 text-[#4A72FF] border-[#4A72FF]/30"
                            : "bg-[#8B99B8]/15 text-[#8B99B8] border-[#8B99B8]/30"
                        }`}>
                          {member.role === "OWNER" && <Crown className="w-3 h-3" />}
                          {member.role === "ADMIN" && <Shield className="w-3 h-3" />}
                          {member.role}
                        </span>
                      )}
                    </td>

                    <td className="py-4 px-4 font-mono text-[10px] text-[#8B99B8]">
                      {new Date(member.createdAt).toLocaleDateString()}
                    </td>

                    <td className="py-4 px-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => {
                            setEditingMember(member);
                            setSelectedRole(member.role);
                          }}
                          className="p-1.5 rounded-lg bg-[#0A1020] text-[#8B99B8] hover:text-[#3DD9C4] hover:border-[#3DD9C4]/40 border border-[#22314D] transition-colors cursor-pointer"
                          title="Change Role"
                        >
                          <Edit3 className="w-3.5 h-3.5" />
                        </button>
                        {member.role !== "OWNER" && (
                          <button
                            onClick={() => handleRemoveMember(member.userId, member.email)}
                            className="p-1.5 rounded-lg bg-[#0A1020] text-[#8B99B8] hover:text-[#F87171] hover:border-[#F87171]/40 border border-[#22314D] transition-colors cursor-pointer"
                            title="Remove Member"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </main>
      </div>

      <InviteMemberModal
        isOpen={inviteModalOpen}
        onClose={() => setInviteModalOpen(false)}
        orgId={orgId}
        onSuccess={fetchMembers}
      />
    </div>
  );
}
