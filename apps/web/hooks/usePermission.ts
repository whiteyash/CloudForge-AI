"use client";

import { useState, useEffect } from "react";
import { api } from "@/lib/api";

export function usePermission(permissionCode: string, orgId = "default-org-id") {
  const [hasPermission, setHasPermission] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    let isMounted = true;

    api.getMyPermissions(orgId)
      .then((permissions) => {
        if (isMounted) {
          setHasPermission(permissions.includes(permissionCode));
          setLoading(false);
        }
      })
      .catch(() => {
        if (isMounted) {
          setHasPermission(true); // Default fallback for dev mock UI
          setLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [permissionCode, orgId]);

  return { hasPermission, loading };
}
