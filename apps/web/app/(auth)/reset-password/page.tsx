"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Lock, Key, CheckCircle2, Shield, AlertCircle } from "lucide-react";

export default function ResetPasswordPage() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }
    setSubmitted(true);
  };

  return (
    <div className="min-h-screen bg-[#0A1020] flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-8 shadow-2xl space-y-6">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
            <Shield className="w-5 h-5" />
          </div>
          <span className="font-heading font-bold text-lg text-[#E7EDF7]">CloudForge AI</span>
        </div>

        {!submitted ? (
          <>
            <div>
              <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">Set New Password</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Please enter your new strong password below</p>
            </div>

            {error && (
              <div className="p-3 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
                <AlertCircle className="w-4 h-4" />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">New Password</label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
                  <input
                    type="password"
                    required
                    minLength={8}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Confirm New Password</label>
                <div className="relative">
                  <Key className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
                  <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                Reset Password
              </button>
            </form>
          </>
        ) : (
          <div className="space-y-4 text-center py-4">
            <div className="w-12 h-12 rounded-full bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30 flex items-center justify-center mx-auto">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <h2 className="text-lg font-heading font-bold text-[#E7EDF7]">Password Reset Successful</h2>
            <p className="text-xs text-[#8B99B8]">Your credentials have been updated successfully. You can now log in with your new password.</p>
            <Link
              href="/login"
              className="inline-block px-6 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all"
            >
              Sign In Now
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
