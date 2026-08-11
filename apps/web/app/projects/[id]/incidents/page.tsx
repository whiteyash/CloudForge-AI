"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { AlertOctagon, BrainCircuit, ShieldAlert, Sparkles, CheckCircle2 } from "lucide-react";
import Link from "next/link";

interface IncidentItem {
  id: string;
  title: string;
  severity: string;
  status: string;
  rootCause: string;
  confidenceScore: number;
}

export default function IncidentsDashboardPage() {
  const [incidents, setIncidents] = useState<IncidentItem[]>([
    {
      id: "inc-1",
      title: "Production Runner Agent Memory Eviction",
      severity: "CRITICAL",
      status: "OPEN",
      rootCause: "Memory leak in pod container runtime caused k8s node memory pressure eviction.",
      confidenceScore: 0.94,
    },
    {
      id: "inc-2",
      title: "Deployment Timeout on Staging Target",
      severity: "HIGH",
      status: "INVESTIGATING",
      rootCause: "Network latency spike on database connection pool initialization.",
      confidenceScore: 0.88,
    },
  ]);

  const [message, setMessage] = useState<string | null>(null);

  const handleResolve = (id: string) => {
    setIncidents((prev) =>
      prev.map((i) => (i.id === id ? { ...i, status: "RESOLVED" } : i))
    );
    setMessage("Incident resolved successfully.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Incident Intelligence & AIOps</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Automated incident detection, root cause analysis (RCA), and AI-guided remediation</p>
            </div>

            <Link
              href="/projects/proj-1/ai"
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Sparkles className="w-4 h-4" />
              Ask AI Assistant
            </Link>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Incident Roster */}
          <div className="space-y-4">
            {incidents.map((inc) => (
              <div key={inc.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#EF4444]">
                    <AlertOctagon className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/incidents/${inc.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        {inc.title}
                      </Link>
                      <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                        inc.severity === "CRITICAL" ? "bg-[#EF4444]/10 text-[#EF4444] border border-[#EF4444]/30" : "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30"
                      }`}>
                        {inc.severity}
                      </span>
                      <span className={`px-2 py-0.5 rounded text-[10px] font-mono ${
                        inc.status === "RESOLVED" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" : "bg-[#16233A] text-[#8B99B8] border border-[#22314D]"
                      }`}>
                        {inc.status}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-sans flex items-center gap-1">
                      <BrainCircuit className="w-3.5 h-3.5 text-[#3DD9C4]" />
                      Root Cause: {inc.rootCause}
                    </p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-1">
                      AI Confidence Score: Math.round({inc.confidenceScore * 100})% | Status: {inc.status}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {inc.status !== "RESOLVED" && (
                    <button
                      onClick={() => handleResolve(inc.id)}
                      className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#34D399] hover:bg-[#34D399]/10 text-xs font-semibold flex items-center gap-1.5 transition-all"
                    >
                      <ShieldAlert className="w-3.5 h-3.5" />
                      Resolve Incident
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
