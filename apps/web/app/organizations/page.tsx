"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Plus, CheckCircle2, Globe, Users, ArrowRight } from "lucide-react";
import Link from "next/link";

export default function OrganizationsPage() {
  const [orgs] = useState([
    {
      id: "org-1",
      name: "CloudForge AI Engineering",
      slug: "cloudforge-engineering",
      role: "OWNER",
      membersCount: 12,
      projectsCount: 8,
      plan: "ENTERPRISE",
      status: "ACTIVE",
    },
    {
      id: "org-2",
      name: "Acme Cyber Ops",
      slug: "acme-cyber-ops",
      role: "ADMIN",
      membersCount: 5,
      projectsCount: 3,
      plan: "PRO",
      status: "ACTIVE",
    },
  ]);

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organizations Directory</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Manage tenant workspaces, member access policies, and enterprise subscriptions</p>
            </div>

            <button
              onClick={() => alert("Create organization modal ready")}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5"
            >
              <Plus className="w-4 h-4 stroke-[2.5]" />
              New Organization
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {orgs.map((org) => (
              <div
                key={org.id}
                className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                        <Building2 className="w-5 h-5" />
                      </div>
                      <div>
                        <h3 className="text-base font-heading font-bold text-[#E7EDF7]">{org.name}</h3>
                        <p className="text-xs font-mono text-[#8B99B8]">slug: {org.slug}</p>
                      </div>
                    </div>
                    <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/30 font-bold">
                      {org.plan}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 gap-2 my-4 pt-3 border-t border-[#22314D]/60 text-xs">
                    <div className="flex items-center gap-1.5 text-[#8B99B8]">
                      <Users className="w-4 h-4 text-[#3DD9C4]" />
                      <span>{org.membersCount} Members</span>
                    </div>
                    <div className="flex items-center gap-1.5 text-[#8B99B8]">
                      <Globe className="w-4 h-4 text-[#3DD9C4]" />
                      <span>{org.projectsCount} Projects</span>
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-[#22314D] flex items-center justify-between">
                  <span className="text-xs font-mono text-[#34D399] flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    ROLE: {org.role}
                  </span>
                  <Link
                    href="/organizations/settings"
                    className="text-xs font-heading font-semibold text-[#3DD9C4] hover:underline flex items-center gap-1"
                  >
                    Manage Settings
                    <ArrowRight className="w-3.5 h-3.5" />
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
