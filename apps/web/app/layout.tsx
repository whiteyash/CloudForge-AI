import type { Metadata } from "next";
import "./globals.css";
import { EnvironmentProvider } from "@/context/EnvironmentContext";
import { LanguageProvider } from "@/lib/i18n";
import { MobileSidebarProvider } from "@/context/MobileSidebarContext";

export const metadata: Metadata = {
  title: "CloudForge AI",
  description: "Internal developer platform",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <EnvironmentProvider>
          <LanguageProvider>
            <MobileSidebarProvider>{children}</MobileSidebarProvider>
          </LanguageProvider>
        </EnvironmentProvider>
      </body>
    </html>
  );
}
