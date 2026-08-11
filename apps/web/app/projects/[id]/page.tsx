"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { FolderGit2, GitBranch, Terminal, Shield, KeyRound, Boxes, Lock, ArrowRight } from "lucide-react";
import Link from "next/link";

export default function ProjectDetailPage() {
  const [name] = useState("cloudforge-api-gateway");
  const [repoUrl] = useState("https://github.com/cloudforge/api-gateway");
  const [k8sNamespace] = useState("prod-gateway");

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Top Header */}
          <div className="flex items-center justify-between pb-4 border-b border-[#22314D]">
            <div className="flex items-center gap-3">
              <div className="p-3 rounded-2xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/40 text-[#3DD9C4]">
                <FolderGit2 className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">{name}</h1>
                <p className="text-xs text-[#8B99B8] font-mono mt-0.5">Namespace: {k8sNamespace} | Status: ACTIVE</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Link
                href="/projects/p-1/variables"
                className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4]/40 text-xs font-bold text-[#E7EDF7] flex items-center gap-1.5"
              >
                <KeyRound className="w-4 h-4 text-[#3DD9C4]" />
                Variables & Secrets
              </Link>
            </div>
          </div>

          {/* Quick Sub-Navigation Grid */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Link
              href="/projects/p-1/environments"
              className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-lg flex items-center justify-between group"
            >
              <div className="flex items-center gap-3">
                <Boxes className="w-5 h-5 text-[#3DD9C4]" />
                <div>
                  <h3 className="text-xs font-heading font-bold text-[#E7EDF7]">Environments</h3>
                  <p className="text-[10px] text-[#8B99B8] font-mono">DEV, STAGING, PROD</p>
                </div>
              </div>
              <ArrowRight className="w-4 h-4 text-[#8B99B8] group-hover:text-[#3DD9C4] transition-colors" />
            </Link>

            <Link
              href="/projects/p-1/repositories"
              className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-lg flex items-center justify-between group"
            >
              <div className="flex items-center gap-3">
                <GitBranch className="w-5 h-5 text-[#3DD9C4]" />
                <div>
                  <h3 className="text-xs font-heading font-bold text-[#E7EDF7]">Repositories</h3>
                  <p className="text-[10px] text-[#8B99B8] font-mono">1 Repository Linked</p>
                </div>
              </div>
              <ArrowRight className="w-4 h-4 text-[#8B99B8] group-hover:text-[#3DD9C4] transition-colors" />
            </Link>

            <Link
              href="/projects/p-1/secrets"
              className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-lg flex items-center justify-between group"
            >
              <div className="flex items-center gap-3">
                <Lock className="w-5 h-5 text-[#FBBF24]" />
                <div>
                  <h3 className="text-xs font-heading font-bold text-[#E7EDF7]">Vault Secrets</h3>
                  <p className="text-[10px] text-[#8B99B8] font-mono">Vault Ready</p>
                </div>
              </div>
              <ArrowRight className="w-4 h-4 text-[#8B99B8] group-hover:text-[#3DD9C4] transition-colors" />
            </Link>

            <Link
              href="/projects/p-1/variables"
              className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-all shadow-lg flex items-center justify-between group"
            >
              <div className="flex items-center gap-3">
                <KeyRound className="w-5 h-5 text-[#34D399]" />
                <div>
                  <h3 className="text-xs font-heading font-bold text-[#E7EDF7]">Variables</h3>
                  <p className="text-[10px] text-[#8B99B8] font-mono">3 Variables Configured</p>
                </div>
              </div>
              <ArrowRight className="w-4 h-4 text-[#8B99B8] group-hover:text-[#3DD9C4] transition-colors" />
            </Link>
          </div>

          {/* Project Details */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg space-y-4">
            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">Repository & Cluster Integration</h3>

            <div className="space-y-3 font-mono text-xs">
              <div className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <GitBranch className="w-4 h-4 text-[#3DD9C4]" />
                  <span>Git Repository: {repoUrl}</span>
                </div>
                <span className="text-[#34D399]">CONNECTED</span>
              </div>

              <div className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Terminal className="w-4 h-4 text-[#FBBF24]" />
                  <span>Kubernetes Namespace: {k8sNamespace}</span>
                </div>
                <span className="text-[#34D399]">PROVISIONED</span>
              </div>

              <div className="p-3.5 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Shield className="w-4 h-4 text-[#34D399]" />
                  <span>Tenant Isolation Boundary</span>
                </div>
                <span className="text-[#34D399]">ENFORCED</span>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
