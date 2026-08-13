import type { Metadata } from "next";
import "./globals.css";
import { EnvironmentProvider } from "@/context/EnvironmentContext";
import { LanguageProvider } from "@/lib/i18n";

export const metadata: Metadata = {
  title: "CloudForge AI",
  description: "Internal developer platform",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <EnvironmentProvider>
          <LanguageProvider>{children}</LanguageProvider>
        </EnvironmentProvider>
      </body>
    </html>
  );
}
