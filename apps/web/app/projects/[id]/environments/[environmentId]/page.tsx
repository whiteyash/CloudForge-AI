"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ArrowLeft, Shield, Snowflake, Power, Plus, CheckCircle2 } from "lucide-react";
import Link from "next/link";

interface EnvVar {
  key: string;
  value: string;
  isSecret: boolean;
}

interface TargetBinding {
  name: string;
  type: string;
  endpoint: string;
}

export default function EnvironmentDetailsPage() {
  const [isFrozen, setIsFrozen] = useState(false);
  const [isMaintenance, setIsMaintenance] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const [variables] = useState<EnvVar[]>([
    { key: "DATABASE_URL", value: "jdbc:postgresql://staging-db.cloudforge.internal:5432/staging_db", isSecret: false },
    { key: "API_SECRET_KEY", value: "••••••••••••••••", isSecret: true },
  ]);

  const [targets] = useState<TargetBinding[]>([
    { name: "staging-k8s-namespace", type: "KUBERNETES_NAMESPACE", endpoint: "k8s://staging-us-east" },
    { name: "docker-staging-host", type: "DOCKER_HOST", endpoint: "tcp://docker-staging:2375" },
  ]);

  const handleToggleFreeze = () => {
    setIsFrozen(!isFrozen);
    setMessage(`Environment freeze window status set to ${!isFrozen}.`);
  };

  const handleToggleMaintenance = () => {
    setIsMaintenance(!isMaintenance);
    setMessage(`Maintenance mode set to ${!isMaintenance}.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/environments" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Staging-US-East</h1>
                  <span className="px-2.5 py-0.5 rounded text-xs font-mono font-semibold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                    STAGING
                  </span>
                  {isFrozen && (
                    <span className="px-2 py-0.5 rounded text-xs font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center gap-1">
                      <Snowflake className="w-3 h-3" /> FROZEN
                    </span>
                  )}
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Precedence: Pipeline $\rightarrow$ Project $\rightarrow$ Environment $\rightarrow$ Runtime</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={handleToggleFreeze}
                className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#3DD9C4]/10 text-xs font-semibold flex items-center gap-1.5 transition-all"
              >
                <Snowflake className="w-3.5 h-3.5" />
                {isFrozen ? "Unfreeze Window" : "Freeze Window"}
              </button>

              <button
                onClick={handleToggleMaintenance}
                className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#F59E0B] hover:bg-[#F59E0B]/10 text-xs font-semibold flex items-center gap-1.5 transition-all"
              >
                <Power className="w-3.5 h-3.5" />
                {isMaintenance ? "Exit Maintenance" : "Maintenance Mode"}
              </button>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2 space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Environment Variables & Secret References</h3>
                  <button className="px-3 py-1.5 rounded-lg bg-[#16233A] text-[#3DD9C4] text-xs font-semibold flex items-center gap-1 hover:bg-[#3DD9C4]/10">
                    <Plus className="w-3.5 h-3.5" /> Add Variable
                  </button>
                </div>

                <div className="space-y-2 font-mono text-xs">
                  {variables.map((v) => (
                    <div key={v.key} className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                      <span className="text-[#3DD9C4] font-bold">{v.key}</span>
                      <span className="text-[#8B99B8]">{v.value}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Deployment Target Bindings (1 Environment $\rightarrow$ N Targets)</h3>

                <div className="space-y-2 font-mono text-xs">
                  {targets.map((t) => (
                    <div key={t.name} className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                      <div>
                        <span className="text-[#E7EDF7] font-bold">{t.name}</span>
                        <p className="text-[10px] text-[#8B99B8]">{t.endpoint}</p>
                      </div>
                      <span className="px-2 py-0.5 rounded text-[10px] bg-[#16233A] text-[#8B99B8] border border-[#22314D]">
                        {t.type}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Shield className="w-4 h-4 text-[#34D399]" />
                  Environment Health
                </h3>
                <div className="text-xs font-mono text-[#8B99B8] space-y-2">
                  <p>Health Probes: HEALTHY</p>
                  <p>Deployment Policy: ROLLING</p>
                  <p>Max Parallel Deployments: 2</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
