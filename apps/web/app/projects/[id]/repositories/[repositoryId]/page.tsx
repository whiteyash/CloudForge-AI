"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { GitBranch, GitCommit, GitPullRequest, Users, Star, GitFork, ArrowLeft } from "lucide-react";
import Link from "next/link";

interface BranchItem {
  id: string;
  name: string;
  commitSha: string;
  isDefault: boolean;
  isProtected: boolean;
}

interface CommitItem {
  id: string;
  shortSha: string;
  message: string;
  authorName: string;
  committedAt: string;
}

interface ContributorItem {
  id: string;
  username: string;
  displayName: string;
  contributionCount: number;
}

interface PullRequestItem {
  id: string;
  number: number;
  title: string;
  state: string;
  authorUsername: string;
  sourceBranch: string;
  targetBranch: string;
  isDraft: boolean;
}

export default function RepositoryDetailsPage() {
  const [activeTab, setActiveTab] = useState<"overview" | "branches" | "commits" | "contributors" | "pulls">("overview");

  const [branches] = useState<BranchItem[]>([
    { id: "b-1", name: "main", commitSha: "a1b2c3d", isDefault: true, isProtected: true },
    { id: "b-2", name: "feature/oauth", commitSha: "e5f6789", isDefault: false, isProtected: false },
  ]);

  const [commits] = useState<CommitItem[]>([
    { id: "c-1", shortSha: "a1b2c3d", message: "Initial repository import & sync", authorName: "CloudForge Bot", committedAt: new Date().toISOString() },
    { id: "c-2", shortSha: "e5f6789", message: "Add OAuth 2.0 provider integration", authorName: "DevOps Lead", committedAt: new Date().toISOString() },
  ]);

  const [contributors] = useState<ContributorItem[]>([
    { id: "u-1", username: "cloudforge-admin", displayName: "CloudForge Administrator", contributionCount: 14 },
    { id: "u-2", username: "devops-lead", displayName: "DevOps Engineer", contributionCount: 8 },
  ]);

  const [pullRequests] = useState<PullRequestItem[]>([
    { id: "pr-1", number: 101, title: "Add OAuth 2.0 Provider Connection", state: "MERGED", authorUsername: "devops-lead", sourceBranch: "feature/oauth", targetBranch: "main", isDraft: false },
    { id: "pr-2", number: 102, title: "Configure Webhook Signature Verification", state: "OPEN", authorUsername: "cloudforge-admin", sourceBranch: "feature/webhooks", targetBranch: "main", isDraft: true },
  ]);

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center gap-3">
            <Link href="/projects" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">cloudforge-ai/cloudforge-web</h1>
              <p className="text-xs text-[#8B99B8] mt-0.5">GitHub Repository | Default Branch: main | Language: TypeScript</p>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center gap-2 border-b border-[#22314D] pb-3">
            <button
              onClick={() => setActiveTab("overview")}
              className={`px-4 py-2 rounded-xl font-heading text-xs font-semibold transition-all ${
                activeTab === "overview" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              Overview
            </button>
            <button
              onClick={() => setActiveTab("branches")}
              className={`px-4 py-2 rounded-xl font-heading text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeTab === "branches" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <GitBranch className="w-3.5 h-3.5" /> Branches ({branches.length})
            </button>
            <button
              onClick={() => setActiveTab("commits")}
              className={`px-4 py-2 rounded-xl font-heading text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeTab === "commits" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <GitCommit className="w-3.5 h-3.5" /> Commits ({commits.length})
            </button>
            <button
              onClick={() => setActiveTab("contributors")}
              className={`px-4 py-2 rounded-xl font-heading text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeTab === "contributors" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <Users className="w-3.5 h-3.5" /> Contributors ({contributors.length})
            </button>
            <button
              onClick={() => setActiveTab("pulls")}
              className={`px-4 py-2 rounded-xl font-heading text-xs font-semibold transition-all flex items-center gap-1.5 ${
                activeTab === "pulls" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <GitPullRequest className="w-3.5 h-3.5" /> Pull Requests ({pullRequests.length})
            </button>
          </div>

          {/* Tab Content */}
          {activeTab === "overview" && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="md:col-span-2 p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Repository Overview</h3>
                <p className="text-xs text-[#8B99B8]">CloudForge AI enterprise web application frontend built with Next.js 16 and TailwindCSS.</p>

                <div className="grid grid-cols-3 gap-4 pt-2">
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-center">
                    <span className="text-xs text-[#8B99B8] font-mono">Stars</span>
                    <p className="text-lg font-heading font-bold text-[#3DD9C4] flex items-center justify-center gap-1 mt-1">
                      <Star className="w-4 h-4" /> 42
                    </p>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-center">
                    <span className="text-xs text-[#8B99B8] font-mono">Forks</span>
                    <p className="text-lg font-heading font-bold text-[#3DD9C4] flex items-center justify-center gap-1 mt-1">
                      <GitFork className="w-4 h-4" /> 12
                    </p>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-center">
                    <span className="text-xs text-[#8B99B8] font-mono">Status</span>
                    <p className="text-sm font-heading font-bold text-[#34D399] mt-1">SYNCHRONIZED</p>
                  </div>
                </div>
              </div>

              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Security & Settings</h3>
                <div className="text-xs text-[#8B99B8] space-y-2 font-mono">
                  <p>Visibility: PRIVATE</p>
                  <p>Default Branch: main</p>
                  <p>Encrypted Tokens: AES-256-GCM</p>
                </div>
              </div>
            </div>
          )}

          {activeTab === "branches" && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Branches</h3>
              <div className="space-y-3">
                {branches.map((b) => (
                  <div key={b.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <GitBranch className="w-4 h-4 text-[#3DD9C4]" />
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{b.name}</span>
                      {b.isDefault && (
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                          DEFAULT
                        </span>
                      )}
                    </div>
                    <span className="text-xs font-mono text-[#8B99B8]">{b.commitSha}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === "commits" && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Recent Commits</h3>
              <div className="space-y-3">
                {commits.map((c) => (
                  <div key={c.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <div>
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{c.message}</span>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Author: {c.authorName}</p>
                    </div>
                    <span className="px-2 py-1 rounded text-xs font-mono bg-[#16233A] text-[#3DD9C4] border border-[#22314D]">
                      {c.shortSha}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === "contributors" && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Contributors</h3>
              <div className="grid grid-cols-2 gap-4">
                {contributors.map((u) => (
                  <div key={u.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center gap-3">
                    <div className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                      <Users className="w-5 h-5" />
                    </div>
                    <div>
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{u.displayName}</span>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">@{u.username} | {u.contributionCount} Commits</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === "pulls" && (
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
              <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Pull Requests</h3>
              <div className="space-y-3">
                {pullRequests.map((pr) => (
                  <div key={pr.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <GitPullRequest className="w-5 h-5 text-[#3DD9C4]" />
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="font-heading text-xs font-bold text-[#E7EDF7]">#{pr.number} {pr.title}</span>
                          <span className={`px-2 py-0.5 rounded text-[10px] font-mono font-semibold ${
                            pr.state === "MERGED" ? "bg-[#A855F7]/10 text-[#A855F7] border border-[#A855F7]/30" : "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30"
                          }`}>
                            {pr.state}
                          </span>
                        </div>
                        <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">
                          {pr.sourceBranch} $\rightarrow$ {pr.targetBranch} | Author: @{pr.authorUsername}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
