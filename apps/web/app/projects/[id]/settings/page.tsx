"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Settings, Save, CheckCircle2, ShieldAlert, Archive, Trash2 } from "lucide-react";

export default function ProjectSettingsPage() {
  const [name, setName] = useState("cloudforge-api-gateway");
  const [description, setDescription] = useState("Core API Gateway microservice infrastructure");
  const [labels, setLabels] = useState("production, backend, golang");
  const [visibility, setVisibility] = useState("PRIVATE");
  const [defaultBranch, setDefaultBranch] = useState("main");
  const [message, setMessage] = useState<string | null>(null);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setMessage("Project settings updated successfully.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-4xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Settings</h1>
            <p className="text-xs text-[#8B99B8] mt-1">General project configuration, metadata, branding, and danger zone actions</p>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* General Settings */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-4">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D]">
              <Settings className="w-5 h-5 text-[#3DD9C4]" />
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">General Configuration</h2>
            </div>

            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Project Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Description</label>
                <textarea
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Visibility</label>
                  <select
                    value={visibility}
                    onChange={(e) => setVisibility(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  >
                    <option value="PRIVATE">Private (Organization Members Only)</option>
                    <option value="INTERNAL">Internal (Authenticated Platform Users)</option>
                    <option value="PUBLIC">Public (Read-Only Public Access)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Default Branch</label>
                  <input
                    type="text"
                    required
                    value={defaultBranch}
                    onChange={(e) => setDefaultBranch(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Labels & Tags (Comma Separated)</label>
                <input
                  type="text"
                  value={labels}
                  onChange={(e) => setLabels(e.target.value)}
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                />
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5"
                >
                  <Save className="w-4 h-4" />
                  Save Settings
                </button>
              </div>
            </form>
          </div>

          {/* Danger Zone */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#F87171]/40 shadow-lg space-y-4">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D]">
              <ShieldAlert className="w-5 h-5 text-[#F87171]" />
              <h2 className="text-base font-heading font-bold text-[#F87171]">Danger Zone</h2>
            </div>

            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="text-sm font-heading font-bold text-[#E7EDF7]">Archive Project</h4>
                  <p className="text-xs text-[#8B99B8]">Mark project as read-only and suspend active pipeline deployments</p>
                </div>
                <button className="px-3.5 py-1.5 rounded-xl bg-[#16233A] border border-[#22314D] text-[#FBBF24] hover:border-[#FBBF24]/40 text-xs font-bold flex items-center gap-1.5">
                  <Archive className="w-3.5 h-3.5" /> Archive
                </button>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-[#22314D]">
                <div>
                  <h4 className="text-sm font-heading font-bold text-[#F87171]">Delete Project</h4>
                  <p className="text-xs text-[#8B99B8]">Permanently delete project workspace, variables, and environment targets</p>
                </div>
                <button className="px-3.5 py-1.5 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] hover:bg-[#F87171]/20 text-xs font-bold flex items-center gap-1.5">
                  <Trash2 className="w-3.5 h-3.5" /> Delete Project
                </button>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
