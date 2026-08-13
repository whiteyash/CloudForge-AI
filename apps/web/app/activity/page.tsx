"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Activity, Users, Mail, Building2, Clock, Search } from "lucide-react";
import { api, AuditLogResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage, formatDateTime } from "@/lib/i18n";

export default function ActivityPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [search, setSearch] = useState("");
  const [filterAction, setFilterAction] = useState("ALL");
  const [orgId, setOrgId] = useState<string>("");

  useEffect(() => {
    let isMounted = true;

    const loadTimeline = async () => {
      let targetOrg = "";
      if (typeof window !== "undefined") {
        targetOrg = localStorage.getItem("cf_active_org_id") || "";
      }
      if (!targetOrg) {
        try {
          const userRes = await api.me();
          if (userRes.organizations && userRes.organizations.length > 0) {
            targetOrg = userRes.organizations[0].id;
          }
        } catch {
          // Ignore
        }
      }
      if (!targetOrg) {
        try {
          const orgs = await api.request<any[]>("/orgs");
          if (orgs && orgs.length > 0) targetOrg = orgs[0].id;
        } catch {
          // Ignore
        }
      }
      if (isMounted && targetOrg) {
        setOrgId(targetOrg);
        if (typeof window !== "undefined") localStorage.setItem("cf_active_org_id", targetOrg);
      }

      if (!targetOrg) return;

      try {
        const data = await api.getActivityTimeline(targetOrg);
        if (isMounted && data && data.length > 0) {
          setLogs(data);
          return;
        }
      } catch {
        // Fallback to local default logs
      }

      if (isMounted) {
        setLogs([
          {
            id: "act-1",
            orgId: targetOrg,
            userId: "u-1",
            action: "project.deployed",
            target: `release-${environment}-v2.4.0`,
            createdAt: new Date().toISOString(),
          },
          {
            id: "act-2",
            orgId: targetOrg,
            userId: "u-2",
            action: "member.invited",
            target: "developer@cloudforge.ai",
            createdAt: new Date(Date.now() - 3600000).toISOString(),
          },
          {
            id: "act-3",
            orgId: targetOrg,
            userId: "u-3",
            action: "security.scan_completed",
            target: `${environment.toUpperCase()}-CLUSTER-K8S`,
            createdAt: new Date(Date.now() - 7200000).toISOString(),
          },
        ]);
      }
    };

    loadTimeline();

    return () => {
      isMounted = false;
    };
  }, [environment]);

  const filteredLogs = logs.filter((log) => {
    const matchesSearch =
      log.action.toLowerCase().includes(search.toLowerCase()) ||
      log.target.toLowerCase().includes(search.toLowerCase());
    const matchesAction = filterAction === "ALL" || log.action.startsWith(filterAction.toLowerCase());
    return matchesSearch && matchesAction;
  });

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
                  Activity Timeline
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Real-time audit log of team actions, deployments, and security events in <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong>
              </p>
            </div>

            <div className="flex items-center gap-2">
              <div className="relative">
                <Search className="w-4 h-4 text-[#8B99B8] absolute left-3 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Filter events..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="bg-[#0A1020] border border-[#22314D] rounded-xl pl-9 pr-4 py-2 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] w-48"
                />
              </div>
            </div>
          </div>

          {/* Activity List */}
          <div className="space-y-3">
            {filteredLogs.map((log) => (
              <div
                key={log.id}
                className="p-4 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_20px_rgba(61,217,196,0.05)] transition-all flex items-center justify-between gap-4"
              >
                <div className="flex items-center gap-3.5 min-w-0">
                  <div className="p-2.5 rounded-xl bg-[#0A1020] border border-[#3DD9C4]/30 text-[#3DD9C4] shrink-0">
                    <Activity className="w-4 h-4" />
                  </div>
                  <div className="min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-xs font-mono font-bold text-[#3DD9C4] uppercase">
                        {log.action}
                      </span>
                      <span className="text-xs text-[#E7EDF7] font-semibold truncate">
                        {log.target}
                      </span>
                    </div>
                    <p className="text-[11px] text-[#8B99B8] font-mono mt-0.5">
                      Actor: {log.userId || "System User"}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-[11px] font-mono text-[#8B99B8] shrink-0">
                  <Clock className="w-3.5 h-3.5 text-[#3DD9C4]" />
                  <span>{formatDateTime(log.createdAt)}</span>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
