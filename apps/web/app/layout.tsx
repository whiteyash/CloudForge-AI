import type { Metadata } from "next";
import "./globals.css";
import { EnvironmentProvider } from "@/context/EnvironmentContext";

export const metadata: Metadata = {
  title: "CloudForge AI",
  description: "Internal developer platform",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <EnvironmentProvider>{children}</EnvironmentProvider>
      </body>
    </html>
  );
}
