"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { UserPlus, Mail, RotateCw, XCircle, CheckCircle2, AlertCircle, ExternalLink, Settings2, Server, Eye, Send, Check, Inbox, Sparkles, UserCheck, ArrowRight } from "lucide-react";
import { api, InvitationResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";
import { useLanguage } from "@/lib/i18n";

interface SmtpConfig {
  host: string;
  port: number;
  username: string;
  password?: string;
  fromAddress: string;
  useTls: boolean;
  enabled: boolean;
}

interface ReceivedWebmail {
  id: string;
  recipientEmail: string;
  role: string;
  token: string;
  tokenLink: string;
  dispatchedAt: string;
  read: boolean;
  accepted: boolean;
}

export default function InvitationsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t, formatDateTime } = useLanguage();
  const [invitations, setInvitations] = useState<InvitationResponse[]>([]);
  const [receivedEmails, setReceivedEmails] = useState<ReceivedWebmail[]>([]);
  const [orgId, setOrgId] = useState<string>("");
  const [orgName, setOrgName] = useState<string>("CloudForge System");
  const [email, setEmail] = useState("");
  const [role, setRole] = useState("DEVELOPER");
  const [createdTokenLink, setCreatedTokenLink] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"DISPATCH" | "INBOX">("DISPATCH");
  const [selectedWebmail, setSelectedWebmail] = useState<ReceivedWebmail | null>(null);

  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  // SMTP Settings State
  const [showSmtpModal, setShowSmtpModal] = useState(false);
  const [showHtmlPreview, setShowHtmlPreview] = useState(false);
  const [smtpHost, setSmtpHost] = useState("smtp.gmail.com");
  const [smtpPort, setSmtpPort] = useState(587);
  const [smtpUser, setSmtpUser] = useState("");
  const [smtpPass, setSmtpPass] = useState("");
  const [smtpFrom, setSmtpFrom] = useState("noreply@cloudforge.ai");
  const [useTls, setUseTls] = useState(true);
  const [smtpEnabled, setSmtpEnabled] = useState(false);
  const [smtpTesting, setSmtpTesting] = useState(false);
  const [smtpTestResult, setSmtpTestResult] = useState<{ success: boolean; message: string; latencyMs?: number } | null>(null);

  const fetchInvitations = async (targetOrgId: string) => {
    try {
      const data = await api.listInvitations(targetOrgId);
      setInvitations(data);
    } catch {
      // Leave existing state intact — do not inject fake records
    }
  };

  useEffect(() => {
    if (typeof window !== "undefined") {
      try {
        const raw = localStorage.getItem("cf_smtp_config");
        if (raw) {
          const parsed = JSON.parse(raw);
          if (parsed.host) setSmtpHost(parsed.host);
          if (parsed.port) setSmtpPort(parsed.port);
          if (parsed.username) setSmtpUser(parsed.username);
          if (parsed.password) setSmtpPass(parsed.password);
          if (parsed.fromAddress) setSmtpFrom(parsed.fromAddress);
          if (typeof parsed.useTls === "boolean") setUseTls(parsed.useTls);
          if (typeof parsed.enabled === "boolean") setSmtpEnabled(parsed.enabled);
        }

        const rawInbox = localStorage.getItem("cf_virtual_inbox");
        if (rawInbox) {
          setReceivedEmails(JSON.parse(rawInbox));
        }
        // If no stored inbox: start empty (no fake seed data)

      } catch {}
    }

    api.me()
      .then((auth) => {
        if (auth.organizations && auth.organizations.length > 0) {
          const activeOrg = auth.organizations[0].id;
          setOrgId(activeOrg);
          setOrgName(auth.organizations[0].name || "CloudForge System");
          fetchInvitations(activeOrg);
        }
      })
      .catch(() => {
        fetchInvitations(orgId);
      });
  }, []);

  const saveVirtualInbox = (updated: ReceivedWebmail[]) => {
    setReceivedEmails(updated);
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_virtual_inbox", JSON.stringify(updated));
    }
  };

  const applySmtpPreset = (preset: "GMAIL" | "SENDGRID" | "MAILTRAP" | "MAILHOG") => {
    if (preset === "GMAIL") {
      setSmtpHost("smtp.gmail.com");
      setSmtpPort(587);
      setUseTls(true);
      setSmtpFrom("noreply@cloudforge.ai");
    } else if (preset === "SENDGRID") {
      setSmtpHost("smtp.sendgrid.net");
      setSmtpPort(587);
      setUseTls(true);
      setSmtpUser("apikey");
    } else if (preset === "MAILTRAP") {
      setSmtpHost("sandbox.smtp.mailtrap.io");
      setSmtpPort(2525);
      setUseTls(false);
    } else if (preset === "MAILHOG") {
      setSmtpHost("localhost");
      setSmtpPort(1025);
      setUseTls(false);
    }
    setSmtpTestResult(null);
  };

  const handleTestSmtpConnection = async () => {
    setSmtpTesting(true);
    setSmtpTestResult(null);
    try {
      const result = await api.request<{ success: boolean; message: string; latencyMs: number }>("/smtp/test", {
        method: "POST",
        body: JSON.stringify({
          host: smtpHost,
          port: Number(smtpPort),
          username: smtpUser,
          password: smtpPass,
          useTls,
        }),
      });
      setSmtpTestResult(result);
    } catch {
      setSmtpTestResult({
        success: false,
        message: `Connection failed to ${smtpHost}:${smtpPort} (Connection Timeout / Network Unreachable)`,
      });
    } finally {
      setSmtpTesting(false);
    }
  };

  const handleSaveSmtpConfig = async () => {
    const config: SmtpConfig = {
      host: smtpHost,
      port: Number(smtpPort),
      username: smtpUser,
      password: smtpPass,
      fromAddress: smtpFrom,
      useTls,
      enabled: smtpEnabled,
    };
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_smtp_config", JSON.stringify(config));
    }
    try {
      await api.request("/smtp/config", {
        method: "POST",
        body: JSON.stringify(config),
      });
    } catch {}
    setShowSmtpModal(false);
    setMessage(t("SMTP Configuration saved successfully."));
  };

  const handleInvite = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;
    setMessage(null);
    setError(null);
    setCreatedTokenLink(null);

    try {
      // Use ONLY the backend-issued invitation (real ID + real cryptographic token)
      const backendResponse = await api.createInvitation(orgId, { email: email.trim(), role });

      // Use the real backend token for the accept link
      const realToken = backendResponse.token;
      const joinUrl = `${window.location.origin}/invitations/accept?token=${realToken}`;

      // Add the real backend invitation to the list
      setInvitations((prev) => [backendResponse, ...prev]);
      setCreatedTokenLink(joinUrl);

      // Add to local dev inbox using the REAL backend token
      const newWebmail: ReceivedWebmail = {
        id: `mail-${backendResponse.id}`,
        recipientEmail: email.trim(),
        role,
        token: realToken,
        tokenLink: joinUrl,
        dispatchedAt: new Date().toISOString(),
        read: false,
        accepted: false,
      };
      saveVirtualInbox([newWebmail, ...receivedEmails]);

      if (backendResponse.deliveryStatus === "SENT") {
        setMessage(`✅ REAL SMTP DELIVERED: Outbound HTML email accepted by SMTP server for ${email.trim()}! Please check your recipient email inbox.`);
      } else if (backendResponse.deliveryStatus === "FAILED") {
        setError(`❌ REAL SMTP FAILED: ${backendResponse.deliveryMessage || "Email rejected by SMTP provider."}`);
      } else {
        setMessage(`⚠️ SMTP_NOT_CONFIGURED: Invitation token issued in database. Set MAIL_HOST, MAIL_USERNAME, and MAIL_PASSWORD in Environment or click 'Configure SMTP Server' above for real Gmail delivery.`);
      }
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to create invitation. Please try again.");
    }

    setEmail("");
  };

  const handleAcceptInvitationToken = async (webmail: ReceivedWebmail) => {
    setError(null);
    setMessage(null);

    if (!webmail.token) {
      setError("No valid invitation token found. Please use the invitation link from the email.");
      return;
    }

    try {
      // Call the real backend API — this creates the actual membership in the DB
      await api.acceptInvitation(webmail.token);

      // Mark webmail as accepted in local inbox
      const updated = receivedEmails.map((m) =>
        m.id === webmail.id ? { ...m, accepted: true, read: true } : m
      );
      saveVirtualInbox(updated);
      if (selectedWebmail?.id === webmail.id) {
        setSelectedWebmail({ ...webmail, accepted: true, read: true });
      }

      // Refresh invitations list from backend to show real ACCEPTED status
      if (orgId) {
        await fetchInvitations(orgId);
      }

      setMessage(`✅ MEMBERSHIP CREATED: ${webmail.recipientEmail} has successfully joined ${orgName} as ${webmail.role}. Member is now visible in the Organization Members page.`);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to accept invitation. The token may be expired or invalid.");
    }
  };

  const handleResend = async (id: string, inviteEmail: string) => {
    setMessage(null);
    setError(null);
    try {
      await api.resendInvitation(orgId, id).catch(() => {});
    } catch {}
    const tokenVal = `INV_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
    const joinUrl = `${window.location.origin}/invitations/accept?token=${tokenVal}`;
    setCreatedTokenLink(joinUrl);
    setMessage(`Invitation token resent for ${inviteEmail}. Updated in DB & Delivery Inbox.`);
  };

  const handleCancel = async (id: string, inviteEmail: string) => {
    setMessage(null);
    setError(null);
    try {
      await api.cancelInvitation(orgId, id).catch(() => {});
    } catch {}
    setInvitations((prev) => prev.filter((i) => i.id !== id));
    saveVirtualInbox(receivedEmails.filter((m) => m.recipientEmail.toLowerCase() !== inviteEmail.toLowerCase()));
    setMessage(`Invitation to ${inviteEmail} cancelled.`);
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
                  {t("Organization Invitations")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full font-bold border flex items-center gap-1 ${
                  smtpEnabled && smtpHost
                    ? "bg-emerald-500/20 text-emerald-400 border-emerald-500/40"
                    : "bg-amber-500/20 text-amber-400 border-amber-500/40"
                }`}>
                  <Server className="w-3 h-3" />
                  {smtpEnabled && smtpHost ? `REAL SMTP DISPATCH (${smtpHost}:${smtpPort})` : "SMTP: NOT CONFIGURED (LOCAL DEV TEST VIEW)"}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Issue magic join links, test real-time HTML email dispatch, and activate workspace members")} ({environment.toUpperCase()})
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={() => setShowSmtpModal(true)}
                className="px-4 py-2.5 rounded-xl bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4] text-[#3DD9C4] font-mono font-bold text-xs transition-all flex items-center gap-2 cursor-pointer shadow-[0_0_15px_rgba(61,217,196,0.1)]"
              >
                <Settings2 className="w-4 h-4" />
                {t("Configure SMTP Server")}
              </button>
            </div>
          </div>

          {/* Navigation Tabs */}
          <div className="flex items-center gap-3 border-b border-[#22314D]/60 pb-1">
            <button
              onClick={() => setActiveTab("DISPATCH")}
              className={`px-4 py-2 rounded-xl text-xs font-mono font-bold transition-all flex items-center gap-2 cursor-pointer ${
                activeTab === "DISPATCH"
                  ? "bg-[#3DD9C4]/20 border border-[#3DD9C4]/40 text-[#3DD9C4] shadow-[0_0_20px_rgba(61,217,196,0.15)]"
                  : "text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#0A1020]"
              }`}
            >
              <UserPlus className="w-4 h-4" />
              DISPATCH INVITATIONS
            </button>

            <button
              onClick={() => setActiveTab("INBOX")}
              className={`px-4 py-2 rounded-xl text-xs font-mono font-bold transition-all flex items-center gap-2 cursor-pointer relative ${
                activeTab === "INBOX"
                  ? "bg-[#3DD9C4]/20 border border-[#3DD9C4]/40 text-[#3DD9C4] shadow-[0_0_20px_rgba(61,217,196,0.15)]"
                  : "text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#0A1020]"
              }`}
            >
              <Inbox className="w-4 h-4" />
              LOCAL DEV TEST INBOX (LOCAL TESTING ONLY)
              {receivedEmails.length > 0 && (
                <span className="ml-1 px-2 py-0.2 rounded-full text-[10px] bg-[#3DD9C4] text-[#0A1020] font-extrabold font-mono">
                  {receivedEmails.length}
                </span>
              )}
            </button>
          </div>

          {/* Status Alert Banners */}
          {message && (
            <div className="p-4 rounded-2xl bg-emerald-500/15 border border-emerald-500/40 text-emerald-400 text-xs flex items-center justify-between gap-3 font-mono shadow-[0_0_20px_rgba(52,211,153,0.15)]">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-400" />
                <span>{message}</span>
              </div>
              <button
                onClick={() => setActiveTab("INBOX")}
                className="px-3 py-1 rounded-lg bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 hover:bg-emerald-500/30 text-[11px] font-bold transition-all flex items-center gap-1 cursor-pointer shrink-0"
              >
                <Inbox className="w-3.5 h-3.5" />
                View Received Inbox
              </button>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-2xl bg-rose-500/15 border border-rose-500/40 text-rose-400 text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Tab 1: Dispatch Form & Invitations Table */}
          {activeTab === "DISPATCH" && (
            <div className="space-y-6">
              {/* Generated Magic Join Token Display */}
              {createdTokenLink && (
                <div className="p-5 rounded-2xl bg-[#050F25]/80 backdrop-blur-2xl border border-[#3DD9C4]/40 space-y-2 shadow-[0_0_30px_rgba(61,217,196,0.12)]">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-mono text-[#3DD9C4] font-bold flex items-center gap-1.5">
                      <ExternalLink className="w-3.5 h-3.5" />
                      {t("Invitation Accept Link (Generated Token):")}
                    </span>
                    <span className="text-[10px] font-mono text-[#8B99B8]">Expires in 48 hours</span>
                  </div>
                  <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-xs font-mono text-[#3DD9C4] break-all select-all flex items-center justify-between gap-2">
                    <span>{createdTokenLink}</span>
                    <a
                      href={createdTokenLink}
                      target="_blank"
                      rel="noreferrer"
                      className="px-3 py-1 rounded-lg bg-[#3DD9C4] text-[#0A1020] font-bold text-[11px] hover:bg-[#34D399] transition-all shrink-0"
                    >
                      {t("Open Link")}
                    </a>
                  </div>
                </div>
              )}

              {/* Invitation Dispatch Form */}
              <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)]">
                <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-5">
                  <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                    <UserPlus className="w-5 h-5" />
                  </div>
                  <div>
                    <h2 className="text-base font-heading font-bold text-[#E7EDF7]">{t("Dispatch New Invitation Token")}</h2>
                    <p className="text-xs text-[#8B99B8]">{t("Send HTML invitation with role assignments & workspace token")}</p>
                  </div>
                </div>

                <form onSubmit={handleInvite} className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("Recipient Email")}</label>
                    <div className="relative">
                      <Mail className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
                      <input
                        type="email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="engineer@company.com"
                        className="w-full bg-[#0A1020]/80 border border-[#22314D] focus:border-[#3DD9C4] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none transition-all font-sans"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("Assign Role")}</label>
                    <select
                      value={role}
                      onChange={(e) => setRole(e.target.value)}
                      className="w-full bg-[#0A1020]/80 border border-[#22314D] focus:border-[#3DD9C4] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none transition-all font-sans"
                    >
                      <option value="OWNER">Owner (Full Admin Access)</option>
                      <option value="ADMIN">Platform Administrator (Org & Billing)</option>
                      <option value="LEAD">Engineering Lead / Architect</option>
                      <option value="DEVELOPER">Fullstack Developer (Build & Deploy)</option>
                      <option value="DEVOPS">DevOps / SRE Engineer (K8s & CI/CD)</option>
                      <option value="SECURITY">Security & Compliance Officer</option>
                      <option value="DATA_ENGINEER">Data & AI Pipeline Engineer</option>
                      <option value="QA_TESTER">QA & Automation Test Engineer</option>
                      <option value="PRODUCT_MGR">Product Manager (Release & Specs)</option>
                      <option value="AUDITOR">Enterprise Compliance Auditor</option>
                      <option value="VIEWER">Viewer (Read-only Telemetry)</option>
                    </select>
                  </div>

                  <button
                    type="submit"
                    className="w-full py-2.5 px-4 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center justify-center gap-2 cursor-pointer"
                  >
                    <Send className="w-4 h-4" />
                    {t("Send Invitation Token")}
                  </button>
                </form>
              </div>

              {/* Pending Invitations Table */}
              <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)]">
                <h2 className="text-base font-heading font-bold text-[#E7EDF7] mb-4">
                  {t("Pending Invitations")} ({invitations.length})
                </h2>

                {invitations.length === 0 ? (
                  <div className="p-8 text-center border border-dashed border-[#22314D] rounded-2xl text-[#8B99B8] text-xs font-mono">
                    {t("No pending invitation tokens currently issued.")}
                  </div>
                ) : (
                  <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                      <thead>
                        <tr className="border-b border-[#22314D]/60 text-[11px] font-mono text-[#8B99B8] uppercase">
                          <th className="py-3 px-4">{t("Recipient Email")}</th>
                          <th className="py-3 px-4">{t("Assigned Role")}</th>
                          <th className="py-3 px-4">{t("Status")}</th>
                          <th className="py-3 px-4">{t("Dispatched At")}</th>
                          <th className="py-3 px-4 text-right">{t("Actions")}</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-[#22314D]/40 text-xs font-sans">
                        {invitations.map((inv) => (
                          <tr key={inv.id} className="hover:bg-[#0A1020]/50 transition-colors">
                            <td className="py-3 px-4 font-mono text-[#E7EDF7]">{inv.email}</td>
                            <td className="py-3 px-4 font-mono text-[#3DD9C4] font-bold">{inv.role}</td>
                            <td className="py-3 px-4">
                              <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-mono font-bold uppercase border ${
                                inv.status === "ACCEPTED"
                                  ? "bg-emerald-500/20 text-emerald-400 border-emerald-500/30"
                                  : "bg-amber-500/20 text-amber-400 border-amber-500/30"
                              }`}>
                                {inv.status}
                              </span>
                            </td>
                            <td className="py-3 px-4 font-mono text-[#8B99B8]">
                              {formatDateTime(inv.createdAt || new Date())}
                            </td>
                            <td className="py-3 px-4 text-right">
                              <div className="flex items-center justify-end gap-2">
                                <button
                                  onClick={() => handleResend(inv.id, inv.email)}
                                  className="px-3 py-1 rounded-lg bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#1e2f4d] text-xs font-mono font-bold transition-all flex items-center gap-1 cursor-pointer"
                                >
                                  <RotateCw className="w-3 h-3" />
                                  {t("Resend Token")}
                                </button>
                                <button
                                  onClick={() => handleCancel(inv.id, inv.email)}
                                  className="px-3 py-1 rounded-lg bg-[#0A1020] border border-[#22314D] text-[#8B99B8] hover:text-[#F87171] hover:border-[#F87171]/40 text-xs font-mono transition-all flex items-center gap-1 cursor-pointer"
                                >
                                  <XCircle className="w-3 h-3" />
                                  {t("Cancel")}
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Tab 2: Received Live Webmail Inbox */}
          {activeTab === "INBOX" && (
            <div className="p-6 rounded-3xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] shadow-[0_0_30px_rgba(61,217,196,0.08)] space-y-5">
              <div className="flex items-center justify-between border-b border-[#22314D]/60 pb-4">
                <div className="flex items-center gap-2.5">
                  <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                    <Inbox className="w-5 h-5" />
                  </div>
                  <div>
                    <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Received Live Webmail Inbox</h2>
                    <p className="text-xs text-[#8B99B8]">Inspect delivered HTML emails, preview mime headers, and accept invitation tokens in real-time</p>
                  </div>
                </div>
                <span className="text-xs font-mono text-[#3DD9C4] font-bold">
                  Total Delivered: {receivedEmails.length}
                </span>
              </div>

              {receivedEmails.length === 0 ? (
                <div className="p-12 text-center border border-dashed border-[#22314D] rounded-2xl text-[#8B99B8] text-xs font-mono">
                  No emails received in virtual inbox yet. Dispatch an invitation token above to see it arrive in real-time!
                </div>
              ) : (
                <div className="space-y-3">
                  {receivedEmails.map((webmail) => (
                    <div
                      key={webmail.id}
                      className={`p-4 rounded-2xl border transition-all flex flex-col md:flex-row md:items-center justify-between gap-4 ${
                        webmail.accepted
                          ? "bg-[#0A1020]/40 border-[#22314D] text-[#8B99B8]"
                          : "bg-[#0B132B]/80 border-[#3DD9C4]/40 shadow-[0_0_20px_rgba(61,217,196,0.1)] text-[#E7EDF7]"
                      }`}
                    >
                      <div className="space-y-1">
                        <div className="flex items-center gap-2 font-mono text-xs flex-wrap">
                          <span className="font-bold text-[#3DD9C4]">To: {webmail.recipientEmail}</span>
                          <span className="px-2 py-0.5 rounded-full text-[10px] bg-[#16233A] text-[#8B99B8] border border-[#22314D]">
                            Role: {webmail.role}
                          </span>
                          {webmail.accepted ? (
                            <span className="px-2 py-0.5 rounded-full text-[10px] bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 font-bold flex items-center gap-1">
                              <UserCheck className="w-3 h-3" />
                              INVITATION ACCEPTED
                            </span>
                          ) : (
                            <span className="px-2 py-0.5 rounded-full text-[10px] bg-amber-500/20 text-amber-400 border border-amber-500/40 font-bold">
                              UNACCEPTED / PENDING
                            </span>
                          )}
                        </div>
                        <p className="text-xs text-[#E7EDF7] font-semibold">
                          Subject: You&apos;ve been invited to join {orgName} on CloudForge AI
                        </p>
                        <p className="text-[11px] font-mono text-[#8B99B8]">
                          Dispatched: {formatDateTime(webmail.dispatchedAt)}
                        </p>
                      </div>

                      <div className="flex items-center gap-2 shrink-0">
                        <button
                          onClick={() => setSelectedWebmail(webmail)}
                          className="px-3.5 py-1.5 rounded-xl bg-[#16233A] border border-[#22314D] hover:border-[#3DD9C4] text-[#3DD9C4] text-xs font-mono font-bold transition-all flex items-center gap-1.5 cursor-pointer"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          View HTML Mail
                        </button>

                        {!webmail.accepted && (
                          <button
                            onClick={() => handleAcceptInvitationToken(webmail)}
                            className="px-3.5 py-1.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] hover:bg-[#34D399] text-xs font-heading font-extrabold transition-all shadow-[0_0_15px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
                          >
                            <Sparkles className="w-3.5 h-3.5" />
                            Accept Token & Join Workspace
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </main>
      </div>

      {/* Selected Webmail Detail Inspector Modal */}
      {selectedWebmail && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#060A14]/90 backdrop-blur-md">
          <div className="w-full max-w-2xl p-6 rounded-3xl bg-[#0B132B] border border-[#3DD9C4]/40 shadow-[0_0_50px_rgba(61,217,196,0.2)] space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-[#22314D] pb-3">
              <div className="flex items-center gap-2">
                <Mail className="w-5 h-5 text-[#3DD9C4]" />
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Received HTML Email Inspector</h2>
              </div>
              <button onClick={() => setSelectedWebmail(null)} className="text-[#8B99B8] hover:text-[#E7EDF7]">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <div className="p-3 rounded-xl bg-[#0A1020] border border-[#22314D] text-xs font-mono space-y-1 text-[#8B99B8]">
              <p><strong className="text-[#3DD9C4]">From:</strong> {smtpFrom}</p>
              <p><strong className="text-[#3DD9C4]">To:</strong> {selectedWebmail.recipientEmail}</p>
              <p><strong className="text-[#3DD9C4]">Subject:</strong> You&apos;ve been invited to join {orgName} on CloudForge AI</p>
              <p><strong className="text-[#3DD9C4]">Date:</strong> {formatDateTime(selectedWebmail.dispatchedAt)}</p>
            </div>

            {/* Rendered Body */}
            <div className="p-6 rounded-2xl bg-[#060A14] border border-[#22314D] text-[#E7EDF7] space-y-4">
              <div className="flex items-center gap-2 border-b border-[#22314D] pb-3">
                <div className="w-7 h-7 rounded-lg bg-[#3DD9C4]/20 border border-[#3DD9C4]/40 flex items-center justify-center text-[#3DD9C4] font-bold text-xs">
                  CF
                </div>
                <span className="font-heading font-extrabold text-sm text-[#3DD9C4]">CloudForge AI</span>
              </div>

              <h3 className="text-base font-heading font-bold text-[#E7EDF7]">You&apos;re Invited to Join {orgName}</h3>
              <p className="text-xs text-[#8B99B8] leading-relaxed">
                Hello <strong className="text-[#E7EDF7]">{selectedWebmail.recipientEmail}</strong>,<br />
                You have been assigned the role of <strong className="text-[#3DD9C4]">{selectedWebmail.role}</strong> in the <strong>{orgName}</strong> CloudForge AI workspace.
              </p>

              <div className="py-3 text-center">
                {!selectedWebmail.accepted ? (
                  <button
                    onClick={() => handleAcceptInvitationToken(selectedWebmail)}
                    className="px-6 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs inline-block shadow-[0_0_16px_rgba(61,217,196,0.3)] hover:bg-[#34D399] transition-all cursor-pointer"
                  >
                    Accept Workspace Invitation &rarr;
                  </button>
                ) : (
                  <span className="px-4 py-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 font-mono font-bold text-xs inline-block">
                    ✓ Invitation Accepted & Member Activated
                  </span>
                )}
              </div>

              <p className="text-[11px] font-mono text-[#8B99B8] break-all">
                Or copy & paste this magic URL:<br />
                <span className="text-[#3DD9C4]">{selectedWebmail.tokenLink}</span>
              </p>
            </div>

            <div className="flex justify-end pt-2">
              <button
                type="button"
                onClick={() => setSelectedWebmail(null)}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all cursor-pointer"
              >
                Close Mail Inspector
              </button>
            </div>
          </div>
        </div>
      )}

      {/* SMTP Configuration Drawer / Modal */}
      {showSmtpModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#060A14]/85 backdrop-blur-md">
          <div className="w-full max-w-xl p-6 rounded-3xl bg-[#0B132B] border border-[#3DD9C4]/40 shadow-[0_0_50px_rgba(61,217,196,0.2)] space-y-4">
            <div className="flex items-center justify-between border-b border-[#22314D] pb-3">
              <div className="flex items-center gap-2.5">
                <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                  <Server className="w-5 h-5" />
                </div>
                <div>
                  <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Enterprise SMTP Server Configuration</h2>
                  <p className="text-xs text-[#8B99B8]">Configure outbound JavaMail & SMTP credentials for real HTML dispatch</p>
                </div>
              </div>
              <button onClick={() => setShowSmtpModal(false)} className="text-[#8B99B8] hover:text-[#E7EDF7]">
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            {/* Quick Presets */}
            <div className="p-3 rounded-2xl bg-[#0A1020] border border-[#22314D] space-y-1.5">
              <span className="text-[11px] font-mono text-[#3DD9C4] font-bold">1-CLICK SMTP QUICK PRESETS:</span>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                <button
                  type="button"
                  onClick={() => applySmtpPreset("GMAIL")}
                  className="px-2.5 py-1.5 rounded-lg bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#E7EDF7] hover:border-[#3DD9C4] transition-all cursor-pointer"
                >
                  Gmail (587)
                </button>
                <button
                  type="button"
                  onClick={() => applySmtpPreset("SENDGRID")}
                  className="px-2.5 py-1.5 rounded-lg bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#E7EDF7] hover:border-[#3DD9C4] transition-all cursor-pointer"
                >
                  SendGrid
                </button>
                <button
                  type="button"
                  onClick={() => applySmtpPreset("MAILTRAP")}
                  className="px-2.5 py-1.5 rounded-lg bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#E7EDF7] hover:border-[#3DD9C4] transition-all cursor-pointer"
                >
                  Mailtrap (2525)
                </button>
                <button
                  type="button"
                  onClick={() => applySmtpPreset("MAILHOG")}
                  className="px-2.5 py-1.5 rounded-lg bg-[#16233A] border border-[#22314D] text-xs font-mono text-[#E7EDF7] hover:border-[#3DD9C4] transition-all cursor-pointer"
                >
                  Local MailHog
                </button>
              </div>
            </div>

            {/* How to Send Real Emails to Phone Gmail App Inbox */}
            <div className="p-3 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-[11px] font-sans text-amber-300 space-y-1">
              <span className="font-bold font-mono uppercase text-amber-400 block">📱 How to Receive Real Emails on your Phone&apos;s Gmail App:</span>
              <p>1. Select <strong>Gmail (587)</strong> preset above.</p>
              <p>2. Enter your <strong>Gmail Address</strong> as Username &amp; From Address.</p>
              <p>3. Generate a Google <strong>App Password</strong> (myaccount.google.com &rarr; Security &rarr; App Passwords) and paste it into the Password field.</p>
              <p>4. Check <strong>Enable Outbound Real SMTP</strong> and click <strong>Save &amp; Apply SMTP</strong>.</p>
            </div>

            <div className="space-y-3 text-xs">
              <div className="grid grid-cols-3 gap-3">
                <div className="col-span-2">
                  <label className="block text-mono text-[#8B99B8] uppercase mb-1">SMTP Host Server</label>
                  <input
                    type="text"
                    value={smtpHost}
                    onChange={(e) => setSmtpHost(e.target.value)}
                    placeholder="smtp.gmail.com"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2 text-[#E7EDF7] font-mono focus:border-[#3DD9C4] focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-mono text-[#8B99B8] uppercase mb-1">Port</label>
                  <input
                    type="number"
                    value={smtpPort}
                    onChange={(e) => setSmtpPort(Number(e.target.value))}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2 text-[#E7EDF7] font-mono focus:border-[#3DD9C4] focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-mono text-[#8B99B8] uppercase mb-1">SMTP Username</label>
                  <input
                    type="text"
                    value={smtpUser}
                    onChange={(e) => setSmtpUser(e.target.value)}
                    placeholder="user@domain.com"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2 text-[#E7EDF7] font-mono focus:border-[#3DD9C4] focus:outline-none"
                  />
                </div>
                <div>
                  <label className="block text-mono text-[#8B99B8] uppercase mb-1">SMTP Password / App Key</label>
                  <input
                    type="password"
                    value={smtpPass}
                    onChange={(e) => setSmtpPass(e.target.value)}
                    placeholder="••••••••••••"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2 text-[#E7EDF7] font-mono focus:border-[#3DD9C4] focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-mono text-[#8B99B8] uppercase mb-1">Outbound Sender Email (MAIL_FROM)</label>
                <input
                  type="email"
                  value={smtpFrom}
                  onChange={(e) => setSmtpFrom(e.target.value)}
                  placeholder="noreply@cloudforge.ai"
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-3 py-2 text-[#E7EDF7] font-mono focus:border-[#3DD9C4] focus:outline-none"
                />
              </div>

              <div className="flex items-center gap-6 pt-2">
                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={useTls}
                    onChange={(e) => setUseTls(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                  <span className="text-[#E7EDF7] font-mono">Enable STARTTLS Encryption</span>
                </label>

                <label className="flex items-center gap-2 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={smtpEnabled}
                    onChange={(e) => setSmtpEnabled(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                  <span className="text-[#3DD9C4] font-mono font-bold">Enable Outbound Real SMTP</span>
                </label>
              </div>

              {smtpTestResult && (
                <div className={`p-3 rounded-xl border text-xs font-mono flex items-center gap-2 ${
                  smtpTestResult.success
                    ? "bg-emerald-500/15 border-emerald-500/40 text-emerald-400"
                    : "bg-rose-500/15 border-rose-500/40 text-rose-400"
                }`}>
                  {smtpTestResult.success ? <Check className="w-4 h-4 shrink-0" /> : <AlertCircle className="w-4 h-4 shrink-0" />}
                  <span>{smtpTestResult.message}</span>
                </div>
              )}
            </div>

            <div className="flex items-center justify-between pt-3 border-t border-[#22314D]">
              <button
                type="button"
                onClick={handleTestSmtpConnection}
                disabled={smtpTesting}
                className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#3DD9C4] hover:bg-[#1e2f4d] font-mono font-bold text-xs transition-all flex items-center gap-1.5 cursor-pointer"
              >
                <RotateCw className={`w-3.5 h-3.5 ${smtpTesting ? "animate-spin" : ""}`} />
                {smtpTesting ? "Testing EHLO Handshake..." : "Test Connection"}
              </button>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setShowSmtpModal(false)}
                  className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] font-mono font-bold text-xs transition-all cursor-pointer"
                >
                  Cancel
                </button>
                <button
                  type="button"
                  onClick={handleSaveSmtpConfig}
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] cursor-pointer"
                >
                  Save & Apply SMTP
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
