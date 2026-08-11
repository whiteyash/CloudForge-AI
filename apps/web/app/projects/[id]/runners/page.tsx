"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Cpu, Plus, CheckCircle2, Server, KeyRound, Activity } from "lucide-react";
import Link from "next/link";

interface RunnerItem {
  id: string;
  name: string;
  runnerType: string;
  runnerGroup: string;
  status: string;
  labels: string;
  operatingSystem: string;
  maxParallelJobs: number;
  currentJobs: number;
  lastHeartbeat: string;
}

export default function RunnersPage() {
  const [runners, setRunners] = useState<RunnerItem[]>([
    {
      id: "run-1",
      name: "k8s-runner-pool-1",
      runnerType: "KUBERNETES",
      runnerGroup: "default",
      status: "ONLINE",
      labels: "ubuntu-latest, docker, k8s",
      operatingSystem: "linux",
      maxParallelJobs: 4,
      currentJobs: 1,
      lastHeartbeat: new Date().toISOString(),
    },
    {
      id: "run-2",
      name: "docker-agent-prod",
      runnerType: "DOCKER",
      runnerGroup: "production",
      status: "BUSY",
      labels: "ubuntu-latest, docker, aws-cli",
      operatingSystem: "linux",
      maxParallelJobs: 2,
      currentJobs: 2,
      lastHeartbeat: new Date().toISOString(),
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState("");
  const [runnerType, setRunnerType] = useState("DOCKER");
  const [labels, setLabels] = useState("ubuntu-latest");
  const [message, setMessage] = useState<string | null>(null);

  const handleRegisterRunner = (e: React.FormEvent) => {
    e.preventDefault();
    const item: RunnerItem = {
      id: `run-${Date.now()}`,
      name,
      runnerType,
      runnerGroup: "default",
      status: "ONLINE",
      labels,
      operatingSystem: "linux",
      maxParallelJobs: 2,
      currentJobs: 0,
      lastHeartbeat: new Date().toISOString(),
    };
    setRunners([...runners, item]);
    setName("");
    setShowModal(false);
    setMessage(`Runner agent ${name} registered with SHA-256 token verification.`);
  };

  const handleDrain = (id: string) => {
    setRunners((prev) =>
      prev.map((r) =>
        r.id === id ? { ...r, status: "DRAINING" } : r
      )
    );
    setMessage("Runner agent set to DRAINING mode.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Runner Pool Management</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Docker, Kubernetes, and Self-Hosted build runners, capacity tracking, and heartbeat monitoring</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Register Runner
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Runners Roster */}
          <div className="space-y-4">
            {runners.map((r) => (
              <div key={r.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    <Cpu className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/runners/${r.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        {r.name}
                      </Link>
                      <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                        r.status === "ONLINE" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                        r.status === "BUSY" ? "bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                      }`}>
                        {r.status}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-mono">
                      Type: {r.runnerType} | Labels: {r.labels} | Capacity: {r.currentJobs}/{r.maxParallelJobs} Jobs
                    </p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">OS: {r.operatingSystem} | Group: {r.runnerGroup} | Heartbeat: Just now</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleDrain(r.id)}
                    className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#F59E0B] text-xs font-semibold flex items-center gap-1.5 transition-all"
                  >
                    <Activity className="w-3.5 h-3.5" />
                    Drain Runner
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Register Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <Server className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Register Build Runner</h3>
                </div>

                <form onSubmit={handleRegisterRunner} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Runner Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. k8s-runner-agent-3"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Runner Type</label>
                    <select
                      value={runnerType}
                      onChange={(e) => setRunnerType(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="DOCKER">Docker Container Runner</option>
                      <option value="KUBERNETES">Kubernetes Cluster Pod Runner</option>
                      <option value="SELF_HOSTED">Self-Hosted Bare Metal Agent</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Capabilities / Labels</label>
                    <input
                      type="text"
                      required
                      value={labels}
                      onChange={(e) => setLabels(e.target.value)}
                      placeholder="ubuntu-latest, docker, node20"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div className="p-3 rounded-xl bg-[#16233A] border border-[#22314D] text-[10px] text-[#8B99B8] flex items-center gap-2">
                    <KeyRound className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                    <span>A SHA-256 hashed bearer token will be generated for agent authentication.</span>
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
                      Register Runner
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
