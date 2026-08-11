"use client";

import React, { useEffect, useRef } from "react";

interface Node3D {
  x: number;
  y: number;
  z: number;
  vx: number;
  vy: number;
  radius: number;
  color: string;
}

export default function InfrastructureGridBackground() {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let animationFrameId: number;
    const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

    let width = (canvas.width = window.innerWidth);
    let height = (canvas.height = window.innerHeight);

    // Mouse Tracking for Interactive Parallax & Magnetic Field
    let rawMouseX = width / 2;
    let rawMouseY = height / 2;
    let targetMouseX = 0;
    let targetMouseY = 0;
    let cameraX = 0;
    let cameraY = 0;

    const handleMouseMove = (e: MouseEvent) => {
      rawMouseX = e.clientX;
      rawMouseY = e.clientY;
      targetMouseX = (e.clientX - width / 2) * 0.08;
      targetMouseY = (e.clientY - height / 2) * 0.08;
    };

    window.addEventListener("mousemove", handleMouseMove);

    const handleResize = () => {
      if (!canvas) return;
      width = canvas.width = window.innerWidth;
      height = canvas.height = window.innerHeight;
    };

    window.addEventListener("resize", handleResize);

    // Generate 3D Infrastructure Network Nodes
    const NODE_COUNT = window.innerWidth < 768 ? 30 : 65;
    const nodes: Node3D[] = [];

    for (let i = 0; i < NODE_COUNT; i++) {
      nodes.push({
        x: (Math.random() - 0.5) * width * 1.5,
        y: (Math.random() - 0.5) * height * 1.5,
        z: Math.random() * 800 + 100, // Depth (100 to 900)
        vx: (Math.random() - 0.5) * 0.5,
        vy: (Math.random() - 0.5) * 0.5,
        radius: Math.random() * 2.5 + 1.5,
        color: Math.random() > 0.35 ? "#3DD9C4" : "#34D399",
      });
    }

    const FOCAL_LENGTH = 450; // 3D Camera Projection Focal Length

    // Animation Loop
    let time = 0;
    const render = () => {
      time += 0.015;

      // Ultra-Silky Smooth Mouse Dampening (Lerp factor 0.04)
      cameraX += (targetMouseX - cameraX) * 0.04;
      cameraY += (targetMouseY - cameraY) * 0.04;

      ctx.clearRect(0, 0, width, height);

      // 1. Dark Cyber Background Fill
      ctx.fillStyle = "#0A1020";
      ctx.fillRect(0, 0, width, height);

      // 2. Interactive Cursor Telemetry Glow Spotlight
      const gradient = ctx.createRadialGradient(
        rawMouseX, rawMouseY, 0,
        rawMouseX, rawMouseY, 220
      );
      gradient.addColorStop(0, "rgba(61, 217, 196, 0.12)");
      gradient.addColorStop(0.5, "rgba(52, 211, 153, 0.04)");
      gradient.addColorStop(1, "rgba(10, 16, 32, 0)");

      ctx.fillStyle = gradient;
      ctx.beginPath();
      ctx.arc(rawMouseX, rawMouseY, 220, 0, Math.PI * 2);
      ctx.fill();

      // 3. Cyber Horizon Grid Lines with Dynamic Cursor Pitch Shift
      ctx.strokeStyle = "rgba(34, 49, 77, 0.3)";
      ctx.lineWidth = 1;
      const gridSpacing = 65;
      const horizon = height * 0.65 + cameraY * 0.5;

      for (let x = -width; x < width * 2; x += gridSpacing) {
        ctx.beginPath();
        ctx.moveTo(x + cameraX * 0.6, height);
        ctx.lineTo(width / 2 + (x - width / 2) * 0.25 + cameraX * 0.15, horizon);
        ctx.stroke();
      }

      for (let y = horizon; y < height; y += 22) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        ctx.stroke();
      }

      // 4. Project 3D Nodes to 2D Screen Space with Magnetic Cursor Displacement
      const projectedNodes = nodes.map((node) => {
        if (!prefersReducedMotion) {
          node.x += node.vx;
          node.y += node.vy;

          // Boundary Wrap Around for Continuous Smooth Motion
          if (node.x > width * 0.8) node.x = -width * 0.8;
          if (node.x < -width * 0.8) node.x = width * 0.8;
          if (node.y > height * 0.8) node.y = -height * 0.8;
          if (node.y < -height * 0.8) node.y = height * 0.8;
        }

        // Apply 3D Perspective Projection
        const scale = FOCAL_LENGTH / (FOCAL_LENGTH + node.z);
        let projX = width / 2 + (node.x + cameraX) * scale;
        let projY = height / 2 + (node.y + cameraY) * scale;

        // Interactive Cursor Magnetic Repulsion / Attraction
        const cdx = projX - rawMouseX;
        const cdy = projY - rawMouseY;
        const distToCursor = Math.sqrt(cdx * cdx + cdy * cdy);
        const maxDist = 200;

        let cursorGlow = 1;
        if (distToCursor < maxDist) {
          const pushFactor = (1 - distToCursor / maxDist);
          projX += (cdx / distToCursor) * pushFactor * 30;
          projY += (cdy / distToCursor) * pushFactor * 30;
          cursorGlow = 1 + pushFactor * 1.5; // Brighten node near cursor
        }

        const projRadius = node.radius * scale * cursorGlow;
        const opacity = Math.min(1, Math.max(0.12, (1000 - node.z) / 900)) * Math.min(1.8, cursorGlow);

        return { projX, projY, projRadius, opacity, color: node.color, cursorGlow, node };
      });

      // 5. Draw Infrastructure Network Linkages & Active Mouse Reactive Lines
      for (let i = 0; i < projectedNodes.length; i++) {
        for (let j = i + 1; j < projectedNodes.length; j++) {
          const n1 = projectedNodes[i];
          const n2 = projectedNodes[j];

          const dx = n1.projX - n2.projX;
          const dy = n1.projY - n2.projY;
          const dist = Math.sqrt(dx * dx + dy * dy);
          const maxLinkDist = 175;

          if (dist < maxLinkDist) {
            // Distance from Cursor to Line Center
            const midX = (n1.projX + n2.projX) / 2;
            const midY = (n1.projY + n2.projY) / 2;
            const mouseDistToLine = Math.sqrt(
              (midX - rawMouseX) * (midX - rawMouseX) + (midY - rawMouseY) * (midY - rawMouseY)
            );
            const lineGlow = mouseDistToLine < 180 ? 1.8 : 1.0;

            const alpha = (1 - dist / maxLinkDist) * n1.opacity * n2.opacity * 0.45 * lineGlow;
            ctx.strokeStyle = mouseDistToLine < 180 ? `rgba(52, 211, 153, ${alpha})` : `rgba(61, 217, 196, ${alpha})`;
            ctx.lineWidth = lineGlow > 1 ? 1.5 : 1;

            ctx.beginPath();
            ctx.moveTo(n1.projX, n1.projY);
            ctx.lineTo(n2.projX, n2.projY);
            ctx.stroke();

            // Active Telemetry Signal Packet Pulses
            if (!prefersReducedMotion && (i + j) % 2 === 0) {
              const pulsePos = (time * 0.9 + i * 0.1) % 1;
              const pulseX = n1.projX + (n2.projX - n1.projX) * pulsePos;
              const pulseY = n1.projY + (n2.projY - n1.projY) * pulsePos;

              ctx.fillStyle = lineGlow > 1 ? "#3DD9C4" : "#34D399";
              ctx.shadowColor = "#3DD9C4";
              ctx.shadowBlur = 10 * lineGlow;
              ctx.beginPath();
              ctx.arc(pulseX, pulseY, 2.2 * n1.projRadius, 0, Math.PI * 2);
              ctx.fill();
              ctx.shadowBlur = 0;
            }
          }
        }
      }

      // 6. Render Glowing Infrastructure Node Points
      projectedNodes.forEach((pn) => {
        ctx.save();
        ctx.fillStyle = pn.color;
        ctx.shadowColor = pn.color;
        ctx.shadowBlur = 14 * pn.opacity * pn.cursorGlow;
        ctx.globalAlpha = Math.min(1, pn.opacity);

        ctx.beginPath();
        ctx.arc(pn.projX, pn.projY, pn.projRadius, 0, Math.PI * 2);
        ctx.fill();
        ctx.restore();
      });

      if (!prefersReducedMotion) {
        animationFrameId = requestAnimationFrame(render);
      }
    };

    render();

    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("resize", handleResize);
      cancelAnimationFrame(animationFrameId);
    };
  }, []);

  return (
    <div className="fixed inset-0 pointer-events-none z-0 overflow-hidden bg-[#0A1020]">
      {/* Ambient Gradient Glows */}
      <div className="absolute w-[600px] h-[600px] bg-[#3DD9C4]/10 rounded-full blur-[140px] pointer-events-none -top-40 -left-40 animate-pulse" />
      <div className="absolute w-[500px] h-[500px] bg-[#16233A]/80 rounded-full blur-[140px] pointer-events-none -bottom-40 -right-40" />

      {/* Interactive 3D Infrastructure Canvas */}
      <canvas ref={canvasRef} className="absolute inset-0 w-full h-full opacity-85" />

      {/* Monospace HUD Top-Left Overlay */}
      <div className="absolute top-6 left-6 hidden md:flex items-center gap-2 text-[10px] font-mono text-[#8B99B8] bg-[#0A1020]/80 backdrop-blur-md border border-[#22314D] px-3 py-1.5 rounded-lg shadow-lg">
        <span className="w-2 h-2 rounded-full bg-[#34D399] animate-ping" />
        <span className="text-[#3DD9C4] font-bold">CLOUDFORGE MISSION CONTROL</span>
        <span className="text-[#8B99B8]">|</span>
        <span>ORCHESTRATION GATEWAY</span>
      </div>

      {/* Monospace HUD Top-Right Overlay */}
      <div className="absolute top-6 right-6 hidden md:flex items-center gap-2 text-[10px] font-mono text-[#8B99B8] bg-[#0A1020]/80 backdrop-blur-md border border-[#22314D] px-3 py-1.5 rounded-lg shadow-lg">
        <span>ENV: <strong className="text-[#E7EDF7]">PROD</strong></span>
        <span>•</span>
        <span>REGION: <strong className="text-[#3DD9C4]">US-EAST-1</strong></span>
      </div>

      {/* Monospace HUD Bottom-Left Overlay */}
      <div className="absolute bottom-6 left-6 hidden md:flex items-center gap-2 text-[10px] font-mono text-[#8B99B8] bg-[#0A1020]/80 backdrop-blur-md border border-[#22314D] px-3 py-1.5 rounded-lg shadow-lg">
        <span className="text-[#34D399] font-bold">SYSTEM OK</span>
        <span>•</span>
        <span>60 FPS</span>
        <span>•</span>
        <span>100% HEALTHY</span>
      </div>

      {/* Monospace HUD Bottom-Right Overlay */}
      <div className="absolute bottom-6 right-6 hidden md:flex items-center gap-2 text-[10px] font-mono text-[#8B99B8] bg-[#0A1020]/80 backdrop-blur-md border border-[#22314D] px-3 py-1.5 rounded-lg shadow-lg">
        <span>v0.1.0-SPRING</span>
        <span>•</span>
        <span className="text-[#3DD9C4]">K8S-MESH-ACTIVE</span>
      </div>
    </div>
  );
}
