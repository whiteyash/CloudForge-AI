"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { TrendingUp, BrainCircuit, Sparkles, CheckCircle2, ShieldCheck, Database } from "lucide-react";
import Link from "next/link";

interface CapacityForecastItem {
  metric: string;
  current: string;
  projected: string;
  days: number;
}

export default function PredictionsPage() {
  const [capacity] = useState<CapacityForecastItem[]>([
    { metric: "Artifact Repository Storage", current: "450.5 GB", projected: "890.0 GB", days: 42 },
    { metric: "Runner Memory Pressure", current: "78.5%", projected: "96.2%", days: 14 },
    { metric: "Log Ingestion Volume", current: "12.4 GB/day", projected: "34.0 GB/day", days: 60 },
  ]);

  const [analyzed, setAnalyzed] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleRunPredictive = () => {
    setAnalyzed(true);
    setMessage("AI Predictive Operations Forecast generated. 94% deployment success probability across 7-day window.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/ai" className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4] border border-[#22314D]">
                <TrendingUp className="w-5 h-5" />
              </Link>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Predictive Operations Platform</h1>
                <p className="text-xs text-[#8B99B8] mt-0.5">AI-powered operational forecasting, capacity planning, & failure prediction</p>
              </div>
            </div>

            <button
              onClick={handleRunPredictive}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Sparkles className="w-4 h-4" />
              Generate Forecast Report
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Operational Health Metrics Cards */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <span className="text-xs text-[#8B99B8] font-heading">Overall Health Score</span>
              <div className="text-2xl font-heading font-bold text-[#34D399]">96 / 100</div>
              <p className="text-[10px] text-[#8B99B8]">Optimal platform stability</p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <span className="text-xs text-[#8B99B8] font-heading">Deployment Success Probability</span>
              <div className="text-2xl font-heading font-bold text-[#3DD9C4]">94.2%</div>
              <p className="text-[10px] text-[#34D399]">Low rollback risk</p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <span className="text-xs text-[#8B99B8] font-heading">Pipeline Forecast Rate</span>
              <div className="text-2xl font-heading font-bold text-[#38BDF8]">91.8%</div>
              <p className="text-[10px] text-[#8B99B8]">Est. duration: 2m 00s</p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-2">
              <span className="text-xs text-[#8B99B8] font-heading">Incident Likelihood</span>
              <div className="text-2xl font-heading font-bold text-[#F59E0B]">15% (Low)</div>
              <p className="text-[10px] text-[#8B99B8]">7-day forecast window</p>
            </div>
          </div>

          {/* AI Predictive Summary Banner */}
          {analyzed && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#3DD9C4]/30 space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#3DD9C4] flex items-center gap-2">
                <BrainCircuit className="w-5 h-5 text-[#3DD9C4]" />
                AI Predictive Operations Report — 92% Confidence
              </h3>
              <p className="text-xs text-[#E7EDF7] leading-relaxed">
                Operational Forecast: High platform stability across next 7-day window. Runner memory usage is projected to reach 96% capacity in 14 days under current pipeline growth volume.
              </p>
            </div>
          )}

          {/* Capacity Forecasts & Risk Factors Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Capacity Forecasts */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Database className="w-4 h-4 text-[#38BDF8]" />
                Capacity Growth Forecasts
              </h3>

              <div className="space-y-3 font-mono text-xs">
                {capacity.map((item, idx) => (
                  <div key={idx} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{item.metric}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30">
                        {item.days} Days to Exhaustion
                      </span>
                    </div>
                    <div className="flex items-center justify-between text-[11px] text-[#8B99B8]">
                      <span>Current: {item.current}</span>
                      <span className="text-[#3DD9C4]">Projected: {item.projected}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Risk Factors & Recommendations */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <ShieldCheck className="w-4 h-4 text-[#34D399]" />
                Preventative Recommendations
              </h3>

              <div className="space-y-3">
                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                  <span className="font-heading text-xs font-bold text-[#34D399]">1. Provision Additional Runner Node</span>
                  <p className="text-xs text-[#8B99B8]">Prevents projected runner memory saturation at Day 14 under peak load.</p>
                </div>

                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] space-y-1">
                  <span className="font-heading text-xs font-bold text-[#38BDF8]">2. Enable Artifact Pruning Policy</span>
                  <p className="text-xs text-[#8B99B8]">Automate deletion of untagged build artifacts to extend storage window beyond 90 days.</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
