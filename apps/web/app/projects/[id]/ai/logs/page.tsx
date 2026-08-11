"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Terminal, BrainCircuit, Sparkles, Fingerprint, Layers, CheckCircle2 } from "lucide-react";
import Link from "next/link";

interface LogClusterItem {
  id: string;
  clusterName: string;
  severity: string;
  count: number;
  services: string;
}

interface FingerprintItem {
  hash: string;
  exceptionClass: string;
  location: string;
}

export default function LogIntelligencePage() {
  const [clusters] = useState<LogClusterItem[]>([
    { id: "c-1", clusterName: "NullPointerException in Pipeline Stage", severity: "ERROR", count: 14, services: "pipeline-engine, runner-orchestration" },
    { id: "c-2", clusterName: "Connection Timeout to Postgres DB Pool", severity: "CRITICAL", count: 6, services: "deployment-engine, environment-service" },
  ]);

  const [fingerprints] = useState<FingerprintItem[]>([
    { hash: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", exceptionClass: "java.lang.NullPointerException", location: "PipelineEngine.java:142" },
    { hash: "f2ca1bb6c7e907d06dafe4687e579fce76b37e4e93b7605022da52e6ccc26fd2", exceptionClass: "java.util.concurrent.TimeoutException", location: "HttpClientAdapter.java:88" },
  ]);

  const [analyzed, setAnalyzed] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleRunAnalysis = () => {
    setAnalyzed(true);
    setMessage("AI Log Intelligence Analysis completed. Identified 2 clusters & 1 root cause recommendation.");
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
                <Terminal className="w-5 h-5" />
              </Link>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Log Intelligence Platform</h1>
                <p className="text-xs text-[#8B99B8] mt-0.5">Exception fingerprinting, error clustering, and stack trace intelligence</p>
              </div>
            </div>

            <button
              onClick={handleRunAnalysis}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Sparkles className="w-4 h-4" />
              Run AI Log Analysis
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* AI Log Summary Banner */}
          {analyzed && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#3DD9C4]/30 space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#3DD9C4] flex items-center gap-2">
                <BrainCircuit className="w-5 h-5 text-[#3DD9C4]" />
                AI Executive Log Summary — 92% Confidence
              </h3>
              <p className="text-xs text-[#E7EDF7] leading-relaxed font-sans">
                Executive Summary: 20 log entries analyzed across 5 subsystems. Root Cause: Buffer overflow during image extraction in container runtime evicted runner node daemon.
              </p>
            </div>
          )}

          {/* Clusters & Fingerprints Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Error Clusters */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Layers className="w-4 h-4 text-[#F59E0B]" />
                Error Clusters
              </h3>

              <div className="space-y-3">
                {clusters.map((c) => (
                  <div key={c.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{c.clusterName}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30">
                        {c.count} Occurrences
                      </span>
                    </div>
                    <p className="text-[10px] text-[#8B99B8] font-mono">Affected: {c.services}</p>
                  </div>
                ))}
              </div>
            </div>

            {/* Exception Fingerprints */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Fingerprint className="w-4 h-4 text-[#3DD9C4]" />
                Exception Fingerprints
              </h3>

              <div className="space-y-3 font-mono text-xs">
                {fingerprints.map((f) => (
                  <div key={f.hash} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                    <span className="text-[#3DD9C4] font-bold block">{f.exceptionClass}</span>
                    <p className="text-[#8B99B8] text-[11px] truncate">SHA-256: {f.hash}</p>
                    <p className="text-[#34D399] text-[11px] font-sans">Location: {f.location}</p>
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
