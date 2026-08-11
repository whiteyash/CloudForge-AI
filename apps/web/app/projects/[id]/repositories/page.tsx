"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { GitBranch, Plus, CheckCircle2, Shield, RefreshCw, FolderGit2, Search } from "lucide-react";

interface ImportedRepoItem {
  id: string;
  name: string;
  fullName: string;
  providerName: string;
  cloneUrl: string;
  defaultBranch: string;
  visibility: string;
  language: string;
  syncStatus: string;
  lastSyncedAt: string;
  branchesCount: number;
}

export default function ProjectRepositoriesPage() {
  const [repositories, setRepositories] = useState<ImportedRepoItem[]>([
    {
      id: "repo-1",
      name: "cloudforge-microservice",
      fullName: "cloudforge-ai/cloudforge-microservice",
      providerName: "GITHUB",
      cloneUrl: "https://github.com/cloudforge-ai/cloudforge-microservice.git",
      defaultBranch: "main",
      visibility: "PRIVATE",
      language: "TypeScript",
      syncStatus: "SYNCHRONIZED",
      lastSyncedAt: new Date().toISOString(),
      branchesCount: 8,
    },
  ]);

  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [repoName, setRepoName] = useState("");
  const [providerName, setProviderName] = useState("GITHUB");
  const [message, setMessage] = useState<string | null>(null);

  const handleImport = (e: React.FormEvent) => {
    e.preventDefault();
    const item: ImportedRepoItem = {
      id: `repo-${Date.now()}`,
      name: repoName,
      fullName: `cloudforge-ai/${repoName}`,
      providerName,
      cloneUrl: `https://github.com/cloudforge-ai/${repoName}.git`,
      defaultBranch: "main",
      visibility: "PRIVATE",
      language: "Java",
      syncStatus: "SYNCHRONIZED",
      lastSyncedAt: new Date().toISOString(),
      branchesCount: 3,
    };
    setRepositories([...repositories, item]);
    setRepoName("");
    setShowModal(false);
    setMessage(`Repository ${item.fullName} imported and synchronized successfully.`);
  };

  const handleSyncNow = (id: string) => {
    setRepositories((prev) =>
      prev.map((r) =>
        r.id === id
          ? { ...r, syncStatus: "SYNCHRONIZED", lastSyncedAt: new Date().toISOString() }
          : r
      )
    );
    setMessage("Repository synchronization triggered and branches updated.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Repositories</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Import & synchronize Git repositories, default branches, tags, and contributors</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Import Repository
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Search Filter */}
          <div className="p-4 rounded-2xl bg-[#111B2E] border border-[#22314D] flex items-center gap-3">
            <Search className="w-4 h-4 text-[#8B99B8]" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search imported repositories..."
              className="bg-transparent border-none text-xs text-[#E7EDF7] focus:outline-none w-full"
            />
          </div>

          {/* Repository List */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Linked Repositories ({repositories.length})</h3>

            <div className="space-y-3">
              {repositories.map((r) => (
                <div key={r.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-start justify-between gap-4">
                  <div className="flex items-start gap-3">
                    <div className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4] shrink-0 mt-0.5">
                      <FolderGit2 className="w-5 h-5" />
                    </div>

                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-heading text-sm font-bold text-[#E7EDF7]">{r.fullName}</span>
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30 flex items-center gap-1">
                          <CheckCircle2 className="w-3 h-3" /> {r.syncStatus}
                        </span>
                      </div>

                      <p className="text-xs text-[#8B99B8] font-mono">
                        Provider: {r.providerName} | Branch: {r.defaultBranch} | Language: {r.language}
                      </p>

                      <div className="flex items-center gap-2 pt-1">
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#16233A] text-[#3DD9C4] border border-[#22314D] flex items-center gap-1">
                          <GitBranch className="w-3 h-3" /> {r.branchesCount} Branches
                        </span>
                      </div>
                    </div>
                  </div>

                  <button
                    onClick={() => handleSyncNow(r.id)}
                    className="p-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#3DD9C4] text-xs font-medium flex items-center gap-1 transition-all shrink-0"
                    title="Trigger Immediate Sync"
                  >
                    <RefreshCw className="w-3.5 h-3.5" />
                    Sync Now
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Import Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <FolderGit2 className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Import Git Repository</h3>
                </div>

                <form onSubmit={handleImport} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Git Provider</label>
                    <select
                      value={providerName}
                      onChange={(e) => setProviderName(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="GITHUB">GitHub Enterprise / Cloud</option>
                      <option value="GITLAB">GitLab Self-Managed / Cloud</option>
                      <option value="BITBUCKET">Bitbucket Data Center / Cloud</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Repository Name</label>
                    <input
                      type="text"
                      required
                      value={repoName}
                      onChange={(e) => setRepoName(e.target.value)}
                      placeholder="e.g. cloudforge-backend"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div className="p-3 rounded-xl bg-[#16233A] border border-[#22314D] text-[10px] text-[#8B99B8] flex items-center gap-2">
                    <Shield className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                    <span>Metadata, branches, and commit histories will be synchronized automatically.</span>
                  </div>

                  <div className="flex justify-end gap-2 pt-2">
                    <button
                      type="button"
                      onClick={() => setShowModal(false)}
                      className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-bold"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399]"
                    >
                      Import & Synchronize
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
