// frontend/src/components/ui/Input.jsx
import React from 'react';

export default function Input({ label, ...props }) {
  return (
    <div className="mb-4">
      {label && <label className="block mb-2 text-sm font-medium text-gray-700">{label}</label>}
      <input className="input-default" {...props} />
    </div>
  );
}
