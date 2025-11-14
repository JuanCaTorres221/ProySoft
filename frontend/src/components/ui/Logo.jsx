// frontend/src/components/ui/Logo.jsx
import React from 'react';

export default function Logo({ className = '' }) {
  return (
    <div className={`flex items-center gap-3 ${className}`}>
      <div className="w-12 h-12 rounded-full bg-black flex items-center justify-center text-white font-bold text-lg">:)</div>
      <div className="text-2xl font-extrabold" style={{color: 'black'}}>Kair<span aria-hidden>🙂</span></div>
    </div>
  );
}
