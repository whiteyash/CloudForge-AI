"use client";

import React, { useEffect, useRef } from "react";
import { useEnvironment, EnvironmentType } from "@/context/EnvironmentContext";

interface Node3D {
  x: number;
  y: number;
  z: number;
  vx: number;
  vy: number;
  radius: number;
  color: string;
  layer: "far" | "mid" | "near";
}

interface DataPacket {
  p1Index: number;
  p2Index: number;
  progress: number;
  speed: number;
  color: string;
}

interface OrbitRing {
  radiusX: number;
  radiusY: number;
  rotation: number;
  speed: number;
  color: string;
  packetPos: number;
}

interface FloatingBadge {
  label: string;
  x: number;
  y: number;
  z: number;
  relX: number;
  relY: number;
  color: string;
  icon: string;
}

interface ResponsiveParams {
  nodeCount: number;
  packetCount: number;
  coreX: number;
  coreY: number;
  corePulseSize: number;
  maxConnectionDist: number;
  interactionRadius: number;
  orbitRings: OrbitRing[];
  badges: FloatingBadge[];
}

function calculateResponsiveLayout(width: number, height: number): ResponsiveParams {
  const area = width * height;
  const isSmallMobile = width < 640;
  const isTablet = width >= 640 && width < 1024;
  const isUltrawide = width >= 1920;

  // Fluid node & packet density scaling
  const nodeCount = isSmallMobile ? 35 : isTablet ? 60 : isUltrawide ? 95 : 75;
  const packetCount = isSmallMobile ? 10 : isTablet ? 16 : 22;

  // Control plane central AI core position
  const coreX = width * (isSmallMobile ? 0.5 : 0.5);
  const coreY = height * (isSmallMobile ? 0.35 : 0.45);
  const corePulseSize = Math.min(65, width * (isSmallMobile ? 0.12 : 0.08));

  const baseRx = Math.min(width * 0.20, isSmallMobile ? 120 : 180);
  const baseRy = Math.min(height * 0.14, isSmallMobile ? 60 : 90);

  const orbitRings: OrbitRing[] = [
    { radiusX: baseRx, radiusY: baseRy, rotation: 0.2, speed: 0.003, color: "rgba(61, 217, 196, 0.25)", packetPos: 0 },
    { radiusX: baseRx * 1.4, radiusY: baseRy * 1.4, rotation: -0.3, speed: -0.002, color: "rgba(74, 114, 255, 0.20)", packetPos: 0.5 },
    { radiusX: baseRx * 1.85, radiusY: baseRy * 1.85, rotation: 0.5, speed: 0.0015, color: "rgba(168, 85, 247, 0.15)", packetPos: 0.2 },
  ];

  const maxConnectionDist = Math.min(150, Math.max(85, width * 0.10));
  const interactionRadius = Math.min(200, Math.max(120, width * 0.13));

  const badgeConfigs = [
    { label: "K8S", color: "#3DD9C4", icon: "☸", relX: -0.38, relY: -0.36 },
    { label: "DOCKER", color: "#4A72FF", icon: "🐳", relX: 0.38, relY: -0.36 },
    { label: "AWS", color: "#FBBF24", icon: "☁", relX: -0.42, relY: 0.0 },
    { label: "GIT", color: "#F87171", icon: "⎇", relX: 0.42, relY: 0.0 },
    { label: "AI/ML", color: "#A855F7", icon: "🧠", relX: -0.38, relY: 0.36 },
    { label: "CI/CD", color: "#34D399", icon: "⚡", relX: 0.38, relY: 0.36 },
  ];

  const badges: FloatingBadge[] = badgeConfigs.map((cfg, idx) => ({
    label: cfg.label,
    color: cfg.color,
    icon: cfg.icon,
    x: 0,
    y: 0,
    relX: cfg.relX,
    relY: cfg.relY,
    z: 180 + idx * 35,
  }));

  return {
    nodeCount,
    packetCount,
    coreX,
    coreY,
    corePulseSize,
    maxConnectionDist,
    interactionRadius,
    orbitRings,
    badges,
  };
}

