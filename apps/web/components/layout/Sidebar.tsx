"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  LayoutDashboard,
  Boxes,
  GitPullRequest,
  Activity,
  ShieldCheck,
  AlertTriangle,
  Bot,
  ScrollText,
  Settings,
  ChevronLeft,
  ChevronRight,
  Zap,
  Users,
  Mail,
  Building2,
  Lock,
  Sliders,
  Shield,
  FolderGit2,
  X,
} from "lucide-react";

import { useLanguage } from "@/lib/i18n";
import { useMobileSidebar } from "@/context/MobileSidebarContext";

interface SidebarProps {
  currentOrgName?: string;
  userRole?: string;
}

export default function Sidebar({ currentOrgName = "CloudForge System", userRole = "OWNER" }: SidebarProps) {
  const [collapsed, setCollapsed] = useState(false);
  const pathname = usePathname();
  const { t } = useLanguage();
  const { mobileOpen, closeMobileSidebar } = useMobileSidebar();

  const navItems = [
    { label: "Overview", href: "/", icon: LayoutDashboard },
    { label: "Organizations", href: "/organizations", icon: Building2 },
    { label: "Projects", href: "/projects", icon: FolderGit2 },
    { label: "Members", href: "/members", icon: Users },
    { label: "Invitations", href: "/invitations", icon: Mail },
    { label: "Activity Timeline", href: "/activity", icon: Activity },
    { label: "Permissions Matrix", href: "/settings/permissions", icon: Lock },
    { label: "User Preferences", href: "/settings/preferences", icon: Sliders },
    { label: "Personal Audit", href: "/audit/personal", icon: Shield },
    { label: "Kubernetes", href: "/k8s", icon: Boxes },
    { label: "Pipelines", href: "/cicd", icon: GitPullRequest },
    { label: "Security", href: "/security", icon: ShieldCheck },
    { label: "Incidents", href: "/incidents", icon: AlertTriangle },
    { label: "AI Copilot", href: "/ai", icon: Bot, badge: "AI" },
    { label: "Audit Logs", href: "/audit-logs", icon: ScrollText },
    { label: "Settings", href: "/settings", icon: Settings },
  ];

  const renderNavContent = (isMobile = false) => (
    <>
      {/* Animated Phosphor Teal Signal Line along sidebar inner edge */}
      <div className="absolute top-0 right-0 bottom-0 w-[2px] bg-gradient-to-b from-transparent via-[#3DD9C4] to-transparent opacity-80 animate-signal-pulse pointer-events-none" />

      {/* Top Brand Header */}
      <div className="flex items-center justify-between h-14 px-4 border-b border-[#22314D] bg-[#111B2E]/50">
        {(!collapsed || isMobile) && (
          <Link
            href="/"
            onClick={() => isMobile && closeMobileSidebar()}
            className="flex items-center gap-2 font-heading font-bold text-lg text-[#E7EDF7] tracking-tight"
          >
            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-[#3DD9C4] to-[#16233A] flex items-center justify-center text-[#0A1020] shadow-[0_0_12px_rgba(61,217,196,0.4)]">
              <Zap className="w-4 h-4 text-[#0A1020] stroke-[2.5]" />
            </div>
            <span>CloudForge</span>
            <span className="text-[10px] uppercase tracking-widest px-1.5 py-0.5 rounded bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30 font-mono">
              AI
            </span>
          </Link>
        )}

        {collapsed && !isMobile && (
          <div className="w-full flex justify-center">
            <div className="w-8 h-8 rounded-lg bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 flex items-center justify-center text-[#3DD9C4]">
              <Zap className="w-4 h-4 stroke-[2.5]" />
            </div>
          </div>
        )}

        {!isMobile ? (
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="p-1 rounded-md text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#16233A] transition-colors"
            title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          >
            {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
          </button>
        ) : (
          <button
            onClick={closeMobileSidebar}
            className="p-1.5 rounded-lg text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#16233A] transition-colors"
            title="Close navigation menu"
          >
            <X className="w-5 h-5 text-[#8B99B8]" />
          </button>
        )}
      </div>

      {/* Organization Badge / Role Pill */}
      {(!collapsed || isMobile) && (
        <div className="mx-3 my-3 p-2.5 rounded-lg bg-[#111B2E] border border-[#22314D] flex items-center justify-between">
          <div className="truncate">
            <div className="text-xs font-medium text-[#E7EDF7] truncate">{currentOrgName}</div>
            <div className="text-[10px] font-mono text-[#8B99B8] uppercase tracking-wider">ROLE: {userRole}</div>
          </div>
          <span className="w-2 h-2 rounded-full bg-[#34D399] shadow-[0_0_8px_#34D399]" title="Cluster Connected" />
        </div>
      )}

      {/* Navigation Menu */}
      <nav className="flex-1 px-2 py-2 space-y-1 overflow-y-auto">
        {navItems.map((item) => {
          const isActive = pathname === item.href;
          const Icon = item.icon;

          return (
            <Link
              key={item.href}
              href={item.href}
              onClick={() => isMobile && closeMobileSidebar()}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all group relative ${
                isActive
                  ? "bg-[#16233A] text-[#3DD9C4] border border-[#3DD9C4]/30 shadow-[0_0_12px_rgba(61,217,196,0.15)]"
                  : "text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#111B2E]"
              }`}
            >
              <Icon className={`w-4 h-4 shrink-0 ${isActive ? "text-[#3DD9C4]" : "group-hover:text-[#E7EDF7]"}`} />

              {(!collapsed || isMobile) && (
                <span className="flex-1 truncate">{t(item.label)}</span>
              )}

              {(!collapsed || isMobile) && item.badge && (
                <span className="px-1.5 py-0.2 text-[10px] font-mono font-semibold rounded bg-[#3DD9C4]/20 text-[#3DD9C4] border border-[#3DD9C4]/40">
                  {item.badge}
                </span>
              )}

              {/* Tooltip for rail mode on desktop */}
              {collapsed && !isMobile && (
                <div className="absolute left-full ml-2 px-2 py-1 bg-[#16233A] border border-[#22314D] text-[#E7EDF7] text-xs rounded shadow-lg opacity-0 group-hover:opacity-100 pointer-events-none whitespace-nowrap z-50">
                  {t(item.label)}
                </div>
              )}
            </Link>
          );
        })}
      </nav>

      {/* System Status Footbar */}
      <div className="p-3 border-t border-[#22314D] bg-[#111B2E]/30">
        {!collapsed || isMobile ? (
          <div className="flex items-center justify-between text-xs text-[#8B99B8]">
            <span className="font-mono text-[11px]">v0.1.0-SPRING</span>
            <div className="flex items-center gap-1.5 text-[11px] text-[#34D399]">
              <span className="w-1.5 h-1.5 rounded-full bg-[#34D399] animate-ping" />
              SYSTEM OK
            </div>
          </div>
        ) : (
          <div className="flex justify-center">
            <span className="w-2 h-2 rounded-full bg-[#34D399]" />
          </div>
        )}
      </div>
    </>
  );

  return (
    <>
      {/* DESKTOP SIDEBAR (>= 1024px) - LOCKED 100% UNCHANGED */}
      <aside
        className={`hidden lg:flex relative flex-col h-screen bg-[#0A1020] border-r border-[#22314D] transition-all duration-300 z-30 ${
          collapsed ? "w-16" : "w-60"
        }`}
      >
        {renderNavContent(false)}
      </aside>

      {/* MOBILE NAVIGATION DRAWER (< 1024px) */}
      {mobileOpen && (
        <div className="lg:hidden fixed inset-0 z-50 flex">
          {/* Backdrop Overlay */}
          <div
            className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-md transition-opacity duration-300"
            onClick={closeMobileSidebar}
          />

          {/* Drawer Container */}
          <aside className="relative flex flex-col h-full w-72 max-w-[80vw] bg-[#0A1020] border-r border-[#22314D] shadow-2xl z-50 animate-in slide-in-from-left duration-300">
            {renderNavContent(true)}
          </aside>
        </div>
      )}
    </>
  );
}
