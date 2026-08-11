"use client";

import React, { useState, useEffect } from "react";
import { Search, Bell, LogOut, ChevronDown, ShieldCheck, CheckCheck, ExternalLink } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import CommandPalette from "@/components/search/CommandPalette";
import { useEnvironment, EnvironmentType } from "@/context/EnvironmentContext";
import { api } from "@/lib/api";

interface HeaderProps {
  userEmail?: string;
  userFullName?: string;
  onLogout?: () => void;
}

interface NotificationItem {
  id: string;
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  type: "info" | "warning" | "error" | "success";
}

export default function Header({ userEmail = "admin@cloudforge.ai", userFullName = "Platform Engineer", onLogout }: HeaderProps) {
  const router = useRouter();
  const { environment, setEnvironment } = useEnvironment();
  const [showMenu, setShowMenu] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const [commandPaletteOpen, setCommandPaletteOpen] = useState(false);

  const [notifications, setNotifications] = useState<NotificationItem[]>([
    {
      id: "notif-1",
      title: "Pipeline #142 Succeeded",
      message: "Production release v2.4.0 deployed to US-EAST-1 cluster",
      timestamp: "5m ago",
      read: false,
      type: "success",
    },
    {
      id: "notif-2",
      title: "Security Scanner Alert",
      message: "1 High severity vulnerability detected in docker image node:18-alpine",
      timestamp: "22m ago",
      read: false,
      type: "warning",
    },
    {
      id: "notif-3",
      title: "New Team Invitation",
      message: "Developer developer@cloudforge.ai accepted organization invite",
      timestamp: "1h ago",
      read: true,
      type: "info",
    },
  ]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleSignOut = () => {
    if (onLogout) {
      onLogout();
    } else {
      api.logout();
      if (typeof window !== "undefined") {
        window.location.href = "/login?logout=true";
      }
    }
  };

  const markAllRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  return (
    <>
      <header className="h-14 bg-[#111B2E] border-b border-[#22314D] px-6 flex items-center justify-between shrink-0 z-20">
        {/* Environment Selector Pill & Quick Search */}
        <div className="flex items-center gap-4">
          <div className="flex items-center p-1 rounded-lg bg-[#0A1020] border border-[#22314D]">
            {(["dev", "staging", "prod"] as const).map((env) => (
              <button
                key={env}
                onClick={() => setEnvironment(env as EnvironmentType)}
                className={`px-3 py-1 text-xs font-mono font-medium rounded-md uppercase transition-all cursor-pointer ${
                  environment === env
                    ? env === "prod"
                      ? "bg-[#F87171]/20 text-[#F87171] border border-[#F87171]/40 shadow-[0_0_12px_rgba(248,113,113,0.3)]"
                      : env === "staging"
                      ? "bg-[#FBBF24]/20 text-[#FBBF24] border border-[#FBBF24]/40 shadow-[0_0_12px_rgba(251,191,36,0.3)]"
                      : "bg-[#3DD9C4]/20 text-[#3DD9C4] border border-[#3DD9C4]/40 shadow-[0_0_12px_rgba(61,217,196,0.3)]"
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
          {/* Notification Dropdown */}
          <div className="relative">
            <button
              onClick={() => {
                setShowNotifications(!showNotifications);
                setShowMenu(false);
              }}
              className="relative p-2 rounded-lg text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#16233A] transition-colors border border-transparent hover:border-[#22314D] cursor-pointer"
              title="Notification Center"
            >
              <Bell className="w-4 h-4" />
              {unreadCount > 0 && (
                <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[#3DD9C4] ring-2 ring-[#111B2E]" />
              )}
            </button>

            {showNotifications && (
              <div className="absolute right-0 mt-2 w-80 bg-[#111B2E] border border-[#22314D] rounded-xl shadow-2xl py-2 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3.5 py-2 border-b border-[#22314D] flex items-center justify-between">
                  <span className="text-xs font-heading font-semibold text-[#E7EDF7] flex items-center gap-1.5">
                    Notifications
                    {unreadCount > 0 && (
                      <span className="text-[10px] font-mono bg-[#3DD9C4]/20 text-[#3DD9C4] px-1.5 py-0.5 rounded-full border border-[#3DD9C4]/30">
                        {unreadCount} new
                      </span>
                    )}
                  </span>
                  {unreadCount > 0 && (
                    <button
                      onClick={markAllRead}
                      className="text-[10px] font-mono text-[#3DD9C4] hover:underline flex items-center gap-1 cursor-pointer"
                    >
                      <CheckCheck className="w-3 h-3" />
                      Mark all read
                    </button>
                  )}
                </div>

                <div className="max-h-64 overflow-y-auto divide-y divide-[#22314D]/40">
                  {notifications.map((n) => (
                    <div
                      key={n.id}
                      className={`p-3 text-xs transition-colors hover:bg-[#16233A]/60 ${
                        n.read ? "opacity-75" : "bg-[#16233A]/20"
                      }`}
                    >
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-semibold text-[#E7EDF7] font-sans">{n.title}</span>
                        <span className="text-[10px] font-mono text-[#8B99B8]">{n.timestamp}</span>
                      </div>
                      <p className="text-[11px] text-[#8B99B8] leading-tight mb-1">{n.message}</p>
                    </div>
                  ))}
                </div>

                <div className="p-2 border-t border-[#22314D] text-center">
                  <Link
                    href="/notifications"
                    onClick={() => setShowNotifications(false)}
                    className="text-[10px] font-mono text-[#3DD9C4] hover:underline flex items-center justify-center gap-1 cursor-pointer"
                  >
                    <span>View all notifications</span>
                    <ExternalLink className="w-3 h-3" />
                  </Link>
                </div>
              </div>
            )}
          </div>

          {/* User Dropdown */}
          <div className="relative">
            <button
              onClick={() => {
                setShowMenu(!showMenu);
                setShowNotifications(false);
              }}
              className="flex items-center gap-2 px-2.5 py-1.5 rounded-lg bg-[#0A1020] border border-[#22314D] hover:border-[#3DD9C4]/40 transition-colors text-left cursor-pointer"
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
              <div className="absolute right-0 mt-2 w-48 bg-[#111B2E] border border-[#22314D] rounded-xl shadow-2xl py-1 z-50 animate-in fade-in slide-in-from-top-2">
                <div className="px-3 py-2 border-b border-[#22314D] sm:hidden">
                  <div className="text-xs font-medium text-[#E7EDF7]">{userFullName}</div>
                  <div className="text-[10px] text-[#8B99B8] font-mono">{userEmail}</div>
                </div>
                <Link
                  href="/security"
                  onClick={() => setShowMenu(false)}
                  className="w-full flex items-center gap-2 px-3 py-2 text-xs text-[#E7EDF7] hover:bg-[#16233A] transition-colors text-left cursor-pointer"
                >
                  <ShieldCheck className="w-3.5 h-3.5 text-[#3DD9C4]" />
                  Security Center
                </Link>
                <button
                  onClick={handleSignOut}
                  className="w-full flex items-center gap-2 px-3 py-2 text-xs text-[#F87171] hover:bg-[#16233A] transition-colors text-left border-t border-[#22314D] cursor-pointer"
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
