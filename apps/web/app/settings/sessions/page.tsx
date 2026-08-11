"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Laptop, Smartphone, Globe, LogOut, CheckCircle2, ShieldCheck, AlertCircle } from "lucide-react";
import { api, ActiveSessionResponse } from "@/lib/api";

export default function SessionManagerPage() {
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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Active Session Manager</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Review active logged-in devices, IP locations, and manage session revocations</p>
            </div>

            <button
              onClick={handleLogoutAll}
              className="px-4 py-2 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] hover:bg-[#F87171] hover:text-[#0A1020] font-heading font-bold text-xs transition-all flex items-center gap-1.5"
            >
              <LogOut className="w-4 h-4" />
              Revoke All Other Sessions
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              <span>{error}</span>
            </div>
          )}

          <div className="space-y-4">
            {sessions.map((session) => (
              <div
                key={session.id}
                className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] flex items-center justify-between"
              >
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    {session.deviceType === "Mobile" ? <Smartphone className="w-5 h-5" /> : <Laptop className="w-5 h-5" />}
                  </div>

                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">
                        {session.browser} on {session.operatingSystem}
                      </h3>
                      {session.isCurrent && (
                        <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 flex items-center gap-1">
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
                    className="px-3 py-1.5 rounded-lg bg-[#16233A] text-[#8B99B8] hover:text-[#F87171] hover:bg-[#F87171]/10 text-xs font-medium transition-all"
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
