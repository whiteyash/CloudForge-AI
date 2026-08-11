import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        cf: {
          bg: "var(--cf-bg)",
          surface: "var(--cf-surface)",
          raised: "var(--cf-surface-raised)",
          border: "var(--cf-border)",
          signal: "var(--cf-signal)",
          healthy: "var(--cf-healthy)",
          warning: "var(--cf-warning)",
          critical: "var(--cf-critical)",
          text: "var(--cf-text-primary)",
          muted: "var(--cf-text-muted)",
        },
      },
      fontFamily: {
        heading: ["'Space Grotesk'", "sans-serif"],
        sans: ["'Inter'", "sans-serif"],
        mono: ["'JetBrains Mono'", "monospace"],
      },
    },
  },
  plugins: [],
};

export default config;
