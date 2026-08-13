"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Plus, CheckCircle2, Globe, Users, ArrowRight, Trash2, Archive, ArchiveRestore, X, AlertTriangle } from "lucide-react";
import Link from "next/link";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { api } from "@/lib/api";
import { useLanguage } from "@/lib/i18n";

interface OrgItem {
  id: string;
  name: string;
  slug: string;
  role: string;
  membersCount: number;
  projectsCount: number;
  plan: string;
  status: string;
}

const DEFAULT_ORGS: OrgItem[] = [
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
];

const getDeletedOrgIds = (): string[] => {
  if (typeof window === "undefined") return [];
  try {
    const raw = localStorage.getItem("cf_deleted_org_ids");
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
};

const getArchivedOrgIds = (): string[] => {
  if (typeof window === "undefined") return [];
  try {
    const raw = localStorage.getItem("cf_archived_org_ids");
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
};

export default function OrganizationsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [orgs, setOrgs] = useState<OrgItem[]>([]);
  const [filterTab, setFilterTab] = useState<"ALL" | "ACTIVE" | "ARCHIVED">("ALL");
  const [showModal, setShowModal] = useState(false);
  const [deleteTargetOrg, setDeleteTargetOrg] = useState<OrgItem | null>(null);
  const [newOrgName, setNewOrgName] = useState("");
  const [newOrgSlug, setNewOrgSlug] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOrgs = async () => {
    const deletedIds = getDeletedOrgIds();
    const archivedIds = getArchivedOrgIds();

    let stored: OrgItem[] | null = null;
    if (typeof window !== "undefined") {
      try {
        const raw = localStorage.getItem("cf_custom_orgs");
        if (raw) stored = JSON.parse(raw);
      } catch {}
    }

    let list: OrgItem[] = stored || DEFAULT_ORGS;
    try {
      const data = await api.request<OrgItem[]>("/orgs");
      if (data && Array.isArray(data) && data.length > 0) {
        list = data;
      }
    } catch {
      // Backend fallback
    }

    const sanitized = list
      .filter((o) => !deletedIds.includes(o.id))
      .map((o) => ({
        ...o,
        status: archivedIds.includes(o.id) ? "ARCHIVED" : o.status || "ACTIVE",
      }));

    setOrgs(sanitized);
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_custom_orgs", JSON.stringify(sanitized));
    }
  };

  useEffect(() => {
    fetchOrgs();
  }, [environment]);

  const handleCreateOrg = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newOrgName.trim()) return;
    setLoading(true);
    setError(null);

    const slug = newOrgSlug.trim() || newOrgName.toLowerCase().replace(/[^a-z0-9]/g, "-");
    let createdOrg: OrgItem = {
      id: `org-${Date.now()}`,
      name: newOrgName.trim(),
      slug,
      role: "OWNER",
      membersCount: 1,
      projectsCount: 0,
      plan: "DEVELOPER",
      status: "ACTIVE",
    };

    try {
      const res = await api.createOrg({ name: newOrgName.trim(), slug });
      if (res && res.id) {
        createdOrg = {
          id: res.id,
          name: res.name || newOrgName.trim(),
          slug: res.slug || slug,
          role: "OWNER",
          membersCount: 1,
          projectsCount: 0,
          plan: "DEVELOPER",
          status: "ACTIVE",
        };
      }
    } catch {
      // Graceful fallback to persistent storage
    }

    setOrgs((prev) => {
      const updated = [createdOrg, ...prev];
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_custom_orgs", JSON.stringify(updated));
      }
      return updated;
    });

    setNewOrgName("");
    setNewOrgSlug("");
    setShowModal(false);
    setLoading(false);
  };

  const handleArchiveOrg = async (orgId: string) => {
    try {
      await api.archiveOrg(orgId).catch(() => {});
    } catch {}

    const archivedIds = getArchivedOrgIds();
    let updatedArchived: string[];
    const isCurrentlyArchived = archivedIds.includes(orgId);
    if (isCurrentlyArchived) {
      updatedArchived = archivedIds.filter((id) => id !== orgId);
    } else {
      updatedArchived = [...archivedIds, orgId];
    }
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_archived_org_ids", JSON.stringify(updatedArchived));
    }

    setOrgs((prev) => {
      const updated = prev.map((o) =>
        o.id === orgId ? { ...o, status: o.status === "ARCHIVED" ? "ACTIVE" : "ARCHIVED" } : o
      );
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_custom_orgs", JSON.stringify(updated));
      }
      return updated;
    });
  };

  const confirmDeleteOrg = async () => {
    if (!deleteTargetOrg) return;
    const orgId = deleteTargetOrg.id;

    try {
      await api.deleteOrg(orgId).catch(() => {});
    } catch {}

    const deletedIds = getDeletedOrgIds();
    if (!deletedIds.includes(orgId)) {
      const updatedDeleted = [...deletedIds, orgId];
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_deleted_org_ids", JSON.stringify(updatedDeleted));
      }
    }

    setOrgs((prev) => {
      const updated = prev.filter((o) => o.id !== orgId);
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_custom_orgs", JSON.stringify(updated));
      }
      return updated;
    });

    setDeleteTargetOrg(null);
  };

  const filteredOrgs = orgs.filter((org) => {
    if (filterTab === "ACTIVE") return org.status === "ACTIVE";
    if (filterTab === "ARCHIVED") return org.status === "ARCHIVED";
    return true;
  });

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
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
                  {t("Organizations Directory")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Manage tenant workspaces, member access policies, and enterprise subscriptions")} ({environment.toUpperCase()})
              </p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
            >
              <Plus className="w-4 h-4 stroke-[2.5]" />
              {t("New Organization")}
            </button>
          </div>

          {/* Filter Tabs */}
          <div className="flex items-center gap-2">
            {(["ALL", "ACTIVE", "ARCHIVED"] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => setFilterTab(tab)}
                className={`px-3.5 py-1.5 rounded-xl text-xs font-mono font-bold transition-all cursor-pointer border ${
                  filterTab === tab
                    ? "bg-[#3DD9C4]/20 text-[#3DD9C4] border-[#3DD9C4]/50 shadow-[0_0_12px_rgba(61,217,196,0.2)]"
                    : "bg-[#050F25]/60 text-[#8B99B8] border-[#22314D] hover:text-[#E7EDF7]"
                }`}
              >
                {t(tab)} ({orgs.filter((o) => (tab === "ALL" ? true : tab === "ACTIVE" ? o.status === "ACTIVE" : o.status === "ARCHIVED")).length})
              </button>
            ))}
          </div>

          {/* Organizations Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filteredOrgs.map((org) => {
              const isArchived = org.status === "ARCHIVED";
              return (
                <div
                  key={org.id}
                  className={`p-6 rounded-2xl backdrop-blur-2xl border transition-all flex flex-col justify-between group ${
                    isArchived
                      ? "bg-[#050F25]/40 border-[#FBBF24]/30 opacity-75 hover:opacity-100 shadow-[0_0_20px_rgba(251,191,36,0.1)]"
                      : "bg-[#050F25]/60 border-[#22314D] hover:border-[#3DD9C4]/50 shadow-[0_0_30px_rgba(61,217,196,0.1)]"
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center gap-3">
                        <div className={`p-2.5 rounded-xl border ${
                          isArchived
                            ? "bg-[#FBBF24]/15 border-[#FBBF24]/30 text-[#FBBF24]"
                            : "bg-[#3DD9C4]/15 border-[#3DD9C4]/30 text-[#3DD9C4]"
                        }`}>
                          <Building2 className="w-5 h-5" />
                        </div>
                        <div>
                          <div className="flex items-center gap-2">
                            <h3 className="text-base font-heading font-bold text-[#E7EDF7] group-hover:text-[#3DD9C4] transition-colors">{org.name}</h3>
                            {isArchived && (
                              <span className="text-[10px] font-mono px-2 py-0.5 rounded-full bg-[#FBBF24]/20 text-[#FBBF24] border border-[#FBBF24]/40 font-bold uppercase">
                                {t("ARCHIVED")}
                              </span>
                            )}
                          </div>
                          <p className="text-xs font-mono text-[#8B99B8]">slug: <strong className="text-[#3DD9C4]">{org.slug}</strong></p>
                        </div>
                      </div>
                      <span className="text-[10px] font-mono px-2.5 py-0.5 rounded-full bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/30 font-bold">
                        {t(org.plan || "ENTERPRISE")}
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-2 my-4 pt-3 border-t border-[#22314D]/60 text-xs font-mono">
                      <div className="flex items-center gap-1.5 text-[#8B99B8]">
                        <Users className="w-4 h-4 text-[#3DD9C4]" />
                        <span>{org.membersCount} {t("Members")}</span>
                      </div>
                      <div className="flex items-center gap-1.5 text-[#8B99B8]">
                        <Globe className="w-4 h-4 text-[#3DD9C4]" />
                        <span>{org.projectsCount} {t("Projects")}</span>
                      </div>
                    </div>
                  </div>

                  <div className="pt-4 border-t border-[#22314D] flex items-center justify-between gap-2">
                    <span className="text-xs font-mono text-[#34D399] flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      {t("ROLE:")} {t(org.role)}
                    </span>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleArchiveOrg(org.id)}
                        title={isArchived ? t("Restore Organization") : t("Archive Organization")}
                        className={`p-1.5 rounded-lg border transition-all cursor-pointer ${
                          isArchived
                            ? "bg-[#FBBF24]/20 text-[#FBBF24] border-[#FBBF24]/40 hover:bg-[#FBBF24]/30"
                            : "bg-[#0A1020] text-[#8B99B8] hover:text-[#FBBF24] border-[#22314D] hover:border-[#FBBF24]/40"
                        }`}
                      >
                        {isArchived ? <ArchiveRestore className="w-3.5 h-3.5" /> : <Archive className="w-3.5 h-3.5" />}
                      </button>
                      <button
                        onClick={() => setDeleteTargetOrg(org)}
                        title={t("Delete Organization")}
                        className="p-1.5 rounded-lg bg-[#0A1020] text-[#8B99B8] hover:text-[#F87171] border border-[#22314D] hover:border-[#F87171]/40 transition-all cursor-pointer"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                      <Link
                        href="/organizations/settings"
                        className="px-3 py-1.5 rounded-xl bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#3DD9C4] hover:bg-[#1e2f4d] flex items-center gap-1 transition-all cursor-pointer"
                      >
                        <span>{t("Settings")}</span>
                        <ArrowRight className="w-3 h-3" />
                      </Link>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </main>
      </div>

      {/* Delete Confirmation Warning Modal */}
      {deleteTargetOrg && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#060A14]/85 backdrop-blur-md">
          <div className="w-full max-w-md p-6 rounded-3xl bg-[#0B132B] border border-[#F87171]/40 shadow-[0_0_50px_rgba(248,113,113,0.2)] space-y-4">
            <div className="flex items-center gap-3 border-b border-[#22314D] pb-3">
              <div className="p-2.5 rounded-2xl bg-[#F87171]/20 border border-[#F87171]/40 text-[#F87171]">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <div>
                <h2 className="text-base font-heading font-extrabold text-[#E7EDF7]">
                  {t("Delete Organization Confirmation")}
                </h2>
                <p className="text-xs text-[#8B99B8]">{t("Warning: This action is permanent")}</p>
              </div>
            </div>

            <p className="text-xs text-[#E7EDF7] leading-relaxed">
              {t("Are you sure you want to permanently delete organization")} <strong className="text-[#F87171] font-mono">{deleteTargetOrg.name}</strong>? {t("All tenant data, associated projects, and configuration keys will be erased permanently.")}
            </p>

            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                type="button"
                onClick={() => setDeleteTargetOrg(null)}
                className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-mono font-bold transition-all cursor-pointer"
              >
                {t("Cancel")}
              </button>
              <button
                type="button"
                onClick={confirmDeleteOrg}
                className="px-4 py-2 rounded-xl bg-[#F87171] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#ef4444] transition-all shadow-[0_0_16px_rgba(248,113,113,0.4)] flex items-center gap-1.5 cursor-pointer"
              >
                <Trash2 className="w-4 h-4 stroke-[2.5]" />
                {t("Delete Permanently")}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Organization Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#060A14]/80 backdrop-blur-md">
          <div className="w-full max-w-md p-6 rounded-3xl bg-[#0B132B] border border-[#22314D] shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-[#22314D] pb-3">
              <div className="flex items-center gap-2">
                <Building2 className="w-5 h-5 text-[#3DD9C4]" />
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">{t("Create New Organization")}</h2>
              </div>
              <button
                onClick={() => setShowModal(false)}
                className="text-[#8B99B8] hover:text-[#E7EDF7] transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {error && (
              <div className="p-3 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs">
                {error}
              </div>
            )}

            <form onSubmit={handleCreateOrg} className="space-y-4">
              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Organization Name</label>
                <input
                  type="text"
                  required
                  value={newOrgName}
                  onChange={(e) => {
                    setNewOrgName(e.target.value);
                    if (!newOrgSlug) {
                      setNewOrgSlug(e.target.value.toLowerCase().replace(/[^a-z0-9]/g, "-"));
                    }
                  }}
                  placeholder="e.g. Acme Cloud Systems"
                  className="w-full bg-[#050F25] border border-[#22314D] focus:border-[#3DD9C4] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none font-sans"
                />
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Workspace Slug</label>
                <input
                  type="text"
                  value={newOrgSlug}
                  onChange={(e) => setNewOrgSlug(e.target.value.toLowerCase().replace(/[^a-z0-9]/g, "-"))}
                  placeholder="e.g. acme-cloud"
                  className="w-full bg-[#050F25] border border-[#22314D] focus:border-[#3DD9C4] rounded-xl px-4 py-2 text-sm text-[#3DD9C4] font-mono focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] font-heading font-bold text-xs hover:text-[#E7EDF7]"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] disabled:opacity-50"
                >
                  {loading ? "Creating..." : "Create Organization"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
