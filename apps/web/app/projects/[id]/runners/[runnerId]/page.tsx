"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Cpu, ArrowLeft, Heart, Shield, Activity, Power } from "lucide-react";
import Link from "next/link";

export default function RunnerDetailsPage() {
  const [status, setStatus] = useState("ONLINE");
  const [message, setMessage] = useState<string | null>(null);

  const handleToggleDrain = () => {
    const nextStatus = status === "DRAINING" ? "ONLINE" : "DRAINING";
    setStatus(nextStatus);
    setMessage(`Runner status changed to ${nextStatus}.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/runners" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">k8s-runner-pool-1</h1>
                  <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                    status === "ONLINE" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" : "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30"
                  }`}>
                    {status}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Type: KUBERNETES | Group: default | Max Parallel Jobs: 4</p>
              </div>
            </div>

            <button
              onClick={handleToggleDrain}
              className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] font-heading font-semibold text-xs transition-all flex items-center gap-1.5"
            >
              <Power className="w-4 h-4 text-[#F59E0B]" />
              {status === "DRAINING" ? "Undrain Runner" : "Drain Runner"}
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <Heart className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2 space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Cpu className="w-4 h-4 text-[#3DD9C4]" />
                  Agent Operating Metadata
                </h3>

                <div className="grid grid-cols-2 gap-4 text-xs font-mono text-[#8B99B8]">
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D]">
                    <span className="text-[#E7EDF7] font-bold">Operating System</span>
                    <p className="mt-1 text-sm text-[#3DD9C4]">Linux x86_64</p>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D]">
                    <span className="text-[#E7EDF7] font-bold">Labels / Tags</span>
                    <p className="mt-1 text-sm text-[#3DD9C4]">ubuntu-latest, docker, k8s</p>
                  </div>
                </div>
              </div>

              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Activity className="w-4 h-4 text-[#F59E0B]" />
                  Currently Assigned Jobs (1 / 4)
                </h3>

                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">Job: test-suite-execution</span>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Pipeline: main-build-ci #42 | Stage: Test</p>
                  </div>
                  <span className="px-2 py-0.5 rounded text-xs font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                    RUNNING
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Shield className="w-4 h-4 text-[#34D399]" />
                  Heartbeat & Health
                </h3>
                <div className="text-xs font-mono text-[#8B99B8] space-y-2">
                  <p>Heartbeat Interval: 10s</p>
                  <p>Last Ping: 2 seconds ago</p>
                  <p>Token Status: HASHED (SHA-256)</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
