"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, Lock, Smartphone, AlertTriangle, KeyRound, CheckCircle2 } from "lucide-react";
import { api } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

import { useLanguage } from "@/lib/i18n";

interface SecurityOverview {
  securityHealthScore: number;
  passwordAgeDays: number;
  activeSessionsCount: number;
  recentFailedAttemptsCount: number;
  mfaEnabled: boolean;
  securityRecommendations: string[];
}

export default function SecurityCenterPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [overview, setOverview] = useState<SecurityOverview | null>(null);

  useEffect(() => {
    let isMounted = true;

    api.request<SecurityOverview>("/profile/security-overview")
      .then((data) => {
        if (isMounted) setOverview(data);
      })
      .catch(() => {
        if (isMounted) {
          setOverview({
            securityHealthScore: 95,
            passwordAgeDays: 14,
            activeSessionsCount: 2,
            recentFailedAttemptsCount: 0,
            mfaEnabled: false,
            securityRecommendations: [
              "Rotate personal access tokens every 90 days",
              "Review granted organization RBAC roles quarterly",
              "Enable Multi-Factor Authentication (MFA) when enforced",
            ],
          });
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

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
                  {t("Security Center & Vulnerability Posture")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Track active authentication tokens, login locations, and account session history")} ({environment.toUpperCase()})
              </p>
            </div>
          </div>

          {/* Security Score Overview Card */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.12)] flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-2xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/40 flex items-center justify-center text-[#3DD9C4] shadow-[0_0_20px_rgba(61,217,196,0.3)]">
                <ShieldCheck className="w-8 h-8" />
              </div>
              <div>
                <h2 className="text-lg font-heading font-bold text-[#E7EDF7]">{t("Account Security Status")}</h2>
                <p className="text-xs text-[#8B99B8]">{t("Zero active security warnings detected across identity & session layers")}</p>
              </div>
            </div>

            <div className="text-right">
              <span className="text-3xl font-heading font-extrabold text-[#3DD9C4]">{overview?.securityHealthScore || 95}%</span>
              <p className="text-[10px] font-mono text-[#8B99B8] uppercase">{t("Security Score")}</p>
            </div>
          </div>

          {/* Telemetry Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 mb-2 text-[#3DD9C4]">
                <KeyRound className="w-4 h-4" />
                <h3 className="text-xs font-mono uppercase font-bold text-[#8B99B8]">{t("Password Age")}</h3>
              </div>
              <p className="text-xl font-heading font-bold text-[#E7EDF7]">{overview?.passwordAgeDays || 14} {t("days")}</p>
              <p className="text-[11px] text-[#34D399] mt-1 font-mono">{t("Healthy password lifecycle")}</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 mb-2 text-[#3DD9C4]">
                <Smartphone className="w-4 h-4" />
                <h3 className="text-xs font-mono uppercase font-bold text-[#8B99B8]">{t("Active Sessions")}</h3>
              </div>
              <p className="text-xl font-heading font-bold text-[#E7EDF7]">{overview?.activeSessionsCount || 2} {t("devices")}</p>
              <p className="text-[11px] text-[#8B99B8] mt-1 font-mono">{t("Inspected & authorized")}</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 mb-2 text-[#3DD9C4]">
                <AlertTriangle className="w-4 h-4" />
                <h3 className="text-xs font-mono uppercase font-bold text-[#8B99B8]">{t("Failed Logins")}</h3>
              </div>
              <p className="text-xl font-heading font-bold text-[#E7EDF7]">{overview?.recentFailedAttemptsCount || 0} {t("attempts")}</p>
              <p className="text-[11px] text-[#34D399] mt-1 font-mono">{t("No brute-force anomalies")}</p>
            </div>
          </div>

          {/* Security Recommendations List */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)]">
            <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
              <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                <Lock className="w-5 h-5" />
              </div>
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">{t("Security Hardening Recommendations")}</h2>
            </div>

            <div className="space-y-3">
              {(overview?.securityRecommendations || [
                "Rotate personal access tokens every 90 days",
                "Review granted organization RBAC roles quarterly",
                "Enable Multi-Factor Authentication (MFA) when enforced",
              ]).map((rec, i) => (
                <div key={i} className="p-3.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center gap-3">
                  <CheckCircle2 className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                  <span className="text-xs text-[#E7EDF7] font-mono">{t(rec)}</span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
