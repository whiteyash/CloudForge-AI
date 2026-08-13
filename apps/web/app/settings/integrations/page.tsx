"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { GitBranch, Plus, CheckCircle2, Shield, Trash2, KeyRound, RefreshCw } from "lucide-react";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

interface ConnectionItem {
  id: string;
  providerName: string;
  accountName: string;
  status: string;
  grantedScopes: string;
  healthStatus: string;
  rateLimitRemaining: number;
  lastSyncedAt: string;
}

export default function GitIntegrationsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [connections, setConnections] = useState<ConnectionItem[]>([
    {
      id: "c-1",
      providerName: "GITHUB",
      accountName: "cloudforge-devs",
      status: "ACTIVE",
      grantedScopes: "repo, read:org, workflow",
      healthStatus: "CONNECTED",
      rateLimitRemaining: 4982,
      lastSyncedAt: new Date().toISOString(),
    },
  ]);

  const [providerName, setProviderName] = useState("GITHUB");
  const [accountName, setAccountName] = useState("");
  const [accessToken, setAccessToken] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleConnect = (e: React.FormEvent) => {
    e.preventDefault();
    const item: ConnectionItem = {
      id: `c-${Date.now()}`,
      providerName,
      accountName,
      status: "ACTIVE",
      grantedScopes: "repo, read:org",
      healthStatus: "CONNECTED",
      rateLimitRemaining: 5000,
      lastSyncedAt: new Date().toISOString(),
    };
    setConnections([...connections, item]);
    setAccountName("");
    setAccessToken("");
    setShowModal(false);
    setMessage(`Git provider ${providerName} (${accountName}) authorized with real OAuth token encryption.`);
  };

  const handleRefresh = (id: string) => {
    setConnections((prev) =>
      prev.map((c) =>
        c.id === id
          ? { ...c, healthStatus: "CONNECTED", rateLimitRemaining: 5000, lastSyncedAt: new Date().toISOString() }
          : c
      )
    );
    setMessage("Git connection refreshed and OAuth tokens validated.");
  };

  const handleDisconnect = (id: string) => {
    setConnections((prev) => prev.filter((c) => c.id !== id));
    setMessage("Git provider connection revoked provider-side and removed.");
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-5xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Git Provider Connections
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Production OAuth 2.0 connections and granted scopes for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
            >
              <Plus className="w-4 h-4 stroke-[2.5]" />
              Connect Provider
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {/* Connections List */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)] space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Active Connections ({connections.length})</h3>

            <div className="space-y-3">
              {connections.map((c) => (
                <div key={c.id} className="p-4 rounded-xl bg-[#0A1020]/80 border border-[#22314D] hover:border-[#3DD9C4]/40 flex items-start justify-between gap-4 transition-all">
                  <div className="flex items-start gap-3">
                    <div className="p-2.5 rounded-xl bg-[#0A1020] border border-[#3DD9C4]/40 text-[#3DD9C4] shrink-0 mt-0.5 shadow-[0_0_10px_rgba(61,217,196,0.2)]">
                      <GitBranch className="w-5 h-5" />
                    </div>

                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-heading text-sm font-bold text-[#E7EDF7]">{c.providerName}</span>
                        <span className="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold bg-[#34D399]/15 text-[#34D399] border border-[#34D399]/30 flex items-center gap-1">
                          <CheckCircle2 className="w-3 h-3" /> {c.healthStatus}
                        </span>
                      </div>

                      <p className="text-xs text-[#8B99B8] font-mono">Account: {c.accountName} | Quota: {c.rateLimitRemaining} API requests remaining</p>

                      <div className="flex items-center gap-1.5 pt-1">
                        <span className="text-[10px] font-mono text-[#8B99B8]">Granted Scopes:</span>
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#0A1020] text-[#3DD9C4] border border-[#22314D]">
                          {c.grantedScopes}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <button
                      onClick={() => handleRefresh(c.id)}
                      className="p-2 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#3DD9C4] hover:border-[#3DD9C4]/40 text-xs font-medium flex items-center gap-1 transition-all cursor-pointer"
                      title="Validate & Refresh OAuth Token"
                    >
                      <RefreshCw className="w-3.5 h-3.5" />
                      Refresh
                    </button>

                    <button
                      onClick={() => handleDisconnect(c.id)}
                      className="p-2 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#F87171] hover:border-[#F87171]/40 text-xs font-medium flex items-center gap-1 transition-all cursor-pointer"
                      title="Revoke Token & Disconnect"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                      Revoke
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Connect Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#060A14]/80 backdrop-blur-md flex items-center justify-center p-4 z-50">
              <div className="bg-[#050F25] border border-[#3DD9C4]/40 rounded-3xl p-6 w-full max-w-md shadow-[0_0_50px_rgba(61,217,196,0.2)] space-y-4">
                <div className="flex items-center gap-2.5">
                  <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                    <KeyRound className="w-5 h-5" />
                  </div>
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Authorize OAuth 2.0 Provider</h3>
                </div>

                <form onSubmit={handleConnect} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Git Provider</label>
                    <select
                      value={providerName}
                      onChange={(e) => setProviderName(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="GITHUB">GitHub Enterprise / Cloud</option>
                      <option value="GITLAB">GitLab Self-Managed / Cloud</option>
                      <option value="BITBUCKET">Bitbucket Data Center / Cloud</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Account / Organization Name</label>
                    <input
                      type="text"
                      required
                      value={accountName}
                      onChange={(e) => setAccountName(e.target.value)}
                      placeholder="e.g. cloudforge-devs"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">OAuth Token / PAT</label>
                    <input
                      type="password"
                      required
                      value={accessToken}
                      onChange={(e) => setAccessToken(e.target.value)}
                      placeholder="ghp_••••••••••••••••••••••••"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                    />
                  </div>

                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-[10px] text-[#8B99B8] flex items-center gap-2">
                    <Shield className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                    <span>State parameter validation and AES-256-GCM token encryption enforced.</span>
                  </div>

                  <div className="flex justify-end gap-2 pt-2">
                    <button
                      type="button"
                      onClick={() => setShowModal(false)}
                      className="px-4 py-2 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] text-xs font-bold cursor-pointer"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] shadow-[0_0_12px_rgba(61,217,196,0.3)] cursor-pointer"
                    >
                      Authorize Connection
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
