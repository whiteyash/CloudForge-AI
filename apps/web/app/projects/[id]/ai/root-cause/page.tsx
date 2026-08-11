"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { GitBranch, BrainCircuit, Sparkles, Layers, ShieldCheck, CheckCircle2, ArrowRight } from "lucide-react";
import Link from "next/link";

interface DependencyNode {
  source: string;
  target: string;
  type: string;
}

export default function RootCausePage() {
  const [links] = useState<DependencyNode[]>([
    { source: "Pipeline #44 (Build)", target: "Job #BuildAndTest", type: "TRIGGERED" },
    { source: "Job #BuildAndTest", target: "Runner us-east-1a", type: "EXECUTED_ON" },
    { source: "Runner us-east-1a", target: "Deployment v1.4", type: "DEPLOYED_TO" },
    { source: "Deployment v1.4", target: "Staging Environment", type: "TARGETS" },
    { source: "Staging Environment", target: "Incident #INC-802", type: "TRIGGERED_INCIDENT" },
    { source: "Incident #INC-802", target: "OOMKilled Eviction", type: "CAUSED_BY" },
  ]);

  const [analyzed, setAnalyzed] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleRunRca = () => {
    setAnalyzed(true);
    setMessage("AI Multi-System Root Cause Analysis complete. Causal graph generated with 98% confidence.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/ai" className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4] border border-[#22314D]">
                <GitBranch className="w-5 h-5" />
              </Link>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Root Cause Intelligence Platform</h1>
                <p className="text-xs text-[#8B99B8] mt-0.5">Failure correlation & interactive dependency graph analysis</p>
              </div>
            </div>

            <button
              onClick={handleRunRca}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Sparkles className="w-4 h-4" />
              Analyze Incident Root Cause
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* AI RCA Summary Card */}
          {analyzed && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#3DD9C4]/30 space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-heading font-bold text-[#3DD9C4] flex items-center gap-2">
                  <BrainCircuit className="w-5 h-5 text-[#3DD9C4]" />
                  AI Root Cause Report — Incident #INC-802
                </h3>
                <span className="px-3 py-1 rounded-full text-xs font-mono font-bold bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/30">
                  Risk: HIGH (98% Confidence)
                </span>
              </div>
              <p className="text-xs text-[#E7EDF7] leading-relaxed">
                Root Cause: Cascading failure triggered by OOMKilled runner daemon evicting deployment target pod during memory spike in pipeline stage.
              </p>
            </div>
          )}

          {/* Dependency Graph & Recommendations Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Causal Failure Chain */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Layers className="w-4 h-4 text-[#3DD9C4]" />
                Failure Dependency Chain
              </h3>

              <div className="space-y-3 font-mono text-xs">
                {links.map((link, idx) => (
                  <div key={idx} className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <span className="text-[#E7EDF7] font-semibold">{link.source}</span>
                    <div className="flex items-center gap-1 text-[#8B99B8] text-[10px]">
                      <span>{link.type}</span>
                      <ArrowRight className="w-3.5 h-3.5 text-[#3DD9C4]" />
                    </div>
                    <span className="text-[#F59E0B] font-semibold">{link.target}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Recommendations Explorer */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-[#34D399]" />
                Remediation Recommendations
              </h3>

              <div className="space-y-3">
                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                  <span className="font-heading text-xs font-bold text-[#34D399]">1. Increase Runner Memory Allocation</span>
                  <p className="text-xs text-[#8B99B8]">Raise container heap threshold to 4GB to prevent OOM daemon eviction.</p>
                </div>
                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                  <span className="font-heading text-xs font-bold text-[#38BDF8]">2. Roll Back Staging Target</span>
                  <p className="text-xs text-[#8B99B8]">Revert Staging environment target deployment to v1.3 pending runner upgrade.</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
