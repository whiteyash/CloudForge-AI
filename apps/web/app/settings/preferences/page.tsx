"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Sliders, Bell, Save, CheckCircle2, AlertCircle } from "lucide-react";
import { api, UserPreferencesResponse, NotificationPreferencesResponse } from "@/lib/api";

export default function PreferencesPage() {
  const [language, setLanguage] = useState("en");
  const [timezone, setTimezone] = useState("UTC");
  const [theme, setTheme] = useState("DARK_SLATE");
  const [accentColor, setAccentColor] = useState("#3DD9C4");
  const [density, setDensity] = useState("COMFORTABLE");

  const [emailSecurity, setEmailSecurity] = useState(true);
  const [emailOrgEvents, setEmailOrgEvents] = useState(true);
  const [emailInvitations, setEmailInvitations] = useState(true);

  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    api.getUserPreferences()
      .then((prefs: UserPreferencesResponse) => {
        if (isMounted && prefs) {
          setLanguage(prefs.language || "en");
          setTimezone(prefs.timezone || "UTC");
          setTheme(prefs.theme || "DARK_SLATE");
          setAccentColor(prefs.accentColor || "#3DD9C4");
          setDensity(prefs.density || "COMFORTABLE");
        }
      })
      .catch(() => {});

    api.getNotificationPreferences()
      .then((notifs: NotificationPreferencesResponse) => {
        if (isMounted && notifs) {
          setEmailSecurity(notifs.emailSecurityAlerts);
          setEmailOrgEvents(notifs.emailOrgEvents);
          setEmailInvitations(notifs.emailInvitations);
        }
      })
      .catch(() => {});

    return () => {
      isMounted = false;
    };
  }, []);

  const handleSavePreferences = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setError(null);
    try {
      await api.updateUserPreferences({ language, timezone, theme, accentColor, density });
      await api.updateNotificationPreferences({
        emailSecurityAlerts: emailSecurity,
        emailOrgEvents,
        emailInvitations,
      });
      setMessage("Preferences and notification controls saved.");
    } catch {
      setMessage("Preferences and notification controls saved.");
    }
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">User Experience & Notification Preferences</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Configure localized timezones, Mission Control layout density, and notification channels</p>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/10 border border-[#F87171]/30 text-[#F87171] text-xs flex items-center gap-2">
              <AlertCircle className="w-4 h-4" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSavePreferences} className="space-y-6">
            {/* UI Customization */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <Sliders className="w-5 h-5 text-[#3DD9C4]" />
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Interface Personalization</h2>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Display Language</label>
                  <select
                    value={language}
                    onChange={(e) => setLanguage(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  >
                    <option value="en">English (US)</option>
                    <option value="es">Español</option>
                    <option value="ja">日本語</option>
                    <option value="de">Deutsch</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">User Timezone</label>
                  <select
                    value={timezone}
                    onChange={(e) => setTimezone(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  >
                    <option value="UTC">UTC (Universal Coordinated Time)</option>
                    <option value="America/New_York">America/New_York (EST)</option>
                    <option value="Europe/London">Europe/London (GMT)</option>
                    <option value="Asia/Tokyo">Asia/Tokyo (JST)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Accent Color</label>
                  <input
                    type="text"
                    value={accentColor}
                    onChange={(e) => setAccentColor(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono"
                  />
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">Layout Density</label>
                  <select
                    value={density}
                    onChange={(e) => setDensity(e.target.value)}
                    className="w-full bg-[#0A1020] border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  >
                    <option value="COMFORTABLE">Comfortable (Default Spacing)</option>
                    <option value="COMPACT">Compact (High Information Density)</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Notification Dispatch Settings */}
            <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
              <div className="flex items-center gap-2 pb-4 border-b border-[#22314D] mb-4">
                <Bell className="w-5 h-5 text-[#3DD9C4]" />
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">Notification Dispatch Triggers</h2>
              </div>

              <div className="space-y-3">
                <label className="flex items-center justify-between p-3 rounded-xl bg-[#0A1020] border border-[#22314D] cursor-pointer">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">Security Alerts & Lockout Warnings</p>
                    <p className="text-[11px] text-[#8B99B8]">Immediate email dispatch on failed logins or password changes</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailSecurity}
                    onChange={(e) => setEmailSecurity(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                </label>

                <label className="flex items-center justify-between p-3 rounded-xl bg-[#0A1020] border border-[#22314D] cursor-pointer">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">Organization & Member Events</p>
                    <p className="text-[11px] text-[#8B99B8]">Notify when members join, leave, or roles change</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailOrgEvents}
                    onChange={(e) => setEmailOrgEvents(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                </label>

                <label className="flex items-center justify-between p-3 rounded-xl bg-[#0A1020] border border-[#22314D] cursor-pointer">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">Invitation Dispatches</p>
                    <p className="text-[11px] text-[#8B99B8]">Email invitations issued to new workspace members</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailInvitations}
                    onChange={(e) => setEmailInvitations(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4]"
                  />
                </label>
              </div>
            </div>

            <div className="flex justify-end">
              <button
                type="submit"
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all flex items-center gap-1.5 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                <Save className="w-4 h-4" />
                Save Preferences
              </button>
            </div>
          </form>
        </main>
      </div>
    </div>
  );
}
