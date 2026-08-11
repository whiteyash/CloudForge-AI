"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, CheckCircle2, XCircle, FileText, Lock } from "lucide-react";

interface PlanItem {
  id: string;
  title: string;
  targetType: string;
  confidence: number;
  status: "PENDING_APPROVAL" | "APPROVED" | "EXECUTED" | "REJECTED";
  summary: string;
  risk: string;
  rollback: string;
}

export default function OperationsPage() {
  const [plans, setPlans] = useState<PlanItem[]>([
    {
      id: "plan-101",
      title: "Remediation Plan for DEPLOYMENT",
      targetType: "DEPLOYMENT",
      confidence: 96,
      status: "PENDING_APPROVAL",
      summary: "Increase runner heap memory allocation limit to 4GB and restart evicted runner pod daemon.",
      risk: "Low Risk: Zero downtime, single container heap scale from 2GB to 4GB",
      rollback: "Revert container spec memory limits to 2GB via DeploymentEngineService",
    },
    {
      id: "plan-98",
      title: "Remediation Plan for PIPELINE",
      targetType: "PIPELINE",
      confidence: 94,
      status: "EXECUTED",
      summary: "Clear build workspace cache and retry stalled pipeline stage #4.",
      risk: "Low Risk: Workspace cache purge",
      rollback: "None required",
    },
  ]);

  const [message, setMessage] = useState<string | null>(null);

  const handleApprove = (id: string) => {
    setPlans((prev) =>
      prev.map((p) => (p.id === id ? { ...p, status: "APPROVED" } : p))
    );
    setMessage(`Remediation Plan #${id} APPROVED by Human Operator. Action delegated to DeploymentEngineService.`);
  };

  const handleReject = (id: string) => {
    setPlans((prev) =>
      prev.map((p) => (p.id === id ? { ...p, status: "REJECTED" } : p))
    );
    setMessage(`Remediation Plan #${id} REJECTED by Human Operator. No action executed.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4] border border-[#22314D]">
                <ShieldCheck className="w-5 h-5" />
              </div>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Human-Approved Autonomous Operations</h1>
                <p className="text-xs text-[#8B99B8] mt-0.5">AI Remediation Planning with Mandatory Human Approval Gate Execution</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <span className="px-3 py-1 rounded-xl bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 font-mono text-xs flex items-center gap-1.5">
                <Lock className="w-3.5 h-3.5" />
                Human Approval Gate ACTIVE
              </span>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Safety Validation Panel */}
          <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="space-y-1">
              <span className="text-[10px] text-[#8B99B8] uppercase tracking-wider">Freeze Window</span>
              <p className="text-xs font-mono text-[#34D399] font-bold">INACTIVE (Safe)</p>
            </div>
            <div className="space-y-1">
              <span className="text-[10px] text-[#8B99B8] uppercase tracking-wider">Approval Policies</span>
              <p className="text-xs font-mono text-[#34D399] font-bold">PASSED</p>
            </div>
            <div className="space-y-1">
              <span className="text-[10px] text-[#8B99B8] uppercase tracking-wider">Runner Fleet Health</span>
              <p className="text-xs font-mono text-[#38BDF8] font-bold">4 Online / Healthy</p>
            </div>
            <div className="space-y-1">
              <span className="text-[10px] text-[#8B99B8] uppercase tracking-wider">Target Env Status</span>
              <p className="text-xs font-mono text-[#34D399] font-bold">98% STABLE</p>
            </div>
          </div>

          {/* Remediation Plans List */}
          <div className="space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
              <FileText className="w-4 h-4 text-[#3DD9C4]" />
              Remediation Approval Queue
            </h3>

            <div className="space-y-4">
              {plans.map((p) => (
                <div key={p.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <span className="font-heading text-sm font-bold text-[#E7EDF7]">{p.title}</span>
                      <span className="px-2.5 py-0.5 rounded text-[10px] font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                        {p.confidence}% Confidence
                      </span>
                    </div>

                    <span
                      className={`px-3 py-1 rounded-xl text-xs font-mono font-bold border ${
                        p.status === "PENDING_APPROVAL"
                          ? "bg-[#F59E0B]/10 text-[#F59E0B] border-[#F59E0B]/30"
                          : p.status === "APPROVED" || p.status === "EXECUTED"
                          ? "bg-[#34D399]/10 text-[#34D399] border-[#34D399]/30"
                          : "bg-[#EF4444]/10 text-[#EF4444] border-[#EF4444]/30"
                      }`}
                    >
                      {p.status}
                    </span>
                  </div>

                  <p className="text-xs text-[#E7EDF7] leading-relaxed">{p.summary}</p>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs font-mono p-4 rounded-xl bg-[#0A1020] border border-[#22314D]">
                    <div>
                      <span className="text-[10px] text-[#F59E0B]">Risk Assessment:</span>
                      <p className="text-[#8B99B8] mt-0.5">{p.risk}</p>
                    </div>
                    <div>
                      <span className="text-[10px] text-[#38BDF8]">Rollback Plan:</span>
                      <p className="text-[#8B99B8] mt-0.5">{p.rollback}</p>
                    </div>
                  </div>

                  {p.status === "PENDING_APPROVAL" && (
                    <div className="flex items-center justify-end gap-3 pt-2">
                      <button
                        onClick={() => handleReject(p.id)}
                        className="px-4 py-2 rounded-xl bg-[#EF4444]/10 text-[#EF4444] font-heading font-semibold text-xs border border-[#EF4444]/30 hover:bg-[#EF4444]/20 transition-all flex items-center gap-1.5"
                      >
                        <XCircle className="w-4 h-4" />
                        Reject Plan
                      </button>

                      <button
                        onClick={() => handleApprove(p.id)}
                        className="px-5 py-2 rounded-xl bg-[#34D399] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#10B981] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(52,211,153,0.3)]"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        Approve & Execute Remediation
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
