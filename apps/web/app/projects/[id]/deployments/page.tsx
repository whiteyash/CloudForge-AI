"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Rocket, Plus, CheckCircle2, ShieldCheck, Activity } from "lucide-react";
import Link from "next/link";

interface DeploymentItem {
  id: string;
  targetName: string;
  strategy: string;
  status: string;
  requestedBy: string;
  approvedBy: string | null;
  idempotencyKey: string;
}

export default function DeploymentsPage() {
  const [deployments, setDeployments] = useState<DeploymentItem[]>([
    {
      id: "dep-1",
      targetName: "STAGING",
      strategy: "ROLLING",
      status: "SUCCEEDED",
      requestedBy: "lead@cloudforge.ai",
      approvedBy: null,
      idempotencyKey: "idem-staging-101",
    },
    {
      id: "dep-2",
      targetName: "PRODUCTION",
      strategy: "CANARY",
      status: "PENDING_APPROVAL",
      requestedBy: "lead@cloudforge.ai",
      approvedBy: null,
      idempotencyKey: "idem-prod-202",
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [targetName, setTargetName] = useState("STAGING");
  const [strategy, setStrategy] = useState("ROLLING");
  const [message, setMessage] = useState<string | null>(null);

  const handleCreateDeployment = (e: React.FormEvent) => {
    e.preventDefault();
    const isProd = targetName === "PRODUCTION";
    const item: DeploymentItem = {
      id: `dep-${Date.now()}`,
      targetName,
      strategy,
      status: isProd ? "PENDING_APPROVAL" : "SUCCEEDED",
      requestedBy: "lead@cloudforge.ai",
      approvedBy: null,
      idempotencyKey: `idem-${Date.now()}`,
    };
    setDeployments([...deployments, item]);
    setShowModal(false);
    setMessage(`Deployment to ${targetName} (${strategy}) requested with idempotency protection.`);
  };

  const handleApprove = (id: string) => {
    setDeployments((prev) =>
      prev.map((d) =>
        d.id === id ? { ...d, status: "SUCCEEDED", approvedBy: "lead@cloudforge.ai" } : d
      )
    );
    setMessage("Production deployment approved and executed successfully.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Deployments & Releases</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Idempotent deployment orchestration across Rolling, Blue/Green, and Canary strategies with RBAC approval gates</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              New Deployment
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Deployments Roster */}
          <div className="space-y-4">
            {deployments.map((d) => (
              <div key={d.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    <Rocket className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/deployments/${d.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        Target: {d.targetName}
                      </Link>
                      <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                        d.status === "SUCCEEDED" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                        d.status === "PENDING_APPROVAL" ? "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                      }`}>
                        {d.status}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-mono">
                      Strategy: {d.strategy} | Requested by: {d.requestedBy}
                    </p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">
                      Idempotency Key: {d.idempotencyKey} {d.approvedBy && `| Approved by: ${d.approvedBy}`}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {d.status === "PENDING_APPROVAL" && (
                    <button
                      onClick={() => handleApprove(d.id)}
                      className="px-3 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] text-xs font-semibold flex items-center gap-1.5 hover:bg-[#34D399] transition-all"
                    >
                      <ShieldCheck className="w-3.5 h-3.5" />
                      Approve Release
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>

          {/* New Deployment Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <Activity className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Trigger Deployment</h3>
                </div>

                <form onSubmit={handleCreateDeployment} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Target Environment</label>
                    <select
                      value={targetName}
                      onChange={(e) => setTargetName(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="STAGING">Staging Environment</option>
                      <option value="PRODUCTION">Production (Protected Gate)</option>
                      <option value="DEV">Development Environment</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Deployment Strategy</label>
                    <select
                      value={strategy}
                      onChange={(e) => setStrategy(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="ROLLING">Rolling Update Strategy</option>
                      <option value="CANARY">Canary Deployment Strategy</option>
                      <option value="BLUE_GREEN">Blue/Green Strategy</option>
                      <option value="RECREATE">Recreate Strategy</option>
                    </select>
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
                      Trigger Deployment
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
