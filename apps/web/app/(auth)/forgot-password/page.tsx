"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Mail, ArrowLeft, CheckCircle2, Shield } from "lucide-react";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
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
              <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">Reset Your Password</h1>
              <p className="text-xs text-[#8B99B8] mt-1">Enter your registered email address and we&apos;ll send a password recovery token</p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Email Address</label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@company.com"
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>
              </div>

              <button
                type="submit"
                className="w-full py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                Send Password Reset Token
              </button>
            </form>
          </>
        ) : (
          <div className="space-y-4 text-center py-4">
            <div className="w-12 h-12 rounded-full bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30 flex items-center justify-center mx-auto">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <h2 className="text-lg font-heading font-bold text-[#E7EDF7]">Recovery Link Sent</h2>
            <p className="text-xs text-[#8B99B8]">
              We have dispatched a password reset token to <span className="font-mono text-[#3DD9C4]">{email}</span>. Please check your inbox.
            </p>
          </div>
        )}

        <div className="pt-4 border-t border-[#22314D] text-center">
          <Link href="/login" className="inline-flex items-center gap-1.5 text-xs text-[#8B99B8] hover:text-[#3DD9C4] transition-all">
            <ArrowLeft className="w-3.5 h-3.5" />
            Back to Sign In
          </Link>
        </div>
      </div>
    </div>
  );
}
