"use client";

import React, { useState, Suspense } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { CheckCircle2, AlertCircle, Mail, UserCheck } from "lucide-react";
import { api } from "@/lib/api";

function AcceptContent() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const token = searchParams.get("token") || "";

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAccept = async () => {
    if (!token) {
      setError("No invitation token provided.");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await api.acceptInvitation(token);
      setSuccess(true);
      setTimeout(() => {
        router.push("/members");
      }, 2000);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to accept invitation token.");
    } finally {
      setLoading(false);
    }
  };

  const handleReject = async () => {
    if (!token) return;
    try {
      await api.rejectInvitation(token);
      router.push("/");
    } catch {
      router.push("/");
    }
  };

  return (
    <div className="min-h-screen bg-[#0A1020] text-[#E7EDF7] flex items-center justify-center p-4">
      <div className="max-w-md w-full p-8 rounded-3xl bg-[#111B2E] border border-[#22314D] shadow-2xl text-center space-y-6">
        <div className="w-16 h-16 rounded-2xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 flex items-center justify-center mx-auto text-[#3DD9C4]">
          <Mail className="w-8 h-8" />
        </div>

        <div>
          <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organization Invitation</h1>
          <p className="text-xs text-[#8B99B8] mt-2">
            You have been invited to join an organization on CloudForge AI Platform.
          </p>
        </div>

        {success ? (
          <div className="p-4 rounded-2xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center justify-center gap-2 font-semibold">
            <CheckCircle2 className="w-5 h-5" />
            <span>Invitation Accepted! Adding membership to workspace...</span>
          </div>
        ) : (
          <>
            {error && (
              <div className="p-4 rounded-2xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center justify-center gap-2">
                <AlertCircle className="w-5 h-5" />
                <span>{error}</span>
              </div>
            )}

            <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] font-mono text-xs text-[#3DD9C4] truncate">
              Token: {token || "Missing Token"}
            </div>

            <div className="flex gap-3">
              <button
                onClick={handleReject}
                className="flex-1 py-3 rounded-xl bg-[#16233A] text-[#8B99B8] font-heading font-bold text-xs hover:text-[#E7EDF7] transition-all"
              >
                Decline
              </button>
              <button
                onClick={handleAccept}
                disabled={loading || !token}
                className="flex-1 py-3 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] disabled:opacity-50 flex items-center justify-center gap-2"
              >
                <UserCheck className="w-4 h-4" />
                {loading ? "Joining..." : "Accept & Join"}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default function AcceptInvitationPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-[#0A1020] text-[#E7EDF7] flex items-center justify-center text-xs">Loading invitation...</div>}>
      <AcceptContent />
    </Suspense>
  );
}
