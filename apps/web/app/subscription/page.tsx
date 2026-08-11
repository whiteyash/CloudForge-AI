"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Zap, Check, ShieldCheck, Database, Cpu } from "lucide-react";

export default function SubscriptionPage() {
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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organization Subscription & Quotas</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Review current plan limits, seat allocations, and storage usage</p>
          </div>

          {/* Current Quota Status */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Cpu className="w-4 h-4 text-[#3DD9C4]" />
                <span>SEAT USAGE</span>
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">12 / 25</p>
              <p className="text-xs text-[#8B99B8] mt-1">13 seats remaining in PRO plan</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Database className="w-4 h-4 text-[#3DD9C4]" />
                <span>STORAGE UTILIZATION</span>
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">24.8 GB / 100 GB</p>
              <p className="text-xs text-[#8B99B8] mt-1">Artifact & MinIO bucket usage</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center gap-2 text-[#8B99B8] text-xs font-mono mb-2">
                <Zap className="w-4 h-4 text-[#3DD9C4]" />
                <span>API QUOTA (LAST 24H)</span>
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">142.8k Requests</p>
              <p className="text-xs text-[#8B99B8] mt-1">Healthy rate limit headroom</p>
            </div>
          </div>

          {/* Plan Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-4">
            {plans.map((plan) => (
              <div
                key={plan.name}
                className={`p-6 rounded-2xl bg-[#111B2E] border flex flex-col justify-between ${
                  plan.isCurrent ? 'border-[#3DD9C4] shadow-[0_0_20px_rgba(61,217,196,0.15)]' : 'border-[#22314D]'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-heading font-bold text-[#E7EDF7]">{plan.name}</h3>
                    {plan.isCurrent && (
                      <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 flex items-center gap-1">
                        <ShieldCheck className="w-3 h-3" />
                        CURRENT
                      </span>
                    )}
                  </div>
                  <p className="text-3xl font-heading font-bold text-[#E7EDF7] mb-6">{plan.price}</p>

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
                    className={`w-full py-2.5 rounded-xl font-heading font-bold text-xs transition-all ${
                      plan.isCurrent
                        ? 'bg-[#16233A] text-[#8B99B8] cursor-default border border-[#22314D]'
                        : 'bg-[#3DD9C4] text-[#0A1020] hover:bg-[#34D399]'
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
