"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Zap, Lock, Mail, AlertCircle, KeyRound, Eye, EyeOff, ShieldCheck, ArrowRight, Loader2 } from "lucide-react";
import { api } from "@/lib/api";
import CinematicBackground from "@/components/auth/CinematicBackground";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  // Check if user is already authenticated (unless ?logout=true is passed)
  useEffect(() => {
    if (typeof window !== "undefined" && (window.location.search.includes("logout=true") || window.location.search.includes("clear=true"))) {
      api.setToken(null);
      localStorage.removeItem("cf_access_token");
      localStorage.removeItem("cloudforge_jwt_token");
      localStorage.removeItem("cf_user_session");
      return;
    }

    const token = api.getToken();
    if (token) {
      api.me()
        .then(() => {
          router.replace("/");
        })
        .catch(() => {
          api.setToken(null);
        });
    }
  }, [router]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const trimmedEmail = email.trim().toLowerCase();

    try {
      const res = await api.login({ email: trimmedEmail, password });
      if (res && res.accessToken) {
        api.setToken(res.accessToken);
        localStorage.setItem("cloudforge_jwt_token", res.accessToken);
        localStorage.setItem(
          "cf_user_session",
          JSON.stringify({
            email: res.user.email,
            fullName: res.user.fullName,
            role: "OWNER",
            authenticatedAt: new Date().toISOString(),
          })
        );
        router.push("/");
      } else {
        setError("Invalid email address or password. Access denied.");
      }
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message || "Invalid email address or password.");
      } else {
        setError("Invalid email address or password. Access denied.");
      }
    } finally {
      setLoading(false);
    }
  };

  const fillQuickCredentials = (demoEmail: string) => {
    setEmail(demoEmail);
    setPassword("password123");
  };

  return (
    <div className="min-h-screen bg-[#0A1020] text-[#E7EDF7] flex flex-col lg:flex-row relative overflow-hidden font-sans">
      {/* Background Interactive Engine — Viewport-Wide on Mobile & Left Panel on Desktop */}
      <div className="absolute inset-0 w-full h-full lg:relative lg:w-7/12 min-h-screen z-0 border-r border-[#22314D]/40 overflow-hidden">
        <CinematicBackground />
      </div>

      {/* Right/Foreground Glassmorphism Command Console */}
      <div className="w-full lg:w-5/12 min-h-screen flex flex-col justify-center items-center p-4 sm:p-8 lg:p-12 relative z-20 bg-gradient-to-b from-[#0A1020]/40 via-[#0A1020]/60 to-[#0A1020]/80 lg:bg-[#0A1020]/75 backdrop-blur-md lg:backdrop-blur-2xl">
        <div className="w-[92vw] max-w-md bg-[#050F25]/60 backdrop-blur-2xl border border-[#3DD9C4]/45 rounded-3xl p-6 sm:p-10 shadow-[0_0_90px_rgba(61,217,196,0.25)] relative z-10">
          {/* Brand Header */}
          <div className="flex flex-col items-center mb-6 sm:mb-8 text-center">
            <div className="w-12 h-12 sm:w-14 sm:h-14 rounded-2xl bg-gradient-to-br from-[#3DD9C4] via-[#16233A] to-[#0A1020] border border-[#3DD9C4]/50 flex items-center justify-center text-[#0A1020] shadow-[0_0_30px_rgba(61,217,196,0.45)] mb-3 sm:mb-4 group transition-all duration-300 hover:scale-105">
              <Zap className="w-6 h-6 sm:w-7 sm:h-7 text-[#3DD9C4] stroke-[2.5] drop-shadow-[0_0_10px_rgba(61,217,196,0.8)]" />
            </div>
            <span className="text-[9px] sm:text-[10px] font-mono font-bold tracking-widest text-[#3DD9C4] uppercase px-3 py-1 rounded-full bg-[#3DD9C4]/15 border border-[#3DD9C4]/40 mb-2">
              ⚡ ENTERPRISE MISSION CONTROL
            </span>
            <h1 className="text-2xl sm:text-3xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
              CloudForge AI
            </h1>
            <p className="text-[11px] sm:text-xs text-[#8B99B8] mt-1 sm:mt-1.5 max-w-xs">
              Sign in to manage your cloud infrastructure, CI/CD pipelines &amp; AIOps telemetry
            </p>
          </div>

          {/* Error Alert */}
          {error && (
            <div className="mb-5 p-3.5 rounded-2xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2.5 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Sign In Form */}
          <form onSubmit={handleSubmit} className="space-y-4 sm:space-y-5">
            <div>
              <label className="block text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider mb-1.5 flex items-center justify-between">
                <span>EMAIL ADDRESS</span>
                <span className="text-[#3DD9C4]/80 font-mono text-[9px]">REQUIRED</span>
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 absolute left-3.5 top-3.5 text-[#8B99B8]" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="developer@cloudforge.ai"
                  className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl pl-10 pr-4 py-2.5 sm:py-3 text-xs sm:text-sm text-[#E7EDF7] placeholder-[#8B99B8]/40 focus:outline-none focus:border-[#3DD9C4] focus:ring-1 focus:ring-[#3DD9C4]/50 transition-all font-sans"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="text-[10px] font-mono font-bold text-[#8B99B8] uppercase tracking-wider">
                  PASSWORD
                </label>
                <Link
                  href="/forgot-password"
                  className="text-[10px] font-mono font-bold text-[#3DD9C4] hover:underline flex items-center gap-1 cursor-pointer transition-colors"
                >
                  <KeyRound className="w-3 h-3 text-[#3DD9C4]" />
                  Forgot Password?
                </Link>
              </div>
              <div className="relative">
                <Lock className="w-4 h-4 absolute left-3.5 top-3.5 text-[#8B99B8]" />
                <input
                  type={showPassword ? "text" : "password"}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl pl-10 pr-10 py-2.5 sm:py-3 text-xs sm:text-sm text-[#E7EDF7] placeholder-[#8B99B8]/40 focus:outline-none focus:border-[#3DD9C4] focus:ring-1 focus:ring-[#3DD9C4]/50 transition-all font-sans"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-3.5 text-[#8B99B8] hover:text-[#E7EDF7] transition-colors cursor-pointer"
                  aria-label={showPassword ? "Hide password" : "Show password"}
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 px-4 rounded-xl bg-gradient-to-r from-[#4A72FF] to-[#3DD9C4] hover:from-[#3B5BDB] hover:to-[#34D399] text-[#0A1020] font-heading font-extrabold text-xs sm:text-sm flex items-center justify-center gap-2 shadow-[0_0_24px_rgba(61,217,196,0.4)] transition-all transform active:scale-[0.99] disabled:opacity-50 cursor-pointer"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin text-[#0A1020]" />
                  <span>Authenticating Session...</span>
                </>
              ) : (
                <>
                  <span>Sign In to Mission Control</span>
                  <ArrowRight className="w-4 h-4 stroke-[2.5]" />
                </>
              )}
            </button>
          </form>

          {/* Quick Demo Accelerator Pills */}
          <div className="mt-5 pt-4 border-t border-[#22314D]/50">
            <span className="text-[9px] sm:text-[10px] font-mono text-[#8B99B8] block mb-2 font-bold tracking-wider">
              QUICK ACCELERATOR DEMO CREDENTIALS:
            </span>
            <div className="flex flex-wrap items-center gap-2">
              <button
                type="button"
                onClick={() => fillQuickCredentials("developer@cloudforge.ai")}
                className="px-2.5 py-1 rounded-lg bg-[#16233A]/80 border border-[#22314D] text-[#3DD9C4] hover:border-[#3DD9C4] text-[9px] sm:text-[10px] font-mono transition-all cursor-pointer"
              >
                + developer@cloudforge.ai
              </button>
              <button
                type="button"
                onClick={() => fillQuickCredentials("admin@cloudforge.ai")}
                className="px-2.5 py-1 rounded-lg bg-[#16233A]/80 border border-[#22314D] text-[#8B99B8] hover:text-[#E7EDF7] text-[9px] sm:text-[10px] font-mono transition-all cursor-pointer"
              >
                + admin@cloudforge.ai
              </button>
            </div>
          </div>

          {/* Register Account Footer Link */}
          <div className="mt-5 text-center">
            <p className="text-[11px] sm:text-xs text-[#8B99B8]">
              Don&apos;t have an enterprise account?{" "}
              <Link href="/register" className="text-[#3DD9C4] font-semibold hover:underline cursor-pointer">
                Create Workspace
              </Link>
            </p>
          </div>
        </div>

        {/* Footer Security Badges */}
        <div className="mt-6 text-center text-[9px] sm:text-[10px] font-mono text-[#8B99B8]/80 flex items-center justify-center gap-2.5 relative z-10">
          <span className="flex items-center gap-1 text-emerald-400 font-bold">
            <ShieldCheck className="w-3 h-3 text-emerald-400" />
            256-Bit SSL Encrypted
          </span>
          <span>•</span>
          <span>SOC-2 Type II Certified</span>
        </div>
      </div>
    </div>
  );
}
