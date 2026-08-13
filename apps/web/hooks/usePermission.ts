"use client";

import { useState, useEffect } from "react";
import { api } from "@/lib/api";

export function usePermission(permissionCode: string, orgId?: string) {
  const [hasPermission, setHasPermission] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    let isMounted = true;
    let targetOrg = orgId || "";
    if (!targetOrg && typeof window !== "undefined") {
      targetOrg = localStorage.getItem("cf_active_org_id") || "";
    }

    if (!targetOrg) {
      setHasPermission(true);
      setLoading(false);
      return;
    }

    api.getMyPermissions(targetOrg)
      .then((permissions) => {
        if (isMounted) {
          setHasPermission(permissions.includes(permissionCode));
          setLoading(false);
        }
      })
      .catch(() => {
        if (isMounted) {
          setHasPermission(true); // Permissive fallback if permission API endpoint degrades
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [permissionCode, orgId]);

  return { hasPermission, loading };
}
