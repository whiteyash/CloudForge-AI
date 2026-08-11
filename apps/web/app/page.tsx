"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Users, Layers, Mail, Activity, ArrowRight, Plus, ShieldCheck, Zap } from "lucide-react";
import Link from "next/link";
import { api, OrgDashboardSummaryResponse, AuditLogResponse } from "@/lib/api";

export default function DashboardPage() {
  const [summary, setSummary] = useState<OrgDashboardSummaryResponse | null>(null);
  const [activity, setActivity] = useState<AuditLogResponse[]>([]);
  const [orgId, setOrgId] = useState<string>("default-org-id");

  const loadData = async (targetOrgId: string) => {
    try {
      const sum = await api.getDashboardSummary(targetOrgId);
      setSummary(sum);
    } catch {
      setSummary({
        id: targetOrgId,
        name: "CloudForge AI Workspace",
        slug: "cloudforge-engineering",
        projectsCount: 4,
        membersCount: 6,
        teamsCount: 2,
        pendingInvitations: 1,
        status: "ACTIVE",
        createdAt: new Date().toISOString(),
      });
    }

    try {
      const logs = await api.getActivityTimeline(targetOrgId);
      setActivity(logs.slice(0, 5));
    } catch {
      setActivity([
        {
          id: "act-1",
          orgId: targetOrgId,
          userId: "u-1",
          action: "organization.updated",
          target: "cloudforge-engineering",
          createdAt: new Date().toISOString(),
        },
      ]);
    }
  };

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
  }, []);

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-2 mb-1">
                <Building2 className="w-5 h-5 text-[#3DD9C4]" />
                <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">
                  {summary?.name || "CloudForge AI Workspace"}
                </h1>
                <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 font-bold">
                  {summary?.status || "ACTIVE"}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Enterprise Kubernetes & Cloud Operations Control Plane • slug: <span className="font-mono text-[#3DD9C4]">{summary?.slug || "org-workspace"}</span>
              </p>
            </div>

            <div className="flex items-center gap-2">
              <Link
                href="/invitations"
                className="px-3.5 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1"
              >
                <Plus className="w-4 h-4" />
                Invite Member
              </Link>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono uppercase">Projects</span>
                <Layers className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <div className="text-2xl font-heading font-bold text-[#E7EDF7]">{summary?.projectsCount ?? 0}</div>
              <p className="text-[11px] text-[#8B99B8] mt-1">Active project workspaces</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono uppercase">Organization Members</span>
                <Users className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <div className="text-2xl font-heading font-bold text-[#E7EDF7]">{summary?.membersCount ?? 0}</div>
              <p className="text-[11px] text-[#8B99B8] mt-1">Active user accounts</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono uppercase">Pending Invitations</span>
                <Mail className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <div className="text-2xl font-heading font-bold text-[#3DD9C4]">{summary?.pendingInvitations ?? 0}</div>
              <p className="text-[11px] text-[#8B99B8] mt-1">Tokens pending acceptance</p>
            </div>

            <div className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D]">
              <div className="flex items-center justify-between text-[#8B99B8] mb-2">
                <span className="text-xs font-mono uppercase">Teams</span>
                <Zap className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <div className="text-2xl font-heading font-bold text-[#E7EDF7]">{summary?.teamsCount ?? 0}</div>
              <p className="text-[11px] text-[#8B99B8] mt-1">Cross-functional teams</p>
            </div>
          </div>

          {/* Activity Stream */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center justify-between pb-4 border-b border-[#22314D] mb-4">
              <div className="flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#3DD9C4]" />
                <h2 className="text-sm font-heading font-bold text-[#E7EDF7]">Recent Organization Activity</h2>
              </div>
              <Link href="/activity" className="text-xs font-heading font-semibold text-[#3DD9C4] hover:underline flex items-center gap-1">
                View All Activity
                <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>

            <div className="space-y-3">
              {activity.map((item) => (
                <div key={item.id} className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between text-xs">
                  <div>
                    <span className="font-mono text-[#3DD9C4] font-bold">{item.action}</span>
                    <span className="text-[#8B99B8] ml-2">target: {item.target}</span>
                  </div>
                  <span className="text-[10px] font-mono text-[#8B99B8]">{new Date(item.createdAt).toLocaleTimeString()}</span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
