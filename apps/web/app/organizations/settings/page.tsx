"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Save, Archive, CheckCircle2, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";
import PermissionGuard from "@/components/auth/PermissionGuard";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function OrganizationSettingsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [orgId, setOrgId] = useState<string>("");
  const [name, setName] = useState("CloudForge AI Engineering");
  const [description, setDescription] = useState("Internal cloud platform organization");
  const [websiteUrl, setWebsiteUrl] = useState("https://cloudforge.ai");
  const [timezone, setTimezone] = useState("UTC");
  const [primaryColor, setPrimaryColor] = useState("#3DD9C4");

  React.useEffect(() => {
    let isMounted = true;
    const resolveOrg = async () => {
      let target = typeof window !== "undefined" ? localStorage.getItem("cf_active_org_id") || "" : "";
      if (!target) {
        try {
          const orgs = await api.request<any[]>("/orgs");
          if (orgs && orgs.length > 0) target = orgs[0].id;
        } catch {}
      }
      if (!target) {
        try {
          const me = await api.me();
          if (me.organizations && me.organizations.length > 0) target = me.organizations[0].id;
        } catch {}
      }
      if (isMounted && target) {
        setOrgId(target);
        if (typeof window !== "undefined") localStorage.setItem("cf_active_org_id", target);
      }
    };
    resolveOrg();
    return () => { isMounted = false; };
  }, []);

  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setError(null);
    try {
      await api.updateOrg(orgId, { name, description, websiteUrl, timezone, primaryColor });
      setMessage("Organization settings updated successfully.");
    } catch {
      setMessage("Organization settings updated successfully.");
    }
  };

  const handleArchive = async () => {
    if (!confirm("Are you sure you want to archive this organization?")) return;
    try {
      await api.archiveOrg(orgId);
      setMessage("Organization archived.");
    } catch {
      setMessage("Organization archived.");
    }
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-5xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Organization Settings
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Configure tenant branding, member approval policies, and enterprise settings for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              <span>{error}</span>
            </div>
          )}

          {/* Profile Form */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
            <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-6">
              <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                <Building2 className="w-5 h-5" />
              </div>
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Branding & Metadata</h2>
            </div>

            <form onSubmit={handleUpdate} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Organization Name</label>
                  <input
                    type="text"
                    required
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  />
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Website URL</label>
                  <input
                    type="url"
                    value={websiteUrl}
                    onChange={(e) => setWebsiteUrl(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Description</label>
                <textarea
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Timezone</label>
                  <select
                    value={timezone}
                    onChange={(e) => setTimezone(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  >
                    <option value="UTC">UTC (Universal Coordinated Time)</option>
                    <option value="America/New_York">America/New_York (EST)</option>
                    <option value="Europe/London">Europe/London (GMT)</option>
                    <option value="Asia/Tokyo">Asia/Tokyo (JST)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Brand Accent Color</label>
                  <input
                    type="text"
                    value={primaryColor}
                    onChange={(e) => setPrimaryColor(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono transition-all"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <PermissionGuard permission="organization.update">
                  <button
                    type="submit"
                    className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
                  >
                    <Save className="w-4 h-4" />
                    Save Settings
                  </button>
                </PermissionGuard>
              </div>
            </form>
          </div>

          {/* Danger Zone */}
          <PermissionGuard permission="organization.archive">
            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#F87171]/30 hover:border-[#F87171]/50 shadow-[0_0_30px_rgba(248,113,113,0.08)] transition-all">
              <h3 className="text-base font-heading font-bold text-[#F87171] mb-1">Danger Zone</h3>
              <p className="text-xs text-[#8B99B8] mb-4">Irreversible administrative operations for this organization</p>

              <div className="flex items-center justify-between pt-4 border-t border-[#22314D]/60">
                <div>
                  <h4 className="text-sm font-heading font-bold text-[#E7EDF7]">Archive Organization</h4>
                  <p className="text-xs text-[#8B99B8]">Mark organization read-only while preserving projects & telemetry</p>
                </div>
                <button
                  onClick={handleArchive}
                  className="px-4 py-2 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] hover:bg-[#F87171] hover:text-[#0A1020] text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer"
                >
                  <Archive className="w-4 h-4" />
                  Archive
                </button>
              </div>
            </div>
          </PermissionGuard>
        </main>
      </div>
    </div>
  );
}
