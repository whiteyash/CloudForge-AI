"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Users2, Plus, CheckCircle2, UserCheck } from "lucide-react";

interface TeamItem {
  id: string;
  name: string;
  description: string;
  memberCount: number;
}

export default function ProjectTeamsPage() {
  const [teams, setTeams] = useState<TeamItem[]>([
    { id: "t-1", name: "Core Platform Team", description: "Primary backend engineers and cloud architects", memberCount: 4 },
    { id: "t-[id]", name: "DevOps & SRE", description: "Infrastructure, CI/CD pipelines, and observability", memberCount: 3 },
  ]);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleCreateTeam = (e: React.FormEvent) => {
    e.preventDefault();
    const item: TeamItem = {
      id: `t-${Date.now()}`,
      name,
      description,
      memberCount: 1,
    };
    setTeams([...teams, item]);
    setName("");
    setDescription("");
    setShowModal(false);
    setMessage(`Team ${name} created successfully.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Teams</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Organize team members, assign permissions, and track team-level activity</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Create Team
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Teams Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {teams.map((t) => (
              <div key={t.id} className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex flex-col justify-between space-y-4">
                <div>
                  <div className="flex items-center justify-between pb-3 border-b border-[#22314D]">
                    <div className="flex items-center gap-2">
                      <Users2 className="w-5 h-5 text-[#3DD9C4]" />
                      <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">{t.name}</h3>
                    </div>
                  </div>
                  <p className="text-xs text-[#8B99B8] mt-3">{t.description}</p>
                </div>

                <div className="pt-3 border-t border-[#22314D] flex items-center justify-between text-[10px] font-mono text-[#8B99B8]">
                  <span className="flex items-center gap-1">
                    <UserCheck className="w-3.5 h-3.5 text-[#3DD9C4]" /> {t.memberCount} Members
                  </span>
                  <span>ACTIVE TEAM</span>
                </div>
              </div>
            ))}
          </div>

          {/* Create Team Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Create Project Team</h3>

                <form onSubmit={handleCreateTeam} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Team Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. Frontend Engineers"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Description</label>
                    <textarea
                      rows={2}
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      placeholder="Team responsibility..."
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
                      Create Team
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
