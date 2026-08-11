"use client";

import React, { useState } from "react";
import Link from "next/link";
import { MailCheck, Shield, ArrowRight, CheckCircle2 } from "lucide-react";

export default function VerifyEmailPage() {
  const [resent, setResent] = useState(false);

  return (
    <div className="min-h-screen bg-[#0A1020] flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-8 shadow-2xl space-y-6 text-center">
        <div className="flex items-center justify-center gap-2">
          <div className="p-2 rounded-lg bg-[#16233A] text-[#3DD9C4]">
            <Shield className="w-5 h-5" />
          </div>
          <span className="font-heading font-bold text-lg text-[#E7EDF7]">CloudForge AI</span>
        </div>

        <div className="w-14 h-14 rounded-full bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 flex items-center justify-center mx-auto">
          <MailCheck className="w-7 h-7" />
        </div>

        <div>
          <h1 className="text-xl font-heading font-bold text-[#E7EDF7]">Verify Your Email Address</h1>
          <p className="text-xs text-[#8B99B8] mt-2">
            We sent a verification link to your registered email address. Please click the link to verify your account and activate your organization.
          </p>
        </div>

        {resent && (
          <div className="p-3 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center justify-center gap-2">
            <CheckCircle2 className="w-4 h-4" />
            <span>Verification email resent!</span>
          </div>
        )}

        <div className="space-y-3 pt-2">
          <button
            onClick={() => setResent(true)}
            className="w-full py-2.5 rounded-xl bg-[#16233A] border border-[#3DD9C4]/40 text-[#3DD9C4] font-heading font-bold text-xs hover:bg-[#3DD9C4] hover:text-[#0A1020] transition-all"
          >
            Resend Verification Email
          </button>

          <Link
            href="/"
            className="w-full py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-bold text-xs hover:bg-[#34D399] transition-all flex items-center justify-center gap-1.5"
          >
            Continue to Dashboard
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>
    </div>
  );
}
