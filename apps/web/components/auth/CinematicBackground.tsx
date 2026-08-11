"use client";

import React, { useEffect, useRef, useState } from "react";

interface Particle3D {
  x: number;
  y: number;
  z: number;
  vx: number;
  vy: number;
  radius: number;
  color: string;
  layer: "far" | "mid" | "near";
  symbolName?: string;
  phase: number;
}

interface TrailPoint {
  x: number;
  y: number;
  alpha: number;
}

interface SceneInfo {
  tag: string;
  headline: string;
  subhead: string;
  duration: number;
}

const SCENES: SceneInfo[] = [
  {
    tag: "SCENE 01 // INFRASTRUCTURE MESH",
    headline: "Your Cloud. Your Code.",
    subhead: "Distributed multi-region cloud topology & container orchestration",
    duration: 3.5,
  },
  {
    tag: "SCENE 02 // CONTROL PLANE NEXUS",
    headline: "One Intelligent Control Plane.",
    subhead: "Unified governance, service mesh & tenant isolation across environments",
    duration: 3.5,
  },
  {
    tag: "SCENE 03 // GITOPS & CI/CD ENGINE",
    headline: "Build. Deploy. Observe.",
    subhead: "Automated pipelines, native OCI builds & zero-downtime releases",
    duration: 3.5,
  },
  {
    tag: "SCENE 04 // AIOPS & THREAT RESPONSE",
    headline: "Detect. Analyze. Respond.",
    subhead: "AI-powered root cause analysis, automated triage & incident dispatch",
    duration: 3.5,
  },
  {
    tag: "SCENE 05 // ENTERPRISE MISSION CONTROL",
    headline: "CloudForge AI",
    subhead: "Autonomous cloud operations for modern engineering teams",
    duration: 3.5,
  },
  {
    tag: "SCENE 06 // MISSION CONTROL NEXUS",
    headline: "Operational Infrastructure",
    subhead: "Continuous telemetry monitoring, zero-trust RBAC & active compliance",
    duration: 4.0,
  },
];

