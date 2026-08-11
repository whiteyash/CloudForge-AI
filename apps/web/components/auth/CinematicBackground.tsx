"use client";

import React, { useEffect, useRef, useState } from "react";

interface Node3D {
  x: number;
  y: number;
  z: number;
  vx: number;
  vy: number;
  radius: number;
  color: string;
  layer: "far" | "mid" | "near";
  cluster: "k8s" | "cicd" | "git" | "ai" | "core";
  phase: number;
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
  baseX: number;
  baseY: number;
  color: string;
  icon: string;
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

    // Robust Bounds Detection Strategy
    let width = 0;
    let height = 0;

    const updateBounds = () => {
      if (!canvas) return;
      const parent = canvas.parentElement;
      const parentW = parent ? Math.max(parent.clientWidth, parent.getBoundingClientRect().width) : 0;
      const parentH = parent ? Math.max(parent.clientHeight, parent.getBoundingClientRect().height) : 0;

      const dpr = typeof window !== "undefined" ? window.devicePixelRatio || 1 : 1;
      width = parentW > 0 ? parentW : (typeof window !== "undefined" ? window.innerWidth : 800);
      height = parentH > 0 ? parentH : (typeof window !== "undefined" ? window.innerHeight : 600);

      canvas.width = width * dpr;
      canvas.height = height * dpr;
      ctx.scale(dpr, dpr);
    };

    updateBounds();

    // Pointer Tracking with Inertia
    let cursorX = width / 2;
    let cursorY = height / 2;
    let targetCameraX = 0;
    let targetCameraY = 0;
    let cameraX = 0;
    let cameraY = 0;
    let isCursorActive = false;
    let rippleRadius = 0;
    let rippleAlpha = 0;

    const trail: TrailPoint[] = [];

