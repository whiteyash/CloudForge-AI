"use client";

import React from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Tag, ArrowLeft, GitCommit, GitPullRequest, ShieldCheck, Download } from "lucide-react";
import Link from "next/link";

export default function ReleaseDetailsPage() {
  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center gap-3">
            <Link href="/projects/proj-1/releases" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
              <ArrowLeft className="w-4 h-4" />
            </Link>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">v2.4.0 Release Details</h1>
                <span className="px-2.5 py-0.5 rounded text-xs font-mono bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center gap-1">
                  <Tag className="w-3 h-3" />
                  v2.4.0
                </span>
              </div>
              <p className="text-xs text-[#8B99B8] mt-0.5">Published on August 1, 2026 by @cloudforge-release-bot</p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2 space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Release Notes & Changelog</h3>
                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] text-xs text-[#8B99B8] space-y-2 font-mono leading-relaxed">
                  <p>### Features Included</p>
                  <p>- HMAC-SHA256 signature verification for inbound Git provider webhooks</p>
                  <p>- Dead-letter queue routing and event replay engine</p>
                  <p>- Pull Request metadata synchronization and state tracking</p>
                  <p>- Repository governance policy evaluation and compliance scoring</p>
                </div>
              </div>

              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Assets Metadata</h3>
                <div className="space-y-2">
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between font-mono text-xs text-[#8B99B8]">
                    <div className="flex items-center gap-2">
                      <Download className="w-4 h-4 text-[#3DD9C4]" />
                      <span>cloudforge-api-2.4.0.jar</span>
                    </div>
                    <span>42.8 MB | SHA-256 Verified</span>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between font-mono text-xs text-[#8B99B8]">
                    <div className="flex items-center gap-2">
                      <Download className="w-4 h-4 text-[#3DD9C4]" />
                      <span>cloudforge-web-2.4.0.tar.gz</span>
                    </div>
                    <span>18.4 MB | SHA-256 Verified</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Associated Metadata</h3>
                <div className="space-y-2 text-xs font-mono text-[#8B99B8]">
                  <div className="flex items-center gap-2">
                    <GitCommit className="w-4 h-4 text-[#3DD9C4]" />
                    <span>Target Commit: a1b2c3d</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <GitPullRequest className="w-4 h-4 text-[#A855F7]" />
                    <span>Linked PRs: #101, #102</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <ShieldCheck className="w-4 h-4 text-[#34D399]" />
                    <span>Audit Status: VERIFIED</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
