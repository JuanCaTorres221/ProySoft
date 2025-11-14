// frontend/src/components/layout/BackgroundWaves.jsx
import React from 'react';

export default function BackgroundWaves({ className = '' }) {
  return (
    <svg className={className} viewBox="0 0 800 800" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg"
      style={{ position: 'absolute', inset: 0, width: '100%', height: '100%', pointerEvents: 'none' }}>
      <defs>
        <linearGradient id="g1" x1="0" x2="0" y1="0" y2="1">
          <stop offset="0%" stopColor="#F9DF6D" stopOpacity="1"/>
          <stop offset="100%" stopColor="#E7C556" stopOpacity="1"/>
        </linearGradient>
      </defs>

      <rect width="100%" height="100%" fill="url(#g1)" />
      <g fill="none" stroke="#E7C556" strokeOpacity="0.12" strokeWidth="60">
        <circle cx="400" cy="430" r="280"></circle>
        <circle cx="400" cy="430" r="180"></circle>
      </g>
    </svg>
  );
}
