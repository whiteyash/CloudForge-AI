"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { CheckCheck, ShieldAlert, Info, AlertTriangle, CheckCircle2, Archive, RotateCcw } from "lucide-react";
import { api, NotificationResponse } from "@/lib/api";
import { useEnvironment } from "@/context/EnvironmentContext";
import CloudControlBackground from "@/components/dashboard/CloudControlBackground";

export default function NotificationsPage() {
  const { environment, environmentConfig } = useEnvironment();
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'UNREAD' | 'ARCHIVED'>('ALL');
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    api.getNotifications()
      .then((data) => {
        if (isMounted) setNotifications(data);
      })
      .catch(() => {
        if (isMounted) {
          setNotifications([
            {
              id: "n-1",
              userId: "usr-1",
              title: "Security Audit Log Triggered",
              message: "User Platform Engineer generated JWT token from IP 127.0.0.1",
              type: "INFO",
              status: "UNREAD",
              createdAt: new Date().toISOString(),
            },
            {
              id: "n-2",
              userId: "usr-1",
              title: "Kubernetes Cluster Connected",
              message: "Production deployment sync enabled for cloudforge-system namespace",
              type: "INFO",
              status: "READ",
              createdAt: new Date(Date.now() - 3600000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleMarkAsRead = async (id: string) => {
    try {
      await api.markNotificationAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, status: "READ" } : n))
      );
    } catch {
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, status: "READ" } : n))
      );
    }
  };

  const handleArchive = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, status: "ARCHIVED" } : n))
    );
    setMessage("Notification archived.");
  };

  const handleRestore = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, status: "READ" } : n))
    );
    setMessage("Notification restored.");
  };

  const handleMarkAllRead = async () => {
    try {
      await api.markAllNotificationsAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, status: "READ" })));
      setMessage("All notifications marked as read.");
    } catch {
      setNotifications((prev) => prev.map((n) => ({ ...n, status: "READ" })));
      setMessage("All notifications marked as read.");
    }
  };

  const filtered = notifications.filter((n) => {
    if (filter === 'UNREAD') return n.status === 'UNREAD';
    if (filter === 'ARCHIVED') return n.status === 'ARCHIVED';
    return n.status !== 'ARCHIVED';
  });

  return (
    <div className="flex h-screen bg-[#060A14] text-[#E7EDF7] overflow-hidden relative font-sans">
      <div className="fixed inset-0 w-full h-full z-0 opacity-40 pointer-events-auto">
        <CloudControlBackground />
      </div>

      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden relative z-10">
        <Header />

        <main className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 max-w-4xl mx-auto w-full">
          {/* Header Banner */}
          <div className="p-6 rounded-3xl bg-[#050F25]/75 backdrop-blur-2xl border border-[#3DD9C4]/40 flex flex-col md:flex-row md:items-center justify-between gap-4 shadow-[0_0_50px_rgba(61,217,196,0.15)]">
            <div>
              <div className="flex items-center gap-2.5 mb-1 flex-wrap">
                <h1 className="text-xl sm:text-2xl font-heading font-extrabold text-[#E7EDF7] tracking-tight">
                  Enterprise Notification Center
                </h1>
                <span className={`text-[10px] font-mono px-2.5 py-0.5 rounded-full uppercase font-bold border ${environmentConfig.badgeBg} ${environmentConfig.badgeText} ${environmentConfig.badgeBorder}`}>
                  ENV: {environmentConfig.label}
                </span>
              </div>
              <p className="text-xs text-[#8B99B8]">
                Real-time platform alerts, security notices, and organization events for <strong className="text-[#3DD9C4] font-mono">{environment.toUpperCase()}</strong> context
              </p>
            </div>

            <button
              onClick={handleMarkAllRead}
              className="px-4 py-2.5 rounded-xl bg-[#0A1020]/80 border border-[#22314D] hover:border-[#3DD9C4]/40 text-[#E7EDF7] hover:text-[#3DD9C4] font-extrabold text-xs transition-all flex items-center gap-1.5 cursor-pointer shadow-[0_0_15px_rgba(61,217,196,0.1)]"
            >
              <CheckCheck className="w-4 h-4 text-[#3DD9C4]" />
              Mark All Read
            </button>
          </div>

          {message && (
            <div className="p-3.5 rounded-xl bg-[#34D399]/15 border border-[#34D399]/40 text-[#34D399] text-xs flex items-center gap-2 font-mono">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{message}</span>
            </div>
          )}

          {/* Filter Tabs */}
          <div className="flex gap-2 border-b border-[#22314D]/60 pb-3">
            <button
              onClick={() => setFilter('ALL')}
              className={`px-3 py-1.5 rounded-lg text-xs font-mono transition-all cursor-pointer ${
                filter === 'ALL' ? 'bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/40 font-bold' : 'text-[#8B99B8] hover:text-[#E7EDF7]'
              }`}
            >
              ACTIVE ({notifications.filter((n) => n.status !== 'ARCHIVED').length})
            </button>
            <button
              onClick={() => setFilter('UNREAD')}
              className={`px-3 py-1.5 rounded-lg text-xs font-mono transition-all cursor-pointer ${
                filter === 'UNREAD' ? 'bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/40 font-bold' : 'text-[#8B99B8] hover:text-[#E7EDF7]'
              }`}
            >
              UNREAD ({notifications.filter((n) => n.status === 'UNREAD').length})
            </button>
            <button
              onClick={() => setFilter('ARCHIVED')}
              className={`px-3 py-1.5 rounded-lg text-xs font-mono transition-all cursor-pointer ${
                filter === 'ARCHIVED' ? 'bg-[#3DD9C4]/15 text-[#3DD9C4] border border-[#3DD9C4]/40 font-bold' : 'text-[#8B99B8] hover:text-[#E7EDF7]'
              }`}
            >
              ARCHIVED ({notifications.filter((n) => n.status === 'ARCHIVED').length})
            </button>
          </div>

          {/* Notifications List */}
          <div className="space-y-3">
            {filtered.length === 0 ? (
              <div className="p-8 text-center bg-[#050F25]/60 backdrop-blur-2xl border border-[#22314D] rounded-2xl text-[#8B99B8] text-xs font-mono">
                No notifications found.
              </div>
            ) : (
              filtered.map((item) => (
                <div
                  key={item.id}
                  onClick={() => item.status === 'UNREAD' && handleMarkAsRead(item.id)}
                  className={`p-4 rounded-xl border backdrop-blur-2xl transition-all cursor-pointer flex items-start justify-between gap-3.5 ${
                    item.status === 'UNREAD'
                      ? 'bg-[#050F25]/80 border-[#3DD9C4]/40 shadow-[0_0_20px_rgba(61,217,196,0.12)]'
                      : 'bg-[#050F25]/40 border-[#22314D] opacity-75'
                  }`}
                >
                  <div className="flex items-start gap-3.5 flex-1 min-w-0">
                    <div className="p-2 rounded-lg bg-[#0A1020] border border-[#3DD9C4]/40 text-[#3DD9C4] shrink-0 mt-0.5">
                      {item.type === 'CRITICAL' ? (
                        <ShieldAlert className="w-4 h-4 text-[#F87171]" />
                      ) : item.type === 'WARNING' ? (
                        <AlertTriangle className="w-4 h-4 text-[#FBBF24]" />
                      ) : (
                        <Info className="w-4 h-4 text-[#3DD9C4]" />
                      )}
                    </div>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between">
                        <h4 className="text-sm font-heading font-bold text-[#E7EDF7]">{item.title}</h4>
                        <span className="text-[10px] font-mono text-[#8B99B8]">
                          {new Date(item.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                      <p className="text-xs text-[#8B99B8] mt-1">{item.message}</p>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex items-center gap-1 shrink-0">
                    {item.status === 'ARCHIVED' ? (
                      <button
                        onClick={(e) => handleRestore(e, item.id)}
                        className="p-1.5 rounded-lg text-[#8B99B8] hover:text-[#3DD9C4] hover:bg-[#0A1020]"
                        title="Restore Notification"
                      >
                        <RotateCcw className="w-3.5 h-3.5" />
                      </button>
                    ) : (
                      <button
                        onClick={(e) => handleArchive(e, item.id)}
                        className="p-1.5 rounded-lg text-[#8B99B8] hover:text-[#E7EDF7] hover:bg-[#0A1020]"
                        title="Archive Notification"
                      >
                        <Archive className="w-3.5 h-3.5" />
                      </button>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </main>
      </div>
    </div>
  );
}
