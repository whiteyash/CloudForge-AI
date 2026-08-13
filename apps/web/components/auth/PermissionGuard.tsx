"use client";

import React from "react";
import { usePermission } from "@/hooks/usePermission";

interface PermissionGuardProps {
  permission: string;
  orgId?: string;
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export default function PermissionGuard({
  permission,
  orgId,
  fallback = null,
  children,
}: PermissionGuardProps) {
  const { hasPermission, loading } = usePermission(permission, orgId);

  if (loading) return null;
  if (!hasPermission) return <>{fallback}</>;

  return <>{children}</>;
}
