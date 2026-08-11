"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Shield, ShieldAlert, CheckCircle2, AlertTriangle, Lock, Key, FileCheck, Sliders } from "lucide-react";

export default function RepositoryGovernancePage() {
  const [policy, setPolicy] = useState({
    branchProtectionEnabled: true,
    requiredReviewsCount: 2,
    signedCommitsRequired: true,
    secretScanningEnabled: true,
    dependabotEnabled: true,
    codeScanningEnabled: true,
    complianceScore: 100,
    riskScore: 0,
    violationCount: 0,
  });

  const [message, setMessage] = useState<string | null>(null);

  const togglePolicy = (key: keyof typeof policy) => {
    setPolicy((prev) => {
      const updated = { ...prev, [key]: !prev[key] };
      let violations = 0;
      let compliance = 100;
      let risk = 0;

      if (!updated.branchProtectionEnabled) { violations++; compliance -= 30; risk += 35; }
      if (updated.requiredReviewsCount < 1) { violations++; compliance -= 20; risk += 20; }
      if (!updated.signedCommitsRequired) { violations++; compliance -= 15; risk += 15; }
      if (!updated.secretScanningEnabled) { violations++; compliance -= 20; risk += 20; }
      if (!updated.dependabotEnabled) { violations++; compliance -= 15; risk += 10; }

      return {
        ...updated,
        violationCount: violations,
        complianceScore: Math.max(compliance, 0),
        riskScore: Math.min(risk, 100),
      };
    });
    setMessage("Governance policy updated & audited across organizational repositories.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Repository Governance & Compliance</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Enterprise compliance monitoring, branch protection rules, secret scanning, and security posture</p>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Metric Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono uppercase">Compliance Score</span>
                <Shield className="w-5 h-5 text-[#34D399]" />
              </div>
              <p className="text-3xl font-heading font-bold text-[#34D399]">{policy.complianceScore}%</p>
              <p className="text-xs text-[#8B99B8]">SOC 2 & ISO 27001 standard baseline</p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono uppercase">Risk Level</span>
                <ShieldAlert className="w-5 h-5 text-[#3DD9C4]" />
              </div>
              <p className={`text-3xl font-heading font-bold ${policy.riskScore > 30 ? "text-[#F87171]" : "text-[#3DD9C4]"}`}>
                {policy.riskScore}%
              </p>
              <p className="text-xs text-[#8B99B8]">Repository risk exposure score</p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono uppercase">Active Policy Violations</span>
                <AlertTriangle className="w-5 h-5 text-[#F59E0B]" />
              </div>
              <p className="text-3xl font-heading font-bold text-[#E7EDF7]">{policy.violationCount}</p>
              <p className="text-xs text-[#8B99B8]">Policy compliance violations</p>
            </div>
          </div>

          {/* Policy Enforcer Toggles */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
            <div className="flex items-center gap-2">
              <Sliders className="w-5 h-5 text-[#3DD9C4]" />
              <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Governance & Protection Controls</h3>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Lock className="w-4 h-4 text-[#3DD9C4]" />
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">Default Branch Protection</span>
                    <p className="text-[10px] text-[#8B99B8]">Prevent force pushes and deletion on main branch</p>
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={policy.branchProtectionEnabled}
                  onChange={() => togglePolicy("branchProtectionEnabled")}
                  className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                />
              </div>

              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Key className="w-4 h-4 text-[#3DD9C4]" />
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">Secret Scanning</span>
                    <p className="text-[10px] text-[#8B99B8]">Scan inbound commits for hardcoded API secrets</p>
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={policy.secretScanningEnabled}
                  onChange={() => togglePolicy("secretScanningEnabled")}
                  className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                />
              </div>

              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <FileCheck className="w-4 h-4 text-[#3DD9C4]" />
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">Signed Commit Enforcement</span>
                    <p className="text-[10px] text-[#8B99B8]">Require GPG / SSH signature verification on commits</p>
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={policy.signedCommitsRequired}
                  onChange={() => togglePolicy("signedCommitsRequired")}
                  className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                />
              </div>

              <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Shield className="w-4 h-4 text-[#3DD9C4]" />
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">Dependabot Vulnerability Alerts</span>
                    <p className="text-[10px] text-[#8B99B8]">Audit dependencies against CVE advisory databases</p>
                  </div>
                </div>
                <input
                  type="checkbox"
                  checked={policy.dependabotEnabled}
                  onChange={() => togglePolicy("dependabotEnabled")}
                  className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                />
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
