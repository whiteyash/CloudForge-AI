"use client";

import React, { useState, useEffect, use } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import {
  Boxes,
  Plus,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  Trash2,
  ShieldCheck,
  Cpu,
  Layers,
  Copy,
  Check,
  Building2,
  Play
} from "lucide-react";
import {
  api,
  ContainerRegistryDto,
  ContainerImageRepositoryDto,
  ContainerImageTagDto,
  NativeImageBuildDto
} from "@/lib/api";

interface PageProps {
  params: Promise<{ id: string }>;
}

export default function ContainerRegistryPage({ params }: PageProps) {
  const resolvedParams = use(params);
  const projectId = resolvedParams.id || "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33";

  const [activeTab, setActiveTab] = useState<"registries" | "repositories" | "tags" | "builds">("registries");

  // Data States
  const [registries, setRegistries] = useState<ContainerRegistryDto[]>([]);
  const [selectedRegistryId, setSelectedRegistryId] = useState<string | null>(null);
  const [repositories, setRepositories] = useState<ContainerImageRepositoryDto[]>([]);
  const [selectedRepoId, setSelectedRepoId] = useState<string | null>(null);
  const [tags, setTags] = useState<ContainerImageTagDto[]>([]);
  const [builds, setBuilds] = useState<NativeImageBuildDto[]>([]);

  // UI States
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [copiedTagId, setCopiedTagId] = useState<string | null>(null);

  // Modals
  const [showConnectModal, setShowConnectModal] = useState<boolean>(false);
  const [showBuildModal, setShowBuildModal] = useState<boolean>(false);

  // Form States - Connect Registry
  const [name, setName] = useState<string>("");
  const [registryType, setRegistryType] = useState<string>("AWS_ECR");
  const [registryUrl, setRegistryUrl] = useState<string>("https://123456789.dkr.ecr.us-east-1.amazonaws.com");
  const [authType, setAuthType] = useState<string>("AWS_IAM");
  const [credentials, setCredentials] = useState<string>("");

  // Form States - Trigger Build
  const [targetRegistryId, setTargetRegistryId] = useState<string>("");
  const [buildRepoName, setBuildRepoName] = useState<string>("myorg/api-service");
  const [buildTagName, setBuildTagName] = useState<string>("v1.0.0");
  const [dockerfilePath, setDockerfilePath] = useState<string>("Dockerfile");

  const reloadAllData = async () => {
    setLoading(true);
    setError(null);
    try {
      const regList = await api.getRegistries(projectId);
      setRegistries(regList);

      if (regList.length > 0) {
        const firstRegId = regList[0].id;
        setSelectedRegistryId(firstRegId);
        setTargetRegistryId(firstRegId);

        const repos = await api.getImageRepositories(projectId, firstRegId);
        setRepositories(repos);

        if (repos.length > 0) {
          const firstRepoId = repos[0].id;
          setSelectedRepoId(firstRepoId);
          const repoTags = await api.getImageTags(projectId, firstRegId, firstRepoId);
          setTags(repoTags);
        }
      }

      const buildList = await api.getNativeImageBuilds(projectId);
      setBuilds(buildList);
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to load container registry data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let active = true;
    const fetchInitialData = async () => {
      try {
        const regList = await api.getRegistries(projectId);
        if (!active) return;
        setRegistries(regList);

        if (regList.length > 0) {
          const firstRegId = regList[0].id;
          setSelectedRegistryId(firstRegId);
          setTargetRegistryId(firstRegId);

          const repos = await api.getImageRepositories(projectId, firstRegId);
          if (!active) return;
          setRepositories(repos);

          if (repos.length > 0) {
            const firstRepoId = repos[0].id;
            setSelectedRepoId(firstRepoId);
            const repoTags = await api.getImageTags(projectId, firstRegId, firstRepoId);
            if (!active) return;
            setTags(repoTags);
          }
        }

        const buildList = await api.getNativeImageBuilds(projectId);
        if (!active) return;
        setBuilds(buildList);
      } catch (err: unknown) {
        if (active) setError((err as Error)?.message || "Failed to load container registry data");
      } finally {
        if (active) setLoading(false);
      }
    };

    fetchInitialData();
    return () => {
      active = false;
    };
  }, [projectId]);

  const handleSelectRegistry = async (regId: string) => {
    setSelectedRegistryId(regId);
    setLoading(true);
    try {
      const repos = await api.getImageRepositories(projectId, regId);
      setRepositories(repos);
      if (repos.length > 0) {
        setSelectedRepoId(repos[0].id);
        const repoTags = await api.getImageTags(projectId, regId, repos[0].id);
        setTags(repoTags);
      } else {
        setSelectedRepoId(null);
        setTags([]);
      }
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to load image repositories");
    } finally {
      setLoading(false);
    }
  };

  const handleSelectRepo = async (repoId: string) => {
    if (!selectedRegistryId) return;
    setSelectedRepoId(repoId);
    setLoading(true);
    try {
      const repoTags = await api.getImageTags(projectId, selectedRegistryId, repoId);
      setTags(repoTags);
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to load image tags");
    } finally {
      setLoading(false);
    }
  };

  const handleConnectRegistry = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const newReg = await api.connectRegistry(projectId, {
        name,
        registryType,
        registryUrl,
        authType,
        credentials
      });
      setSuccessMsg(`Registry '${newReg.name}' connected successfully.`);
      setShowConnectModal(false);
      setName("");
      setCredentials("");
      await reloadAllData();
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to connect container registry");
    } finally {
      setLoading(false);
    }
  };

  const handleTestConnection = async (regId: string) => {
    setLoading(true);
    setError(null);
    try {
      const updated = await api.testRegistryConnection(projectId, regId);
      setSuccessMsg(`Registry '${updated.name}' connection test passed: Status ${updated.status}`);
      await reloadAllData();
    } catch (err: unknown) {
      setError((err as Error)?.message || "Registry connection test failed");
    } finally {
      setLoading(false);
    }
  };

  const handleDisconnectRegistry = async (regId: string) => {
    setLoading(true);
    setError(null);
    try {
      await api.disconnectRegistry(projectId, regId);
      setSuccessMsg("Registry disconnected successfully.");
      await reloadAllData();
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to disconnect registry");
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTag = async (tagId: string) => {
    if (!selectedRegistryId || !selectedRepoId) return;
    setLoading(true);
    setError(null);
    try {
      await api.deleteImageTag(projectId, selectedRegistryId, selectedRepoId, tagId);
      setSuccessMsg("Image tag deleted successfully.");
      const updatedTags = await api.getImageTags(projectId, selectedRegistryId, selectedRepoId);
      setTags(updatedTags);
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to delete image tag");
    } finally {
      setLoading(false);
    }
  };

  const handleTriggerBuild = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const newBuild = await api.triggerNativeImageBuild(projectId, {
        registryId: targetRegistryId,
        repositoryName: buildRepoName,
        tagName: buildTagName,
        dockerfilePath
      });
      setSuccessMsg(`Native image build #${newBuild.id.substring(0, 8)} triggered cleanly.`);
      setShowBuildModal(false);
      await reloadAllData();
    } catch (err: unknown) {
      setError((err as Error)?.message || "Failed to trigger native image build");
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedTagId(id);
    setTimeout(() => setCopiedTagId(null), 2000);
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          {/* Top Title Bar */}
          <div className="flex items-center justify-between pb-4 border-b border-[#22314D]">
            <div>
              <div className="flex items-center gap-2">
                <div className="p-2.5 rounded-xl bg-[#3DD9C4]/10 border border-[#3DD9C4]/40 text-[#3DD9C4]">
                  <Boxes className="w-5 h-5" />
                </div>
                <div>
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">
                    Container Registry & Native Builder
                  </h1>
                  <p className="text-xs text-[#8B99B8] mt-0.5">
                    Phase 7.0 Enterprise Registry Integration, OCI Repositories, SHA-256 Digests & Native Image Building
                  </p>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={() => setShowBuildModal(true)}
                className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4]/40 text-[#E7EDF7] font-heading font-bold text-xs flex items-center gap-1.5 transition-all"
              >
                <Play className="w-4 h-4 text-[#3DD9C4]" />
                Trigger Image Build
              </button>

              <button
                onClick={() => setShowConnectModal(true)}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                <Plus className="w-4 h-4" />
                Connect Registry
              </button>
            </div>
          </div>

          {/* Feedback Banners */}
          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center justify-between">
              <div className="flex items-center gap-2">
                <AlertCircle className="w-4 h-4 flex-shrink-0" />
                <span>{error}</span>
              </div>
              <button onClick={() => setError(null)} className="text-xs underline hover:text-[#E7EDF7]">Dismiss</button>
            </div>
          )}

          {successMsg && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center justify-between">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 flex-shrink-0" />
                <span>{successMsg}</span>
              </div>
              <button onClick={() => setSuccessMsg(null)} className="text-xs underline hover:text-[#E7EDF7]">Dismiss</button>
            </div>
          )}

          {/* Navigation Tabs */}
          <div className="flex items-center gap-2 border-b border-[#22314D]">
            <button
              onClick={() => setActiveTab("registries")}
              className={`px-4 py-2.5 text-xs font-heading font-bold flex items-center gap-2 border-b-2 transition-all ${
                activeTab === "registries"
                  ? "border-[#3DD9C4] text-[#3DD9C4]"
                  : "border-transparent text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <Building2 className="w-4 h-4" />
              REGISTRIES ({registries.length})
            </button>

            <button
              onClick={() => setActiveTab("repositories")}
              className={`px-4 py-2.5 text-xs font-heading font-bold flex items-center gap-2 border-b-2 transition-all ${
                activeTab === "repositories"
                  ? "border-[#3DD9C4] text-[#3DD9C4]"
                  : "border-transparent text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <Layers className="w-4 h-4" />
              REPOSITORIES ({repositories.length})
            </button>

            <button
              onClick={() => setActiveTab("tags")}
              className={`px-4 py-2.5 text-xs font-heading font-bold flex items-center gap-2 border-b-2 transition-all ${
                activeTab === "tags"
                  ? "border-[#3DD9C4] text-[#3DD9C4]"
                  : "border-transparent text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <ShieldCheck className="w-4 h-4" />
              IMAGE TAGS ({tags.length})
            </button>

            <button
              onClick={() => setActiveTab("builds")}
              className={`px-4 py-2.5 text-xs font-heading font-bold flex items-center gap-2 border-b-2 transition-all ${
                activeTab === "builds"
                  ? "border-[#3DD9C4] text-[#3DD9C4]"
                  : "border-transparent text-[#8B99B8] hover:text-[#E7EDF7]"
              }`}
            >
              <Cpu className="w-4 h-4" />
              NATIVE BUILDS ({builds.length})
            </button>
          </div>

          {/* TAB 1: REGISTRIES */}
          {activeTab === "registries" && (
            <div className="space-y-4">
              {registries.length === 0 ? (
                <div className="p-8 rounded-2xl bg-[#111B2E] border border-[#22314D] text-center space-y-3">
                  <Boxes className="w-10 h-10 text-[#8B99B8] mx-auto" />
                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">No Container Registries Connected</h3>
                  <p className="text-xs text-[#8B99B8] max-w-md mx-auto">
                    Connect AWS ECR, Docker Hub, Google Artifact Registry, GitHub GHCR, Azure ACR, or Harbor to store and manage your container images.
                  </p>
                  <button
                    onClick={() => setShowConnectModal(true)}
                    className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399]"
                  >
                    Connect Your First Registry
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {registries.map((reg) => (
                    <div
                      key={reg.id}
                      className={`p-5 rounded-2xl bg-[#111B2E] border transition-all ${
                        selectedRegistryId === reg.id ? "border-[#3DD9C4]" : "border-[#22314D] hover:border-[#3DD9C4]/40"
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="px-2 py-0.5 rounded text-[10px] font-mono font-bold bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30">
                              {reg.registryType}
                            </span>
                            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">{reg.name}</h3>
                          </div>
                          <p className="text-xs text-[#8B99B8] font-mono mt-2 truncate max-w-sm">{reg.registryUrl}</p>
                        </div>

                        <span
                          className={`px-2.5 py-1 rounded-full text-[10px] font-heading font-bold ${
                            reg.status === "CONNECTED"
                              ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30"
                              : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                          }`}
                        >
                          {reg.status}
                        </span>
                      </div>

                      <div className="mt-4 pt-4 border-t border-[#22314D] flex items-center justify-between text-xs">
                        <span className="text-[10px] text-[#8B99B8] font-mono">Auth: {reg.authType}</span>

                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => handleSelectRegistry(reg.id)}
                            className="px-3 py-1.5 rounded-lg bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4]/40 text-[11px] font-semibold text-[#E7EDF7]"
                          >
                            Explore Repos
                          </button>

                          <button
                            onClick={() => handleTestConnection(reg.id)}
                            className="p-1.5 rounded-lg bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4]/40 text-[#8B99B8] hover:text-[#3DD9C4]"
                            title="Test Connection"
                          >
                            <RefreshCw className="w-3.5 h-3.5 text-inherit" />
                          </button>

                          <button
                            onClick={() => handleDisconnectRegistry(reg.id)}
                            className="p-1.5 rounded-lg bg-[#16233A] border border-[#22314D] hover:border-[#F87171]/40 text-[#8B99B8] hover:text-[#F87171]"
                            title="Disconnect Registry"
                          >
                            <Trash2 className="w-3.5 h-3.5 text-inherit" />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* TAB 2: REPOSITORIES */}
          {activeTab === "repositories" && (
            <div className="space-y-4">
              {repositories.length === 0 ? (
                <div className="p-8 rounded-2xl bg-[#111B2E] border border-[#22314D] text-center space-y-2">
                  <Layers className="w-8 h-8 text-[#8B99B8] mx-auto" />
                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">No Image Repositories Found</h3>
                  <p className="text-xs text-[#8B99B8]">Trigger a native image build or push images to your connected registry.</p>
                </div>
              ) : (
                <div className="rounded-2xl bg-[#111B2E] border border-[#22314D] overflow-hidden">
                  <table className="w-full text-left text-xs text-[#8B99B8]">
                    <thead className="bg-[#16233A] text-[#E7EDF7] font-heading font-bold border-b border-[#22314D]">
                      <tr>
                        <th className="p-4">REPOSITORY NAME</th>
                        <th className="p-4">IMAGE TAGS</th>
                        <th className="p-4">PULL COUNT</th>
                        <th className="p-4">UPDATED</th>
                        <th className="p-4 text-right">ACTION</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-[#22314D]">
                      {repositories.map((repo) => (
                        <tr key={repo.id} className="hover:bg-[#16233A]/50 transition-colors">
                          <td className="p-4 font-mono font-bold text-[#3DD9C4]">{repo.repositoryName}</td>
                          <td className="p-4 font-mono text-[#E7EDF7]">{repo.imageCount} tags</td>
                          <td className="p-4 font-mono">{repo.pullCount} pulls</td>
                          <td className="p-4 font-mono">{new Date(repo.updatedAt || repo.createdAt).toLocaleDateString()}</td>
                          <td className="p-4 text-right">
                            <button
                              onClick={() => {
                                handleSelectRepo(repo.id);
                                setActiveTab("tags");
                              }}
                              className="px-3 py-1 rounded-lg bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 hover:bg-[#3DD9C4]/20 font-bold text-[11px]"
                            >
                              Inspect Tags
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {/* TAB 3: IMAGE TAGS */}
          {activeTab === "tags" && (
            <div className="space-y-4">
              {tags.length === 0 ? (
                <div className="p-8 rounded-2xl bg-[#111B2E] border border-[#22314D] text-center space-y-2">
                  <ShieldCheck className="w-8 h-8 text-[#8B99B8] mx-auto" />
                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">No Image Tags Available</h3>
                  <p className="text-xs text-[#8B99B8]">Select a repository to view associated image tags and digests.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 gap-3">
                  {tags.map((tag) => (
                    <div key={tag.id} className="p-4 rounded-xl bg-[#111B2E] border border-[#22314D] space-y-3">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <span className="px-2.5 py-1 rounded-lg bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 font-mono font-bold text-xs">
                            {tag.tagName}
                          </span>

                          {tag.isImmutable && (
                            <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#FBBF24]/10 text-[#FBBF24] border border-[#FBBF24]/30">
                              IMMUTABLE
                            </span>
                          )}

                          <span className="text-xs text-[#8B99B8] font-mono">
                            {(tag.sizeBytes / (1024 * 1024)).toFixed(1)} MB • {tag.architecture}
                          </span>
                        </div>

                        {!tag.isImmutable && (
                          <button
                            onClick={() => handleDeleteTag(tag.id)}
                            className="p-1.5 rounded-lg hover:bg-[#F87171]/10 text-[#8B99B8] hover:text-[#F87171] transition-colors"
                            title="Delete Image Tag"
                          >
                            <Trash2 className="w-4 h-4 text-inherit" />
                          </button>
                        )}
                      </div>

                      <div className="p-3 rounded-lg bg-[#0A1020] border border-[#22314D] flex items-center justify-between font-mono text-xs">
                        <div className="truncate max-w-2xl text-[#8B99B8]">
                          <span className="text-[#3DD9C4] font-bold">DIGEST:</span> {tag.digestSha256}
                        </div>

                        <button
                          onClick={() => copyToClipboard(tag.pullCommand || `docker pull ${tag.digestSha256}`, tag.id)}
                          className="px-2.5 py-1 rounded bg-[#16233A] border border-[#22314D] text-[10px] font-bold text-[#E7EDF7] hover:border-[#3DD9C4]/40 flex items-center gap-1.5"
                        >
                          {copiedTagId === tag.id ? <Check className="w-3 h-3 text-[#34D399]" /> : <Copy className="w-3 h-3 text-[#3DD9C4]" />}
                          {copiedTagId === tag.id ? "Copied" : "Pull Cmd"}
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* TAB 4: NATIVE IMAGE BUILDS */}
          {activeTab === "builds" && (
            <div className="space-y-4">
              {builds.length === 0 ? (
                <div className="p-8 rounded-2xl bg-[#111B2E] border border-[#22314D] text-center space-y-2">
                  <Cpu className="w-8 h-8 text-[#8B99B8] mx-auto" />
                  <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">No Native Build History</h3>
                  <p className="text-xs text-[#8B99B8]">Trigger a native Docker image build to view build logs and status.</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {builds.map((b) => (
                    <div key={b.id} className="p-5 rounded-2xl bg-[#111B2E] border border-[#22314D] space-y-4">
                      <div className="flex items-center justify-between">
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="px-2.5 py-0.5 rounded text-[10px] font-mono font-bold bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30">
                              {b.status || b.buildStatus}
                            </span>
                            <h3 className="text-sm font-heading font-bold text-[#E7EDF7]">
                              {b.repositoryName}:{b.tagName || b.imageTag}
                            </h3>
                          </div>
                          <p className="text-xs text-[#8B99B8] font-mono mt-1">Dockerfile: {b.dockerfilePath || b.dockerfileName} • Triggered: {new Date(b.createdAt || b.startedAt).toLocaleString()}</p>
                        </div>

                        <span className="text-[10px] font-mono text-[#8B99B8]">Build ID: #{b.id.substring(0, 8)}</span>
                      </div>

                      {b.logOutput && (
                        <div className="p-4 rounded-xl bg-[#0A1020] border border-[#22314D] font-mono text-[11px] text-[#34D399] overflow-x-auto whitespace-pre-wrap max-h-48 leading-relaxed">
                          {b.logOutput}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </main>
      </div>

      {/* CONNECT REGISTRY MODAL */}
      {showConnectModal && (
        <div className="fixed inset-0 z-50 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 shadow-2xl space-y-4">
            <h2 className="text-lg font-heading font-bold text-[#E7EDF7]">Connect Container Registry</h2>

            <form onSubmit={handleConnectRegistry} className="space-y-4 text-xs">
              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">REGISTRY NAME</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Production ECR"
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">REGISTRY TYPE</label>
                <select
                  value={registryType}
                  onChange={(e) => setRegistryType(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                >
                  <option value="AWS_ECR">AWS ECR</option>
                  <option value="DOCKER_HUB">Docker Hub</option>
                  <option value="GOOGLE_GAR">Google Artifact Registry</option>
                  <option value="GITHUB_GHCR">GitHub Container Registry (GHCR)</option>
                  <option value="AZURE_ACR">Azure Container Registry</option>
                  <option value="HARBOR_PRIVATE">Harbor / Private Registry</option>
                </select>
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">REGISTRY URL</label>
                <input
                  type="text"
                  required
                  value={registryUrl}
                  onChange={(e) => setRegistryUrl(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">AUTH TYPE</label>
                <select
                  value={authType}
                  onChange={(e) => setAuthType(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                >
                  <option value="TOKEN">Access Token</option>
                  <option value="USERNAME_PASSWORD">Username & Password</option>
                  <option value="AWS_IAM">AWS IAM Access Key</option>
                  <option value="SERVICE_ACCOUNT">GCP Service Account Key</option>
                </select>
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">CREDENTIALS (AES-256 ENCRYPTED)</label>
                <input
                  type="password"
                  value={credentials}
                  onChange={(e) => setCredentials(e.target.value)}
                  placeholder="Enter token or username:password"
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-[#22314D]">
                <button
                  type="button"
                  onClick={() => setShowConnectModal(false)}
                  className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] font-bold"
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  disabled={loading}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold hover:bg-[#34D399]"
                >
                  {loading ? "Connecting..." : "Connect Registry"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* TRIGGER BUILD MODAL */}
      {showBuildModal && (
        <div className="fixed inset-0 z-50 bg-[#0A1020]/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-6 shadow-2xl space-y-4">
            <h2 className="text-lg font-heading font-bold text-[#E7EDF7]">Trigger Native Image Build</h2>

            <form onSubmit={handleTriggerBuild} className="space-y-4 text-xs">
              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">TARGET REGISTRY</label>
                <select
                  value={targetRegistryId}
                  onChange={(e) => setTargetRegistryId(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                >
                  {registries.map((r) => (
                    <option key={r.id} value={r.id}>
                      {r.name} ({r.registryType})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">REPOSITORY NAME</label>
                <input
                  type="text"
                  required
                  value={buildRepoName}
                  onChange={(e) => setBuildRepoName(e.target.value)}
                  placeholder="e.g. myorg/payment-service"
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">TAG NAME</label>
                <input
                  type="text"
                  required
                  value={buildTagName}
                  onChange={(e) => setBuildTagName(e.target.value)}
                  placeholder="e.g. v1.0.0"
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div>
                <label className="block text-[#8B99B8] mb-1 font-bold">DOCKERFILE PATH</label>
                <input
                  type="text"
                  required
                  value={dockerfilePath}
                  onChange={(e) => setDockerfilePath(e.target.value)}
                  placeholder="Dockerfile"
                  className="w-full p-2.5 rounded-xl bg-[#0A1020] border border-[#22314D] text-[#E7EDF7] focus:border-[#3DD9C4] outline-none font-mono"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-[#22314D]">
                <button
                  type="button"
                  onClick={() => setShowBuildModal(false)}
                  className="px-4 py-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] font-bold"
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  disabled={loading}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold hover:bg-[#34D399]"
                >
                  {loading ? "Triggering..." : "Start Build"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
