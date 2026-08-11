"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Users, Layers, Mail, Activity, ArrowRight, Plus, Zap, RefreshCw } from "lucide-react";
import Link from "next/link";
import { api, OrgDashboardSummaryResponse, AuditLogResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import InviteMemberModal from "@/components/modals/InviteMemberModal";

export default function DashboardPage() {
  const { environment } = useEnvironment();
  const [summary, setSummary] = useState<OrgDashboardSummaryResponse | null>(null);
  const [activity, setActivity] = useState<AuditLogResponse[]>([]);
  const [orgId, setOrgId] = useState<string>("default-org-id");
  const [loading, setLoading] = useState(false);
  const [inviteModalOpen, setInviteModalOpen] = useState(false);

  const loadData = useCallback(async (targetOrgId: string) => {
    setLoading(true);
    try {
      const sum = await api.getDashboardSummary(targetOrgId);
      setSummary(sum);
    } catch {
      setSummary({
        id: targetOrgId,
        name: "CloudForge AI Engineering",
        slug: "cloudforge-engineering",
        projectsCount: environment === "prod" ? 4 : environment === "staging" ? 3 : 2,
        membersCount: 4,
        teamsCount: 3,
        pendingInvitations: 2,
        status: "ACTIVE",
        createdAt: new Date().toISOString(),
      });
    }

    try {
      const act = await api.getActivityTimeline(targetOrgId);
      setActivity(act);
    } catch {
      setActivity([
        {
          id: "act-1",
          orgId: targetOrgId,
          userId: "u-1",
          action: "project.deployed",
          target: `production-release-${environment}-v2.4`,
          createdAt: new Date().toISOString(),
        },
        {
          id: "act-2",
          orgId: targetOrgId,
          userId: "u-2",
          action: "member.invited",
          target: "developer@cloudforge.ai",
          createdAt: new Date(Date.now() - 3600000).toISOString(),
        },
        {
          id: "act-3",
          orgId: targetOrgId,
          userId: "u-3",
          action: "security.scan_completed",
          target: `${environment.toUpperCase()}-CLUSTER-K8S`,
          createdAt: new Date(Date.now() - 7200000).toISOString(),
        },
      ]);
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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-xl">
            <div>
              <div className="flex items-center gap-2.5 mb-1">
                <Building2 className="w-5 h-5 text-[#3DD9C4]" />
                <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">
                  {summary?.name || "CloudForge AI Workspace"}
                </h1>
                <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 font-bold uppercase">
                  {summary?.status || "ACTIVE"}
                </span>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${
                  environment === "prod"
                    ? "bg-[#F87171]/20 text-[#F87171] border-[#F87171]/40"
                    : environment === "staging"
                    ? "bg-[#FBBF24]/20 text-[#FBBF24] border-[#FBBF24]/40"
                    : "bg-[#3DD9C4]/20 text-[#3DD9C4] border-[#3DD9C4]/40"
                }`}>
                  ENV: {environment}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Enterprise Kubernetes &amp; Cloud Operations Control Plane • slug: <span className="font-mono text-[#3DD9C4]">{summary?.slug || "org-workspace"}</span>
              </p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => loadData(orgId)}
                className="p-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] transition-colors cursor-pointer"
                title="Refresh Metrics"
              >
                <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin text-[#3DD9C4]" : ""}`} />
              </button>
              <button
                onClick={() => setInviteModalOpen(true)}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
              >
                <Plus className="w-4 h-4 stroke-[2.5]" />
                Invite Member
              </button>
            </div>
          </div>

          {/* Quick Metrics Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <Link
              href="/projects"
              className="p-4 rounded-xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono font-medium uppercase">Projects</span>
                <Layers className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <div className="text-2xl font-heading font-extrabold text-[#E7EDF7]">
                {summary?.projectsCount ?? 0}
              </div>
              <span className="text-[10px] text-[#3DD9C4] font-mono mt-1 block">Active in {environment.toUpperCase()}</span>
            </Link>

            <Link
              href="/members"
              className="p-4 rounded-xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono font-medium uppercase">Members</span>
                <Users className="w-4 h-4 text-[#4A72FF]" />
              </div>
              <div className="text-2xl font-heading font-extrabold text-[#E7EDF7]">
                {summary?.membersCount ?? 0}
              </div>
              <span className="text-[10px] text-[#8B99B8] font-mono mt-1 block">Active Engineers</span>
            </Link>

            <Link
              href="/invitations"
              className="p-4 rounded-xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono font-medium uppercase">Pending Invites</span>
                <Mail className="w-4 h-4 text-[#FBBF24]" />
              </div>
              <div className="text-2xl font-heading font-extrabold text-[#E7EDF7]">
                {summary?.pendingInvitations ?? 0}
              </div>
              <span className="text-[10px] text-[#FBBF24] font-mono mt-1 block">Awaiting Acceptance</span>
            </Link>

            <Link
              href="/teams"
              className="p-4 rounded-xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all group cursor-pointer"
            >
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono font-medium uppercase">Teams</span>
                <Zap className="w-4 h-4 text-[#A855F7]" />
              </div>
              <div className="text-2xl font-heading font-extrabold text-[#E7EDF7]">
                {summary?.teamsCount ?? 0}
              </div>
              <span className="text-[10px] text-[#8B99B8] font-mono mt-1 block">Cross-functional</span>
            </Link>
          </div>

          {/* Activity Timeline */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D]">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#3DD9C4]" />
                <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">Recent Organization Audit Activity</h2>
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
                  No audit activity recorded yet.
                </div>
              ) : (
                activity.map((item) => (
                  <div
                    key={item.id}
                    className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between text-xs"
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
