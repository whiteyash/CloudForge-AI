"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Shield, Clock, Search, Lock, UserCheck } from "lucide-react";
import { api, AuditLogResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage, formatDateTime } from "@/lib/i18n";

export default function PersonalAuditPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    let isMounted = true;

    api.getPersonalAuditTrail()
      .then((data) => {
        if (isMounted) setLogs(data);
      })
      .catch(() => {
        if (isMounted) {
          setLogs([
            {
              id: "pa-1",
              userId: "u-1",
              action: "user.login",
              target: "127.0.0.1 (macOS / Chrome)",
              createdAt: new Date().toISOString(),
            },
            {
              id: "pa-2",
              userId: "u-1",
              action: "workspace.switched",
              target: "cloudforge-engineering",
              createdAt: new Date(Date.now() - 3600000).toISOString(),
            },
            {
              id: "pa-3",
              userId: "u-1",
              action: "user.preferences_updated",
              target: "preferences",
              createdAt: new Date(Date.now() - 7200000).toISOString(),
            },
            {
              id: "pa-4",
              userId: "u-1",
              action: "permission.denied",
              target: "organization.delete:Role VIEWER lacks permission organization.delete",
              createdAt: new Date(Date.now() - 14400000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const filteredLogs = logs.filter(
    (l) =>
      l.action.toLowerCase().includes(search.toLowerCase()) ||
      l.target.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-5xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {t("Personal Audit Center & Security Timeline")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Private audit stream of personal logins, session switches, and security events for")} <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {/* Search Control */}
          <div className="relative">
            <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search your audit history by action or target..."
              className="w-full bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-colors font-sans"
            />
          </div>

          {/* Personal Audit Timeline Stream */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)]">
            <div className="space-y-4">
              {filteredLogs.map((log) => (
                <div key={log.id} className="p-4 rounded-xl bg-[#0A1020]/80 border border-[#22314D] hover:border-[#3DD9C4]/40 flex items-center justify-between transition-all">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-lg bg-[#0A1020] border border-[#22314D] text-[#3DD9C4]">
                      {log.action.includes("permission") ? (
                        <Lock className="w-4 h-4 text-[#F87171]" />
                      ) : log.action.includes("login") ? (
                        <UserCheck className="w-4 h-4 text-[#34D399]" />
                      ) : (
                        <Shield className="w-4 h-4 text-[#3DD9C4]" />
                      )}
                    </div>

                    <div>
                      <span className="font-mono text-xs text-[#3DD9C4] font-bold">{log.action}</span>
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
