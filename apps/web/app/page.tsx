"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import {
  Building2,
  Users,
  Layers,
  Mail,
  Activity,
  ArrowRight,
  Plus,
  Zap,
  RefreshCw,
  Server,
  GitBranch,
  ShieldCheck,
  CheckCircle2,
} from "lucide-react";
import Link from "next/link";
import { api, OrgDashboardSummaryResponse, AuditLogResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import InviteMemberModal from "@/components/modals/InviteMemberModal";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage } from "@/lib/i18n";

export default function DashboardPage() {
  const { environment, isSwitching, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [summary, setSummary] = useState<OrgDashboardSummaryResponse | null>(null);
  const [activity, setActivity] = useState<AuditLogResponse[]>([]);
  const [orgId, setOrgId] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [inviteModalOpen, setInviteModalOpen] = useState(false);

  const loadData = useCallback(async (targetOrgId: string) => {
    setLoading(true);
    try {
      const sum = await api.getDashboardSummary(targetOrgId);
      setSummary(sum);
    } catch {
      setSummary(null);
    }

    try {
      const act = await api.getActivityTimeline(targetOrgId);
      setActivity(act);
    } catch {
      setActivity([]);
    } finally {
      setLoading(false);
    }
  }, [environment]);

  useEffect(() => {
    api.me()
      .then((auth) => {
        if (auth.organizations && auth.organizations.length > 0) {
          const activeOrg = auth.organizations[0].id;
          setOrgId(activeOrg);
          loadData(activeOrg);
        } else {
          loadData(orgId);
        }
      })
      .catch(() => {
        loadData(orgId);
      });
  }, [loadData, orgId]);

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      {/* Purpose-Built Operational Control Plane Background (Zero Login Text) */}
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-7xl mx-auto w-full">
          {/* Workspace Hero Header Panel */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)] relative overflow-hidden">
            <div className="absolute -right-12 -bottom-12 w-64 h-64 bg-[#3DD9C4]/10 rounded-full blur-3xl pointer-events-none" />
            <div className="relative z-10">
              <div className="flex items-center gap-2.5 mb-1.5 flex-wrap">
                <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-[#3DD9C4] to-[#16233A] flex items-center justify-center text-[#0A1020] shadow-[0_0_16px_rgba(61,217,196,0.4)]">
                  <Building2 className="w-4 h-4 text-[#0A1020] stroke-[2.5]" />
                </div>
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {summary?.name || "CloudForge AI Workspace"}
                </h1>
                <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 font-bold uppercase">
                  {summary?.status || "ACTIVE"}
                </span>
                <span
                  className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border transition-all ${
                    environmentConfig.badgeBg
                  } ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}
                >
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8] flex items-center gap-2">
                <span>{environmentConfig.description}</span>
                <span>•</span>
                <span>slug: <strong className="font-mono text-[#3DD9C4]">{summary?.slug || "org-workspace"}</strong></span>
              </p>
            </div>

            <div className="flex items-center gap-2.5 relative z-10">
              <button
                onClick={() => loadData(orgId)}
                className="p-2.5 rounded-xl bg-[#16233A]/80 border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] hover:border-[#3DD9C4]/40 transition-all cursor-pointer"
                title="Refresh Metrics"
              >
                <RefreshCw className={`w-4 h-4 ${loading || isSwitching ? "animate-spin text-[#3DD9C4]" : ""}`} />
              </button>
              <button
                onClick={() => setInviteModalOpen(true)}
                className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#4A72FF] to-[#3DD9C4] hover:from-[#3B5BDB] hover:to-[#34D399] text-[#0A1020] font-heading font-extrabold text-xs shadow-[0_0_20px_rgba(61,217,196,0.35)] flex items-center gap-2 transition-all transform active:scale-95 cursor-pointer"
              >
                <Plus className="w-4 h-4 stroke-[2.5]" />
                {t("Invite Member")}
              </button>
            </div>
          </div>

          {/* Quick KPI Metric Cards Grid */}
          <div className="grid grid-cols-1 xs:grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
            <Link
              href="/projects"
              className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#3DD9C4]/30 hover:border-[#3DD9C4] shadow-[0_0_30px_rgba(61,217,196,0.1)] transition-all transform hover:-translate-y-1 group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-3">
                <span className="text-xs font-mono font-bold uppercase tracking-wider">{t("PROJECTS")}</span>
                <div className="w-8 h-8 rounded-lg bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 flex items-center justify-center text-[#3DD9C4]">
                  <Layers className="w-4 h-4" />
                </div>
              </div>
              <div className="text-3xl font-heading font-extrabold text-[#E7EDF7] group-hover:text-[#3DD9C4] transition-colors">
                {summary?.projectsCount ?? 0}
              </div>
              <div className="mt-2 flex items-center justify-between text-[10px] font-mono">
                <span className="text-[#3DD9C4]">{t("Active in")} {environment.toUpperCase()}</span>
                <span className="text-emerald-400 font-bold">{t("Scoped")}</span>
              </div>
            </Link>

            <Link
              href="/members"
              className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#4A72FF]/30 hover:border-[#4A72FF] shadow-[0_0_30px_rgba(74,114,255,0.1)] transition-all transform hover:-translate-y-1 group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-3">
                <span className="text-xs font-mono font-bold uppercase tracking-wider">{t("MEMBERS")}</span>
                <div className="w-8 h-8 rounded-lg bg-[#4A72FF]/15 border border-[#4A72FF]/30 flex items-center justify-center text-[#4A72FF]">
                  <Users className="w-4 h-4" />
                </div>
              </div>
              <div className="text-3xl font-heading font-extrabold text-[#E7EDF7] group-hover:text-[#4A72FF] transition-colors">
                {summary?.membersCount ?? 0}
              </div>
              <div className="mt-2 flex items-center justify-between text-[10px] font-mono">
                <span className="text-[#8B99B8]">{t("Active Engineers")}</span>
                <span className="text-[#4A72FF] font-bold">{t("RBAC Enforced")}</span>
              </div>
            </Link>

            <Link
              href="/invitations"
              className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#FBBF24]/30 hover:border-[#FBBF24] shadow-[0_0_30px_rgba(251,191,36,0.1)] transition-all transform hover:-translate-y-1 group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-3">
                <span className="text-xs font-mono font-bold uppercase tracking-wider">{t("PENDING INVITES")}</span>
                <div className="w-8 h-8 rounded-lg bg-[#FBBF24]/15 border border-[#FBBF24]/30 flex items-center justify-center text-[#FBBF24]">
                  <Mail className="w-4 h-4" />
                </div>
              </div>
              <div className="text-3xl font-heading font-extrabold text-[#E7EDF7] group-hover:text-[#FBBF24] transition-colors">
                {summary?.pendingInvitations ?? 0}
              </div>
              <div className="mt-2 flex items-center justify-between text-[10px] font-mono">
                <span className="text-[#FBBF24]">{t("Awaiting Acceptance")}</span>
                <span className="text-[#FBBF24] font-bold">{t("Active Token")}</span>
              </div>
            </Link>

            <Link
              href="/teams"
              className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#A855F7]/30 hover:border-[#A855F7] shadow-[0_0_30px_rgba(168,85,247,0.1)] transition-all transform hover:-translate-y-1 group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-3">
                <span className="text-xs font-mono font-bold uppercase tracking-wider">{t("TEAMS")}</span>
                <div className="w-8 h-8 rounded-lg bg-[#A855F7]/15 border border-[#A855F7]/30 flex items-center justify-center text-[#A855F7]">
                  <Zap className="w-4 h-4" />
                </div>
              </div>
              <div className="text-3xl font-heading font-extrabold text-[#E7EDF7] group-hover:text-[#A855F7] transition-colors">
                {summary?.teamsCount ?? 0}
              </div>
              <div className="mt-2 flex items-center justify-between text-[10px] font-mono">
                <span className="text-[#8B99B8]">{t("Cross-functional")}</span>
                <span className="text-[#A855F7] font-bold">DevOps / SRE</span>
              </div>
            </Link>
          </div>

          {/* Infrastructure Health & Telemetry Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#3DD9C4]/35 shadow-[0_0_40px_rgba(61,217,196,0.12)] space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Server className="w-4 h-4 text-[#3DD9C4]" />
                  <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">{t("Kubernetes Cluster Health")}</h2>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 font-bold">
                  READY
                </span>
              </div>

              <div className="space-y-3">
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">{t("Target Environment")}</span>
                  <span className="text-[#3DD9C4] font-bold">{environment.toUpperCase()}-K8S-CLUSTER</span>
                </div>
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">{t("Node Status")}</span>
                  <span className="text-emerald-400 font-bold">{environment === "prod" ? "12/12 Nodes" : environment === "staging" ? "6/6 Nodes" : "3/3 Nodes"}</span>
                </div>
              </div>

              <Link
                href="/k8s"
                className="w-full py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#1e2f4d] text-xs font-mono font-bold flex items-center justify-center gap-1.5 transition-all cursor-pointer"
              >
                <span>{t("Inspect Clusters")}</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#4A72FF]/35 shadow-[0_0_40px_rgba(74,114,255,0.12)] space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <GitBranch className="w-4 h-4 text-[#4A72FF]" />
                  <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">{t("CI/CD Pipeline Engine")}</h2>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#4A72FF]/20 text-[#4A72FF] border border-[#4A72FF]/30 font-bold">
                  ACTIVE
                </span>
              </div>

              <div className="space-y-3">
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">{t("Target Release")}</span>
                  <span className="text-[#E7EDF7] font-bold">release-{environment}-v2.4.0</span>
                </div>
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">{t("Pipeline Status")}</span>
                  <span className="text-emerald-400 font-bold flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    SUCCESS
                  </span>
                </div>
              </div>

              <Link
                href="/cicd"
                className="w-full py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#4A72FF] hover:bg-[#1e2f4d] text-xs font-mono font-bold flex items-center justify-center gap-1.5 transition-all cursor-pointer"
              >
                <span>{t("View Pipelines")}</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#A855F7]/35 shadow-[0_0_40px_rgba(168,85,247,0.12)] space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-[#A855F7]" />
                  <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">Security Posture</h2>
                </div>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#A855F7]/20 text-[#A855F7] border border-[#A855F7]/30 font-bold">
                  VERIFIED
                </span>
              </div>

              <div className="space-y-3">
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">Scanner Status</span>
                  <span className="text-[#3DD9C4] font-bold">SCAN_COMPLETED</span>
                </div>
                <div className="p-3 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs font-mono">
                  <span className="text-[#8B99B8]">Security Policy</span>
                  <span className="text-emerald-400 font-bold">{environment.toUpperCase()}-STRICT</span>
                </div>
              </div>

              <Link
                href="/security"
                className="w-full py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#A855F7] hover:bg-[#1e2f4d] text-xs font-mono font-bold flex items-center justify-center gap-1.5 transition-all cursor-pointer"
              >
                <span>Inspect {environment.toUpperCase()} Scans</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>

          {/* Activity Timeline Section */}
          <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D]">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#3DD9C4]" />
                <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">Recent {environment.toUpperCase()} Audit Activity</h2>
              </div>
              <Link
                href="/audit-logs"
                className="text-xs font-mono text-[#3DD9C4] hover:underline flex items-center gap-1 cursor-pointer"
              >
                <span>View All Logs</span>
                <ArrowRight className="w-3 h-3" />
              </Link>
            </div>

            <div className="space-y-3">
              {activity.length === 0 ? (
                <div className="text-center py-6 text-xs text-[#8B99B8]">
                  No audit activity recorded for {environment.toUpperCase()}.
                </div>
              ) : (
                activity.map((item) => (
                  <div
                    key={item.id}
                    className="p-3.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] flex items-center justify-between text-xs hover:border-[#3DD9C4]/30 transition-all"
                  >
                    <div className="flex items-center gap-3">
                      <span className="w-2 h-2 rounded-full bg-[#3DD9C4]" />
                      <div>
                        <span className="font-mono text-[#3DD9C4] font-bold">{item.action}</span>
                        <span className="text-[#8B99B8] ml-2">target: <strong className="text-[#E7EDF7] font-mono">{item.target}</strong></span>
                      </div>
                    </div>
                    <span className="font-mono text-[10px] text-[#8B99B8]">
                      {new Date(item.createdAt).toLocaleTimeString()}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Bottom Operational Metrics Navigation Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <Link
              href="/cicd"
              className="p-4 rounded-xl bg-[#050F25]/60 border border-[#22314D] hover:border-[#3DD9C4] transition-all text-xs font-mono flex items-center justify-between cursor-pointer"
            >
              <span className="text-[#8B99B8]">{environment.toUpperCase()} Pipelines</span>
              <span className="text-[#3DD9C4] font-bold">Active</span>
            </Link>

            <Link
              href="/incidents"
              className="p-4 rounded-xl bg-[#050F25]/60 border border-[#22314D] hover:border-[#3DD9C4] transition-all text-xs font-mono flex items-center justify-between cursor-pointer"
            >
              <span className="text-[#8B99B8]">{environment.toUpperCase()} Incidents</span>
              <span className="text-emerald-400 font-bold">0 Active</span>
            </Link>

            <Link
              href="/security"
              className="p-4 rounded-xl bg-[#050F25]/60 border border-[#22314D] hover:border-[#3DD9C4] transition-all text-xs font-mono flex items-center justify-between cursor-pointer"
            >
              <span className="text-[#8B99B8]">{environment.toUpperCase()} Security</span>
              <span className="text-[#FBBF24] font-bold">Clean</span>
            </Link>

            <Link
              href="/projects"
              className="p-4 rounded-xl bg-[#050F25]/60 border border-[#22314D] hover:border-[#3DD9C4] transition-all text-xs font-mono flex items-center justify-between cursor-pointer"
            >
              <span className="text-[#8B99B8]">{environment.toUpperCase()} Deployments</span>
              <span className="text-[#4A72FF] font-bold">Verified</span>
            </Link>
          </div>
        </main>
      </div>

      <InviteMemberModal
        isOpen={inviteModalOpen}
        onClose={() => setInviteModalOpen(false)}
        orgId={orgId}
        onSuccess={() => loadData(orgId)}
      />
    </div>
  );
}