    const handlePointerMove = (clientX: number, clientY: number) => {
      if (prefersReducedMotion || !canvas) return;
      const rect = canvas.getBoundingClientRect();
      cursorX = clientX - rect.left;
      cursorY = clientY - rect.top;
      targetCameraX = (cursorX - width / 2) * 0.04;
      targetCameraY = (cursorY - height / 2) * 0.04;
      isCursorActive = true;

      // Ripple trigger on active pointer movement
      if (Math.random() < 0.15) {
        rippleRadius = 10;
        rippleAlpha = 0.5;
      }

      if (trail.length > 20) trail.shift();
      trail.push({ x: cursorX, y: cursorY, alpha: 0.7 });
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

    let resizeObserver: ResizeObserver | null = null;
    if (typeof ResizeObserver !== "undefined" && canvas.parentElement) {
      resizeObserver = new ResizeObserver(() => {
        updateBounds();
      });
      resizeObserver.observe(canvas.parentElement);
    }

    // Node & Cluster Systems
    const isMobile = width < 768;
    const NODE_COUNT = isMobile ? 32 : 75;
    const nodes: Node3D[] = [];
    const colors = ["#3DD9C4", "#4A72FF", "#34D399", "#A855F7"];
    const clusters: Array<"k8s" | "cicd" | "git" | "ai" | "core"> = ["k8s", "cicd", "git", "ai", "core"];

    for (let i = 0; i < NODE_COUNT; i++) {
      const z = Math.random() * 650 + 50;
      const layer = z > 480 ? "far" : z > 240 ? "mid" : "near";
      const cluster = clusters[i % clusters.length];
      const vx = (Math.random() - 0.5) * (layer === "near" ? 0.4 : layer === "mid" ? 0.25 : 0.12);
      const vy = (Math.random() - 0.5) * (layer === "near" ? 0.4 : layer === "mid" ? 0.25 : 0.12);

      nodes.push({
        x: (Math.random() - 0.5) * (width || 1200) * 1.5,
        y: (Math.random() - 0.5) * (height || 800) * 1.5,
        z,
        vx,
        vy,
        radius: layer === "near" ? Math.random() * 2.8 + 1.8 : Math.random() * 1.8 + 1.0,
        color: colors[i % colors.length],
        layer,
        cluster,
        phase: Math.random() * Math.PI * 2,
      });
    }

    // Data Packets traveling along connection lines
    const packets: DataPacket[] = [];
    for (let p = 0; p < (isMobile ? 8 : 18); p++) {
      packets.push({
        p1Index: Math.floor(Math.random() * NODE_COUNT),
        p2Index: Math.floor(Math.random() * NODE_COUNT),
        progress: Math.random(),
        speed: 0.005 + Math.random() * 0.008,
        color: colors[p % colors.length],
      });
    }

    // Interactive Orbit Rings
    const orbitRings: OrbitRing[] = [
      { radiusX: 180, radiusY: 90, rotation: 0.2, speed: 0.004, color: "rgba(61, 217, 196, 0.25)", packetPos: 0 },
      { radiusX: 280, radiusY: 140, rotation: -0.4, speed: -0.003, color: "rgba(74, 114, 255, 0.20)", packetPos: 0.5 },
      { radiusX: 380, radiusY: 190, rotation: 0.6, speed: 0.002, color: "rgba(168, 85, 247, 0.15)", packetPos: 0.2 },
    ];

    // Floating Technology Badges
    const badgeConfigs = [
      { label: "K8S", color: "#3DD9C4", icon: "☸" },
      { label: "DOCKER", color: "#4A72FF", icon: "🐳" },
      { label: "AWS", color: "#FBBF24", icon: "☁" },
      { label: "GIT", color: "#F87171", icon: "⎇" },
      { label: "CI/CD", color: "#34D399", icon: "⚡" },
      { label: "AI/ML", color: "#A855F7", icon: "🧠" },
    ];

    const badges: FloatingBadge[] = badgeConfigs.map((cfg, idx) => {
      const angle = (idx / badgeConfigs.length) * Math.PI * 2;
      const dist = isMobile ? 120 : 220;
      const bx = Math.cos(angle) * dist;
      const by = Math.sin(angle) * dist;
      return {
        label: cfg.label,
        color: cfg.color,
        icon: cfg.icon,
        x: bx,
        y: by,
        baseX: bx,
        baseY: by,
        z: 200 + idx * 40,
      };
    });

    const FOCAL_LENGTH = 420;
    let time = 0;
    let currentScene = 0;
    let sceneStartTime = Date.now();

    const render = () => {
      time += 0.012;

      // Synchronized Scene Transition Timer
      const now = Date.now();
      const elapsed = (now - sceneStartTime) / 1000;
      if (elapsed > SCENES[currentScene].duration) {
        sceneStartTime = now;
        currentScene = (currentScene + 1) % SCENES.length;
        setActiveSceneIndex(currentScene);
        setTextFade(false);
        setTimeout(() => setTextFade(true), 150);
      }

      // Camera Spring Damping
      cameraX += (targetCameraX - cameraX) * 0.045;
      cameraY += (targetCameraY - cameraY) * 0.045;

      ctx.clearRect(0, 0, width, height);

      // 1. Deep Cyber Background
      ctx.fillStyle = "#0A1020";
      ctx.fillRect(0, 0, width, height);

      // 2. Multi-Glow Atmospheric Bloom
      const bgGlow = ctx.createRadialGradient(
        width / 3, height / 2, 50,
        width / 3, height / 2, Math.max(width, height) * 0.8
      );
      bgGlow.addColorStop(0, "rgba(74, 114, 255, 0.14)");
      bgGlow.addColorStop(0.4, "rgba(61, 217, 196, 0.07)");
      bgGlow.addColorStop(0.8, "rgba(168, 85, 247, 0.03)");
      bgGlow.addColorStop(1, "rgba(10, 16, 32, 1)");
      ctx.fillStyle = bgGlow;
      ctx.fillRect(0, 0, width, height);

      // 3. Central AI Breathing Core
      const coreX = width * 0.32;
      const coreY = height * 0.48;
      const pulseSize = Math.sin(time * 2.5) * 8 + 65;
      const coreGlow = ctx.createRadialGradient(coreX, coreY, 0, coreX, coreY, pulseSize * 2.5);
      coreGlow.addColorStop(0, "rgba(61, 217, 196, 0.28)");
      coreGlow.addColorStop(0.4, "rgba(74, 114, 255, 0.12)");
      coreGlow.addColorStop(1, "rgba(10, 16, 32, 0)");
      ctx.fillStyle = coreGlow;
      ctx.beginPath();
      ctx.arc(coreX, coreY, pulseSize * 2.5, 0, Math.PI * 2);
      ctx.fill();

      // Core Outer Concentric Pulse Rings
      ctx.lineWidth = 1.2;
      ctx.strokeStyle = `rgba(61, 217, 196, ${0.15 + Math.sin(time * 2) * 0.08})`;
      ctx.beginPath();
      ctx.arc(coreX, coreY, pulseSize * 1.2, 0, Math.PI * 2);
      ctx.stroke();

      // 4. Interactive Orbit Ring System
      orbitRings.forEach((ring) => {
        ring.rotation += ring.speed;
        ring.packetPos = (ring.packetPos + 0.006) % 1;

        ctx.save();
        ctx.translate(coreX - cameraX * 0.2, coreY - cameraY * 0.2);
        ctx.rotate(ring.rotation);

        // Orbit Line
        ctx.strokeStyle = ring.color;
        ctx.lineWidth = 1.1;
        ctx.beginPath();
        ctx.ellipse(0, 0, ring.radiusX, ring.radiusY, 0, 0, Math.PI * 2);
        ctx.stroke();

        // Orbit Traveling Packet
        const px = Math.cos(ring.packetPos * Math.PI * 2) * ring.radiusX;
        const py = Math.sin(ring.packetPos * Math.PI * 2) * ring.radiusY;
        ctx.fillStyle = "#3DD9C4";
        ctx.shadowColor = "#3DD9C4";
        ctx.shadowBlur = 8;
        ctx.beginPath();
        ctx.arc(px, py, 2.4, 0, Math.PI * 2);
        ctx.fill();
        ctx.shadowBlur = 0;
        ctx.restore();
      });

      // 5. Cursor Energy Spotlight Aura
      if (isCursorActive && !prefersReducedMotion) {
        const spotlight = ctx.createRadialGradient(cursorX, cursorY, 0, cursorX, cursorY, 240);
        spotlight.addColorStop(0, "rgba(61, 217, 196, 0.22)");
        spotlight.addColorStop(0.5, "rgba(74, 114, 255, 0.08)");
        spotlight.addColorStop(1, "rgba(10, 16, 32, 0)");
        ctx.fillStyle = spotlight;
        ctx.beginPath();
        ctx.arc(cursorX, cursorY, 240, 0, Math.PI * 2);
        ctx.fill();
      }

      // 6. Cursor Ripple Influence Rings
      if (rippleAlpha > 0.01) {
        rippleRadius += 4.5;
        rippleAlpha *= 0.94;
        ctx.strokeStyle = `rgba(61, 217, 196, ${rippleAlpha})`;
        ctx.lineWidth = 1.5;
        ctx.beginPath();
        ctx.arc(cursorX, cursorY, rippleRadius, 0, Math.PI * 2);
        ctx.stroke();
      }

      // 7. Cursor Energy Trail
      if (!prefersReducedMotion && trail.length > 1) {
        ctx.lineWidth = 1.6;
        for (let i = 0; i < trail.length - 1; i++) {
          const pt1 = trail[i];
          const pt2 = trail[i + 1];
          pt1.alpha *= 0.91;
          if (pt1.alpha > 0.02) {
            ctx.strokeStyle = `rgba(61, 217, 196, ${pt1.alpha * 0.6})`;
            ctx.beginPath();
            ctx.moveTo(pt1.x, pt1.y);
            ctx.lineTo(pt2.x, pt2.y);
            ctx.stroke();
          }
        }
      }

      // 8. Project 3D Nodes
      const projected: Array<{ sx: number; sy: number; scale: number; p: Node3D; index: number }> = [];

      nodes.forEach((p, idx) => {
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

        // Magnetic Attraction & Repulsion
        if (!prefersReducedMotion && isCursorActive) {
          const dx = cursorX - sx;
          const dy = cursorY - sy;
          const dist = Math.sqrt(dx * dx + dy * dy);

          if (dist < 170 && dist > 1) {
            const factor = 1 - dist / 170;
            const force = (p.layer === "near" ? 14 : -16) * factor;
            sx += (dx / dist) * force;
            sy += (dy / dist) * force;
          }
        }

        if (sx >= -80 && sx <= width + 80 && sy >= -80 && sy <= height + 80) {
          projected.push({ sx, sy, scale, p, index: idx });
        }
      });

      // 9. Controlled Network Connections
      ctx.lineWidth = 1.0;
      for (let i = 0; i < projected.length; i++) {
        for (let j = i + 1; j < projected.length; j++) {
          const p1 = projected[i];
          const p2 = projected[j];
          const dx = p1.sx - p2.sx;
          const dy = p1.sy - p2.sy;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const maxDist = isMobile ? 100 : 145;

          if (dist < maxDist) {
            let alpha = (1 - dist / maxDist) * 0.3;

            if (isCursorActive && !prefersReducedMotion) {
              const cdist = Math.sqrt(
                Math.pow((p1.sx + p2.sx) / 2 - cursorX, 2) + Math.pow((p1.sy + p2.sy) / 2 - cursorY, 2)
              );
              if (cdist < 160) alpha *= 1.8;
            }

            ctx.strokeStyle = `rgba(61, 217, 196, ${Math.min(alpha, 0.6)})`;
            ctx.beginPath();
            ctx.moveTo(p1.sx, p1.sy);
            ctx.lineTo(p2.sx, p2.sy);
            ctx.stroke();
          }
        }
      }

      // 10. Draw Data Signals Traveling along Topology Lines
      packets.forEach((pkt) => {
        pkt.progress = (pkt.progress + pkt.speed) % 1;
        const n1 = projected.find((item) => item.index === pkt.p1Index);
        const n2 = projected.find((item) => item.index === pkt.p2Index);

        if (n1 && n2) {
          const px = n1.sx + (n2.sx - n1.sx) * pkt.progress;
          const py = n1.sy + (n2.sy - n1.sy) * pkt.progress;

          ctx.fillStyle = pkt.color;
          ctx.shadowColor = pkt.color;
          ctx.shadowBlur = 6;
          ctx.beginPath();
          ctx.arc(px, py, 1.8, 0, Math.PI * 2);
          ctx.fill();
          ctx.shadowBlur = 0;
        }
      });

      // 11. Draw Nodes
      projected.forEach(({ sx, sy, scale, p }) => {
        const drawRadius = Math.max(1.5, p.radius * scale * 1.6);

        ctx.fillStyle = p.color;
        ctx.beginPath();
        ctx.arc(sx, sy, drawRadius, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = p.color === "#3DD9C4" ? "rgba(61, 217, 196, 0.35)" : "rgba(74, 114, 255, 0.35)";
        ctx.beginPath();
        ctx.arc(sx, sy, drawRadius * 2.5, 0, Math.PI * 2);
        ctx.fill();
      });

      // 12. Draw Floating Technology Badges
      badges.forEach((b) => {
        b.baseX += Math.sin(time + b.z) * 0.2;
        b.baseY += Math.cos(time + b.z) * 0.2;

        const scale = FOCAL_LENGTH / (FOCAL_LENGTH + b.z);
        let bx = coreX + b.baseX * scale - cameraX * (scale * 1.5);
        let by = coreY + b.baseY * scale - cameraY * (scale * 1.5);

        // Magnetic Badge Response to Cursor Proximity
        if (isCursorActive && !prefersReducedMotion) {
          const dx = cursorX - bx;
          const dy = cursorY - by;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < 180 && dist > 1) {
            const pull = (1 - dist / 180) * 18;
            bx += (dx / dist) * pull;
            by += (dy / dist) * pull;
          }
        }

        if (bx >= 20 && bx <= width - 20 && by >= 20 && by <= height - 20) {
          ctx.save();
          ctx.font = "bold 10px monospace";
          const badgeText = `${b.icon} ${b.label}`;
          const metrics = ctx.measureText(badgeText);
          const bw = metrics.width + 16;
          const bh = 22;

          // Glass Pill Background
          ctx.fillStyle = "rgba(15, 23, 42, 0.75)";
          ctx.strokeStyle = b.color;
          ctx.lineWidth = 1.1;

          ctx.beginPath();
          ctx.roundRect(bx - bw / 2, by - bh / 2, bw, bh, 11);
          ctx.fill();
          ctx.stroke();

          // Label
          ctx.fillStyle = "#E7EDF7";
          ctx.fillText(badgeText, bx - bw / 2 + 8, by + 3.5);
          ctx.restore();
        }
      });

      // Continuous RAF Loop
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
      {/* Interactive Canvas */}
      <canvas ref={canvasRef} className="absolute inset-0 w-full h-full pointer-events-auto block z-0" />

      {/* Header Operational Status */}
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

      {/* Dynamic Scene Headline Overlay */}
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

      {/* Scene Dots Timeline Navigator */}
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
