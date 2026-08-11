"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Radio, Plus, CheckCircle2, Shield, RefreshCw, KeyRound, AlertCircle } from "lucide-react";

interface WebhookItem {
  id: string;
  providerName: string;
  targetUrl: string;
  events: string;
  status: string;
}

interface EventItem {
  id: string;
  eventType: string;
  providerName: string;
  deliveryId: string;
  status: string;
  receivedAt: string;
}

export default function RepositoryEventsPage() {
  const [webhooks, setWebhooks] = useState<WebhookItem[]>([
    {
      id: "wh-1",
      providerName: "GITHUB",
      targetUrl: "https://api.cloudforge.ai/webhooks/github?projectId=proj-1",
      events: "push, pull_request, release",
      status: "ACTIVE",
    },
  ]);

  const [events, setEvents] = useState<EventItem[]>([
    {
      id: "evt-1",
      eventType: "push",
      providerName: "GITHUB",
      deliveryId: "del-994821",
      status: "PROCESSED",
      receivedAt: new Date().toISOString(),
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [providerName, setProviderName] = useState("GITHUB");
  const [secret, setSecret] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  const handleRegisterWebhook = (e: React.FormEvent) => {
    e.preventDefault();
    const item: WebhookItem = {
      id: `wh-${Date.now()}`,
      providerName,
      targetUrl: `https://api.cloudforge.ai/webhooks/${providerName.toLowerCase()}?projectId=proj-1`,
      events: "push, pull_request",
      status: "ACTIVE",
    };
    setWebhooks([...webhooks, item]);
    setSecret("");
    setShowModal(false);
    setMessage(`Git Webhook registered for ${providerName} with HMAC-SHA256 signature verification.`);
  };

  const handleReplay = (id: string) => {
    setEvents((prev) =>
      prev.map((e) =>
        e.id === id ? { ...e, status: "PROCESSED" } : e
      )
    );
    setMessage("Repository event replayed successfully.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Repository Events & Webhooks</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Real-time webhook receiver, HMAC-SHA256 signature validation, event stream, and event replay</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Register Webhook
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Webhooks Section */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Registered Webhooks ({webhooks.length})</h3>

            <div className="space-y-3">
              {webhooks.map((w) => (
                <div key={w.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2.5 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                      <Radio className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-heading text-sm font-bold text-[#E7EDF7]">{w.providerName} Webhook</span>
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                          {w.status}
                        </span>
                      </div>
                      <p className="text-xs text-[#8B99B8] font-mono mt-0.5">{w.targetUrl}</p>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Events: {w.events} | Verification: HMAC-SHA256</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Event Stream Section */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-3">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Live Event Stream ({events.length})</h3>

            <div className="space-y-3">
              {events.map((e) => (
                <div key={e.id} className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
                      <AlertCircle className="w-4 h-4" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-heading text-xs font-bold text-[#E7EDF7]">{e.eventType.toUpperCase()} EVENT</span>
                        <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                          {e.status}
                        </span>
                      </div>
                      <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5">Delivery ID: {e.deliveryId} | Provider: {e.providerName}</p>
                    </div>
                  </div>

                  <button
                    onClick={() => handleReplay(e.id)}
                    className="p-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#3DD9C4] text-xs font-medium flex items-center gap-1 transition-all"
                    title="Replay Event"
                  >
                    <RefreshCw className="w-3.5 h-3.5" />
                    Replay
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Register Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <KeyRound className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Register Git Webhook</h3>
                </div>

                <form onSubmit={handleRegisterWebhook} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Git Provider</label>
                    <select
                      value={providerName}
                      onChange={(e) => setProviderName(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="GITHUB">GitHub Enterprise / Cloud</option>
                      <option value="GITLAB">GitLab Self-Managed / Cloud</option>
                      <option value="BITBUCKET">Bitbucket Data Center / Cloud</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Webhook Secret</label>
                    <input
                      type="password"
                      required
                      value={secret}
                      onChange={(e) => setSecret(e.target.value)}
                      placeholder="e.g. whsec_••••••••••••••••"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                    />
                  </div>

                  <div className="p-3 rounded-xl bg-[#16233A] border border-[#22314D] text-[10px] text-[#8B99B8] flex items-center gap-2">
                    <Shield className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                    <span>Inbound payloads are validated against HMAC-SHA256 signatures before event dispatching.</span>
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
                      Register Webhook
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
