"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Play, Plus, CheckCircle2, PlayCircle, Layers } from "lucide-react";
import Link from "next/link";

interface PipelineItem {
  id: string;
  name: string;
  description: string;
  status: string;
  lastRunNumber: number;
  lastRunStatus: string;
}

export default function PipelinesPage() {
  const [pipelines, setPipelines] = useState<PipelineItem[]>([
    {
      id: "pipe-1",
      name: "main-build-ci",
      description: "Continuous Integration & Automated Test Pipeline for main branch",
      status: "ACTIVE",
      lastRunNumber: 42,
      lastRunStatus: "SUCCEEDED",
    },
    {
      id: "pipe-2",
      name: "production-deploy-cd",
      description: "Protected Production Deployment Pipeline with Approval Gate",
      status: "ACTIVE",
      lastRunNumber: 18,
      lastRunStatus: "PENDING_APPROVAL",
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  const handleCreatePipeline = (e: React.FormEvent) => {
    e.preventDefault();
    const item: PipelineItem = {
      id: `pipe-${Date.now()}`,
      name,
      description,
      status: "ACTIVE",
      lastRunNumber: 1,
      lastRunStatus: "QUEUED",
    };
    setPipelines([...pipelines, item]);
    setName("");
    setDescription("");
    setShowModal(false);
    setMessage(`Pipeline ${name} created and registered in project workspace.`);
  };

  const handleTrigger = (id: string, pipeName: string) => {
    setPipelines((prev) =>
      prev.map((p) =>
        p.id === id ? { ...p, lastRunNumber: p.lastRunNumber + 1, lastRunStatus: "RUNNING" } : p
      )
    );
    setMessage(`Pipeline ${pipeName} run triggered manually.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">CI/CD Pipelines</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Enterprise automated build, test, and release pipeline execution DAGs</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Create Pipeline
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Pipelines Roster */}
          <div className="space-y-4">
            {pipelines.map((p) => (
              <div key={p.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    <Layers className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/pipelines/${p.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        {p.name}
                      </Link>
                      <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                        p.lastRunStatus === "SUCCEEDED" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                        p.lastRunStatus === "PENDING_APPROVAL" ? "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30" : "bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30"
                      }`}>
                        {p.lastRunStatus}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-sans">{p.description}</p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-1">Last Run: #{p.lastRunNumber} | Trigger: Manual / Git Push</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleTrigger(p.id, p.name)}
                    className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#3DD9C4] hover:text-[#0A1020] text-xs font-semibold flex items-center gap-1.5 transition-all"
                  >
                    <Play className="w-3.5 h-3.5 fill-current" />
                    Run Pipeline
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Create Pipeline Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <PlayCircle className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Create Pipeline Definition</h3>
                </div>

                <form onSubmit={handleCreatePipeline} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Pipeline Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. release-build-pipeline"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Description</label>
                    <input
                      type="text"
                      required
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder="e.g. Build, test, and package release artifacts"
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
                      Create Pipeline
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
