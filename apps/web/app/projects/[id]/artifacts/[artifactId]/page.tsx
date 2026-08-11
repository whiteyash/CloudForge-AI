"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Package, ArrowLeft, Download, ShieldCheck, Trash2, CheckCircle2 } from "lucide-react";
import Link from "next/link";

export default function ArtifactDetailsPage() {
  const [retentionStatus, setRetentionStatus] = useState("ACTIVE");
  const [message, setMessage] = useState<string | null>(null);

  const handleSoftDelete = () => {
    setRetentionStatus("SOFT_DELETED");
    setMessage("Artifact set to SOFT_DELETED status. Data retained for audit compliance.");
  };

  const handleRestore = () => {
    setRetentionStatus("ACTIVE");
    setMessage("Artifact restored to ACTIVE status.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/artifacts" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">core-service-app:2.4.0</h1>
                  <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                    retentionStatus === "ACTIVE" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                  }`}>
                    {retentionStatus}
                  </span>
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Type: JAR | Size: 14.71 MB | Storage Provider: LOCAL</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              {retentionStatus === "ACTIVE" ? (
                <button
                  onClick={handleSoftDelete}
                  className="px-3 py-2 rounded-xl bg-[#16233A] text-[#F87171] text-xs font-bold flex items-center gap-1 hover:bg-[#F87171]/10"
                >
                  <Trash2 className="w-4 h-4" /> Soft Delete
                </button>
              ) : (
                <button
                  onClick={handleRestore}
                  className="px-3 py-2 rounded-xl bg-[#16233A] text-[#34D399] text-xs font-bold flex items-center gap-1 hover:bg-[#34D399]/10"
                >
                  <CheckCircle2 className="w-4 h-4" /> Restore Artifact
                </button>
              )}

              <button className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]">
                <Download className="w-4 h-4" /> Authorized Download
              </button>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="md:col-span-2 space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-[#3DD9C4]" />
                  SHA-256 Checksum Integrity Verification
                </h3>

                <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] font-mono text-xs text-[#34D399] break-all">
                  e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
                </div>
                <p className="text-[10px] text-[#8B99B8]">SHA-256 hash verified against storage key: artifacts/proj-1/core-service-app-2.4.0.jar</p>
              </div>

              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Download className="w-4 h-4 text-[#3DD9C4]" />
                  Download Audit History
                </h3>

                <div className="space-y-2 text-xs font-mono text-[#8B99B8]">
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <span>Downloaded by @cloudforge-lead</span>
                    <span className="text-[10px] text-[#8B99B8]/60">2 minutes ago</span>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] flex items-center justify-between">
                    <span>Downloaded by @k8s-runner-pool-1 (Deployment Agent)</span>
                    <span className="text-[10px] text-[#8B99B8]/60">15 minutes ago</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-3">
                <h3 className="text-sm font-heading font-bold text-[#E7EDF7] flex items-center gap-2">
                  <Package className="w-4 h-4 text-[#34D399]" />
                  Pipeline Origin
                </h3>
                <div className="text-xs font-mono text-[#8B99B8] space-y-2">
                  <p>Pipeline: main-build-ci</p>
                  <p>Run: #42</p>
                  <p>Job: test-suite-execution</p>
                  <p>Storage: Local Storage Provider</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
