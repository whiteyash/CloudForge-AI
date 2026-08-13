"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, Lock, Key, Smartphone } from "lucide-react";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function SecuritySettingsPage() {
  const { environment, environmentConfig } = useEnvironment();

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
                  Security & Policy Configuration
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Active security policies, brute-force lock rules, and multi-factor authentication setup for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {/* Security Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#34D399]/15 border border-[#34D399]/30 text-[#34D399]">
                  <ShieldCheck className="w-5 h-5" />
                </div>
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Brute-Force Protection</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Accounts automatically enter a <span className="font-mono text-[#3DD9C4]">15-minute lock</span> state after 5 consecutive failed login attempts from any IP.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs font-mono">
                STATUS: ACTIVE & ENFORCED
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                  <Lock className="w-5 h-5" />
                </div>
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Password History Policy</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Prevents reuse of the last <span className="font-mono text-[#3DD9C4]">3 hashed passwords</span> using BCrypt cost factor 12.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 text-[#3DD9C4] text-xs font-mono">
                POLICY: 3 RECENT HASHES BLOCKED
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#FBBF24]/15 border border-[#FBBF24]/30 text-[#FBBF24]">
                  <Key className="w-5 h-5" />
                </div>
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">JWT Refresh Rotation</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Refresh tokens are single-use with cryptographic hash rotation on every refresh request.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#FBBF24]/10 border border-[#FBBF24]/30 text-[#FBBF24] text-xs font-mono">
                ROTATION: STRICT ONE-TIME USE
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                  <Smartphone className="w-5 h-5" />
                </div>
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Multi-Factor Authentication (2FA)</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                TOTP Authenticator application support (Google Authenticator, 1Password, Authy).
              </p>
              <div className="mt-4 flex items-center justify-between">
                <span className="text-xs text-[#8B99B8]">Status: Ready for Activation</span>
                <button className="px-3 py-1.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all cursor-pointer shadow-[0_0_12px_rgba(61,217,196,0.3)]">
                  Enable 2FA
                </button>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
