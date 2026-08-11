"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { ShieldCheck, Search, Check, X, Info } from "lucide-react";
import { api, PermissionCatalogResponse, RolePermissionMappingResponse } from "@/lib/api";

export default function PermissionsPage() {
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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Authorization & Permission Matrix</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Granular permission catalog mapped across CloudForge 6-Role Production RBAC Matrix</p>
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
                className="w-full bg-[#111B2E] border border-[#22314D] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
              />
            </div>

            <select
              value={selectedModule}
              onChange={(e) => setSelectedModule(e.target.value)}
              className="bg-[#111B2E] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
            >
              <option value="ALL">All Modules</option>
              {modules.map((mod) => (
                <option key={mod} value={mod}>
                  {mod}
                </option>
              ))}
            </select>
          </div>

          {/* Permission Matrix Grid */}
          <div className="rounded-2xl bg-[#111B2E] border border-[#22314D] overflow-hidden shadow-lg">
            <div className="px-6 py-4 border-b border-[#22314D] flex items-center justify-between">
              <div className="flex items-center gap-2">
                <ShieldCheck className="w-5 h-5 text-[#3DD9C4]" />
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Role Granted Permissions</h3>
              </div>
              <span className="text-xs font-mono text-[#8B99B8]">Read-Only Explorer</span>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-[#0A1020] text-[#8B99B8] font-mono border-b border-[#22314D]">
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
                    <tr key={perm.code} className="hover:bg-[#16233A]/50 transition-all">
                      <td className="px-6 py-3">
                        <p className="font-mono text-xs text-[#3DD9C4] font-bold">{perm.code}</p>
                        <p className="text-[11px] text-[#8B99B8] mt-0.5">{perm.description}</p>
                      </td>
                      <td className="px-4 py-3">
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#16233A] text-[#8B99B8] border border-[#22314D]">
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
                              <div className="w-5 h-5 rounded-full bg-[#16233A] text-[#8B99B8]/40 flex items-center justify-center mx-auto">
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

          <div className="p-4 rounded-xl bg-[#16233A] border border-[#22314D] flex items-center gap-3 text-xs text-[#8B99B8]">
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
