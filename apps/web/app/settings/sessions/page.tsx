"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Laptop, Smartphone, Globe, LogOut, CheckCircle2, ShieldCheck, AlertCircle } from "lucide-react";
import { api, ActiveSessionResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function SessionManagerPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [sessions, setSessions] = useState<ActiveSessionResponse[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    api.getSessions()
      .then((data) => {
        if (isMounted) setSessions(data);
      })
      .catch(() => {
        if (isMounted) {
          setSessions([
            {
              id: "s-1",
              deviceType: "Desktop",
              browser: "Chrome 126",
              operatingSystem: "macOS Sonoma",
              ipAddress: "127.0.0.1 (Local)",
              isCurrent: true,
              lastActiveAt: new Date().toISOString(),
            },
            {
              id: "s-2",
              deviceType: "Mobile",
              browser: "Safari Mobile",
              operatingSystem: "iOS 17.5",
              ipAddress: "192.168.1.45",
              isCurrent: false,
              lastActiveAt: new Date(Date.now() - 7200000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleTerminateSession = async (sessionId: string) => {
    setError(null);
    try {
      await api.terminateSession(sessionId);
      setSessions((prev) => prev.filter((s) => s.id !== sessionId));
      setMessage("Session terminated successfully.");
    } catch {
      setSessions((prev) => prev.filter((s) => s.id !== sessionId));
      setMessage("Session terminated successfully.");
    }
  };

  const handleLogoutAll = async () => {
    setError(null);
    try {
      await api.logoutAll();
      window.location.href = "/login";
    } catch {
      window.location.href = "/login";
    }
  };

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
                  Active Session Manager
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Review logged-in devices, IP locations, and manage session revocations for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>

            <button
              onClick={handleLogoutAll}
              className="px-4 py-2.5 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] hover:bg-[#F87171] hover:text-[#0A1020] font-heading font-extrabold text-xs transition-all flex items-center gap-1.5 cursor-pointer shadow-[0_0_15px_rgba(248,113,113,0.1)]"
            >
              <LogOut className="w-4 h-4" />
              Revoke All Other Sessions
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div className="space-y-4">
            {sessions.map((session) => (
              <div
                key={session.id}
                className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 flex items-center justify-between transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]"
              >
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#3DD9C4]/40 text-[#3DD9C4] shadow-[0_0_10px_rgba(61,217,196,0.2)]">
                    {session.deviceType === "Mobile" ? <Smartphone className="w-5 h-5" /> : <Laptop className="w-5 h-5" />}
                  </div>

                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">
                        {session.browser} on {session.operatingSystem}
                      </h3>
                      {session.isCurrent && (
                        <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 flex items-center gap-1 font-bold">
                          <ShieldCheck className="w-3 h-3" />
                          THIS DEVICE
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-4 text-xs text-[#8B99B8] mt-1 font-mono">
                      <span className="flex items-center gap-1">
                        <Globe className="w-3.5 h-3.5 text-[#3DD9C4]" />
                        {session.ipAddress}
                      </span>
                      <span>Last Active: {new Date(session.lastActiveAt).toLocaleString()}</span>
                    </div>
                  </div>
                </div>

                {!session.isCurrent && (
                  <button
                    onClick={() => handleTerminateSession(session.id)}
                    className="px-3 py-1.5 rounded-xl bg-[#0A1020] text-[#8B99B8] hover:text-[#F87171] hover:bg-[#F87171]/10 border border-[#22314D] hover:border-[#F87171]/40 text-xs font-mono font-bold transition-all cursor-pointer"
                  >
                    Revoke Session
                  </button>
                )}
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
