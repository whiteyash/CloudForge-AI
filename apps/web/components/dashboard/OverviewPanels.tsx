"use client";

import React from "react";
import { Boxes, GitPullRequest, Activity, ShieldCheck, Bot } from "lucide-react";

export default function OverviewPanels() {
  const kpis = [
    {
      title: "Kubernetes Cluster",
      subtitle: "kind-local-cluster (default)",
      value: "10 / 10 Pods Running",
      status: "HEALTHY",
      statusColor: "text-[#34D399] bg-[#34D399]/10 border-[#34D399]/30",
      icon: Boxes,
      metricLabel: "Node CPU / Memory",
      metricValue: "18.4% CPU • 4.2 GB",
    },
    {
      title: "CI/CD Deployments",
      subtitle: "GitHub Actions Webhooks",
      value: "12 Runs (10 Success, 2 Failed)",
      status: "2 FAILED",
      statusColor: "text-[#FBBF24] bg-[#FBBF24]/10 border-[#FBBF24]/30",
      icon: GitPullRequest,
      metricLabel: "AI Failure Analysis",
      metricValue: "2 summaries available",
    },
    {
      title: "Prometheus Telemetry",
      subtitle: "HTTP PromQL Endpoint",
      value: "4 Real Metrics Active",
      status: "STREAMING",
      statusColor: "text-[#3DD9C4] bg-[#3DD9C4]/10 border-[#3DD9C4]/30",
      icon: Activity,
      metricLabel: "Avg Latency (p95)",
      metricValue: "18.5 ms (Normal)",
    },
    {
      title: "Security Posture",
      subtitle: "Trivy Vulnerability Engine",
      value: "15 CVEs Detected",
      status: "0 CRITICAL",
      statusColor: "text-[#34D399] bg-[#34D399]/10 border-[#34D399]/30",
      icon: ShieldCheck,
      metricLabel: "Triage Notes",
      metricValue: "3 High • 12 Medium",
    },
  ];

  return (
    <div className="space-y-6">
      {/* Top Banner / Welcome Callout */}
      <div className="p-6 rounded-2xl bg-gradient-to-r from-[#111B2E] via-[#16233A] to-[#111B2E] border border-[#22314D] relative overflow-hidden shadow-lg">
        <div className="absolute -right-10 -bottom-10 w-60 h-60 bg-[#3DD9C4]/5 rounded-full blur-3xl pointer-events-none" />
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 relative z-10">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <span className="w-2 h-2 rounded-full bg-[#3DD9C4] animate-ping" />
              <span className="text-xs font-mono font-medium text-[#3DD9C4] uppercase tracking-wider">
                Mission Control • Phase 1 Core Active
              </span>
            </div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7] tracking-tight">
              CloudForge Platform Infrastructure
            </h1>
            <p className="text-sm text-[#8B99B8] mt-1 max-w-2xl">
              Unified control plane connecting Kubernetes API, Prometheus metrics, GitHub CI/CD webhooks, Trivy security scanner, and Spring Boot 3 virtual-thread backend.
            </p>
          </div>

          <div className="flex items-center gap-3 shrink-0">
            <button className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5">
              <Bot className="w-4 h-4 stroke-[2.5]" />
              Ask Copilot
            </button>
          </div>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpis.map((kpi, index) => {
          const Icon = kpi.icon;
          return (
            <div
              key={index}
              className="p-5 rounded-xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/30 transition-all duration-200 group relative overflow-hidden"
            >
              <div className="flex items-start justify-between mb-3">
                <div className="p-2.5 rounded-lg bg-[#16233A] text-[#3DD9C4] group-hover:bg-[#3DD9C4]/10 transition-colors">
                  <Icon className="w-5 h-5" />
                </div>
                <span
                  className={`text-[10px] font-mono font-semibold px-2 py-0.5 rounded-full border ${kpi.statusColor}`}
                >
                  {kpi.status}
                </span>
              </div>

              <div className="text-xs text-[#8B99B8] font-mono">{kpi.subtitle}</div>
              <h3 className="text-base font-heading font-bold text-[#E7EDF7] mt-0.5 group-hover:text-[#3DD9C4] transition-colors">
                {kpi.title}
              </h3>
              <div className="text-sm font-semibold text-[#E7EDF7] mt-2">{kpi.value}</div>

              <div className="mt-4 pt-3 border-t border-[#22314D]/60 flex items-center justify-between text-xs text-[#8B99B8]">
                <span>{kpi.metricLabel}</span>
                <span className="font-mono text-[#E7EDF7] font-medium">{kpi.metricValue}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
