"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Sparkles, Send, Bot, User, AlertCircle, RefreshCw } from "lucide-react";
import { api } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

interface ChatMessage {
  sender: "user" | "ai";
  text: string;
  recommendations?: string[];
}

export default function GlobalAiCopilotPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      sender: "ai",
      text: "Hello! I am CloudForge Enterprise AI Copilot. Ask me about pipeline stability, runner health, deployment risks, Kubernetes OOM events, or active incidents.",
    },
  ]);

  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || loading) return;

    const userQuery = input.trim();
    const userMsg: ChatMessage = { sender: "user", text: userQuery };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setLoading(true);
    setError(null);

    try {
      const userRes = await api.me().catch(() => null);
      const orgId = userRes?.organizations?.[0]?.id || "00000000-0000-0000-0000-000000000001";
      const projects = await api.request<any[]>(`/orgs/${orgId}/projects`).catch(() => []);
      const projId = projects[0]?.id || "00000000-0000-0000-0000-000000000001";
      const res = await api.processCopilotChat(projId, orgId, userQuery);

      let aiText = "";
      let recommendations: string[] = [];

      if (res && res.baseResponse) {
        aiText = res.baseResponse.content || res.baseResponse.textResponse || res.baseResponse.summary;
        recommendations = res.baseResponse.recommendations || [];
      } else if (res && res.response) {
        aiText = res.response;
      } else {
        aiText = "Response received from CloudForge AI Copilot Service.";
      }

      setMessages((prev) => [...prev, { sender: "ai", text: aiText, recommendations }]);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to query Copilot API");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 flex flex-col p-4 sm:p-6 max-w-4xl mx-auto w-full overflow-hidden">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)] mb-4">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                <Sparkles className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                  <h1 className="text-xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                    Enterprise AI Copilot Assistant
                  </h1>
                  <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                    ENV: {environmentConfig.label}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8]">
                  Operational intelligence & pre-flight risk insights for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
                </p>
              </div>
            </div>
          </div>

          {error && (
            <div className="mb-4 p-4 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Chat Messages */}
          <div className="flex-1 overflow-y-auto py-4 space-y-4 pr-1">
            {messages.map((m, idx) => (
              <div key={idx} className={`flex items-start gap-3 ${m.sender === "user" ? "flex-row-reverse" : ""}`}>
                <div className={`p-2 rounded-xl text-xs font-bold ${
                  m.sender === "user" ? "bg-[#3DD9C4] text-[#0A1020] shadow-[0_0_10px_rgba(61,217,196,0.3)]" : "bg-[#0A1020] text-[#3DD9C4] border border-[#3DD9C4]/40"
                }`}>
                  {m.sender === "user" ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
                </div>

                <div className={`p-4 rounded-2xl max-w-lg text-xs leading-relaxed backdrop-blur-2xl ${
                  m.sender === "user" ? "bg-[#050F25]/80 border border-[#3DD9C4]/40 text-[#E7EDF7]" : "bg-[#050F25]/60 border border-[#22314D] text-[#E7EDF7] shadow-[0_0_20px_rgba(61,217,196,0.06)]"
                }`}>
                  <p>{m.text}</p>
                  {m.recommendations && m.recommendations.length > 0 && (
                    <div className="mt-3 pt-3 border-t border-[#22314D]/60 space-y-1">
                      <span className="text-[10px] font-mono text-[#3DD9C4] font-bold uppercase">Recommendations:</span>
                      {m.recommendations.map((rec, i) => (
                        <div key={i} className="text-[11px] text-[#8B99B8]">• {rec}</div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}

            {loading && (
              <div className="flex items-center gap-2 text-xs text-[#3DD9C4] font-mono">
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Copilot is reasoning and aggregating operational context...</span>
              </div>
            )}
          </div>

          {/* Chat Input */}
          <form onSubmit={handleSend} className="pt-4 border-t border-[#22314D]/60 flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask AI: e.g. Why did deployment fail? Show runner utilization..."
              className="flex-1 bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] focus:border-[#3DD9C4] rounded-xl px-4 py-3 text-xs text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-colors font-sans"
            />
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-3 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)] disabled:opacity-50 cursor-pointer"
            >
              <Send className="w-4 h-4" />
              Send
            </button>
          </form>
        </main>
      </div>
    </div>
  );
}
