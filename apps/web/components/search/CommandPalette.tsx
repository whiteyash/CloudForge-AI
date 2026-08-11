"use client";

import React, { useState, useEffect } from "react";
import { Search, Building2, Users, Mail, Activity, ArrowRight, X } from "lucide-react";
import Link from "next/link";

interface CommandPaletteProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function CommandPalette({ isOpen, onClose }: CommandPaletteProps) {
  const [query, setQuery] = useState("");

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.metaKey || e.ctrlKey) && e.key === "k") {
        e.preventDefault();
        if (isOpen) onClose();
        else setQuery("");
      }
      if (e.key === "Escape" && isOpen) {
        onClose();
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const quickLinks = [
    { label: "Organizations Directory", href: "/organizations", icon: Building2 },
    { label: "Organization Members & Roles", href: "/members", icon: Users },
    { label: "Invitation Center", href: "/invitations", icon: Mail },
    { label: "Activity Timeline Feed", href: "/activity", icon: Activity },
    { label: "Permissions Matrix Explorer", href: "/settings/permissions", icon: Search },
  ];

  const filteredLinks = quickLinks.filter((item) =>
    item.label.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="fixed inset-0 bg-[#0A1020]/80 backdrop-blur-sm flex items-start justify-center pt-20 p-4 z-50 animate-in fade-in">
      <div className="bg-[#111B2E] border border-[#22314D] rounded-2xl w-full max-w-xl shadow-2xl overflow-hidden">
        {/* Search Header */}
        <div className="relative border-b border-[#22314D] p-4 flex items-center">
          <Search className="w-5 h-5 text-[#3DD9C4] absolute left-4" />
          <input
            type="text"
            autoFocus
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Type a command or search workspace... (Esc to close)"
            className="w-full bg-transparent pl-8 pr-8 text-sm text-[#E7EDF7] focus:outline-none font-mono"
          />
          <button onClick={onClose} className="text-[#8B99B8] hover:text-[#E7EDF7]">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Search Results */}
        <div className="p-4 max-h-80 overflow-y-auto space-y-1">
          {filteredLinks.map((item) => {
            const Icon = item.icon;
            return (
              <Link
                key={item.href}
                href={item.href}
                onClick={onClose}
                className="flex items-center justify-between p-3 rounded-xl hover:bg-[#16233A] transition-all text-xs font-heading font-medium text-[#E7EDF7] group"
              >
                <div className="flex items-center gap-3">
                  <Icon className="w-4 h-4 text-[#3DD9C4]" />
                  <span>{item.label}</span>
                </div>
                <ArrowRight className="w-3.5 h-3.5 text-[#8B99B8] group-hover:text-[#3DD9C4] transition-colors" />
              </Link>
            );
          })}
        </div>
      </div>
    </div>
  );
}
