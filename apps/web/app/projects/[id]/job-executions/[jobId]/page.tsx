"use client";

import React, { useState } from "react";
import Sidebar from "@/components/layout/Sidebar";
import Header from "@/components/layout/Header";
import { Terminal, ArrowLeft, RotateCcw, XCircle, Search, Download, CheckCircle2 } from "lucide-react";
import Link from "next/link";

interface LogLine {
  seq: number;
  line: string;
  type: "STDOUT" | "STDERR" | "SYSTEM";
  timestamp: string;
}

export default function JobExecutionConsolePage() {
  const [status, setStatus] = useState("SUCCESS");
  const [exitCode, setExitCode] = useState<number | null>(0);
  const [retryCount, setRetryCount] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  const [logs] = useState<LogLine[]>([
    { seq: 1, line: "[SYSTEM] Initializing job execution context...", type: "SYSTEM", timestamp: "18:07:01" },
    { seq: 2, line: "[SYSTEM] Allocated runner agent: k8s-runner-pool-1 (Kubernetes Pod)", type: "SYSTEM", timestamp: "18:07:02" },
    { seq: 3, line: "$ git clone https://github.com/cloudforge/core-service.git .", type: "STDOUT", timestamp: "18:07:03" },
    { seq: 4, line: "Cloning into '.'...", type: "STDOUT", timestamp: "18:07:04" },
    { seq: 5, line: "$ ./mvnw clean test -Denv=staging", type: "STDOUT", timestamp: "18:07:05" },
    { seq: 6, line: "[INFO] Building CloudForge Core Service v2.4.0", type: "STDOUT", timestamp: "18:07:06" },
    { seq: 7, line: "[INFO] Running 65 automated integration tests...", type: "STDOUT", timestamp: "18:07:08" },
    { seq: 8, line: "[INFO] Tests run: 65, Failures: 0, Errors: 0, Skipped: 0", type: "STDOUT", timestamp: "18:07:12" },
    { seq: 9, line: "[INFO] ------------------------------------------------------------------------", type: "STDOUT", timestamp: "18:07:13" },
    { seq: 10, line: "[INFO] BUILD SUCCESS", type: "STDOUT", timestamp: "18:07:14" },
    { seq: 11, line: "[SYSTEM] Job process exited with code 0.", type: "SYSTEM", timestamp: "18:07:15" },
  ]);

  const filteredLogs = logs.filter((l) =>
    l.line.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleRetry = () => {
    setRetryCount((prev) => prev + 1);
    setStatus("RUNNING");
    setExitCode(null);
    setMessage(`Job retry triggered (Attempt #${retryCount + 2}).`);
  };

  const handleCancel = () => {
    setStatus("CANCELLED");
    setMessage("Job execution cancelled manually.");
  };

  return (
    <div className="flex h-screen bg-[#0A1020] text-[#E7EDF7] overflow-hidden">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />

        <main className="flex-1 overflow-y-auto p-6 space-y-6 max-w-6xl mx-auto w-full">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Link href="/projects/proj-1/pipelines" className="p-2 rounded-xl bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                <ArrowLeft className="w-4 h-4" />
              </Link>
              <div>
                <div className="flex items-center gap-2">
                  <h1 className="text-2xl font-heading font-bold text-[#E7EDF7]">test-suite-execution</h1>
                  <span className={`px-2.5 py-0.5 rounded text-xs font-mono font-semibold ${
                    status === "SUCCESS" ? "bg-[#34D399]/10 text-[#34D399] border border-[#34D399]/30" :
                    status === "RUNNING" ? "bg-[#3DD9C4]/10 text-[#3DD9C4] border border-[#3DD9C4]/30" : "bg-[#F87171]/10 text-[#F87171] border border-[#F87171]/30"
                  }`}>
                    {status}
                  </span>
                  {exitCode !== null && (
                    <span className="px-2 py-0.5 rounded text-[10px] font-mono bg-[#16233A] text-[#8B99B8] border border-[#22314D]">
                      Exit Code: {exitCode}
                    </span>
                  )}
                </div>
                <p className="text-xs text-[#8B99B8] mt-0.5">Pipeline: main-build-ci #42 | Runner: k8s-runner-pool-1 | Retries: {retryCount}</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              {status === "RUNNING" && (
                <button
                  onClick={handleCancel}
                  className="px-3 py-2 rounded-xl bg-[#16233A] text-[#F87171] text-xs font-bold flex items-center gap-1 hover:bg-[#F87171]/10"
                >
                  <XCircle className="w-4 h-4" /> Cancel Job
                </button>
              )}
              <button
                onClick={handleRetry}
                className="px-4 py-2 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] flex items-center gap-1 shadow-[0_0_16px_rgba(61,217,196,0.3)]"
              >
                <RotateCcw className="w-4 h-4" /> Retry Job
              </button>
            </div>
          </div>

          {message && (
            <div className="p-4 rounded-xl bg-[#34D399]/10 border border-[#34D399]/30 text-[#34D399] text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>{message}</span>
            </div>
          )}

          {/* Terminal Console */}
          <div className="rounded-2xl bg-[#070C18] border border-[#22314D] shadow-2xl overflow-hidden flex flex-col">
            <div className="p-4 bg-[#0F172A] border-b border-[#22314D] flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Terminal className="w-4 h-4 text-[#3DD9C4]" />
                <span className="font-mono text-xs font-bold text-[#E7EDF7]">Live Execution Log Stream (SSE Output)</span>
              </div>

              <div className="flex items-center gap-3">
                <div className="relative">
                  <Search className="w-3.5 h-3.5 text-[#8B99B8] absolute left-3 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Search logs..."
                    className="bg-[#0A1020] border border-[#22314D] rounded-lg pl-8 pr-3 py-1 text-xs text-[#E7EDF7] focus:outline-none focus:border-[#3DD9C4]"
                  />
                </div>

                <button className="p-1.5 rounded-lg bg-[#16233A] text-[#8B99B8] hover:text-[#E7EDF7] transition-all">
                  <Download className="w-4 h-4" />
                </button>
              </div>
            </div>

            <div className="p-6 font-mono text-xs text-[#34D399] space-y-1 max-h-[500px] overflow-y-auto leading-relaxed bg-[#070C18]">
              {filteredLogs.map((log) => (
                <div key={log.seq} className="flex items-start gap-4">
                  <span className="text-[#8B99B8]/40 select-none w-8 text-right shrink-0">{log.seq}</span>
                  <span className="text-[#8B99B8]/60 shrink-0">{log.timestamp}</span>
                  <span className={log.type === "SYSTEM" ? "text-[#3DD9C4]" : log.type === "STDERR" ? "text-[#F87171]" : "text-[#E7EDF7]"}>
                    {log.line}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
