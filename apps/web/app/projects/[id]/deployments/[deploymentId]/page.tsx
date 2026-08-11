"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ArrowLeft, CheckCircle2, RotateCcw, ShieldCheck, Clock, Check } from "lucide-react";
import Link from "next/link";

export default function DeploymentDetailsPage() {
  const [status, setStatus] = useState("PENDING_APPROVAL");
  const [message, setMessage] = useState<string | null>(null);

  const handleApprove = () => {
    setStatus("SUCCEEDED");
    setMessage("Production deployment approved by @lead. Release executed across Canary pods.");
  };

  const handleRollback = () => {
    setStatus("ROLLED_BACK");
    setMessage("Automated rollback executed to previous stable deployment version.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/deployments" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Deployment to PRODUCTION</h1>
                  <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                    status === "SUCCEEDED" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                    status === "PENDING_APPROVAL" ? "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                  }`}>
                    {status}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Strategy: CANARY | Artifact: core-service-app:2.4.0 | Idempotency: idem-prod-202</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              {status === "PENDING_APPROVAL" && (
                <button
                  onClick={handleApprove}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] flex items-center gap-1 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
                >
                  <Check className="w-4 h-4" /> Approve Production Release
                </button>
              )}
              {status === "SUCCEEDED" && (
                <button
                  onClick={handleRollback}
                  className="px-4 py-2 rounded-xl bg-[#16233A] text-[#F87171] font-heading font-semibold text-xs hover:bg-[#F87171]/10 flex items-center gap-1.5 border border-[#22314D]"
                >
                  <RotateCcw className="w-4 h-4" /> Rollback Release
                </button>
              )}
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Deployment Execution Progress Timeline */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Deployment Timeline & Gate Verification</h3>

            <div className="space-y-4 text-xs font-mono">
              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <CheckCircle2 className="w-4 h-4 text-[#34D399]" />
                  <span>1. Artifact & Checksum Validation (SHA-256 Verified)</span>
                </div>
                <span className="text-[10px] text-[#8B99B8]">SUCCEEDED</span>
              </div>

              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <ShieldCheck className="w-4 h-4 text-[#F59E0B]" />
                  <span>2. Protected Production Approval Gate (@cloudforge-lead)</span>
                </div>
                <span className={`text-[10px] ${status === "SUCCEEDED" ? "text-[#34D399]" : "text-[#F59E0B]"}`}>
                  {status === "SUCCEEDED" ? "APPROVED" : "PENDING_APPROVAL"}
                </span>
              </div>

              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Clock className="w-4 h-4 text-[#8B99B8]" />
                  <span>3. Canary Deployment Adapter Pod Rollout (10% $\rightarrow$ 100%)</span>
                </div>
                <span className="text-[10px] text-[#8B99B8]">{status === "SUCCEEDED" ? "SUCCEEDED" : "QUEUED"}</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
