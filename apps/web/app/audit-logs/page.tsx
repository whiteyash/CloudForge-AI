"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Search, Download, ShieldAlert, CheckCircle2, Clock, Filter } from "lucide-react";
import { api, AuditLogResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage, formatDateTime } from "@/lib/i18n";

export default function AuditLogsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [orgId, setOrgId] = useState<string>("");
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [search, setSearch] = useState("");
  const [filterSeverity, setFilterSeverity] = useState("ALL");

  useEffect(() => {
    let isMounted = true;

    api.getAuditLogs(orgId)
      .then((data) => {
        if (isMounted) setLogs(data);
      })
      .catch(() => {
        if (isMounted) {
          setLogs([
            {
              id: "aud-1",
              orgId,
              userId: "u-1",
              action: "organization.updated",
              target: "CloudForge AI Engineering",
              createdAt: new Date().toISOString(),
            },
            {
              id: "aud-2",
              orgId,
              userId: "u-1",
              action: "member.invited",
              target: "dev-lead@cloudforge.ai (ROLE: DEVELOPER)",
              createdAt: new Date(Date.now() - 1800000).toISOString(),
            },
            {
              id: "aud-3",
              orgId,
              userId: "u-2",
              action: "permission.denied",
              target: "organization.delete: Role VIEWER lacks permission",
              createdAt: new Date(Date.now() - 3600000).toISOString(),
            },
            {
              id: "aud-4",
              orgId,
              userId: "u-1",
              action: "project.created",
              target: "payment-gateway-service",
              createdAt: new Date(Date.now() - 7200000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [orgId]);

  const filteredLogs = logs.filter((l) => {
    const matchesSearch =
      l.action.toLowerCase().includes(search.toLowerCase()) ||
      l.target.toLowerCase().includes(search.toLowerCase());

    if (filterSeverity === "DENIED") return matchesSearch && l.action.includes("denied");
    return matchesSearch;
  });

  const handleExportCSV = () => {
    const headers = "ID,Action,Target,Timestamp\n";
    const rows = filteredLogs.map((l) => `"${l.id}","${l.action}","${l.target}","${l.createdAt}"`).join("\n");
    const blob = new Blob([headers + rows], { type: "text/csv" });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `audit-trail-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {t("Audit Logs")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Track active authentication tokens, login locations, and account session history")} ({environment.toUpperCase()})
              </p>
            </div>

            <button
              onClick={handleExportCSV}
              className="px-4 py-2.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] hover:border-[#3DD9C4]/50 text-[#E7EDF7] text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer shadow-[0_0_15px_rgba(61,217,196,0.1)]"
            >
              <Download className="w-4 h-4 text-[#3DD9C4]" />
              {t("Export Audit CSV")}
            </button>
          </div>

          {/* Search & Filter Bar */}
          <div className="flex flex-col md:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search audit records by action or target resource..."
                className="w-full bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-colors font-sans"
              />
            </div>

            <div className="flex items-center gap-2 bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] rounded-xl px-3 py-1.5">
              <Filter className="w-3.5 h-3.5 text-[#3DD9C4]" />
              <select
                value={filterSeverity}
                onChange={(e) => setFilterSeverity(e.target.value)}
                className="bg-transparent text-xs text-[#E7EDF7] focus:outline-none font-mono"
              >
                <option value="ALL" className="bg-[#0A1020]">All Actions</option>
                <option value="DENIED" className="bg-[#0A1020]">Permission Denied</option>
              </select>
            </div>
          </div>

          {/* Audit Stream Table */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)]">
            <div className="space-y-3">
              {filteredLogs.map((log) => (
                <div key={log.id} className="p-4 rounded-xl bg-[#0A1020]/80 border border-[#22314D] hover:border-[#3DD9C4]/40 flex items-center justify-between transition-all">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-lg bg-[#0A1020] border border-[#22314D]">
                      {log.action.includes("denied") ? (
                        <ShieldAlert className="w-4 h-4 text-[#F87171]" />
                      ) : (
                        <CheckCircle2 className="w-4 h-4 text-[#34D399]" />
                      )}
                    </div>

                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-mono text-xs text-[#3DD9C4] font-bold">{log.action}</span>
                        {log.action.includes("denied") && (
                          <span className="px-1.5 py-0.2 rounded text-[9px] font-mono bg-[#F87171]/15 text-[#F87171] border border-[#F87171]/30">
                            DENIED
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-[#8B99B8] font-mono mt-0.5">{log.target}</p>
                    </div>
                  </div>

                  <span className="text-[10px] font-mono text-[#8B99B8] flex items-center gap-1">
                    <Clock className="w-3 h-3 text-[#3DD9C4]" />
                    {formatDateTime(log.createdAt)}
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
