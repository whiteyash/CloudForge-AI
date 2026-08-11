"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Lock, Plus, CheckCircle2, Shield } from "lucide-react";

interface SecretItem {
  id: string;
  secretName: string;
  vaultPath: string;
  vaultKey: string;
  scope: string;
}

export default function ProjectSecretsPage() {
  const [secrets, setSecrets] = useState<SecretItem[]>([
    { id: "s-1", secretName: "STRIPE_API_SECRET", vaultPath: "secret/data/production/stripe", vaultKey: "api_key", scope: "PROD" },
    { id: "s-2", secretName: "AWS_ACCESS_KEY_ID", vaultPath: "secret/data/global/aws", vaultKey: "access_key", scope: "ALL_ENVIRONMENTS" },
  ]);

  const [secretName, setSecretName] = useState("");
  const [vaultPath, setVaultPath] = useState("");
  const [vaultKey, setVaultKey] = useState("");
  const [scope, setScope] = useState("ALL_ENVIRONMENTS");
  const [showModal, setShowModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleAddSecret = (e: React.FormEvent) => {
    e.preventDefault();
    const item: SecretItem = {
      id: `s-${Date.now()}`,
      secretName,
      vaultPath,
      vaultKey,
      scope,
    };
    setSecrets([...secrets, item]);
    setSecretName("");
    setVaultPath("");
    setVaultKey("");
    setShowModal(false);
    setMessage(`Secret reference ${secretName} registered.`);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Vault Secret References</h1>
              <p className="text-xs text-[#8B99B8] mt-1">HashiCorp Vault & AWS Secrets Manager integration paths and scope definitions</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Add Secret Reference
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Secrets List */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Secret References ({secrets.length})</h3>

            <div className="space-y-3">
              {secrets.map((s) => (
                <div key={s.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Lock className="w-5 h-5 text-[#FBBF24]" />
                    <div>
                      <span className="font-mono text-xs font-bold text-[#E7EDF7]">{s.secretName}</span>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Vault Path: {s.vaultPath} ({s.vaultKey})</p>
                    </div>
                  </div>

                  <span className="px-2 py-0.5 rounded text-[10px] font-mono font-semibold bg-[#FBBF24]/10 text-[#FBBF24] border border-[#FBBF24]/30 flex items-center gap-1">
                    <Shield className="w-3 h-3" /> {s.scope}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Add Secret Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Register Vault Secret Reference</h3>

                <form onSubmit={handleAddSecret} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Secret Alias Name</label>
                    <input
                      type="text"
                      required
                      value={secretName}
                      onChange={(e) => setSecretName(e.target.value)}
                      placeholder="e.g. STRIPE_API_SECRET"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Vault Path</label>
                    <input
                      type="text"
                      required
                      value={vaultPath}
                      onChange={(e) => setVaultPath(e.target.value)}
                      placeholder="secret/data/production/stripe"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Vault Key</label>
                    <input
                      type="text"
                      required
                      value={vaultKey}
                      onChange={(e) => setVaultKey(e.target.value)}
                      placeholder="api_key"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Scope</label>
                    <select
                      value={scope}
                      onChange={(e) => setScope(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="ALL_ENVIRONMENTS">All Environments</option>
                      <option value="PROD">Production Only</option>
                      <option value="STAGING">Staging Only</option>
                      <option value="DEV">Development Only</option>
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
                      Register Secret
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
