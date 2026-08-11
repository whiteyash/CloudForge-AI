"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { BookOpen, BrainCircuit, Sparkles, Network, CheckCircle2, FileText } from "lucide-react";
import Link from "next/link";

interface RunbookItem {
  id: string;
  title: string;
  category: string;
  version: string;
  successRate: number;
}

interface SimilarIncidentItem {
  code: string;
  title: string;
  similarity: number;
  duration: string;
}

export default function RunbooksPage() {
  const [runbooks] = useState<RunbookItem[]>([
    { id: "rb-1", title: "Kubernetes OOMKilled Container Remediation", category: "INCIDENT", version: "1.4", successRate: 96 },
    { id: "rb-2", title: "PostgreSQL Connection Pool Exhaustion Procedure", category: "DATABASE", version: "2.1", successRate: 94 },
  ]);

  const [similar] = useState<SimilarIncidentItem[]>([
    { code: "INC-704", title: "Runner Daemon Memory Pressure", similarity: 94, duration: "18 mins" },
    { code: "INC-612", title: "Pipeline Stage OOM Eviction", similarity: 86, duration: "24 mins" },
  ]);

  const [analyzed, setAnalyzed] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleGeneratePostmortem = () => {
    setAnalyzed(true);
    setMessage("AI Postmortem Report & Knowledge Graph generated for Incident #INC-802.");
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
                <BookOpen className="w-5 h-5" />
              </Link>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Enterprise Knowledge & Runbooks Platform</h1>
                <p className="text-xs text-[#8B99B8] mt-0.5">AI-powered operational playbooks, incident similarity, & postmortem generation</p>
              </div>
            </div>

            <button
              onClick={handleGeneratePostmortem}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Sparkles className="w-4 h-4" />
              Generate AI Postmortem
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* AI Recommended Runbook Banner */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#3DD9C4]/30 space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-heading font-bold text-[#3DD9C4] flex items-center gap-2">
                <BrainCircuit className="w-5 h-5 text-[#3DD9C4]" />
                AI Recommended Runbook — 96% Historical Success Rate
              </h3>
              <span className="px-2.5 py-0.5 rounded text-[10px] font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                v1.4 Verified
              </span>
            </div>
            <p className="text-xs text-[#E7EDF7] leading-relaxed">
              Playbook: Runbook #102: Kubernetes OOMKilled Container Remediation. Action: Increase container heap allocation limit to 4GB and restart evicted runner pod daemon.
            </p>
          </div>

          {/* Postmortem Card */}
          {analyzed && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <FileText className="w-4 h-4 text-[#38BDF8]" />
                AI Postmortem Report — Incident #INC-802
              </h3>
              <p className="text-xs text-[#8B99B8]">Root Cause: OOMKilled daemon eviction during parallel image extraction in container stage.</p>
              <p className="text-xs text-[#34D399]">Lessons Learned: Automate workspace cache pruning and enforce memory limits on runner daemon pool.</p>
            </div>
          )}

          {/* Runbook Library & Similar Incidents Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Runbook Library */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <BookOpen className="w-4 h-4 text-[#38BDF8]" />
                Runbook Library
              </h3>

              <div className="space-y-3 font-mono text-xs">
                {runbooks.map((rb) => (
                  <div key={rb.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{rb.title}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                        {rb.successRate}% Success
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-[10px] text-[#8B99B8]">
                      <span>Category: {rb.category}</span>
                      <span>Version: {rb.version}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Similar Incidents */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Network className="w-4 h-4 text-[#F59E0B]" />
                Similar Historical Incidents
              </h3>

              <div className="space-y-3 font-mono text-xs">
                {similar.map((sim) => (
                  <div key={sim.code} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{sim.code}: {sim.title}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                        {sim.similarity}% Match
                      </span>
                    </div>
                    <p className="text-[10px] text-[#8B99B8]">Resolution duration: {sim.duration}</p>
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
