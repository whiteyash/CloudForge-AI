import Link from "next/link";
import { Zap, Home, FolderGit2, Bot } from "lucide-react";

export default function NotFound() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-[#0A1020] text-[#E7EDF7] p-6 text-center">
      <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-[#3DD9C4] to-[#16233A] flex items-center justify-center text-[#0A1020] shadow-[0_0_24px_rgba(61,217,196,0.4)] mb-6">
        <Zap className="w-8 h-8 text-[#0A1020] stroke-[2.5]" />
      </div>

      <h1 className="text-6xl font-heading font-extrabold text-[#E7EDF7] tracking-tight mb-2">404</h1>
      <h2 className="text-xl font-heading font-semibold text-[#3DD9C4] mb-4">Route Not Found</h2>
      <p className="text-sm text-[#8B99B8] max-w-md mb-8">
        The page or resource you are looking for does not exist or has been moved within CloudForge AI Mission Control.
      </p>

      <div className="flex flex-wrap justify-center gap-3">
        <Link
          href="/"
          className="px-4 py-2.5 rounded-xl bg-[#3DD9C4] text-[#0A1020] font-heading font-semibold text-xs hover:bg-[#34D399] transition-all shadow-[0_0_16px_rgba(61,217,196,0.3)] flex items-center gap-2"
        >
          <Home className="w-4 h-4" />
          Dashboard Overview
        </Link>
        <Link
          href="/projects"
          className="px-4 py-2.5 rounded-xl bg-[#16233A] text-[#E7EDF7] border border-[#22314D] font-heading font-semibold text-xs hover:bg-[#1e2f4d] transition-all flex items-center gap-2"
        >
          <FolderGit2 className="w-4 h-4 text-[#3DD9C4]" />
          Projects
        </Link>
        <Link
          href="/projects/proj-1/ai"
          className="px-4 py-2.5 rounded-xl bg-[#16233A] text-[#E7EDF7] border border-[#22314D] font-heading font-semibold text-xs hover:bg-[#1e2f4d] transition-all flex items-center gap-2"
        >
          <Bot className="w-4 h-4 text-[#3DD9C4]" />
          AIOps Control Plane
        </Link>
      </div>
    </div>
  );
}
