// frontend/src/components/auth/AuthCard.jsx
import React from 'react';

export default function AuthCard({ title, icon, children, footer }) {
  return (
    <div className="auth-card">
      <div className="flex flex-col items-center -mt-12 mb-4">
        <div className="w-20 h-20 rounded-full bg-white flex items-center justify-center shadow" style={{border: '6px solid var(--bg-yellow)'}}>
          {/* icon placeholder */}
          {icon ? icon : <div className="w-12 h-12 rounded-full bg-gray-100" />}
        </div>
        {title && <h2 className="mt-3 text-lg font-bold">{title}</h2>}
      </div>

      <div>
        {children}
      </div>

      {footer && <div className="mt-4 text-center text-sm text-gray-600">{footer}</div>}
    </div>
  );
}
