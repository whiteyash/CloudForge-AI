"use client";

import React, { useState } from "react";
import { Search, Bell, LogOut, ChevronDown, ShieldCheck } from "lucide-react";
import Link from "next/link";
import CommandPalette from "@/components/search/CommandPalette";

interface HeaderProps {
  userEmail?: string;
  userFullName?: string;
  onLogout?: () => void;
}

export default function Header({ userEmail = "admin@cloudforge.ai", userFullName = "Platform Engineer", onLogout }: HeaderProps) {
  const [environment, setEnvironment] = useState<"dev" | "staging" | "prod">("dev");
  const [showMenu, setShowMenu] = useState(false);
  const [commandPaletteOpen, setCommandPaletteOpen] = useState(false);

  return (
    <>
      <header className="h-14 bg-[#111B2E] border-b border-[#22314D] px-6 flex items-center justify-between shrink-0 z-20">
        {/* Environment Selector Pill & Quick Search */}
        <div className="flex items-center gap-4">
          <div className="flex items-center p-1 rounded-lg bg-[#0A1020] border border-[#22314D]">
            {(["dev", "staging", "prod"] as const).map((env) => (
              <button
                key={env}
                onClick={() => setEnvironment(env)}
                className={`px-3 py-1 text-xs font-mono font-medium rounded-md uppercase transition-all ${
                  environment === env
                    ? env === "prod"
                      ? "bg-[#F87171]/20 text-[#F87171] border border-[#F87171]/40"
                      : env === "staging"
                      ? "bg-[#FBBF24]/20 text-[#FBBF24] border border-[#FBBF24]/40"
                      : "bg-[#3DD9C4]/20 text-[#3DD9C4] border border-[#3DD9C4]/40"
                    : "text-[#8B99B8] hover:text-[#E7EDF7]"
                }`}
              >
                {env}
              </button>
            ))}
          </div>

          {/* ⌘K Command Search Palette Trigger */}
          <div
            onClick={() => setCommandPaletteOpen(true)}
            className="relative hidden md:flex items-center cursor-pointer"
          >
            <Search className="w-3.5 h-3.5 absolute left-3 text-[#8B99B8]" />
            <input
              type="text"
              placeholder="Search workspace... (⌘K)"
              className="w-72 bg-[#0A1020] border border-[#22314D] rounded-lg pl-9 pr-8 py-1.5 text-xs text-[#E7EDF7] placeholder-[#8B99B8] focus:outline-none focus:border-[#3DD9C4] transition-colors font-mono cursor-pointer"
              readOnly
            />
            <kbd className="absolute right-2.5 px-1.5 py-0.5 text-[10px] font-mono text-[#8B99B8] bg-[#16233A] rounded border border-[#22314D]">
              ⌘K
            </kbd>
          </div>
        </div>

        {/* Right Controls: Notifications & User Profile */}
        <div className="flex items-center gap-3">
          <Link
            href="/notifications"
            className="relative p-2 rounded-lg text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#16233A] transition-colors border border-transparent hover:border-[#22314D]"
            title="Notification Center"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#3DD9C4] ring-2 ring-[#111B2E]" />
          </Link>

          {/* User Dropdown */}
          <div className="relative">
            <button
              onClick={() => setShowMenu(!showMenu)}
              className="flex items-center gap-2 px-2.5 py-1.5 rounded-lg bg-[#0A1020] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-colors text-left"
            >
              <div className="w-7 h-7 rounded-full bg-[#16233A] border border-[#3DD9C4]/30 flex items-center justify-center text-[#3DD9C4] text-xs font-semibold">
                {userFullName.charAt(0)}
              </div>
              <div className="hidden sm:block text-left">
                <div className="text-xs font-medium text-[#E7EDF7] leading-tight truncate max-w-[120px]">{userFullName}</div>
                <div className="text-[10px] text-[#8B99B8] truncate max-w-[120px] font-mono">{userEmail}</div>
              </div>
              <ChevronDown className="w-3.5 h-3.5 text-[#8B99B8]" />
            </button>

            {showMenu && (
              <div className="absolute right-0 mt-2 w-48 bg-[#111B2E] border border-[#22314D] rounded-xl shadow-xl py-1 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3 py-2 border-b border-[#22314D] sm:hidden">
                  <div className="text-xs font-medium text-[#E7EDF7]">{userFullName}</div>
                  <div className="text-[10px] text-[#8B99B8] font-mono">{userEmail}</div>
                </div>
                <Link
                  href="/settings/security-center"
                  className="w-full flex items-center gap-2 px-3 py-2 text-xs text-[#E7EDF7] hover:bg-[#16233A] transition-colors text-left"
                >
                  <ShieldCheck className="w-3.5 h-3.5 text-[#3DD9C4]" />
                  Security Center
                </Link>
                <button
                  onClick={onLogout}
                  className="w-full flex items-center gap-2 px-3 py-2 text-xs text-[#F87171] hover:bg-[#16233A] transition-colors text-left border-t border-[#22314D]"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  Sign Out
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      <CommandPalette
        isOpen={commandPaletteOpen}
        onClose={() => setCommandPaletteOpen(false)}
      />
    </>
  );
}
