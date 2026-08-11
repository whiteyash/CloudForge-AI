"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { BarChart3, TrendingUp, ShieldCheck, GitPullRequest, GitCommit, Users, Activity } from "lucide-react";

export default function RepositoryInsightsPage() {
  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Repository Insights & Intelligence</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Engineering activity metrics, commit velocity, contributor breakdown, and health scores</p>
          </div>

          {/* Metric Cards */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-1">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono">Health Score</span>
                <ShieldCheck className="w-4 h-4 text-[#34D399]" />
              </div>
              <p className="text-2xl font-heading font-bold text-[#34D399]">96%</p>
              <p className="text-[10px] text-[#8B99B8]">Protected branches & active velocity</p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-1">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono">Commit Velocity</span>
                <GitCommit className="w-4 h-4 text-[#3DD9C4]" />
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">42 / wk</p>
              <p className="text-[10px] text-[#34D399] flex items-center gap-0.5">
                <TrendingUp className="w-3 h-3" /> +14% vs last week
              </p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-1">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono">PR Merge Velocity</span>
                <GitPullRequest className="w-4 h-4 text-[#A855F7]" />
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">1.2 days</p>
              <p className="text-[10px] text-[#8B99B8]">Avg time from open to merge</p>
            </div>

            <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-1">
              <div className="flex items-center justify-between text-[#8B99B8]">
                <span className="text-xs font-mono">Active Contributors</span>
                <Users className="w-4 h-4 text-[#F59E0B]" />
              </div>
              <p className="text-2xl font-heading font-bold text-[#E7EDF7]">8 Active</p>
              <p className="text-[10px] text-[#8B99B8]">Across 2 repositories</p>
            </div>
          </div>

          {/* Activity Heatmap & Breakdown */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2 p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <BarChart3 className="w-4 h-4 text-[#3DD9C4]" />
                  Commit Activity Timeline
                </h3>
                <span className="text-xs font-mono text-[#8B99B8]">Last 30 Days</span>
              </div>

              <div className="h-40 flex items-end gap-2 pt-4 px-2 bg-[#0A1020] rounded-xl border border-[#22314D]">
                {[4, 8, 12, 16, 22, 18, 30, 25, 14, 20, 28, 35, 42, 38, 24, 30, 18, 25, 40, 32, 28, 19, 34, 29].map((val, idx) => (
                  <div key={idx} className="flex-1 flex flex-col items-center gap-1 group">
                    <div
                      className="w-full bg-[#3DD9C4] rounded-t hover:bg-[#34D399] transition-all"
                      style={{ height: `${(val / 42) * 100}%` }}
                    />
                  </div>
                ))}
              </div>
            </div>

            {/* Contributor Leaderboard */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                <Activity className="w-4 h-4 text-[#F59E0B]" />
                Top Contributors
              </h3>

              <div className="space-y-3">
                <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">CloudForge Admin</span>
                    <p className="text-[10px] text-[#8B99B8] font-mono">@cloudforge-admin</p>
                  </div>
                  <span className="px-2 py-0.5 rounded text-xs font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                    24 Commits
                  </span>
                </div>

                <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div>
                    <span className="font-heading text-xs font-bold text-[#E7EDF7]">DevOps Lead</span>
                    <p className="text-[10px] text-[#8B99B8] font-mono">@devops-lead</p>
                  </div>
                  <span className="px-2 py-0.5 rounded text-xs font-mono bg-[#A855F7]/10 text-[#A855F7] border border-[#A855F7]/30">
                    18 Commits
                  </span>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
