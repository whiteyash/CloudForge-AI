"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Award, ArrowLeft, Zap, Clock, AlertTriangle, RotateCcw } from "lucide-react";
import Link from "next/link";

export default function DoraMetricsPage() {
  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/analytics" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">DORA Metrics Performance</h1>
                  <span className="px-2.5 py-0.5 rounded text-xs font-mono font-semibold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30 flex items-center gap-1">
                    <Award className="w-3.5 h-3.5" /> ELITE TIER
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">DevOps Research & Assessment (DORA) Key Performance Indicators</p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <div className="flex items-center justify-between text-[#3DD9C4]">
                <Zap className="w-5 h-5" />
                <span className="text-xs font-mono font-bold bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 px-2 py-0.5 rounded">ELITE</span>
              </div>
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Deployment Frequency</h3>
              <p className="text-2xl font-heading font-bold text-[#3DD9C4]">12.4 / day</p>
              <p className="text-xs text-[#8B99B8]">On-demand deployment frequency to production targets.</p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <div className="flex items-center justify-between text-[#34D399]">
                <Clock className="w-5 h-5" />
                <span className="text-xs font-mono font-bold bg-[#34D399]/10 border border-[#34D399]/30 px-2 py-0.5 rounded">HIGH</span>
              </div>
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Lead Time for Changes</h3>
              <p className="text-2xl font-heading font-bold text-[#34D399]">1.5 hours</p>
              <p className="text-xs text-[#8B99B8]">Average hours from commit merge to successful release deployment.</p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <div className="flex items-center justify-between text-[#F59E0B]">
                <AlertTriangle className="w-5 h-5" />
                <span className="text-xs font-mono font-bold bg-[#F59E0B]/10 border border-[#F59E0B]/30 px-2 py-0.5 rounded">ELITE</span>
              </div>
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Change Failure Rate</h3>
              <p className="text-2xl font-heading font-bold text-[#F59E0B]">4.2%</p>
              <p className="text-xs text-[#8B99B8]">Percentage of releases requiring rollback or emergency fix.</p>
            </div>

            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <div className="flex items-center justify-between text-[#3DD9C4]">
                <RotateCcw className="w-5 h-5" />
                <span className="text-xs font-mono font-bold bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 px-2 py-0.5 rounded">ELITE</span>
              </div>
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Mean Time to Restore (MTTR)</h3>
              <p className="text-2xl font-heading font-bold text-[#3DD9C4]">18.0 mins</p>
              <p className="text-xs text-[#8B99B8]">Average minutes required to restore service after a failed deployment.</p>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
