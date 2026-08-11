"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { KeyRound, Boxes, GitBranch } from "lucide-react";

interface ActivityItem {
  id: string;
  action: string;
  actor: string;
  target: string;
  timestamp: string;
  category: string;
}

export default function ProjectActivityPage() {
  const [activities] = useState<ActivityItem[]>(() => [
    { id: "act-1", action: "Environment Provisioned", actor: "Platform Engineer", target: "prod-gateway", timestamp: "2026-07-31T22:00:00.000Z", category: "ENVIRONMENT" },
    { id: "act-2", action: "Variable Added", actor: "Platform Engineer", target: "DATABASE_URL", timestamp: "2026-07-31T21:00:00.000Z", category: "VARIABLE" },
    { id: "act-3", action: "Git Repository Linked", actor: "DevOps Specialist", target: "github.com/cloudforge/api-gateway", timestamp: "2026-07-31T20:00:00.000Z", category: "REPOSITORY" },
  ]);

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Activity Feed</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Real-time timeline of project modifications, environment deployments, and variable updates</p>
          </div>

          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Timeline ({activities.length})</h3>

            <div className="space-y-3">
              {activities.map((act) => (
                <div key={act.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
                      {act.category === "ENVIRONMENT" ? (
                        <Boxes className="w-4 h-4 text-[#3DD9C4]" />
                      ) : act.category === "VARIABLE" ? (
                        <KeyRound className="w-4 h-4 text-[#FBBF24]" />
                      ) : (
                        <GitBranch className="w-4 h-4 text-[#34D399]" />
                      )}
                    </div>
                    <div>
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{act.action}</span>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Actor: {act.actor} | Target: {act.target}</p>
                    </div>
                  </div>

                  <span className="text-[10px] font-mono text-[#8B99B8]">
                    {new Date(act.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
