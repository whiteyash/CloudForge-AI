"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ArrowLeft, CheckCircle2, ShieldCheck, Clock, Check, X } from "lucide-react";
import Link from "next/link";

interface StageItem {
  id: string;
  name: string;
  stageOrder: number;
  status: string;
  requiresApproval: boolean;
  isApproved: boolean;
}

export default function PipelineDetailsPage() {
  const [stages, setStages] = useState<StageItem[]>([
    { id: "stg-1", name: "Build", stageOrder: 1, status: "SUCCEEDED", requiresApproval: false, isApproved: false },
    { id: "stg-2", name: "Test", stageOrder: 2, status: "SUCCEEDED", requiresApproval: false, isApproved: false },
    { id: "stg-3", name: "Deploy to Staging", stageOrder: 3, status: "PENDING_APPROVAL", requiresApproval: true, isApproved: false },
  ]);

  const [runStatus, setRunStatus] = useState("PENDING_APPROVAL");
  const [message, setMessage] = useState<string | null>(null);

  const handleApprove = () => {
    setStages((prev) =>
      prev.map((s) =>
        s.id === "stg-3" ? { ...s, status: "SUCCEEDED", isApproved: true } : s
      )
    );
    setRunStatus("SUCCEEDED");
    setMessage("Deployment stage approved. Pipeline run completed successfully.");
  };

  const handleCancel = () => {
    setRunStatus("CANCELLED");
    setMessage("Pipeline run cancelled.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/pipelines" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">main-build-ci #42</h1>
                  <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                    runStatus === "SUCCEEDED" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                    runStatus === "PENDING_APPROVAL" ? "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                  }`}>
                    {runStatus}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Triggered by @cloudforge-lead | Correlation UUID: 9f8a2b3c-4d5e</p>
              </div>
            </div>

            {runStatus === "PENDING_APPROVAL" && (
              <div className="flex items-center gap-2">
                <button
                  onClick={handleCancel}
                  className="px-3 py-2 rounded-xl bg-[#16233A] text-[#F87171] text-xs font-bold flex items-center gap-1 hover:bg-[#F87171]/10"
                >
                  <X className="w-4 h-4" /> Cancel Run
                </button>
                <button
                  onClick={handleApprove}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] flex items-center gap-1 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
                >
                  <Check className="w-4 h-4" /> Approve Stage
                </button>
              </div>
            )}
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* DAG Stage Execution Diagram */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Stage Execution DAG Tree</h3>

            <div className="flex items-center gap-4 overflow-x-auto pb-2">
              {stages.map((s, idx) => (
                <React.Fragment key={s.id}>
                  <div className="p-5 rounded-2xl bg-[#0A1020] border border-[#22314D] min-w-[200px] space-y-3 shadow-md">
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-mono text-[#8B99B8]">Stage {s.stageOrder}</span>
                      {s.status === "SUCCEEDED" ? (
                        <CheckCircle2 className="w-4 h-4 text-[#34D399]" />
                      ) : s.status === "PENDING_APPROVAL" ? (
                        <Clock className="w-4 h-4 text-[#F59E0B]" />
                      ) : (
                        <Clock className="w-4 h-4 text-[#8B99B8]" />
                      )}
                    </div>

                    <h4 className="font-heading text-sm font-bold text-[#E7EDF7]">{s.name}</h4>

                    {s.requiresApproval && (
                      <div className="p-2 rounded-lg bg-[#16233A] border border-[#22314D] text-[10px] text-[#F59E0B] font-mono flex items-center gap-1.5">
                        <ShieldCheck className="w-3.5 h-3.5" />
                        <span>RBAC Approval Required</span>
                      </div>
                    )}
                  </div>

                  {idx < stages.length - 1 && (
                    <div className="w-8 h-0.5 bg-[#22314D] shrink-0" />
                  )}
                </React.Fragment>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
