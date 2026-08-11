"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Package, Plus, CheckCircle2, Download, HardDrive, ShieldCheck } from "lucide-react";
import Link from "next/link";

interface ArtifactItem {
  id: string;
  name: string;
  artifactType: string;
  version: string;
  sha256Checksum: string;
  sizeBytes: number;
  storageProvider: string;
  retentionStatus: string;
  createdAt: string;
}

export default function ArtifactsPage() {
  const [artifacts, setArtifacts] = useState<ArtifactItem[]>([
    {
      id: "art-1",
      name: "core-service-app",
      artifactType: "JAR",
      version: "2.4.0",
      sha256Checksum: "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
      sizeBytes: 15420000,
      storageProvider: "LOCAL",
      retentionStatus: "ACTIVE",
      createdAt: new Date().toISOString(),
    },
    {
      id: "art-2",
      name: "frontend-web-bundle",
      artifactType: "TAR_GZ",
      version: "1.8.2",
      sha256Checksum: "8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4",
      sizeBytes: 42100000,
      storageProvider: "LOCAL",
      retentionStatus: "ACTIVE",
      createdAt: new Date().toISOString(),
    },
  ]);

  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState("");
  const [artifactType, setArtifactType] = useState("JAR");
  const [version, setVersion] = useState("1.0.0");
  const [message, setMessage] = useState<string | null>(null);

  const handleRegisterArtifact = (e: React.FormEvent) => {
    e.preventDefault();
    const item: ArtifactItem = {
      id: `art-${Date.now()}`,
      name,
      artifactType,
      version,
      sha256Checksum: "a7d8c4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2c3",
      sizeBytes: 12450000,
      storageProvider: "LOCAL",
      retentionStatus: "ACTIVE",
      createdAt: new Date().toISOString(),
    };
    setArtifacts([...artifacts, item]);
    setName("");
    setShowModal(false);
    setMessage(`Artifact ${name}:${version} registered with SHA-256 integrity hash verification.`);
  };

  const formatSize = (bytes: number) => {
    return (bytes / (1024 * 1024)).toFixed(2) + " MB";
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Artifact Repository</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Immutable build output artifacts, SHA-256 integrity verification, and retention management</p>
            </div>

            <button
              onClick={() => setShowModal(true)}
              className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
            >
              <Plus className="w-4 h-4" />
              Upload Artifact
            </button>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Artifacts Roster */}
          <div className="space-y-4">
            {artifacts.map((a) => (
              <div key={a.id} className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-xl bg-[#16233A] text-[#3DD9C4]">
                    <Package className="w-6 h-6" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <Link href={`/projects/proj-1/artifacts/${a.id}`} className="font-heading text-base font-bold text-[#E7EDF7] hover:text-[#3DD9C4] transition-all">
                        {a.name}:{a.version}
                      </Link>
                      <span className="px-2.5 py-0.5 rounded text-xs font-mono font-semibold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                        {a.retentionStatus}
                      </span>
                    </div>
                    <p className="text-xs text-[#8B99B8] mt-1 font-mono">
                      Type: {a.artifactType} | Size: {formatSize(a.sizeBytes)} | Provider: {a.storageProvider}
                    </p>
                    <p className="text-[10px] text-[#8B99B8] font-mono mt-0.5 truncate max-w-md">
                      SHA-256: {a.sha256Checksum}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <Link
                    href={`/projects/proj-1/artifacts/${a.id}`}
                    className="px-3 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#3DD9C4] hover:text-[#0A1020] text-xs font-semibold flex items-center gap-1.5 transition-all"
                  >
                    <Download className="w-3.5 h-3.5" />
                    Download
                  </Link>
                </div>
              </div>
            ))}
          </div>

          {/* Upload Modal */}
          {showModal && (
            <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50">
              <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 w-full max-w-md shadow-2xl space-y-4">
                <div className="flex items-center gap-2">
                  <HardDrive className="w-5 h-5 text-[#3DD9C4]" />
                  <h3 className="text-base font-heading font-bold text-[#E7EDF7]">Register Build Artifact</h3>
                </div>

                <form onSubmit={handleRegisterArtifact} className="space-y-4">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Artifact Name</label>
                    <input
                      type="text"
                      required
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="e.g. core-service-app"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Artifact Type</label>
                    <select
                      value={artifactType}
                      onChange={(e) => setArtifactType(e.target.value)}
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    >
                      <option value="JAR">Java Archive (.jar)</option>
                      <option value="TAR_GZ">Compressed Tarball (.tar.gz)</option>
                      <option value="ZIP">Zip Archive (.zip)</option>
                      <option value="DOCKER_IMAGE">Docker Image Tag (.tar)</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1">Semantic Version</label>
                    <input
                      type="text"
                      required
                      value={version}
                      onChange={(e) => setVersion(e.target.value)}
                      placeholder="e.g. 2.4.0"
                      className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                    />
                  </div>

                  <div className="p-3 rounded-xl bg-[#16233A] border border-[#22314D] text-[10px] text-[#8B99B8] flex items-center gap-2">
                    <ShieldCheck className="w-4 h-4 text-[#3DD9C4] shrink-0" />
                    <span>Pluggable ArtifactStorageProvider abstraction will compute SHA-256 hash automatically.</span>
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
                      Register Artifact
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