export default function CinematicBackground() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [activeSceneIndex, setActiveSceneIndex] = useState(0);
  const [textFade, setTextFade] = useState(true);

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

    // Robust Bounds Strategy
    let width = 0;
    let height = 0;

    const updateBounds = () => {
      if (!canvas) return;
      const parent = canvas.parentElement;
      const parentW = parent ? Math.max(parent.clientWidth, parent.getBoundingClientRect().width) : 0;
      const parentH = parent ? Math.max(parent.clientHeight, parent.getBoundingClientRect().height) : 0;

      width = canvas.width = parentW > 0 ? parentW : (typeof window !== "undefined" ? window.innerWidth : 800);
      height = canvas.height = parentH > 0 ? parentH : (typeof window !== "undefined" ? window.innerHeight : 600);
    };

    updateBounds();

    // Mouse & Touch Tracking with Smooth Inertia
    let cursorX = width / 2;
    let cursorY = height / 2;
    let targetCameraX = 0;
    let targetCameraY = 0;
    let cameraX = 0;
    let cameraY = 0;
    let isCursorActive = false;

    const trail: TrailPoint[] = [];

    const handlePointerMove = (clientX: number, clientY: number) => {
      if (prefersReducedMotion || !canvas) return;
      const rect = canvas.getBoundingClientRect();
      cursorX = clientX - rect.left;
      cursorY = clientY - rect.top;
      targetCameraX = (cursorX - width / 2) * 0.05;
      targetCameraY = (cursorY - height / 2) * 0.05;
      isCursorActive = true;

      if (trail.length > 18) trail.shift();
      trail.push({ x: cursorX, y: cursorY, alpha: 0.6 });
    };

    const onMouseMove = (e: MouseEvent) => handlePointerMove(e.clientX, e.clientY);
    const onTouchMove = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        handlePointerMove(e.touches[0].clientX, e.touches[0].clientY);
      }
    };

    if (typeof window !== "undefined") {
      window.addEventListener("mousemove", onMouseMove);
      window.addEventListener("touchmove", onTouchMove, { passive: true });
      window.addEventListener("resize", updateBounds);
    }

    // ResizeObserver for Container Resizing
    let resizeObserver: ResizeObserver | null = null;
    if (typeof ResizeObserver !== "undefined" && canvas.parentElement) {
      resizeObserver = new ResizeObserver(() => {
        updateBounds();
      });
      resizeObserver.observe(canvas.parentElement);
    }

    // Multi-Layer Particle Initialization
    const isMobile = width < 768;
    const PARTICLE_COUNT = isMobile ? 24 : 65;
    const particles: Particle3D[] = [];
    const colors = ["#3DD9C4", "#4A72FF", "#34D399", "#A855F7"];
    const symbols = ["K8S", "DOCKER", "AWS", "GCP", "GIT", "AI", "API", "CI/CD"];

    for (let i = 0; i < PARTICLE_COUNT; i++) {
      const z = Math.random() * 700 + 50;
      const layer = z > 500 ? "far" : z > 250 ? "mid" : "near";
      const vx = (Math.random() - 0.5) * (layer === "near" ? 0.45 : layer === "mid" ? 0.3 : 0.15);
      const vy = (Math.random() - 0.5) * (layer === "near" ? 0.45 : layer === "mid" ? 0.3 : 0.15);

      particles.push({
        x: (Math.random() - 0.5) * (width || 1200) * 1.6,
        y: (Math.random() - 0.5) * (height || 800) * 1.6,
        z,
        vx,
        vy,
        radius: layer === "near" ? Math.random() * 2.8 + 1.6 : Math.random() * 1.8 + 1.0,
        color: colors[i % colors.length],
        layer,
        symbolName: i % 7 === 0 ? symbols[(i / 7) % symbols.length] : undefined,
        phase: Math.random() * Math.PI * 2,
      });
    }

    const FOCAL_LENGTH = 420;
    let time = 0;
    let currentScene = 0;
    let sceneStartTime = Date.now();

    // Main Render Loop
    const render = () => {
      time += 0.012;

      // Synchronized Scene Cycle
      const now = Date.now();
      const elapsed = (now - sceneStartTime) / 1000;
      if (elapsed > SCENES[currentScene].duration) {
        sceneStartTime = now;
        currentScene = (currentScene + 1) % SCENES.length;
        setActiveSceneIndex(currentScene);
        setTextFade(false);
        setTimeout(() => setTextFade(true), 150);
      }

      // Smooth Camera Spring Damping
      cameraX += (targetCameraX - cameraX) * 0.045;
      cameraY += (targetCameraY - cameraY) * 0.045;

      ctx.clearRect(0, 0, width, height);

      // 1. Dark Enterprise Cyber Background
      ctx.fillStyle = "#0A1020";
      ctx.fillRect(0, 0, width, height);

      // 2. Layer 1: Ambient Atmosphere Radial Gradient
      const bgGlow = ctx.createRadialGradient(
        width / 2, height / 2, 40,
        width / 2, height / 2, Math.max(width, height, 600) * 0.85
      );
      bgGlow.addColorStop(0, "rgba(74, 114, 255, 0.12)");
      bgGlow.addColorStop(0.5, "rgba(61, 217, 196, 0.06)");
      bgGlow.addColorStop(1, "rgba(10, 16, 32, 1)");
      ctx.fillStyle = bgGlow;
      ctx.fillRect(0, 0, width, height);

      // 3. Localized Cursor Spotlight Glow
      if (isCursorActive && !prefersReducedMotion) {
        const spotlight = ctx.createRadialGradient(
          cursorX, cursorY, 0,
          cursorX, cursorY, 220
        );
        spotlight.addColorStop(0, "rgba(61, 217, 196, 0.20)");
        spotlight.addColorStop(0.5, "rgba(74, 114, 255, 0.08)");
        spotlight.addColorStop(1, "rgba(10, 16, 32, 0)");
        ctx.fillStyle = spotlight;
        ctx.beginPath();
        ctx.arc(cursorX, cursorY, 220, 0, Math.PI * 2);
        ctx.fill();
      }

      // 4. Draw Cursor Trail
      if (!prefersReducedMotion && trail.length > 1) {
        ctx.lineWidth = 1.4;
        for (let i = 0; i < trail.length - 1; i++) {
          const pt1 = trail[i];
          const pt2 = trail[i + 1];
          pt1.alpha *= 0.91;
          if (pt1.alpha > 0.02) {
            ctx.strokeStyle = `rgba(61, 217, 196, ${pt1.alpha * 0.55})`;
            ctx.beginPath();
            ctx.moveTo(pt1.x, pt1.y);
            ctx.lineTo(pt2.x, pt2.y);
            ctx.stroke();
          }
        }
      }

      // Project 3D Particles
      const projected: Array<{ sx: number; sy: number; scale: number; p: Particle3D }> = [];

      particles.forEach((p) => {
        if (!prefersReducedMotion) {
          p.x += p.vx;
          p.y += p.vy;

          if (p.x < -width) p.x = width;
          if (p.x > width) p.x = -width;
          if (p.y < -height) p.y = height;
          if (p.y > height) p.y = -height;
        }

        const scale = FOCAL_LENGTH / (FOCAL_LENGTH + p.z);
        let sx = (p.x - cameraX * (scale * 1.5)) * scale + width / 2;
        let sy = (p.y - cameraY * (scale * 1.5)) * scale + height / 2;

        // Gravitational & Repulsion Cursor Physics (<160px proximity)
        if (!prefersReducedMotion && isCursorActive) {
          const dx = cursorX - sx;
          const dy = cursorY - sy;
          const dist = Math.sqrt(dx * dx + dy * dy);

          if (dist < 160 && dist > 1) {
            const factor = (1 - dist / 160);
            const isAttract = p.layer === "near";
            const force = (isAttract ? 12 : -16) * factor;
            sx += (dx / dist) * force;
            sy += (dy / dist) * force;
          }
        }

        if (sx >= -60 && sx <= width + 60 && sy >= -60 && sy <= height + 60) {
          projected.push({ sx, sy, scale, p });
        }
      });

      // Layer 3: Network Topology Connections
      ctx.lineWidth = 1.0;
      for (let i = 0; i < projected.length; i++) {
        for (let j = i + 1; j < projected.length; j++) {
          const p1 = projected[i];
          const p2 = projected[j];
          const dx = p1.sx - p2.sx;
          const dy = p1.sy - p2.sy;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const maxDist = isMobile ? 95 : 140;

          if (dist < maxDist) {
            let alpha = (1 - dist / maxDist) * 0.28;

            if (isCursorActive && !prefersReducedMotion) {
              const cdist = Math.sqrt(
                Math.pow((p1.sx + p2.sx) / 2 - cursorX, 2) +
                Math.pow((p1.sy + p2.sy) / 2 - cursorY, 2)
              );
              if (cdist < 150) alpha *= 1.8;
            }

            ctx.strokeStyle = `rgba(61, 217, 196, ${Math.min(alpha, 0.55)})`;
            ctx.beginPath();
            ctx.moveTo(p1.sx, p1.sy);
            ctx.lineTo(p2.sx, p2.sy);
            ctx.stroke();

            // Animated Data Signals Traveling on Connections
            if (currentScene === 2 || currentScene === 1) {
              const pulsePos = (time * 0.9 + i) % 1;
              const px = p1.sx + (p2.sx - p1.sx) * pulsePos;
              const py = p1.sy + (p2.sy - p1.sy) * pulsePos;
              ctx.fillStyle = "#3DD9C4";
              ctx.beginPath();
              ctx.arc(px, py, 1.6, 0, Math.PI * 2);
              ctx.fill();
            }
          }
        }
      }

      // Draw Particles & Floating Technical Symbol Badges
      projected.forEach(({ sx, sy, scale, p }) => {
        const drawRadius = Math.max(1.5, p.radius * scale * 1.6);

        ctx.fillStyle = p.color;
        ctx.beginPath();
        ctx.arc(sx, sy, drawRadius, 0, Math.PI * 2);
        ctx.fill();

        // Node Glow Halo
        ctx.fillStyle = p.color === "#3DD9C4" ? "rgba(61, 217, 196, 0.35)" : "rgba(74, 114, 255, 0.35)";
        ctx.beginPath();
        ctx.arc(sx, sy, drawRadius * 2.4, 0, Math.PI * 2);
        ctx.fill();

        // Floating Technical Badges
        if (p.symbolName && scale > 0.45 && !isMobile) {
          ctx.font = "bold 9px monospace";
          ctx.fillStyle = "rgba(231, 237, 247, 0.85)";
          ctx.fillText(p.symbolName, sx + drawRadius + 4, sy + 3);
        }
      });

      // Scene 04 Specific Graphic: Security Scan & Triage Pulse
      if (currentScene === 3) {
        const pulseR = (time * 45) % (height * 0.42);
        ctx.strokeStyle = "rgba(52, 211, 153, 0.35)";
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.arc(width / 2, height / 2, pulseR, 0, Math.PI * 2);
        ctx.stroke();
      }

      // Continuous loop request
      animationFrameId = requestAnimationFrame(render);
    };

    render();

    return () => {
      if (animationFrameId) cancelAnimationFrame(animationFrameId);
      if (typeof window !== "undefined") {
        window.removeEventListener("mousemove", onMouseMove);
        window.removeEventListener("touchmove", onTouchMove);
        window.removeEventListener("resize", updateBounds);
      }
      if (resizeObserver) resizeObserver.disconnect();
    };
  }, []);

  const activeScene = SCENES[activeSceneIndex];

  return (
    <div className="relative w-full h-full min-h-screen overflow-hidden flex flex-col justify-between p-8 sm:p-12 select-none font-sans bg-[#0A1020]">
      {/* Background Canvas */}
      <canvas ref={canvasRef} className="absolute inset-0 w-full h-full pointer-events-auto block" />

      {/* Top Brand Tag */}
      <div className="relative z-10 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-[#3DD9C4] animate-pulse" />
          <span className="text-[11px] font-mono font-bold text-[#3DD9C4] tracking-widest uppercase">
            CLOUDFORGE ENGINE v2.4
          </span>
        </div>
        <div className="hidden sm:flex items-center gap-3 text-[11px] font-mono text-[#8B99B8]">
          <span>STATUS: <strong className="text-emerald-400">OPERATIONAL</strong></span>
          <span>•</span>
          <span>REGION: <strong>US-EAST-1</strong></span>
        </div>
      </div>

      {/* Center Cinematic Headlines */}
      <div className="relative z-10 max-w-xl my-auto">
        <div
          className={`transition-all duration-700 ease-out transform ${
            textFade ? "opacity-100 translate-y-0 filter blur-0" : "opacity-0 translate-y-3 filter blur-sm"
          }`}
        >
          <span className="inline-block text-[10px] font-mono font-bold tracking-widest text-[#3DD9C4] uppercase px-3 py-1 rounded-full bg-[#3DD9C4]/10 border border-[#3DD9C4]/30 mb-4 backdrop-blur-md">
            {activeScene.tag}
          </span>
          <h2 className="text-3xl sm:text-4xl lg:text-5xl font-heading font-extrabold text-[#E7EDF7] tracking-tight leading-tight mb-3">
            {activeScene.headline}
          </h2>
          <p className="text-sm sm:text-base text-[#8B99B8] leading-relaxed max-w-md font-sans">
            {activeScene.subhead}
          </p>
        </div>
      </div>

      {/* Bottom Scene Timeline Dots & Controls */}
      <div className="relative z-10 flex items-center justify-between pt-6 border-t border-[#22314D]/40">
        <div className="flex items-center gap-2">
          {SCENES.map((_, idx) => (
            <button
              key={idx}
              onClick={() => setActiveSceneIndex(idx)}
              className={`h-1.5 rounded-full transition-all duration-500 cursor-pointer ${
                idx === activeSceneIndex ? "w-8 bg-[#3DD9C4]" : "w-2 bg-[#22314D] hover:bg-[#8B99B8]"
              }`}
              aria-label={`Jump to scene ${idx + 1}`}
            />
          ))}
        </div>
        <span className="text-[10px] font-mono text-[#8B99B8]">
          SCENE {activeSceneIndex + 1} / {SCENES.length}
        </span>
      </div>
    </div>
  );
}
