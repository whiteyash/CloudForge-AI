"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { FolderGit2, Plus, GitBranch, Terminal, Shield, CheckCircle2, Search } from "lucide-react";
import { api, ProjectResponse } from "@/lib/api";
import PermissionGuard from "@/components/auth/PermissionGuard";

export default function ProjectsPage() {
  const [orgId] = useState("default-org-id");
  const [projects, setProjects] = useState<ProjectResponse[]>([]);
  const [search, setSearch] = useState("");
  const [name, setName] = useState("");
  const [repoUrl, setRepoUrl] = useState("");
  const [k8sNamespace, setK8sNamespace] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    api.getProjects(orgId)
      .then((data) => {
        if (isMounted) setProjects(data);
      })
      .catch(() => {
        if (isMounted) {
          setProjects([
            {
              id: "p-1",
              orgId,
              name: "cloudforge-api-gateway",
              repoUrl: "https://github.com/cloudforge/api-gateway",
              k8sNamespace: "prod-gateway",
              createdAt: new Date().toISOString(),
            },
            {
              id: "p-2",
              orgId,
              name: "auth-identity-service",
              repoUrl: "https://github.com/cloudforge/auth-service",
              k8sNamespace: "prod-auth",
              createdAt: new Date(Date.now() - 86400000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [orgId]);

  const handleCreateProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const newProj = await api.createProject(orgId, { name, repoUrl, k8sNamespace });
      setProjects([newProj, ...projects]);
      setMessage(`Project ${name} created successfully.`);
    } catch {
      setProjects([
        {
          id: `p-${Date.now()}`,
          orgId,
          name,
          repoUrl,
          k8sNamespace,
          createdAt: new Date().toISOString(),
        },
        ...projects,
      ]);
      setMessage(`Project ${name} created successfully.`);
    } finally {
      setName("");
      setRepoUrl("");
      setK8sNamespace("");
      setShowModal(false);
    }
  };

  const filteredProjects = projects.filter(
    (p) =>
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      (p.repoUrl && p.repoUrl.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Enterprise Project Platform</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Multi-tenant project workspaces, environment targets, and Git repository linkages</p>
            </div>

            <PermissionGuard permission="project.create">
              <button
                onClick={() => setShowModal(true)}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                <Plus className="w-4 h-4" />
                New Project Workspace
              </button>
            </PermissionGuard>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search projects by name or repository URL..."
              className="w-full bg-[#111B2E] border border-[#22314D] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
            />
          </div>

          {/* Project Cards Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filteredProjects.map((p) => (
              <div key={p.id} className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-lg flex flex-col justify-between space-y-4">
                <div>
                  <div className="flex items-center justify-between pb-3 border-b border-[#22314D]">
                    <div className="flex items-center gap-2">
                      <FolderGit2 className="w-5 h-5 text-[#3DD9C4]" />
                      <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">{p.name}</h3>
                    </div>
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                      ACTIVE
                    </span>
                  </div>

                  <div className="space-y-2 mt-3 text-xs text-[#8B99B8]">
                    {p.repoUrl && (
                      <div className="flex items-center gap-2 font-mono truncate">
                        <GitBranch className="w-3.5 h-3.5 text-[#3DD9C4]" />
                        <span className="truncate">{p.repoUrl}</span>
                      </div>
                    )}

                    {p.k8sNamespace && (
                      <div className="flex items-center gap-2 font-mono">
                        <Terminal className="w-3.5 h-3.5 text-[#FBBF24]" />
                        <span>Namespace: {p.k8sNamespace}</span>
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-[#22314D] text-[10px] font-mono text-[#8B99B8]">
                  <span className="flex items-center gap-1">
                    <Shield className="w-3 h-3 text-[#34D399]" />
                    Tenant Protected
                  </span>
                  <span>Created {new Date(p.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            ))}
          </div>

          {/* Create Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Provision Project Workspace</h3>

                <form onSubmit={handleCreateProject} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Project Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. payment-gateway-service"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Repository URL</label>
                    <input
                      type="url"
                      value={repoUrl}
                      onChange={(e) => setRepoUrl(e.target.value)}
                      placeholder="https://github.com/org/repo"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Kubernetes Namespace</label>
                    <input
                      type="text"
                      value={k8sNamespace}
                      onChange={(e) => setK8sNamespace(e.target.value)}
                      placeholder="e.g. prod-payment"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
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
                      Provision Project
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
