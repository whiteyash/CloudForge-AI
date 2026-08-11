"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";

export type EnvironmentType = "dev" | "staging" | "prod";

interface EnvironmentContextType {
  environment: EnvironmentType;
  setEnvironment: (env: EnvironmentType) => void;
}

const EnvironmentContext = createContext<EnvironmentContextType | undefined>(undefined);

export function EnvironmentProvider({ children }: { children: ReactNode }) {
  const [environment, setEnvironmentState] = useState<EnvironmentType>("dev");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const saved = localStorage.getItem("cf_environment") as EnvironmentType;
      if (saved && (saved === "dev" || saved === "staging" || saved === "prod")) {
        setEnvironmentState(saved);
      }
    }
  }, []);

  const setEnvironment = (env: EnvironmentType) => {
    setEnvironmentState(env);
    if (typeof window !== "undefined") {
      localStorage.setItem("cf_environment", env);
    }
  };

  return (
    <EnvironmentContext.Provider value={{ environment, setEnvironment }}>
      {children}
    </EnvironmentContext.Provider>
  );
}

export function useEnvironment() {
  const context = useContext(EnvironmentContext);
  if (!context) {
    // Graceful fallback if consumed outside provider
    return {
      environment: "dev" as EnvironmentType,
      setEnvironment: () => {},
    };
  }
  return context;
}