export default function CloudControlBackground() {
  const { environment } = useEnvironment();
  const envRef = useRef<EnvironmentType>(environment);
  envRef.current = environment;

  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let animationFrameId: number;
    let prefersReducedMotion = false;

    if (typeof window !== "undefined" && window.matchMedia) {
      prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    }

    let width = 0;
    let height = 0;
    let layoutParams: ResponsiveParams = calculateResponsiveLayout(800, 600);

    const updateBounds = () => {
      if (!canvas) return;
      const parent = canvas.parentElement;
      const parentW = parent ? Math.max(parent.clientWidth, parent.getBoundingClientRect().width) : 0;
      const parentH = parent ? Math.max(parent.clientHeight, parent.getBoundingClientRect().height) : 0;

      const dpr = typeof window !== "undefined" ? Math.min(window.devicePixelRatio || 1, 2) : 1;
      width = parentW > 0 ? parentW : (typeof window !== "undefined" ? window.innerWidth : 800);
      height = parentH > 0 ? parentH : (typeof window !== "undefined" ? window.innerHeight : 600);

      canvas.width = width * dpr;
      canvas.height = height * dpr;
      ctx.scale(dpr, dpr);

      layoutParams = calculateResponsiveLayout(width, height);
    };

    updateBounds();

    let cursorX = width / 2;
    let cursorY = height / 2;
    let targetCameraX = 0;
    let targetCameraY = 0;
    let cameraX = 0;
    let cameraY = 0;
    let isPointerActive = false;

    const handlePointerMove = (clientX: number, clientY: number) => {
      if (prefersReducedMotion || !canvas) return;
      const rect = canvas.getBoundingClientRect();
      cursorX = clientX - rect.left;
      cursorY = clientY - rect.top;
      targetCameraX = (cursorX - width / 2) * 0.03;
      targetCameraY = (cursorY - height / 2) * 0.03;
      isPointerActive = true;
    };

    const onMouseMove = (e: MouseEvent) => handlePointerMove(e.clientX, e.clientY);
    const onTouchMove = (e: TouchEvent) => {
      if (e.touches.length > 0) handlePointerMove(e.touches[0].clientX, e.touches[0].clientY);
    };
    const onTouchEnd = () => {
      isPointerActive = false;
    };

    if (typeof window !== "undefined") {
      window.addEventListener("mousemove", onMouseMove);
      window.addEventListener("touchmove", onTouchMove, { passive: true });
      window.addEventListener("touchend", onTouchEnd);
      window.addEventListener("resize", updateBounds);
    }

    let resizeObserver: ResizeObserver | null = null;
    if (typeof ResizeObserver !== "undefined" && canvas.parentElement) {
      resizeObserver = new ResizeObserver(() => updateBounds());
      resizeObserver.observe(canvas.parentElement);
    }

    const MAX_NODES = 95;
    const nodes: Node3D[] = [];
    const colors = ["#3DD9C4", "#4A72FF", "#34D399", "#A855F7"];

    for (let i = 0; i < MAX_NODES; i++) {
      const z = Math.random() * 650 + 50;
      const layer = z > 480 ? "far" : z > 240 ? "mid" : "near";
      const vx = (Math.random() - 0.5) * (layer === "near" ? 0.35 : layer === "mid" ? 0.20 : 0.10);
      const vy = (Math.random() - 0.5) * (layer === "near" ? 0.35 : layer === "mid" ? 0.20 : 0.10);

      nodes.push({
        x: (Math.random() - 0.5) * (width || 1200) * 1.5,
        y: (Math.random() - 0.5) * (height || 800) * 1.5,
        z,
        vx,
        vy,
        radius: layer === "near" ? Math.random() * 2.5 + 1.5 : Math.random() * 1.5 + 0.8,
        color: colors[i % colors.length],
        layer,
      });
    }

    const packets: DataPacket[] = [];
    for (let p = 0; p < 22; p++) {
      packets.push({
        p1Index: Math.floor(Math.random() * MAX_NODES),
        p2Index: Math.floor(Math.random() * MAX_NODES),
        progress: Math.random(),
        speed: 0.004 + Math.random() * 0.006,
        color: colors[p % colors.length],
      });
    }

    const FOCAL_LENGTH = 420;
    let time = 0;

    const render = () => {
      const curEnv = envRef.current;
      const envSpeedMult = curEnv === "dev" ? 1.6 : curEnv === "staging" ? 1.0 : 0.6;
      time += 0.010 * envSpeedMult;

      cameraX += (targetCameraX - cameraX) * 0.04;
      cameraY += (targetCameraY - cameraY) * 0.04;

      ctx.clearRect(0, 0, width, height);

      // Deep cyber base fill
      ctx.fillStyle = curEnv === "prod" ? "#060A14" : curEnv === "staging" ? "#080E1B" : "#0A1224";
      ctx.fillRect(0, 0, width, height);

      // Viewport-wide atmospheric glow
      const bgGlow = ctx.createRadialGradient(
        layoutParams.coreX, layoutParams.coreY, 60,
        layoutParams.coreX, layoutParams.coreY, Math.max(width, height) * 0.85
      );

      if (curEnv === "dev") {
        bgGlow.addColorStop(0, "rgba(61, 217, 196, 0.22)");
        bgGlow.addColorStop(0.35, "rgba(168, 85, 247, 0.12)");
        bgGlow.addColorStop(0.7, "rgba(74, 114, 255, 0.06)");
        bgGlow.addColorStop(1, "rgba(10, 18, 36, 1)");
      } else if (curEnv === "staging") {
        bgGlow.addColorStop(0, "rgba(74, 114, 255, 0.18)");
        bgGlow.addColorStop(0.35, "rgba(61, 217, 196, 0.09)");
        bgGlow.addColorStop(0.7, "rgba(251, 191, 36, 0.04)");
        bgGlow.addColorStop(1, "rgba(8, 14, 27, 1)");
      } else {
        bgGlow.addColorStop(0, "rgba(15, 23, 42, 0.35)");
        bgGlow.addColorStop(0.35, "rgba(61, 217, 196, 0.07)");
        bgGlow.addColorStop(0.7, "rgba(74, 114, 255, 0.03)");
        bgGlow.addColorStop(1, "rgba(6, 10, 20, 1)");
      }
      ctx.fillStyle = bgGlow;
      ctx.fillRect(0, 0, width, height);

      // Central Control Plane Breathing Core
      const coreX = layoutParams.coreX;
      const coreY = layoutParams.coreY;
      const pulseSize = Math.sin(time * 2) * 6 + layoutParams.corePulseSize;

      const coreGlow = ctx.createRadialGradient(coreX, coreY, 0, coreX, coreY, pulseSize * 2.4);
      coreGlow.addColorStop(0, curEnv === "dev" ? "rgba(61, 217, 196, 0.28)" : curEnv === "staging" ? "rgba(74, 114, 255, 0.22)" : "rgba(61, 217, 196, 0.16)");
      coreGlow.addColorStop(1, "rgba(6, 10, 20, 0)");
      ctx.fillStyle = coreGlow;
      ctx.beginPath();
      ctx.arc(coreX, coreY, pulseSize * 2.4, 0, Math.PI * 2);
      ctx.fill();

      // Orbit Ring System
      layoutParams.orbitRings.forEach((ring) => {
        ring.rotation += ring.speed * envSpeedMult;
        ring.packetPos = (ring.packetPos + 0.005 * envSpeedMult) % 1;

        ctx.save();
        ctx.translate(coreX - cameraX * 0.2, coreY - cameraY * 0.2);
        ctx.rotate(ring.rotation);

        ctx.strokeStyle = ring.color;
        ctx.lineWidth = 1.1;
        ctx.beginPath();
        ctx.ellipse(0, 0, ring.radiusX, ring.radiusY, 0, 0, Math.PI * 2);
        ctx.stroke();

        const px = Math.cos(ring.packetPos * Math.PI * 2) * ring.radiusX;
        const py = Math.sin(ring.packetPos * Math.PI * 2) * ring.radiusY;
        ctx.fillStyle = curEnv === "prod" ? "#3DD9C4" : curEnv === "staging" ? "#FBBF24" : "#3DD9C4";
        ctx.beginPath();
        ctx.arc(px, py, 2.2, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();
      });

      // 3D Nodes
      const projected: Array<{ sx: number; sy: number; scale: number; p: Node3D; index: number }> = [];
      const activeNodes = nodes.slice(0, layoutParams.nodeCount);

      activeNodes.forEach((p, idx) => {
        if (!prefersReducedMotion) {
          p.x += p.vx * envSpeedMult;
          p.y += p.vy * envSpeedMult;

          if (p.x < -width) p.x = width;
          if (p.x > width) p.x = -width;
          if (p.y < -height) p.y = height;
          if (p.y > height) p.y = -height;
        }

        const scale = FOCAL_LENGTH / (FOCAL_LENGTH + p.z);
        let sx = (p.x - cameraX * (scale * 1.4)) * scale + width / 2;
        let sy = (p.y - cameraY * (scale * 1.4)) * scale + height / 2;

        if (!prefersReducedMotion && isPointerActive) {
          const dx = cursorX - sx;
          const dy = cursorY - sy;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < layoutParams.interactionRadius && dist > 1) {
            const factor = 1 - dist / layoutParams.interactionRadius;
            const force = (p.layer === "near" ? 14 : -15) * factor;
            sx += (dx / dist) * force;
            sy += (dy / dist) * force;
          }
        }

        if (sx >= -60 && sx <= width + 60 && sy >= -60 && sy <= height + 60) {
          projected.push({ sx, sy, scale, p, index: idx });
        }
      });

      // Network Lines
      ctx.lineWidth = 1.0;
      for (let i = 0; i < projected.length; i++) {
        for (let j = i + 1; j < projected.length; j++) {
          const p1 = projected[i];
          const p2 = projected[j];
          const dx = p1.sx - p2.sx;
          const dy = p1.sy - p2.sy;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const maxDist = layoutParams.maxConnectionDist;

          if (dist < maxDist) {
            let alpha = (1 - dist / maxDist) * (curEnv === "dev" ? 0.32 : curEnv === "staging" ? 0.24 : 0.16);
            ctx.strokeStyle = `rgba(61, 217, 196, ${alpha})`;
            ctx.beginPath();
            ctx.moveTo(p1.sx, p1.sy);
            ctx.lineTo(p2.sx, p2.sy);
            ctx.stroke();
          }
        }
      }

      // Render Nodes
      projected.forEach(({ sx, sy, scale, p }) => {
        const drawRadius = Math.max(1.2, p.radius * scale * 1.5);
        ctx.fillStyle = p.color;
        ctx.beginPath();
        ctx.arc(sx, sy, drawRadius, 0, Math.PI * 2);
        ctx.fill();
      });

      // Render Viewport-Wide Infrastructure Badges
      layoutParams.badges.forEach((b) => {
        const scale = FOCAL_LENGTH / (FOCAL_LENGTH + b.z);
        const bx = width / 2 + width * b.relX * scale - cameraX * (scale * 1.4);
        const by = height / 2 + height * b.relY * scale - cameraY * (scale * 1.4);

        if (bx >= 15 && bx <= width - 15 && by >= 15 && by <= height - 15) {
          ctx.save();
          const isSmall = width < 640;
          ctx.font = `bold ${isSmall ? "8px" : "9px"} monospace`;
          const badgeText = `${b.icon} ${b.label}`;
          const metrics = ctx.measureText(badgeText);
          const bw = metrics.width + (isSmall ? 10 : 14);
          const bh = isSmall ? 17 : 20;

          ctx.fillStyle = "rgba(8, 16, 32, 0.75)";
          ctx.strokeStyle = b.color;
          ctx.lineWidth = 1.0;

          ctx.beginPath();
          ctx.roundRect(bx - bw / 2, by - bh / 2, bw, bh, 8);
          ctx.fill();
          ctx.stroke();

          ctx.fillStyle = "#E7EDF7";
          ctx.fillText(badgeText, bx - bw / 2 + (isSmall ? 5 : 7), by + (isSmall ? 2.5 : 3));
          ctx.restore();
        }
      });

      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      if (animationFrameId) cancelAnimationFrame(animationFrameId);
      if (typeof window !== "undefined") {
        window.removeEventListener("mousemove", onMouseMove);
        window.removeEventListener("touchmove", onTouchMove);
        window.removeEventListener("touchend", onTouchEnd);
        window.removeEventListener("resize", updateBounds);
      }
      if (resizeObserver) resizeObserver.disconnect();
    };
  }, []);

  return (
    <div className="fixed inset-0 w-full h-full pointer-events-auto z-0 bg-[#060A14] overflow-hidden">
      <canvas ref={canvasRef} className="w-full h-full block" />
    </div>
  );
}
