"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";

export type EnvironmentType = "dev" | "staging" | "prod";

export interface EnvironmentConfig {
  name: string;
  code: EnvironmentType;
  label: string;
  badgeBg: string;
  badgeText: string;
  badgeBorder: string;
  accentColor: string;
  speedMultiplier: number;
  glowIntensity: number;
  description: string;
}

export const ENVIRONMENT_CONFIGS: Record<EnvironmentType, EnvironmentConfig> = {
  dev: {
    name: "Development",
    code: "dev",
    label: "DEV",
    badgeBg: "bg-[#3DD9C4]/20",
    badgeText: "text-[#3DD9C4]",
    badgeBorder: "border-[#3DD9C4]/40",
    accentColor: "#3DD9C4",
    speedMultiplier: 1.6,
    glowIntensity: 0.28,
    description: "Rapid iteration, feature flags & experimental microservices mesh",
  },
  staging: {
    name: "Staging",
    code: "staging",
    label: "STAGING",
    badgeBg: "bg-[#FBBF24]/20",
    badgeText: "text-[#FBBF24]",
    badgeBorder: "border-[#FBBF24]/40",
    accentColor: "#FBBF24",
    speedMultiplier: 1.0,
    glowIntensity: 0.18,
    description: "Pre-production validation, integration testing & release candidates",
  },
  prod: {
    name: "Production",
    code: "prod",
    label: "PROD",
    badgeBg: "bg-[#F87171]/20",
    badgeText: "text-[#F87171]",
    badgeBorder: "border-[#F87171]/40",
    accentColor: "#F87171",
    speedMultiplier: 0.6,
    glowIntensity: 0.12,
    description: "Mission-critical live infrastructure, SLA monitoring & zero-trust security",
  },
};

interface EnvironmentContextType {
  environment: EnvironmentType;
  setEnvironment: (env: EnvironmentType) => void;
  isSwitching: boolean;
  environmentConfig: EnvironmentConfig;
}

const EnvironmentContext = createContext<EnvironmentContextType | undefined>(undefined);

export function EnvironmentProvider({ children }: { children: ReactNode }) {
  const [environment, setEnvironmentState] = useState<EnvironmentType>("dev");
  const [isSwitching, setIsSwitching] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const saved = localStorage.getItem("cf_environment") as EnvironmentType;
      if (saved && (saved === "dev" || saved === "staging" || saved === "prod")) {
        setEnvironmentState(saved);
      }
    }
  }, []);

  const setEnvironment = (env: EnvironmentType) => {
    if (env === environment) return;
    setIsSwitching(true);
    setEnvironmentState(env);

    if (typeof window !== "undefined") {
      localStorage.setItem("cf_environment", env);
    }

    // 500ms smooth transition window
    setTimeout(() => {
      setIsSwitching(false);
    }, 500);
  };

  return (
    <EnvironmentContext.Provider
      value={{
        environment,
        setEnvironment,
        isSwitching,
        environmentConfig: ENVIRONMENT_CONFIGS[environment],
      }}
    >
      {children}
    </EnvironmentContext.Provider>
  );
}

export function useEnvironment() {
  const context = useContext(EnvironmentContext);
  if (!context) {
    return {
      environment: "dev" as EnvironmentType,
      setEnvironment: () => {},
      isSwitching: false,
      environmentConfig: ENVIRONMENT_CONFIGS.dev,
    };
  }
  return context;
}
