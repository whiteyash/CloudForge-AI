"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Tag, Rocket, Download, FileText, Calendar, ExternalLink, Sparkles } from "lucide-react";
import Link from "next/link";

interface ReleaseItem {
  id: string;
  tagName: string;
  name: string;
  body: string;
  authorUsername: string;
  isDraft: boolean;
  isPrerelease: boolean;
  publishedAt: string;
  webUrl: string;
}

export default function ProjectReleasesPage() {
  const [releases] = useState<ReleaseItem[]>([
    {
      id: "rel-1",
      tagName: "v2.4.0",
      name: "v2.4.0 — Enterprise Repository Event Platform",
      body: "Introduced HMAC-SHA256 signature verification, dead-letter queueing, event stream replay, and pull request metadata synchronization.",
      authorUsername: "cloudforge-release-bot",
      isDraft: false,
      isPrerelease: false,
      publishedAt: new Date().toISOString(),
      webUrl: "https://github.com/cloudforge-ai/cloudforge/releases/tag/v2.4.0",
    },
    {
      id: "rel-2",
      tagName: "v2.3.0-rc.1",
      name: "v2.3.0-rc.1 — OAuth 2.0 Provider Connection Hardening",
      body: "Release Candidate 1 featuring AES-256-GCM token encryption and automatic rate limit monitoring.",
      authorUsername: "devops-lead",
      isDraft: false,
      isPrerelease: true,
      publishedAt: "2026-07-27T12:00:00.000Z",
      webUrl: "https://github.com/cloudforge-ai/cloudforge/releases/tag/v2.3.0-rc.1",
    },
  ]);

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Release Management</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Synchronized repository releases, semantic versioning, release notes, and assets metadata</p>
            </div>

            <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#3DD9C4]">
              <Sparkles className="w-4 h-4 text-[#3DD9C4]" />
              <span>Latest Release: {releases[0]?.tagName}</span>
            </div>
          </div>

          {/* Release Cards Roster */}
          <div className="space-y-4">
            {releases.map((r) => (
              <div key={r.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-4">
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                      <Rocket className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <Link href={`/projects/proj-1/releases/${r.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                          {r.name}
                        </Link>
                        <span className="px-2.5 py-0.5 rounded text-xs font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center gap-1">
                          <Tag className="w-3 h-3" />
                          {r.tagName}
                        </span>
                        {r.isPrerelease && (
                          <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#F59E0B]/10 text-[#F59E0B] border border-[#F59E0B]/30">
                            PRE-RELEASE
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-[#8B99B8] font-mono mt-1 flex items-center gap-3">
                        <span>Published by @{r.authorUsername}</span>
                        <span className="flex items-center gap-1">
                          <Calendar className="w-3 h-3 text-[#3DD9C4]" />
                          {new Date(r.publishedAt).toLocaleDateString()}
                        </span>
                      </p>
                    </div>
                  </div>

                  <a
                    href={r.webUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="p-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#3DD9C4] text-xs font-medium flex items-center gap-1.5 transition-all"
                  >
                    <ExternalLink className="w-3.5 h-3.5" />
                    Git Provider Link
                  </a>
                </div>

                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] text-xs text-[#E7EDF7] space-y-2">
                  <div className="flex items-center gap-1.5 font-heading font-semibold text-[#8B99B8]">
                    <FileText className="w-3.5 h-3.5 text-[#3DD9C4]" />
                    Release Notes
                  </div>
                  <p className="text-xs text-[#8B99B8] leading-relaxed font-sans">{r.body}</p>
                </div>

                <div className="flex items-center justify-between pt-1 border-t border-[#22314D] text-xs font-mono text-[#8B99B8]">
                  <div className="flex items-center gap-2">
                    <Download className="w-3.5 h-3.5 text-[#3DD9C4]" />
                    <span>Assets Metadata: 3 Binaries (SHA-256 Verified)</span>
                  </div>
                  <span>Semantic Version: {r.tagName.replace(/^v/, "")}</span>
                </div>
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}
