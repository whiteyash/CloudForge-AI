"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ArrowLeft, BrainCircuit, ShieldAlert, Sparkles, CheckCircle2 } from "lucide-react";
import Link from "next/link";

export default function IncidentDetailsPage() {
  const [isResolved, setIsResolved] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const timelineEvents = [
    { time: "10:42:00 AM", event: "Deployment #dep-881 initiated to PRODUCTION-GLOBAL target", type: "DEPLOYMENT_START" },
    { time: "10:44:15 AM", event: "Runner node k8s-runner-04 lost heartbeat ping", type: "RUNNER_OFFLINE" },
    { time: "10:45:00 AM", event: "Job execution timed out after 300 seconds", type: "JOB_FAILED" },
    { time: "10:46:10 AM", event: "System Alert triggered: High Runner Capacity & Deployment Gate Failure", type: "ALERT_TRIGGERED" },
    { time: "10:47:00 AM", event: "AIOps Engine generated Root Cause Analysis & Automated Rollback Recommendation", type: "RCA_ANALYSIS" },
  ];

  const recommendations = [
    {
      action: "Execute Automated Rollback to Previous Release v2.4.1",
      reasoning: "Runner node disconnect caused partial pod deployment; rolling back ensures zero-downtime stability.",
      confidence: 94,
    },
    {
      action: "Restart Runner Agent on Node k8s-runner-04",
      reasoning: "Node memory pressure evicted runner daemon process; restarting frees buffer cache.",
      confidence: 88,
    },
  ];

  const handleExecuteRollback = () => {
    setIsResolved(true);
    setMessage("AI Action executed: Automated Rollback triggered and incident status set to RESOLVED.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/incidents" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Production Runner Agent Memory Eviction</h1>
                  <span className="px-2.5 py-0.5 rounded text-xs font-mono font-semibold bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/30">
                    CRITICAL
                  </span>
                  <span className={`px-2 py-0.5 rounded text-xs font-mono ${
                    isResolved ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" : "bg-[#16233A] text-[#8B99B8] border border-[#22314D]"
                  }`}>
                    {isResolved ? "RESOLVED" : "OPEN"}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">AI Root Cause Analysis & Automated Incident Timeline</p>
              </div>
            </div>

            {!isResolved && (
              <button
                onClick={handleExecuteRollback}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                <Sparkles className="w-4 h-4" />
                Apply AI Recommendation
              </button>
            )}
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Root Cause Analysis Panel */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
              <BrainCircuit className="w-5 h-5 text-[#3DD9C4]" />
              AI Root Cause Analysis (RCA) — Confidence 94%
            </h3>
            <p className="text-xs text-[#8B99B8] leading-relaxed">
              Likely Root Cause: Memory leak in container runtime caused Kubernetes node memory pressure eviction on runner node <code className="text-[#3DD9C4]">k8s-runner-04</code> during image extraction step.
            </p>
          </div>

          {/* Timeline & Recommendations Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Timeline */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">AI Incident Timeline</h3>
              <div className="space-y-3 font-mono text-xs">
                {timelineEvents.map((t) => (
                  <div key={t.time} className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                    <div className="flex items-center justify-between text-[#8B99B8]">
                      <span className="text-[#3DD9C4]">{t.time}</span>
                      <span className="text-[10px] bg-[#16233A] px-2 py-0.5 rounded">{t.type}</span>
                    </div>
                    <p className="text-[#E7EDF7] font-sans">{t.event}</p>
                  </div>
                ))}
              </div>
            </div>

            {/* Recommendations */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <ShieldAlert className="w-4 h-4 text-[#F59E0B]" />
                AI Guided Action Recommendations
              </h3>
              <div className="space-y-3">
                {recommendations.map((r) => (
                  <div key={r.action} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-heading text-xs font-bold text-[#3DD9C4]">{r.action}</span>
                      <span className="text-[10px] font-mono text-[#34D399] bg-[#34D399]/10 border border-[#34D399]/30 px-2 py-0.5 rounded">
                        {r.confidence}% Confidence
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8]">{r.reasoning}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
