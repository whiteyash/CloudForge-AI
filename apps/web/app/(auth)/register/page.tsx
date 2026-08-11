"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Zap, Lock, Mail, User, Building2, ArrowRight, AlertCircle } from "lucide-react";
import { api } from "@/lib/api";

export default function RegisterPage() {
  const router = useRouter();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [organizationName, setOrganizationName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await api.register({ fullName, email, organizationName, password });
      router.push("/");
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Failed to register account. Please check your inputs.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0A1020] text-[#E7EDF7] flex flex-col justify-center items-center p-6 relative overflow-hidden">
      {/* Background Ambient Glow */}
      <div className="absolute w-[500px] h-[500px] bg-[#3DD9C4]/5 rounded-full blur-3xl pointer-events-none -top-20 -right-20" />
      <div className="absolute w-[400px] h-[400px] bg-[#16233A] rounded-full blur-3xl pointer-events-none -bottom-20 -left-20" />

      <div className="w-full max-w-md bg-[#111B2E] border border-[#22314D] rounded-2xl p-8 shadow-2xl relative z-10">
        {/* Brand Header */}
        <div className="flex flex-col items-center mb-8 text-center">
          <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#3DD9C4] to-[#16233A] flex items-center justify-center text-[#0A1020] shadow-[0_0_20px_rgba(61,217,196,0.4)] mb-3">
            <Zap className="w-6 h-6 text-[#0A1020] stroke-[2.5]" />
          </div>
          <h1 className="text-2xl font-heading font-bold text-[#E7EDF7] tracking-tight">
            Create Platform Account
          </h1>
          <p className="text-xs text-[#8B99B8] mt-1">
            Initialize your organization workspace & role permissions
          </p>
        </div>

        {error && (
          <div className="mb-6 p-3 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-mono font-medium text-[#8B99B8] uppercase mb-1.5">
              Full Name
            </label>
            <div className="relative">
              <User className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="Jane Doe"
                className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] placeholder-[#8B99B8]/60 focus:outline-none focus:border-[#3DD9C4] transition-colors"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-mono font-medium text-[#8B99B8] uppercase mb-1.5">
              Email Address
            </label>
            <div className="relative">
              <Mail className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="jane@company.com"
                className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] placeholder-[#8B99B8]/60 focus:outline-none focus:border-[#3DD9C4] transition-colors"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-mono font-medium text-[#8B99B8] uppercase mb-1.5">
              Organization Name
            </label>
            <div className="relative">
              <Building2 className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
              <input
                type="text"
                required
                value={organizationName}
                onChange={(e) => setOrganizationName(e.target.value)}
                placeholder="Acme Engineering"
                className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] placeholder-[#8B99B8]/60 focus:outline-none focus:border-[#3DD9C4] transition-colors"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-mono font-medium text-[#8B99B8] uppercase mb-1.5">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 absolute left-3.5 top-3 text-[#8B99B8]" />
              <input
                type="password"
                required
                minLength={8}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Minimum 8 characters"
                className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 text-sm text-[#E7EDF7] placeholder-[#8B99B8]/60 focus:outline-none focus:border-[#3DD9C4] transition-colors"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl bg-[#3DD9C4] hover:bg-[#34D399] text-[#0A1020] font-heading font-semibold text-sm transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center justify-center gap-2 mt-2"
          >
            {loading ? "Creating Organization..." : "Create Account"}
            {!loading && <ArrowRight className="w-4 h-4" />}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-[#8B99B8]">
          Already have an account?{" "}
          <Link href="/login" className="text-[#3DD9C4] font-medium hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
