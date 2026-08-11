"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, FileSpreadsheet, FileCode } from "lucide-react";

interface AuditItem {
  id: string;
  action: string;
  actor: string;
  correlationId: string;
  severity: string;
  timestamp: string;
}

export default function ProjectAuditPage() {
  const [auditLogs] = useState<AuditItem[]>(() => [
    { id: "aud-1", action: "PROJECT_VARIABLE_UPDATE", actor: "engineer@cloudforge.ai", correlationId: "c8e1-93fa-4b12", severity: "INFO", timestamp: "2026-07-31T22:00:00.000Z" },
    { id: "aud-2", action: "ENVIRONMENT_PROVISION", actor: "engineer@cloudforge.ai", correlationId: "f921-11a2-87c1", severity: "WARN", timestamp: "2026-07-31T21:00:00.000Z" },
  ]);

  const handleExportCSV = () => {
    const headers = "ID,Action,Actor,CorrelationID,Severity,Timestamp\n";
    const rows = auditLogs.map((l) => `${l.id},${l.action},${l.actor},${l.correlationId},${l.severity},${l.timestamp}`).join("\n");
    const blob = new Blob([headers + rows], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "project-audit-logs.csv";
    a.click();
  };

  const handleExportJSON = () => {
    const blob = new Blob([JSON.stringify(auditLogs, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "project-audit-logs.json";
    a.click();
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Immutable Audit Trail</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Security-hardened audit logs with unique correlation tracking IDs</p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={handleExportCSV}
                className="px-3.5 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#E7EDF7] hover:text-[#3DD9C4] font-medium text-xs transition-all flex items-center gap-1.5"
              >
                <FileSpreadsheet className="w-4 h-4 text-[#3DD9C4]" />
                Export CSV
              </button>

              <button
                onClick={handleExportJSON}
                className="px-3.5 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#E7EDF7] hover:text-[#3DD9C4] font-medium text-xs transition-all flex items-center gap-1.5"
              >
                <FileCode className="w-4 h-4 text-[#FBBF24]" />
                Export JSON
              </button>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Audit Records ({auditLogs.length})</h3>

            <div className="space-y-3 font-mono text-xs">
              {auditLogs.map((l) => (
                <div key={l.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <ShieldCheck className="w-4 h-4 text-[#3DD9C4]" />
                    <div>
                      <span className="font-bold text-[#E7EDF7]">{l.action}</span>
                      <p className="text-[10px] text-[#8B99B8] mt-0.5">Actor: {l.actor} | Correlation ID: {l.correlationId}</p>
                    </div>
                  </div>

                  <span className="px-2 py-0.5 rounded text-[10px] bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                    {l.severity}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
