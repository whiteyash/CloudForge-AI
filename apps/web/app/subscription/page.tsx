"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Zap, Check, ShieldCheck, Database, Cpu } from "lucide-react";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function SubscriptionPage() {
  const { environment, environmentConfig } = useEnvironment();
  const plans = [
    {
      name: "FREE",
      price: "$0",
      seats: "5 Members",
      storage: "10 GB Storage",
      apiLimit: "1,000 API Requests/min",
      isCurrent: false,
    },
    {
      name: "PRO",
      price: "$49 / mo",
      seats: "25 Members",
      storage: "100 GB Storage",
      apiLimit: "10,000 API Requests/min",
      isCurrent: true,
    },
    {
      name: "ENTERPRISE",
      price: "Custom",
      seats: "Unlimited Members",
      storage: "1 TB Storage",
      apiLimit: "Dedicated Rate Limits",
      isCurrent: false,
    },
  ];

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
                  Organization Subscription & Quotas
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Review plan limits, seat allocations, and storage usage for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {/* Current Quota Status */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Cpu className="w-4 h-4 text-[#3DD9C4]" />
                <span>SEAT USAGE</span>
              </div>
              <p className="text-2xl font-heading font-extrabold text-[#E7EDF7]">12 / 25</p>
              <p className="text-xs text-[#8B99B8] mt-1 font-mono">13 seats remaining in PRO plan</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Database className="w-4 h-4 text-[#3DD9C4]" />
                <span>STORAGE UTILIZATION</span>
              </div>
              <p className="text-2xl font-heading font-extrabold text-[#E7EDF7]">24.8 GB / 100 GB</p>
              <p className="text-xs text-[#8B99B8] mt-1 font-mono">Artifact & MinIO bucket usage</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-[0_0_20px_rgba(61,217,196,0.05)]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Zap className="w-4 h-4 text-[#3DD9C4]" />
                <span>API QUOTA (LAST 24H)</span>
              </div>
              <p className="text-2xl font-heading font-extrabold text-[#E7EDF7]">142.8k Requests</p>
              <p className="text-xs text-[#8B99B8] mt-1 font-mono">Healthy rate limit headroom</p>
            </div>
          </div>

          {/* Plan Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
            {plans.map((plan) => (
              <div
                key={plan.name}
                className={`p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border flex flex-col justify-between transition-all ${
                  plan.isCurrent
                    ? 'border-[#3DD9C4] shadow-[0_0_30px_rgba(61,217,196,0.2)]'
                    : 'border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_20px_rgba(61,217,196,0.05)]'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-heading font-bold text-[#E7EDF7]">{plan.name}</h3>
                    {plan.isCurrent && (
                      <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 flex items-center gap-1 font-bold">
                        <ShieldCheck className="w-3 h-3" />
                        CURRENT
                      </span>
                    )}
                  </div>
                  <p className="text-3xl font-heading font-extrabold text-[#E7EDF7] mb-6">{plan.price}</p>

                  <ul className="space-y-3 text-xs text-[#8B99B8]">
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-[#3DD9C4]" />
                      <span>{plan.seats}</span>
                    </li>
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-[#3DD9C4]" />
                      <span>{plan.storage}</span>
                    </li>
                    <li className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-[#3DD9C4]" />
                      <span>{plan.apiLimit}</span>
                    </li>
                  </ul>
                </div>

                <div className="mt-8">
                  <button
                    disabled={plan.isCurrent}
                    className={`w-full py-2.5 rounded-xl font-heading font-extrabold text-xs transition-all ${
                      plan.isCurrent
                        ? 'bg-[#0A1020] text-[#8B99B8] cursor-default border border-[#22314D]'
                        : 'bg-[#3DD9C4] text-[#0A1020] hover:bg-[#34D399] cursor-pointer shadow-[0_0_12px_rgba(61,217,196,0.3)]'
                    }`}
                  >
                    {plan.isCurrent ? 'Active Plan' : 'Upgrade Plan'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
