"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { KeyRound, Plus, Lock, Eye, EyeOff, CheckCircle2, Shield } from "lucide-react";

interface VariableItem {
  id: string;
  key: string;
  value: string;
  isMasked: boolean;
  isProtected: boolean;
}

export default function ProjectVariablesPage() {
  const [variables, setVariables] = useState<VariableItem[]>([
    { id: "v-1", key: "DATABASE_URL", value: "postgresql://pg.cloudforge.internal:5432/main", isMasked: true, isProtected: true },
    { id: "v-2", key: "LOG_LEVEL", value: "INFO", isMasked: false, isProtected: false },
    { id: "v-3", key: "JWT_EXPIRATION_SECONDS", value: "86400", isMasked: false, isProtected: true },
  ]);

  const [showValues, setShowValues] = useState<Record<string, boolean>>({});
  const [newKey, setNewKey] = useState("");
  const [newValue, setNewValue] = useState("");
  const [isMasked, setIsMasked] = useState(false);
  const [isProtected, setIsProtected] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleAddVariable = (e: React.FormEvent) => {
    e.preventDefault();
    const item: VariableItem = {
      id: `v-${Date.now()}`,
      key: newKey,
      value: newValue,
      isMasked,
      isProtected,
    };
    setVariables([...variables, item]);
    setNewKey("");
    setNewValue("");
    setIsMasked(false);
    setIsProtected(false);
    setMessage(`Variable ${newKey} added successfully.`);
  };

  const toggleShowValue = (id: string) => {
    setShowValues((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Environment & Project Variables</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Configure masked secrets, runtime configuration, and protected deployment variables</p>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Add Variable Form */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
              <KeyRound className="w-5 h-5 text-[#3DD9C4]" />
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Add Variable</h2>
            </div>

            <form onSubmit={handleAddVariable} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Variable Key</label>
                  <input
                    type="text"
                    required
                    value={newKey}
                    onChange={(e) => setNewKey(e.target.value)}
                    placeholder="e.g. API_SECRET_KEY"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  />
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Variable Value</label>
                  <input
                    type="text"
                    required
                    value={newValue}
                    onChange={(e) => setNewValue(e.target.value)}
                    placeholder="Value..."
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  />
                </div>
              </div>

              <div className="flex items-center gap-6 pt-2">
                <label className="flex items-center gap-2 cursor-pointer text-xs text-[#E7EDF7]">
                  <input
                    type="checkbox"
                    checked={isMasked}
                    onChange={(e) => setIsMasked(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                  <span>Mask Variable (Hide value in logs)</span>
                </label>

                <label className="flex items-center gap-2 cursor-pointer text-xs text-[#E7EDF7]">
                  <input
                    type="checkbox"
                    checked={isProtected}
                    onChange={(e) => setIsProtected(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                  <span>Protected Variable (Require elevated RBAC role)</span>
                </label>
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5"
                >
                  <Plus className="w-4 h-4" />
                  Save Variable
                </button>
              </div>
            </form>
          </div>

          {/* Variables Table */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Project Variables ({variables.length})</h3>

            <div className="space-y-3">
              {variables.map((v) => (
                <div key={v.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <span className="font-mono text-xs font-bold text-[#3DD9C4]">{v.key}</span>

                    {v.isMasked && (
                      <span className="px-1.5 py-0.2 rounded text-[9px] font-mono bg-[#FBBF24]/10 text-[#FBBF24] border border-[#FBBF24]/30 flex items-center gap-1">
                        <Lock className="w-2.5 h-2.5" /> MASKED
                      </span>
                    )}

                    {v.isProtected && (
                      <span className="px-1.5 py-0.2 rounded text-[9px] font-mono bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30 flex items-center gap-1">
                        <Shield className="w-2.5 h-2.5" /> PROTECTED
                      </span>
                    )}
                  </div>

                  <div className="flex items-center gap-3 font-mono text-xs text-[#8B99B8]">
                    <span>
                      {v.isMasked && !showValues[v.id] ? "••••••••••••••••" : v.value}
                    </span>

                    {v.isMasked && (
                      <button
                        onClick={() => toggleShowValue(v.id)}
                        className="text-[#8B99B8] hover:text-[#E7EDF7]"
                      >
                        {showValues[v.id] ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
