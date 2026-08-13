"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Sliders, Bell, Save, CheckCircle2, AlertCircle } from "lucide-react";
import { api, UserPreferencesResponse, NotificationPreferencesResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import { useLanguage, LanguageCode } from "@/lib/i18n";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function PreferencesPage() {
  const { environment, environmentConfig } = useEnvironment();
  const { t, setLanguage: setGlobalLanguage, setTimezone: setGlobalTimezone } = useLanguage();
  const [language, setLanguage] = useState("en");
  const [timezone, setTimezone] = useState("Asia/Kolkata");
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
          setTimezone(prefs.timezone || "Asia/Kolkata");
          setTheme(prefs.theme || "DARK_SLATE");
          setAccentColor(prefs.accentColor || "#3DD9C4");
          const dens = prefs.density || "COMFORTABLE";
          setDensity(dens);
          if (typeof window !== "undefined") {
            const d = dens.toLowerCase() === "compact" ? "compact" : "comfortable";
            document.documentElement.setAttribute("data-density", d);
          }
        }
      })
      .catch(() => {
        if (typeof window !== "undefined") {
          const stored = localStorage.getItem("cf_app_language");
          if (stored) setLanguage(stored);
          const tzStored = localStorage.getItem("cf_app_timezone");
          if (tzStored) setTimezone(tzStored);
          const densStored = localStorage.getItem("cf_app_density");
          if (densStored) {
            setDensity(densStored.toUpperCase());
            document.documentElement.setAttribute("data-density", densStored);
          }
        }
      });

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
      const d = density.toLowerCase() === "compact" ? "compact" : "comfortable";
      if (typeof window !== "undefined") {
        localStorage.setItem("cf_app_language", language);
        localStorage.setItem("cf_app_timezone", timezone);
        localStorage.setItem("cf_app_density", d);
        localStorage.setItem("cf_user_preferences", JSON.stringify({ language, timezone, theme, accentColor, density }));
        document.documentElement.setAttribute("data-density", d);
        window.dispatchEvent(new Event("storage"));
      }
      setGlobalLanguage(language as LanguageCode);
      setGlobalTimezone(timezone);

      await api.updateUserPreferences({ language, timezone, theme, accentColor, density });
      await api.updateNotificationPreferences({
        emailSecurityAlerts: emailSecurity,
        emailOrgEvents,
        emailInvitations,
      });
      setMessage(t("Preferences and notification controls saved."));
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : t("Failed to persist preferences to backend."));
    }
  };

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-5xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  {t("User Experience & Preferences")}
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                {t("Configure localized timezones, Mission Control density, and notification channels for")} <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {error && (
            <div className="p-4 rounded-xl bg-[#F87171]/15 border border-[#F87171]/40 text-[#F87171] text-xs flex items-center gap-2 font-mono">
              <AlertCircle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSavePreferences} className="space-y-6">
            {/* UI Customization */}
            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                  <Sliders className="w-5 h-5" />
                </div>
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">{t("Interface Personalization")}</h2>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("Display Language")}</label>
                  <select
                    value={language}
                    onChange={(e) => setLanguage(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  >
                    <option value="en">English (US)</option>
                    <option value="es">Español</option>
                    <option value="ja">日本語</option>
                    <option value="de">Deutsch</option>
                    <option value="fr">Français</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("User Timezone")}</label>
                  <select
                    value={timezone}
                    onChange={(e) => setTimezone(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  >
                    <option value="Asia/Kolkata">Asia/Kolkata (IST - India)</option>
                    <option value="UTC">UTC (Universal Coordinated Time)</option>
                    <option value="America/New_York">America/New_York (EST)</option>
                    <option value="Europe/London">Europe/London (GMT)</option>
                    <option value="Asia/Tokyo">Asia/Tokyo (JST)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("Accent Color")}</label>
                  <input
                    type="text"
                    value={accentColor}
                    onChange={(e) => setAccentColor(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] font-mono transition-all"
                  />
                </div>

                <div>
                  <label className="block text-xs font-mono text-[#8B99B8] uppercase mb-1.5">{t("Layout Density")}</label>
                  <select
                    value={density}
                    onChange={(e) => setDensity(e.target.value)}
                    className="w-full bg-[#0A1020]/80 border border-[#22314D] rounded-xl px-4 py-2.5 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4] transition-all"
                  >
                    <option value="COMFORTABLE">{t("Comfortable (Default Spacing)")}</option>
                    <option value="COMPACT">{t("Compact (High Information Density)")}</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Notification Dispatch Settings */}
            <div className="p-6 rounded-2xl bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] hover:border-[#3DD9C4]/40 shadow-[0_0_30px_rgba(61,217,196,0.08)] transition-all">
              <div className="flex items-center gap-2.5 pb-4 border-b border-[#22314D]/60 mb-4">
                <div className="p-2 rounded-xl bg-[#3DD9C4]/15 border border-[#3DD9C4]/30 text-[#3DD9C4]">
                  <Bell className="w-5 h-5" />
                </div>
                <h2 className="text-base font-heading font-bold text-[#E7EDF7]">{t("Notification Dispatch Triggers")}</h2>
              </div>

              <div className="space-y-3">
                <label className="flex items-center justify-between p-3.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] cursor-pointer hover:border-[#3DD9C4]/40 transition-all">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">{t("Security Alerts & Lockout Warnings")}</p>
                    <p className="text-[11px] text-[#8B99B8]">{t("Immediate email dispatch on failed logins or password changes")}</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailSecurity}
                    onChange={(e) => setEmailSecurity(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                  />
                </label>

                <label className="flex items-center justify-between p-3.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] cursor-pointer hover:border-[#3DD9C4]/40 transition-all">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">{t("Organization & Member Events")}</p>
                    <p className="text-[11px] text-[#8B99B8]">{t("Notify when members join, leave, or roles change")}</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailOrgEvents}
                    onChange={(e) => setEmailOrgEvents(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                  />
                </label>

                <label className="flex items-center justify-between p-3.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] cursor-pointer hover:border-[#3DD9C4]/40 transition-all">
                  <div>
                    <p className="text-xs font-heading font-bold text-[#E7EDF7]">{t("Invitation Dispatches")}</p>
                    <p className="text-[11px] text-[#8B99B8]">{t("Email invitations issued to new workspace members")}</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={emailInvitations}
                    onChange={(e) => setEmailInvitations(e.target.checked)}
                    className="w-4 h-4 accent-[#3DD9C4] cursor-pointer"
                  />
                </label>
              </div>
            </div>

            <div className="flex justify-end">
              <button
                type="submit"
                className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-extrabold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-1.5 cursor-pointer"
              >
                <Save className="w-4 h-4 stroke-[2.5]" />
                {t("Save Preferences")}
              </button>
            </div>
          </form>
        </main>
      </div>
    </div>
  );
}
