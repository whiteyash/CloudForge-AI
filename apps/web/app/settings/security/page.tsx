"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, Lock, Key, Smartphone } from "lucide-react";

export default function SecuritySettingsPage() {
  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Security & Policy Configuration</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Review active security policies, brute-force lock rules, and multi-factor authentication setup</p>
          </div>

          {/* Security Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <ShieldCheck className="w-5 h-5 text-[#34D399]" />
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Brute-Force Protection</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Accounts automatically enter a <span className="font-mono text-[#3DD9C4]">15-minute lock</span> state after 5 consecutive failed login attempts from any IP.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs font-mono">
                STATUS: ACTIVE & ENFORCED
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <Lock className="w-5 h-5 text-[#3DD9C4]" />
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Password History Policy</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Prevents reuse of the last <span className="font-mono text-[#3DD9C4]">3 hashed passwords</span> using BCrypt cost factor 12.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 text-[#3DD9C4] text-xs font-mono">
                POLICY: 3 RECENT HASHES BLOCKED
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <Key className="w-5 h-5 text-[#FBBF24]" />
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">JWT Refresh Rotation</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Refresh tokens are single-use with cryptographic hash rotation on every refresh request.
              </p>
              <div className="mt-4 p-3 rounded-xl bg-[#FBBF24]/10 border border-[#FBBF24]/30 text-[#FBBF24] text-xs font-mono">
                ROTATION: STRICT ONE-TIME USE
              </div>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <Smartphone className="w-5 h-5 text-[#3DD9C4]" />
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Multi-Factor Authentication (2FA)</h3>
              </div>
              <p className="text-xs text-[#8B99B8]">
                TOTP Authenticator application support (Google Authenticator, 1Password, Authy).
              </p>
              <div className="mt-4 flex items-center justify-between">
                <span className="text-xs text-[#8B99B8]">Status: Ready for Activation</span>
                <button className="px-3 py-1.5 rounded-lg bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399]">
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
