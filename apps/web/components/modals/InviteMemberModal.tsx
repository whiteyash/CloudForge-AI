"use client";

import React, { useState } from "react";
import { X, Mail, Shield, UserPlus, Loader2, AlertCircle, CheckCircle2 } from "lucide-react";
import { api } from "@/lib/api";

interface InviteMemberModalProps {
  isOpen: boolean;
  onClose: () => void;
  orgId: string;
  onSuccess?: () => void;
}

export default function InviteMemberModal({ isOpen, onClose, orgId, onSuccess }: InviteMemberModalProps) {
  const [email, setEmail] = useState("");
  const [role, setRole] = useState<"OWNER" | "ADMIN" | "MEMBER">("MEMBER");
  const [team, setTeam] = useState("Engineering");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    const trimmedEmail = email.trim().toLowerCase();
    if (!trimmedEmail || !trimmedEmail.includes("@")) {
      setError("Please enter a valid email address.");
      return;
    }

    setLoading(true);

    try {
      await api.createInvitation(orgId, {
        email: trimmedEmail,
        role,
      });

      setSuccess(true);
      if (onSuccess) onSuccess();

      setTimeout(() => {
        setSuccess(false);
        setEmail("");
        setMessage("");
        onClose();
      }, 1500);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message || "Failed to send invitation. Please try again.");
      } else {
        setError("Failed to send invitation. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#0A1020]/80 backdrop-blur-md animate-in fade-in">
      <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-[#8B99B8] hover:text-[#E7EDF7] transition-colors p-1 rounded-lg hover:bg-[#16233A] cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 flex items-center justify-center text-[#3DD9C4]">
            <UserPlus className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Invite Organization Member</h2>
            <p className="text-xs text-[#8B99B8]">Grant cloud operations &amp; platform access</p>
          </div>
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {success ? (
          <div className="py-8 text-center space-y-3">
            <div className="w-12 h-12 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-400 flex items-center justify-center mx-auto animate-bounce">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Invitation Sent!</h3>
            <p className="text-xs text-[#8B99B8]">
              An invitation email and access token have been dispatched to <span className="font-mono text-[#3DD9C4]">{email}</span>.
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                EMAIL ADDRESS *
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3 top-3 text-[#8B99B8]" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="engineer@cloudforge.ai"
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-9 pr-4 py-2.5 text-xs text-[#E7EDF7] placeholder-[#8B99B8]/40 focus:outline-none focus:border-[#3DD9C4] transition-colors"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                  ROLE
                </label>
                <div className="relative">
                  <Shield className="w-4 h-4 absolute left-3 top-3 text-[#8B99B8]" />
                  <select
                    value={role}
                    onChange={(e) => setRole(e.target.value as "OWNER" | "ADMIN" | "MEMBER")}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-9 pr-3 py-2.5 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  >
                    <option value="MEMBER">MEMBER</option>
                    <option value="ADMIN">ADMIN</option>
                    <option value="OWNER">OWNER</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                  TEAM
                </label>
                <select
                  value={team}
                  onChange={(e) => setTeam(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2.5 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                >
                  <option value="Engineering">Engineering</option>
                  <option value="DevOps & K8s">DevOps &amp; K8s</option>
                  <option value="Security Operations">Security Ops</option>
                  <option value="SRE & Reliability">SRE &amp; Reliability</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                OPTIONAL MESSAGE
              </label>
              <textarea
                rows={2}
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Welcome to CloudForge AI Mission Control..."
                className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl p-3 text-xs text-[#E7EDF7] placeholder-[#8B99B8]/40 focus:outline-none focus:border-[#3DD9C4] transition-colors"
              />
            </div>

            <div className="flex items-center justify-end gap-2 pt-3 border-t border-[#22314D]">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-xs text-[#8B99B8] hover:text-[#E7EDF7] transition-colors cursor-pointer"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-5 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-colors flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)] disabled:opacity-50 cursor-pointer"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-3.5 h-3.5 animate-spin" />
                    <span>Dispatching...</span>
                  </>
                ) : (
                  <>
                    <UserPlus className="w-3.5 h-3.5" />
                    <span>Send Invitation</span>
                  </>
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
