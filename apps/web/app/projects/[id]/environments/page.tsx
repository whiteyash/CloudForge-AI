"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Server, Plus, CheckCircle2, Shield, Power } from "lucide-react";
import Link from "next/link";

interface EnvironmentItem {
  id: string;
  name: string;
  environmentType: string;
  description: string;
  status: string;
  isProtected: boolean;
  isMaintenanceMode: boolean;
  isFrozen: boolean;
  healthStatus: string;
}

export default function EnvironmentsPage() {
  const [environments, setEnvironments] = useState<EnvironmentItem[]>([
    {
      id: "env-1",
      name: "Staging-US-East",
      environmentType: "STAGING",
      description: "QA & Integration Staging environment bound to k8s-staging namespace",
      status: "ACTIVE",
      isProtected: false,
      isMaintenanceMode: false,
      isFrozen: false,
      healthStatus: "HEALTHY",
    },
    {
      id: "env-2",
      name: "Production-Global",
      environmentType: "PRODUCTION",
      description: "Protected Global Production Environment with RBAC approval gates",
      status: "ACTIVE",
      isProtected: true,
      isMaintenanceMode: false,
      isFrozen: false,
      healthStatus: "HEALTHY",
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState("");
  const [environmentType, setEnvironmentType] = useState("STAGING");
  const [description, setDescription] = useState("");
  const [isProtected, setIsProtected] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleCreateEnvironment = (e: React.FormEvent) => {
    e.preventDefault();
    const item: EnvironmentItem = {
      id: `env-${Date.now()}`,
      name,
      environmentType,
      description,
      status: "ACTIVE",
      isProtected,
      isMaintenanceMode: false,
      isFrozen: false,
      healthStatus: "HEALTHY",
    };
    setEnvironments([...environments, item]);
    setName("");
    setDescription("");
    setShowModal(false);
    setMessage(`Logical environment ${name} created and bound to target namespace.`);
  };

  const handleToggleMaintenance = (id: string) => {
    setEnvironments((prev) =>
      prev.map((e) =>
        e.id === id ? { ...e, isMaintenanceMode: !e.isMaintenanceMode, status: !e.isMaintenanceMode ? "MAINTENANCE" : "ACTIVE" } : e
      )
    );
    setMessage("Environment maintenance mode toggled.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Environment Management</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Logical deployment spaces, variable precedence rules, freeze windows, and maintenance mode controls</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Create Environment
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Environments Roster */}
          <div className="space-y-4">
            {environments.map((env) => (
              <div key={env.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    <Server className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/environments/${env.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        {env.name}
                      </Link>
                      <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                        env.status === "ACTIVE" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" : "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30"
                      }`}>
                        {env.status}
                      </span>
                      {env.isProtected && (
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center gap-1">
                          <Shield className="w-3 h-3" /> Protected
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-sans">{env.description}</p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-1">
                      Type: {env.environmentType} | Health: {env.healthStatus} {env.isFrozen && "| FREEZE WINDOW ACTIVE"}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleToggleMaintenance(env.id)}
                    className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#F59E0B] text-xs font-semibold flex items-center gap-1.5 transition-all"
                  >
                    <Power className="w-3.5 h-3.5 text-[#F59E0B]" />
                    {env.isMaintenanceMode ? "Disable Maintenance" : "Maintenance Mode"}
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Create Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <Server className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Create Logical Environment</h3>
                </div>

                <form onSubmit={handleCreateEnvironment} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Environment Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. Staging-US-East"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Environment Type</label>
                    <select
                      value={environmentType}
                      onChange={(e) => setEnvironmentType(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="DEVELOPMENT">Development Environment</option>
                      <option value="QA">QA Testing Environment</option>
                      <option value="STAGING">Staging Environment</option>
                      <option value="PRODUCTION">Production Environment</option>
                      <option value="CUSTOM">Custom Logical Space</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Description</label>
                    <input
                      type="text"
                      required
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder="e.g. Integration testing space bound to k8s namespace"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div className="flex items-center gap-2 pt-1">
                    <input
                      type="checkbox"
                      id="protected"
                      checked={isProtected}
                      onChange={(e) => setIsProtected(e.target.checked)}
                      className="rounded bg-[#0A1020] border-[#22314D] text-[#3DD9C4]"
                    />
                    <label htmlFor="protected" className="text-xs text-[#E7EDF7]">Protected Environment (Requires RBAC Approval Gate)</label>
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
                      Create Environment
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
