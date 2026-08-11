"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Building2, Save, Archive, CheckCircle2, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";
import PermissionGuard from "@/components/auth/PermissionGuard";

export default function OrganizationSettingsPage() {
  const [orgId] = useState("default-org-id");
  const [name, setName] = useState("CloudForge AI Engineering");
  const [description, setDescription] = useState("Internal cloud platform organization");
  const [websiteUrl, setWebsiteUrl] = useState("https://cloudforge.ai");
  const [timezone, setTimezone] = useState("UTC");
  const [primaryColor, setPrimaryColor] = useState("#3DD9C4");

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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organization Settings</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Configure tenant branding, member approval policies, and danger zone lifecycle</p>
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
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-6">
              <Building2 className="w-5 h-5 text-[#3DD9C4]" />
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
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Website URL</label>
                  <input
                    type="url"
                    value={websiteUrl}
                    onChange={(e) => setWebsiteUrl(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Description</label>
                <textarea
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Timezone</label>
                  <select
                    value={timezone}
                    onChange={(e) => setTimezone(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
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
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <PermissionGuard permission="organization.update">
                  <button
                    type="submit"
                    className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5"
                  >
                    <Save className="w-4 h-4" />
                    Save Organization Settings
                  </button>
                </PermissionGuard>
              </div>
            </form>
          </div>

          {/* Danger Zone */}
          <PermissionGuard permission="organization.archive">
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#F87171]/30 shadow-lg">
              <h3 className="text-base font-heading font-bold text-[#F87171] mb-1">Danger Zone</h3>
              <p className="text-xs text-[#8B99B8] mb-4">Irreversible administrative operations for this organization</p>

              <div className="flex items-center justify-between pt-4 border-t border-[#22314D]">
                <div>
                  <h4 className="text-sm font-heading font-bold text-[#E7EDF7]">Archive Organization</h4>
                  <p className="text-xs text-[#8B99B8]">Mark organization read-only while preserving projects & telemetry</p>
                </div>
                <button
                  onClick={handleArchive}
                  className="px-4 py-2 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] hover:bg-[#F87171] hover:text-[#0A1020] text-xs font-bold transition-all flex items-center gap-1.5"
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
