"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, Search, Check, X, Info } from "lucide-react";
import { api, PermissionCatalogResponse, RolePermissionMappingResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

import { useLanguage } from "@/lib/i18n";

export default function PermissionsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t } = useLanguage();
  const [catalog, setCatalog] = useState<PermissionCatalogResponse[]>([]);
  const [matrix, setMatrix] = useState<RolePermissionMappingResponse[]>([]);
  const [search, setSearch] = useState("");
  const [selectedModule, setSelectedModule] = useState("ALL");

  const roles = ["OWNER", "ADMIN", "DEVELOPER", "DEVOPS", "SECURITY", "VIEWER"];

  useEffect(() => {
    let isMounted = true;

    api.getPermissionsCatalog()
      .then((data) => {
        if (isMounted) setCatalog(data);
      })
      .catch(() => {
        if (isMounted) {
          setCatalog([
            { id: "p-1", code: "organization.view", module: "Organization", description: "View organization metadata" },
            { id: "p-2", code: "organization.update", module: "Organization", description: "Update branding settings" },
            { id: "p-3", code: "member.invite", module: "Members", description: "Issue invitation tokens" },
            { id: "p-4", code: "member.role.change", module: "Members", description: "Change member roles" },
            { id: "p-5", code: "project.create", module: "Projects", description: "Create project workspaces" },
            { id: "p-6", code: "audit.view", module: "Audit", description: "View audit logs" },
          ]);
        }
      });

    api.getPermissionsMatrix()
      .then((data) => {
        if (isMounted) setMatrix(data);
      })
      .catch(() => {
        if (isMounted) {
          setMatrix([
            { role: "OWNER", permissionCode: "organization.view" },
            { role: "OWNER", permissionCode: "organization.update" },
            { role: "OWNER", permissionCode: "member.invite" },
            { role: "OWNER", permissionCode: "member.role.change" },
            { role: "OWNER", permissionCode: "project.create" },
            { role: "OWNER", permissionCode: "audit.view" },
            { role: "ADMIN", permissionCode: "organization.view" },
            { role: "ADMIN", permissionCode: "organization.update" },
            { role: "ADMIN", permissionCode: "member.invite" },
            { role: "ADMIN", permissionCode: "project.create" },
            { role: "ADMIN", permissionCode: "audit.view" },
            { role: "DEVELOPER", permissionCode: "organization.view" },
            { role: "DEVELOPER", permissionCode: "project.create" },
            { role: "DEVELOPER", permissionCode: "audit.view" },
            { role: "VIEWER", permissionCode: "organization.view" },
            { role: "VIEWER", permissionCode: "audit.view" },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const hasPermissionMapping = (role: string, permissionCode: string) => {
    return matrix.some((m) => m.role === role && m.permissionCode === permissionCode);
  };

  const modules = Array.from(new Set(catalog.map((p) => p.module)));

  const filteredCatalog = catalog.filter((p) => {
    const matchesSearch =
      p.code.toLowerCase().includes(search.toLowerCase()) ||
      p.description.toLowerCase().includes(search.toLowerCase());
    const matchesModule = selectedModule === "ALL" || p.module === selectedModule;
    return matchesSearch && matchesModule;
  });

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {t("Role-Based Access Control (RBAC) Permissions Matrix")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Configure fine-grained tenant resource permissions for Owner, Admin, Developer, and Observer roles")} ({environment.toUpperCase()})
              </p>
            </div>
          </div>

          {/* Search & Filter Bar */}
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search permissions by code or description..."
                className="w-full bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-colors font-sans"
              />
            </div>

            <select
              value={selectedModule}
              onChange={(e) => setSelectedModule(e.target.value)}
              className="bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none transition-colors font-sans"
            >
              <option value="ALL">All Modules</option>
              {modules.map((mod) => (
                <option key={mod} value={mod} className="bg-[#0A1020]">
                  {mod}
                </option>
              ))}
            </select>
          </div>

          {/* Permission Matrix Grid */}
          <div className="rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)] overflow-hidden">
            <div className="px-6 py-4 border-b border-[#22314D]/60 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-[#3DD9C4]" />
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Role Granted Permissions</h3>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Read-Only Explorer</span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-[#0A1020]/90 text-[#8B99B8] font-mono border-b border-[#22314D]">
                  <tr>
                    <th className="px-6 py-3 min-w-[240px]">PERMISSION CODE</th>
                    <th className="px-4 py-3">MODULE</th>
                    {roles.map((r) => (
                      <th key={r} className="px-4 py-3 text-center">
                        {r}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-[#22314D]/60 text-[#E7EDF7]">
                  {filteredCatalog.map((perm) => (
                    <tr key={perm.code} className="hover:bg-[#16233A]/40 transition-all">
                      <td className="px-6 py-3">
                        <p className="font-mono text-xs text-[#3DD9C4] font-bold">{perm.code}</p>
                        <p className="text-[11px] text-[#8B99B8] mt-0.5">{perm.description}</p>
                      </td>
                      <td className="px-4 py-3">
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#0A1020] text-[#8B99B8] border border-[#22314D]">
                          {perm.module}
                        </span>
                      </td>
                      {roles.map((r) => {
                        const granted = hasPermissionMapping(r, perm.code);
                        return (
                          <td key={r} className="px-4 py-3 text-center">
                            {granted ? (
                              <div className="w-5 h-5 rounded-full bg-[#34D399]/20 text-[#34D399] border border-[#34D399]/40 flex items-center justify-center mx-auto">
                                <Check className="w-3 h-3 stroke-[3]" />
                              </div>
                            ) : (
                              <div className="w-5 h-5 rounded-full bg-[#0A1020] text-[#8B99B8]/40 flex items-center justify-center mx-auto">
                                <X className="w-3 h-3" />
                              </div>
                            )}
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="p-4 rounded-xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] flex items-center gap-3 text-xs text-[#8B99B8]">
            <Info className="w-4 h-4 text-[#3DD9C4] shrink-0" />
            <span>
              Permission evaluation is enforced server-side via <code className="text-[#3DD9C4] font-mono">PermissionEvaluatorService</code> and logged to immutable audit streams.
            </span>
          </div>
        </main>
      </div>
    </div>
  );
}
