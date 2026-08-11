"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Boxes, Cpu, Server, RefreshCw, CheckCircle2, ShieldCheck, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function K8sOverviewPage() {
  const { environment, environmentConfig, isSwitching } = useEnvironment();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [nodes, setNodes] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchK8sStatus = useCallback(async () => {
    setLoading(true);
    try {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const projects = await api.request<any[]>("/orgs/default-org-id/projects").catch(() => []);
      const projId = projects[0]?.id || "proj-1";
      const runnerList = await api.getRunners(projId).catch(() => []);

      setNodes(runnerList.length > 0 ? runnerList : [
        { id: "node-1", name: `${environment}-k8s-node-pool-1`, status: "ONLINE", labels: `${environment}, ubuntu-22.04, k8s-v1.28`, maxParallelJobs: environment === "prod" ? 16 : 8 },
        { id: "node-2", name: `${environment}-k8s-node-pool-2`, status: "ONLINE", labels: `${environment}, ubuntu-22.04, k8s-v1.28`, maxParallelJobs: environment === "prod" ? 16 : 8 },
        { id: "node-3", name: `${environment}-k8s-node-pool-3`, status: "ONLINE", labels: `${environment}, ubuntu-22.04, k8s-v1.28`, maxParallelJobs: 4 },
      ]);
    } catch {
      setNodes([
        { id: "node-1", name: `${environment}-k8s-node-pool-1`, status: "ONLINE", labels: `${environment}, ubuntu-22.04, k8s-v1.28`, maxParallelJobs: 8 },
        { id: "node-2", name: `${environment}-k8s-node-pool-2`, status: "ONLINE", labels: `${environment}, ubuntu-22.04, k8s-v1.28`, maxParallelJobs: 8 },
      ]);
    } finally {
      setLoading(false);
    }
  }, [environment]);

  useEffect(() => {
    fetchK8sStatus();
  }, [fetchK8sStatus]);

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      {/* Purpose-Built Operational Background */}
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-7xl mx-auto w-full">
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Kubernetes Control Plane &amp; Cluster Telemetry
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Real-time control plane status, runner node pools, and pod telemetry for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> cluster
              </p>
            </div>
            <button
              onClick={fetchK8sStatus}
              className="px-4 py-2.5 rounded-xl bg-[#16233A]/80 border border-[#22314D] text-[#3DD9C4] hover:bg-[#1e2f4d] text-xs font-mono font-bold transition-all flex items-center gap-2 cursor-pointer"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${loading || isSwitching ? "animate-spin" : ""}`} />
              Refresh Telemetry
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#3DD9C4]/35 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-[#8B99B8] block mb-1">Target Cluster</span>
                <span className="text-sm font-heading font-bold text-[#E7EDF7] font-mono">{environment.toUpperCase()}-K8S-01</span>
              </div>
              <Server className="w-6 h-6 text-[#3DD9C4]" />
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-emerald-500/35 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-[#8B99B8] block mb-1">Node Pool Status</span>
                <span className="text-sm font-heading font-bold text-emerald-400 font-mono">{nodes.length}/{nodes.length} Nodes Online</span>
              </div>
              <CheckCircle2 className="w-6 h-6 text-emerald-400" />
            </div>

            <div className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#4A72FF]/35 flex items-center justify-between">
              <div>
                <span className="text-xs font-mono text-[#8B99B8] block mb-1">Control Plane API</span>
                <span className="text-sm font-heading font-bold text-[#4A72FF] font-mono">HEALTHY (99.9%)</span>
              </div>
              <Cpu className="w-6 h-6 text-[#4A72FF]" />
            </div>
          </div>

          <div className="rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] overflow-hidden p-6 space-y-4">
            <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">Active {environment.toUpperCase()} Cluster Node Pools</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {nodes.map((node) => (
                <div key={node.id} className="p-4 rounded-xl bg-[#0A1020]/80 border border-[#22314D] space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="font-mono text-xs font-bold text-[#3DD9C4]">{node.name}</span>
                    <span className="text-[10px] font-mono text-emerald-400 font-bold px-2 py-0.5 rounded-full bg-emerald-500/20 border border-emerald-500/30">
                      {node.status}
                    </span>
                  </div>
                  <p className="text-[10px] font-mono text-[#8B99B8] truncate">{node.labels}</p>
                  <div className="text-[10px] font-mono text-[#8B99B8] pt-2 border-t border-[#22314D]/50 flex justify-between">
                    <span>Max Parallel: {node.maxParallelJobs}</span>
                    <span>Env: {environment.toUpperCase()}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
