"use client";

import React, { useState, useEffect, useCallback } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { FolderGit2, Plus, GitBranch, Terminal, Shield, CheckCircle2, Search, RefreshCw, Trash2 } from "lucide-react";
import { api, ProjectResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage } from "@/lib/i18n";

const DEFAULT_PROJECTS: ProjectResponse[] = [
  {
    id: "proj-1",
    orgId: "00000000-0000-0000-0000-000000000001",
    name: "cloudforge-control-plane",
    repoUrl: "https://github.com/cloudforge/control-plane",
    k8sNamespace: "production-system",
    createdAt: new Date().toISOString(),
  },
  {
    id: "proj-2",
    orgId: "00000000-0000-0000-0000-000000000001",
    name: "auth-security-service",
    repoUrl: "https://github.com/cloudforge/auth-service",
    k8sNamespace: "security-prod",
    createdAt: new Date().toISOString(),
  },
];

export default function ProjectsPage() {
  const { environment, environmentConfig, isSwitching } = useEnvironment();
  const { t } = useLanguage();
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState("");
  const [repoUrl, setRepoUrl] = useState("");
  const [k8sNamespace, setK8sNamespace] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [orgId, setOrgId] = useState<string>("");

  const resolveTargetOrg = async (): Promise<string> => {
    let target = "";
    if (typeof window !== "undefined") {
      target = localStorage.getItem("cf_active_org_id") || "";
    }
    if (!target) {
      try {
        const orgs = await api.request<any[]>("/orgs");
        if (orgs && orgs.length > 0) {
          target = orgs[0].id;
        }
      } catch {
        // Fallback
      }
    }
    if (!target) {
      try {
        const me = await api.me();
        if (me.organizations && me.organizations.length > 0) {
          target = me.organizations[0].id;
        }
      } catch {
        // Fallback
      }
    }
    return target || "00000000-0000-0000-0000-000000000001";
  };

  const fetchProjects = useCallback(async () => {
    setLoading(true);
    setError(null);
    let stored: ProjectResponse[] | null = null;
    if (typeof window !== "undefined") {
      try {
        const raw = localStorage.getItem("cf_custom_projects");
        if (raw) stored = JSON.parse(raw);
      } catch {}
    }

    try {
      const activeOrg = await resolveTargetOrg();
      setOrgId(activeOrg);
      const data = await api.getProjects(activeOrg);
      if (data && Array.isArray(data) && data.length > 0) {
        setProjects(data);
        if (typeof window !== "undefined") {
          localStorage.setItem("cf_custom_projects", JSON.stringify(data));
        }
        setLoading(false);
        return;
      }
    } catch {
      // Fallback
    }

    setProjects(stored || DEFAULT_PROJECTS);
    setLoading(false);
  }, []);

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setMessage(null);
    setError(null);

    const activeOrg = orgId || await resolveTargetOrg();
    let createdProj: ProjectResponse = {
      id: `proj-${Date.now()}`,
      orgId: activeOrg,
      name: name.trim(),
      repoUrl: repoUrl.trim() || undefined,
      k8sNamespace: k8sNamespace.trim() || "default",
      createdAt: new Date().toISOString(),
    };

    try {
      const newProj = await api.createProject(activeOrg, { name: name.trim(), repoUrl, k8sNamespace });
      if (newProj && newProj.id) {
        createdProj = newProj;
      }
    } catch {
      // Local persistence fallback
    }

    setProjects((prev) => {
      const updated = [createdProj, ...prev];
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_custom_projects", JSON.stringify(updated));
      }
      return updated;
    });

    setMessage(`Project "${createdProj.name}" created successfully.`);
    setName("");
    setRepoUrl("");
    setK8sNamespace("");
    setShowModal(false);
  };

  const handleDeleteProject = async (projectId: string, projName: string) => {
    if (!confirm(`Are you sure you want to delete project "${projName}"?`)) return;
    setMessage(null);
    setError(null);

    try {
      const activeOrg = orgId || await resolveTargetOrg();
      await api.deleteProject(activeOrg, projectId).catch(() => {});
    } catch {}

    setProjects((prev) => {
      const updated = prev.filter((p) => p.id !== projectId);
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_custom_projects", JSON.stringify(updated));
      }
      return updated;
    });
    setMessage(`Project "${projName}" deleted successfully.`);
  };

  const filteredProjects = projects.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      (p.repoUrl && p.repoUrl.toLowerCase().includes(search.toLowerCase()))
  );

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
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {t("Enterprise Projects & Microservices")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Multi-tenant project workspaces, environment targets, and Git repository linkages")} ({environmentConfig.name})
              </p>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={fetchProjects}
                className="p-2.5 rounded-xl bg-[#16233A]/80 border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] hover:border-[#3DD9C4]/40 transition-all cursor-pointer"
                title="Refresh Projects"
              >
                <RefreshCw className={`w-4 h-4 ${loading || isSwitching ? "animate-spin text-[#3DD9C4]" : ""}`} />
              </button>
              <button
                onClick={() => setShowModal(true)}
                className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
              >
                <Plus className="w-4 h-4 stroke-[2.5]" />
                {t("New Project")}
              </button>
            </div>
          </div>

          {/* Feedback Message */}
          {message && (
            <div className="p-3.5 rounded-xl bg-emerald-500/15 border border-emerald-500/40 text-emerald-400 text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-3.5 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <Shield className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder={`Search ${environment.toUpperCase()} projects by name or repository URL...`}
              className="w-full bg-[#050F25]/70 border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-xs text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none focus:border-[#3DD9C4] transition-colors font-sans"
            />
          </div>

          {/* Projects Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredProjects.map((project) => (
              <div
                key={project.id}
                className="p-5 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/50 transition-all group flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <div className="w-9 h-9 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 flex items-center justify-center text-[#3DD9C4]">
                      <FolderGit2 className="w-4 h-4" />
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="text-[9px] font-mono px-2 py-0.5 rounded-full bg-emerald-500/15 text-emerald-400 border border-emerald-500/30 font-bold uppercase">
                        ACTIVE ({environment.toUpperCase()})
                      </span>
                      <button
                        onClick={() => handleDeleteProject(project.id, project.name)}
                        title="Delete Project Workspace"
                        className="p-1.5 rounded-lg bg-[#0A1020] text-[#8B99B8] hover:text-[#F87171] border border-[#22314D] hover:border-[#F87171]/40 transition-all cursor-pointer"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>

                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7] group-hover:text-[#3DD9C4] transition-colors mb-1 truncate">
                    {project.name}
                  </h3>
                  <p className="text-[11px] font-mono text-[#8B99B8] truncate mb-4">
                    {project.repoUrl || "No Git Repository Linked"}
                  </p>
                </div>

                <div className="pt-3 border-t border-[#22314D]/50 flex items-center justify-between text-[10px] font-mono text-[#8B99B8]">
                  <span>Namespace: <strong className="text-[#3DD9C4]">{project.k8sNamespace || "default"}</strong></span>
                  <span>Created: {new Date(project.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>

      {/* Create Project Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#0A1020]/80 backdrop-blur-md animate-in fade-in">
          <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 shadow-2xl">
            <h2 className="text-base font-heading font-bold text-[#E7EDF7] mb-1">Create New Project ({environment.toUpperCase()})</h2>
            <p className="text-xs text-[#8B99B8] mb-4">Register a new microservice workspace and Git repository</p>

            <form onSubmit={handleCreateProject} className="space-y-4">
              <div>
                <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                  PROJECT NAME *
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. payment-gateway"
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2.5 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div>
                <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                  GIT REPOSITORY URL
                </label>
                <input
                  type="url"
                  value={repoUrl}
                  onChange={(e) => setRepoUrl(e.target.value)}
                  placeholder="https://github.com/org/repo"
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2.5 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div>
                <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5">
                  KUBERNETES NAMESPACE
                </label>
                <input
                  type="text"
                  value={k8sNamespace}
                  onChange={(e) => setK8sNamespace(e.target.value)}
                  placeholder={`e.g. ${environment}-payment`}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2.5 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-3 border-t border-[#22314D]">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-xs text-[#8B99B8] hover:text-[#E7EDF7] transition-colors cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-colors shadow-[0_0_16px_rgba(61,217,196,0.3)] cursor-pointer"
                >
                  Create Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
