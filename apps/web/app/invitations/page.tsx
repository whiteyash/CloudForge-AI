"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { UserPlus, Mail, RotateCw, XCircle, CheckCircle2, AlertCircle, ExternalLink } from "lucide-react";
import { api, InvitationResponse } from "@/lib/api";

export default function InvitationsPage() {
  const [invitations, setInvitations] = useState<InvitationResponse[]>([]);
  const [orgId, setOrgId] = useState<string>("default-org-id");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("DEVELOPER");
  const [createdTokenLink, setCreatedTokenLink] = useState<string | null>(null);

  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const fetchInvitations = async (targetOrgId: string) => {
    try {
      const data = await api.listInvitations(targetOrgId);
      setInvitations(data);
    } catch {
      // Fallback empty list if not logged in or org not found
      setInvitations([]);
    }
  };

  useEffect(() => {
    api.me()
      .then((auth) => {
        if (auth.organizations && auth.organizations.length > 0) {
          const activeOrg = auth.organizations[0].id;
          setOrgId(activeOrg);
          fetchInvitations(activeOrg);
        }
      })
      .catch(() => {
        fetchInvitations(orgId);
      });
  }, []);

  const handleInvite = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setError(null);
    setCreatedTokenLink(null);

    try {
      const created = await api.inviteMember(orgId, { email, role });
      setMessage(`Invitation dispatched to ${email}. Token generated in DB.`);
      if (created && created.token) {
        setCreatedTokenLink(`${window.location.origin}/invitations/accept?token=${created.token}`);
      }
      setEmail("");
      fetchInvitations(orgId);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to send invitation");
    }
  };

  const handleResend = async (id: string, inviteEmail: string) => {
    setMessage(null);
    setError(null);
    try {
      const updated = await api.resendInvitation(orgId, id);
      setMessage(`Invitation token resent for ${inviteEmail}.`);
      if (updated && updated.token) {
        setCreatedTokenLink(`${window.location.origin}/invitations/accept?token=${updated.token}`);
      }
      fetchInvitations(orgId);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : `Failed to resend invitation to ${inviteEmail}`);
    }
  };

  const handleCancel = async (id: string, inviteEmail: string) => {
    setMessage(null);
    setError(null);
    try {
      await api.cancelInvitation(orgId, id);
      setInvitations((prev) => prev.filter((i) => i.id !== id));
      setMessage(`Invitation to ${inviteEmail} cancelled.`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : `Failed to cancel invitation to ${inviteEmail}`);
    }
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Invitation Center</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Issue, resend, or revoke organization invitation tokens</p>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {createdTokenLink && (
            <div className="p-4 rounded-xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 text-[#E7EDF7] text-xs space-y-2">
              <div className="flex items-center gap-1.5 text-[#3DD9C4] font-bold">
                <ExternalLink className="w-4 h-4" />
                Invitation Accept Link (Simulated Dispatch):
              </div>
              <div className="p-2 rounded bg-[#0A1020] border border-[#22314D] font-mono text-[11px] text-[#3DD9C4] break-all select-all">
                <a href={createdTokenLink} target="_blank" rel="noreferrer" className="hover:underline">
                  {createdTokenLink}
                </a>
              </div>
            </div>
          )}

          {/* Issue Invitation Form */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
              <UserPlus className="w-5 h-5 text-[#3DD9C4]" />
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Dispatch New Invitation Token</h2>
            </div>

            <form onSubmit={handleInvite} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
              <div className="md:col-span-1">
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Recipient Email</label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="engineer@company.com"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Assign Role</label>
                <select
                  value={role}
                  onChange={(e) => setRole(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                >
                  <option value="DEVELOPER">DEVELOPER</option>
                  <option value="ADMIN">ADMIN</option>
                  <option value="DEVOPS">DEVOPS</option>
                  <option value="SECURITY">SECURITY</option>
                  <option value="VIEWER">VIEWER</option>
                </select>
              </div>

              <div>
                <button
                  type="submit"
                  className="w-full py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)]"
                >
                  Send Invitation Token
                </button>
              </div>
            </form>
          </div>

          {/* Pending Invitations Table */}
          <div className="rounded-2xl bg-[#111B2E] border border-[#22314D] overflow-hidden shadow-lg">
            <div className="px-6 py-4 border-b border-[#22314D] flex items-center justify-between">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Pending Invitations ({invitations.length})</h3>
            </div>

            <table className="w-full text-left text-xs">
              <thead className="bg-[#0A1020] text-[#8B99B8] font-mono border-b border-[#22314D]">
                <tr>
                  <th className="px-6 py-3">RECIPIENT EMAIL</th>
                  <th className="px-6 py-3">ASSIGNED ROLE</th>
                  <th className="px-6 py-3">STATUS</th>
                  <th className="px-6 py-3 text-right">ACTIONS</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#22314D]/60 text-[#E7EDF7]">
                {invitations.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="px-6 py-8 text-center text-xs text-[#8B99B8] font-mono">
                      No pending invitations found for this organization.
                    </td>
                  </tr>
                ) : (
                  invitations.map((inv) => (
                    <tr key={inv.id} className="hover:bg-[#16233A]/50 transition-all">
                      <td className="px-6 py-4 font-mono text-xs">{inv.email}</td>
                      <td className="px-6 py-4 font-mono text-xs text-[#3DD9C4]">{inv.role}</td>
                      <td className="px-6 py-4">
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/30 font-bold">
                          {inv.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-right space-x-2">
                        <button
                          onClick={() => handleResend(inv.id, inv.email)}
                          className="px-3 py-1 rounded-lg bg-[#16233A] text-[#3DD9C4] hover:bg-[#3DD9C4] hover:text-[#0A1020] transition-all font-medium inline-flex items-center gap-1"
                        >
                          <RotateCw className="w-3 h-3" />
                          Resend Token
                        </button>
                        <button
                          onClick={() => handleCancel(inv.id, inv.email)}
                          className="px-3 py-1 rounded-lg bg-[#16233A] text-[#8B99B8] hover:text-[#F87171] hover:bg-[#F87171]/10 transition-all font-medium inline-flex items-center gap-1"
                        >
                          <XCircle className="w-3 h-3" />
                          Cancel
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </main>
      </div>
    </div>
  );
}
