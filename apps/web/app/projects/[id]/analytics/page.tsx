"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Activity, Cpu, Rocket, ShieldCheck, Award, Bell } from "lucide-react";
import Link from "next/link";

export default function AnalyticsDashboardPage() {
  const [alerts, setAlerts] = useState([
    { id: "alt-1", name: "High Runner Pool Capacity", severity: "WARNING", message: "Runner pool capacity reached 87.5% across Kubernetes nodes." },
    { id: "alt-2", name: "Production Deployment Gate", severity: "INFO", message: "Deployment to PRODUCTION-GLOBAL pending RBAC approval." },
  ]);

  const handleAcknowledge = (id: string) => {
    setAlerts((prev) => prev.filter((a) => a.id !== id));
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">CI/CD Observability & Analytics</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Real-time operational metrics, runner utilization, deployment throughput, and system health</p>
            </div>

            <Link
              href="/projects/proj-1/analytics/dora"
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Award className="w-4 h-4" />
              View DORA Metrics
            </Link>
          </div>

          {/* Metric Cards Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#34D399]">
                <Activity className="w-5 h-5" />
                <span className="text-[10px] font-mono font-semibold bg-[#34D399]/10 border border-[#34D399]/30 px-2 py-0.5 rounded">94.8%</span>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Pipeline Success Rate</span>
              <h3 className="text-xl font-heading font-bold text-[#E7EDF7]">94.8%</h3>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#3DD9C4]">
                <Cpu className="w-5 h-5" />
                <span className="text-[10px] font-mono font-semibold bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 px-2 py-0.5 rounded">87.5%</span>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Runner Utilization</span>
              <h3 className="text-xl font-heading font-bold text-[#E7EDF7]">87.5%</h3>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#F59E0B]">
                <Rocket className="w-5 h-5" />
                <span className="text-[10px] font-mono font-semibold bg-[#F59E0B]/10 border border-[#F59E0B]/30 px-2 py-0.5 rounded">12.4/day</span>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Deployment Frequency</span>
              <h3 className="text-xl font-heading font-bold text-[#E7EDF7]">12.4 / day</h3>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <div className="flex items-center justify-between text-[#34D399]">
                <ShieldCheck className="w-5 h-5" />
                <span className="text-[10px] font-mono font-semibold bg-[#34D399]/10 border border-[#34D399]/30 px-2 py-0.5 rounded">HEALTHY</span>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Platform Health</span>
              <h3 className="text-xl font-heading font-bold text-[#E7EDF7]">HEALTHY</h3>
            </div>
          </div>

          {/* System Alerts Center */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
              <Bell className="w-4 h-4 text-[#F59E0B]" />
              Active System Alert Center
            </h3>

            <div className="space-y-3">
              {alerts.map((alt) => (
                <div key={alt.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{alt.name}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30">
                        {alt.severity}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1">{alt.message}</p>
                  </div>

                  <button
                    onClick={() => handleAcknowledge(alt.id)}
                    className="px-3 py-1.5 rounded-lg bg-[#16233A] text-[#3DD9C4] text-xs font-semibold hover:bg-[#3DD9C4]/10 transition-all"
                  >
                    Acknowledge
                  </button>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
