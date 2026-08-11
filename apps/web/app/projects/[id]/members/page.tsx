"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Users, UserPlus, CheckCircle2, Shield } from "lucide-react";

interface MemberItem {
  id: string;
  name: string;
  email: string;
  role: string;
}

export default function ProjectMembersPage() {
  const [members, setMembers] = useState<MemberItem[]>([
    { id: "m-1", name: "Platform Engineer", email: "engineer@cloudforge.ai", role: "ADMIN" },
    { id: "m-2", name: "DevOps Specialist", email: "devops@cloudforge.ai", role: "DEVELOPER" },
  ]);

  const [email, setEmail] = useState("");
  const [role, setRole] = useState("DEVELOPER");
  const [showModal, setShowModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleInvite = (e: React.FormEvent) => {
    e.preventDefault();
    const item: MemberItem = {
      id: `m-${Date.now()}`,
      name: email.split("@")[0],
      email,
      role,
    };
    setMembers([...members, item]);
    setEmail("");
    setShowModal(false);
    setMessage(`Invitation sent to ${email}.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Project Members & Roles</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Project-level membership roster, RBAC roles, and inherited organization permissions</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <UserPlus className="w-4 h-4" />
              Invite Member
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Members Table */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Project Members ({members.length})</h3>

            <div className="space-y-3">
              {members.map((m) => (
                <div key={m.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
                      <Users className="w-4 h-4" />
                    </div>
                    <div>
                      <span className="font-heading text-xs font-bold text-[#E7EDF7]">{m.name}</span>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">{m.email}</p>
                    </div>
                  </div>

                  <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center gap-1">
                    <Shield className="w-3 h-3" /> {m.role}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Invite Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Invite Project Member</h3>

                <form onSubmit={handleInvite} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Email Address</label>
                    <input
                      type="email"
                      required
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="developer@cloudforge.ai"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Project Role</label>
                    <select
                      value={role}
                      onChange={(e) => setRole(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="DEVELOPER">DEVELOPER (Deploy & Manage Vars)</option>
                      <option value="ADMIN">ADMIN (Full Project Control)</option>
                      <option value="VIEWER">VIEWER (Read-Only Access)</option>
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
                      Send Invitation
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
