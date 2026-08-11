"use client";

import React, { useState, useEffect } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Activity, Users, Mail, Building2, Clock, Search } from "lucide-react";
import { api, AuditLogResponse } from "@/lib/api";

export default function ActivityPage() {
  const [logs, setLogs] = useState<AuditLogResponse[]>([]);
  const [search, setSearch] = useState("");
  const [filterAction, setFilterAction] = useState("ALL");
  const [orgId] = useState("default-org-id");

  useEffect(() => {
    let isMounted = true;
    api.getActivityTimeline(orgId)
      .then((data) => {
        if (isMounted) setLogs(data);
      })
      .catch(() => {
        if (isMounted) {
          setLogs([
            {
              id: "act-1",
              orgId,
              userId: "u-1",
              action: "organization.updated",
              target: "cloudforge-engineering",
              createdAt: new Date().toISOString(),
            },
            {
              id: "act-2",
              orgId,
              userId: "u-1",
              action: "member.role_updated",
              target: "sarah.ops@cloudforge.ai:ADMIN",
              createdAt: new Date(Date.now() - 3600000).toISOString(),
            },
            {
              id: "act-3",
              orgId,
              userId: "u-2",
              action: "invitation.created",
              target: "new.engineer@cloudforge.ai:DEVELOPER",
              createdAt: new Date(Date.now() - 7200000).toISOString(),
            },
            {
              id: "act-4",
              orgId,
              userId: "u-1",
              action: "workspace.switched",
              target: "cloudforge-engineering",
              createdAt: new Date(Date.now() - 14400000).toISOString(),
            },
          ]);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [orgId]);

  const filteredLogs = logs.filter((log) => {
    const matchesSearch =
      log.action.toLowerCase().includes(search.toLowerCase()) ||
      log.target.toLowerCase().includes(search.toLowerCase());
    const matchesFilter = filterAction === "ALL" || log.action.startsWith(filterAction.toLowerCase());
    return matchesSearch && matchesFilter;
  });

  const getActionIcon = (action: string) => {
    if (action.startsWith("organization")) return <Building2 className="w-4 h-4 text-[#3DD9C4]" />;
    if (action.startsWith("member")) return <Users className="w-4 h-4 text-[#34D399]" />;
    if (action.startsWith("invitation")) return <Mail className="w-4 h-4 text-[#FBBF24]" />;
    return <Activity className="w-4 h-4 text-[#8B99B8]" />;
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-5xl mx-auto w-full">
          <div>
            <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">Organization Activity Timeline</h1>
            <p className="text-xs text-[#8B99B8] mt-1">Real-time audit log stream of member changes, invitations, settings, and workspace events</p>
          </div>

          {/* Search & Filter Controls */}
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-[#8B99B8] absolute left-3.5 top-3" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Filter events by action or target..."
                className="w-full bg-[#111B2E] border border-[#22314D] rounded-xl pl-10 pr-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
              />
            </div>

            <select
              value={filterAction}
              onChange={(e) => setFilterAction(e.target.value)}
              className="bg-[#111B2E] border border-[#22314D] rounded-xl px-4 py-2 text-sm text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
            >
              <option value="ALL">All Event Types</option>
              <option value="ORGANIZATION">Organization Events</option>
              <option value="MEMBER">Member Events</option>
              <option value="INVITATION">Invitation Events</option>
              <option value="WORKSPACE">Workspace Events</option>
            </select>
          </div>

          {/* Activity Timeline List */}
          <div className="p-6 rounded-2xl bg-[#111B2E] border border-[#22314D] shadow-lg">
            <div className="space-y-6 relative before:absolute before:left-5 before:top-3 before:bottom-3 before:w-0.5 before:bg-[#22314D]">
              {filteredLogs.map((log) => (
                <div key={log.id} className="flex items-start gap-4 relative z-10">
                  <div className="p-2 rounded-xl bg-[#16233A] border border-[#22314D] shrink-0">
                    {getActionIcon(log.action)}
                  </div>

                  <div className="flex-1 bg-[#0A1020] border border-[#22314D] rounded-xl p-4">
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-mono text-xs text-[#3DD9C4] font-bold">{log.action}</span>
                      <span className="text-[10px] font-mono text-[#8B99B8] flex items-center gap-1">
                        <Clock className="w-3 h-3" />
                        {new Date(log.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <p className="text-xs text-[#E7EDF7] font-mono mt-1">Target: {log.target}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
