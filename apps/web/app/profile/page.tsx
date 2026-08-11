"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { User, Lock, Save, Key, CheckCircle2, AlertCircle } from "lucide-react";
import { api, UserProfileResponse } from "@/lib/api";

export default function ProfilePage() {
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
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header userFullName={fullName || "Platform Engineer"} />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Account & Security Profile</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Manage your identity credentials, active session security, and account preferences</p>
          </div>

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Personal Information */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-6">
              <User className="w-5 h-5 text-[#3DD9C4]" />
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Personal Details</h2>
            </div>

            {profileMessage && (
              <div className="mb-4 p-3 rounded-lg bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
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
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Email Address</label>
                  <input
                    type="email"
                    disabled
                    value={profile?.email || "admin@cloudforge.ai"}
                    className="w-full bg-[#0A1020]/60 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#8B99B8] cursor-not-allowed font-mono"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5"
                >
                  <Save className="w-4 h-4" />
                  Save Changes
                </button>
              </div>
            </form>
          </div>

          {/* Change Password */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-6">
              <Lock className="w-5 h-5 text-[#3DD9C4]" />
              <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Security & Credentials</h2>
            </div>

            {passwordMessage && (
              <div className="mb-4 p-3 rounded-lg bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
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
                  className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
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
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Confirm New Password</label>
                  <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <div className="flex justify-end pt-2">
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-[#16233A] border border-[#3DD9C4]/40 text-[#3DD9C4] font-heading font-semibold text-xs hover:bg-[#3DD9C4] hover:text-[#0A1020] transition-all flex items-center gap-1.5"
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
