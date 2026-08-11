"use client";

import React, { useState } from "react";
import { useParams } from "next/navigation";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Sparkles, Send, Bot, User, RefreshCw, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";

interface ChatMessage {
  sender: "user" | "ai";
  text: string;
  recommendations?: string[];
}

export default function AiChatPage() {
  const params = useParams();
  const projectId = (params?.id as string) || "proj-1";

  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      sender: "ai",
      text: "Hello! I am CloudForge Mission Control AI. Ask me about pipeline stability, runner health, deployment risks, or recent incidents.",
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
      const res = await api.processCopilotChat(projectId, orgId, userQuery);

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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 flex flex-col p-6 max-w-4xl mx-auto w-full overflow-hidden">
          <div className="flex items-center gap-3 pb-4 border-b border-[#22314D]">
            <div className="p-2.5 rounded-xl bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
              <Sparkles className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">Mission Control AI Assistant</h1>
              <p className="text-xs text-[#8B99B8]">Operational intelligence, incident querying, and pre-flight risk insights</p>
            </div>
          </div>

          {error && (
            <div className="mt-4 p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              <span>{error}</span>
            </div>
          )}

          {/* Chat Messages */}
          <div className="flex-1 overflow-y-auto py-6 space-y-4">
            {messages.map((m, idx) => (
              <div key={idx} className={`flex items-start gap-3 ${m.sender === "user" ? "flex-row-reverse" : ""}`}>
                <div className={`p-2 rounded-xl text-xs font-bold ${
                  m.sender === "user" ? "bg-[#3DD9C4] text-[#0A1020]" : "bg-[#16233A] text-[#3DD9C4] border border-[#22314D]"
                }`}>
                  {m.sender === "user" ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
                </div>

                <div className={`p-4 rounded-2xl max-w-lg text-xs leading-relaxed ${
                  m.sender === "user" ? "bg-[#16233A] border border-[#22314D] text-[#E7EDF7]" : "bg-[#111B2E] border border-[#22314D] text-[#E7EDF7]"
                }`}>
                  <p>{m.text}</p>
                  {m.recommendations && m.recommendations.length > 0 && (
                    <div className="mt-3 pt-3 border-t border-[#22314D] space-y-1">
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
          <form onSubmit={handleSend} className="pt-4 border-t border-[#22314D] flex gap-2">
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask AI: e.g. Why did deployment fail? Show runner utilization..."
              className="flex-1 bg-[#111B2E] border border-[#22314D] rounded-xl px-4 py-3 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
            />
            <button
              type="submit"
              disabled={loading}
              className="px-5 py-3 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)] disabled:opacity-50"
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
