"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { User, Lock, Save, Key, CheckCircle2, AlertCircle } from "lucide-react";
import { api, UserProfileResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function ProfilePage() {
  const { environment, environmentConfig } = useEnvironment();
  const [profile, setProfile] = useState<UserProfileResponse | null>(null);
  const [fullName, setFullName] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  
  const [profileMessage, setProfileMessage] = useState<string | null>(null);
  const [passwordMessage, setPasswordMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    api.getProfile()
      .then((data) => {
        if (isMounted) {
          setProfile(data);
          setFullName(data.fullName || "");
        }
      })
      .catch(() => {
        if (isMounted) {
          setProfile({
            id: "usr-1",
            email: "admin@cloudforge.ai",
            fullName: "Platform Engineer",
            createdAt: new Date().toISOString(),
          });
          setFullName("Platform Engineer");
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setProfileMessage(null);
    setError(null);
    try {
      const updated = await api.updateProfile({ fullName });
      setProfile(updated);
      setProfileMessage("Profile details updated successfully.");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to update profile");
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordMessage(null);
    setError(null);

    if (newPassword !== confirmPassword) {
      setError("New passwords do not match");
      return;
    }

    try {
      await api.changePassword({ oldPassword, newPassword });
      setPasswordMessage("Password changed successfully.");
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Failed to change password");
    }
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header userFullName={fullName || "Platform Engineer"} />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-5xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Account & Security Profile
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Identity credentials, active session security, and account preferences for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Personal Information */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
            <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-6">
              <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                <User className="w-5 h-5" />
              </div>
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Personal Details</h2>
            </div>

            {profileMessage && (
              <div className="mb-4 p-3 rounded-lg bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
                <CheckCircle2 className="w-4 h-4" />
                <span>{profileMessage}</span>
              </div>
            )}

            <form onSubmit={handleUpdateProfile} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Full Name</label>
                  <input
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  />
                </div>
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Email Address</label>
                  <input
                    type="email"
                    disabled
                    value={profile?.email || "admin@cloudforge.ai"}
                    className="w-full bg-[#0A1020]/40 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#8B99B8] cursor-not-allowed font-mono"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
                >
                  <Save className="w-4 h-4 stroke-[2.5]" />
                  Save Changes
                </button>
              </div>
            </form>
          </div>

          {/* Change Password */}
          <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
            <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-6">
              <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                <Lock className="w-5 h-5" />
              </div>
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Security & Credentials</h2>
            </div>

            {passwordMessage && (
              <div className="mb-4 p-3 rounded-lg bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
                <CheckCircle2 className="w-4 h-4" />
                <span>{passwordMessage}</span>
              </div>
            )}

            <form onSubmit={handleChangePassword} className="space-y-4">
              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Current Password</label>
                <input
                  type="password"
                  required
                  value={oldPassword}
                  onChange={(e) => setOldPassword(e.target.value)}
                  className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">New Password</label>
                  <input
                    type="password"
                    required
                    minLength={8}
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  />
                </div>
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Confirm New Password</label>
                  <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2.5 rounded-xl bg-[#0A1020] border border-[#3DD9C4]/40 text-[#3DD9C4] font-heading font-extrabold text-xs hover:bg-[#3DD9C4] hover:text-[#0A1020] transition-all flex items-center gap-1.5 cursor-pointer shadow-[0_0_12px_rgba(61,217,196,0.2)]"
                >
                  <Key className="w-4 h-4" />
                  Update Password
                </button>
              </div>
            </form>
          </div>
        </main>
      </div>
    </div>
  );
}
