import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/api";
import AuthBackground from "../components/AuthBackground.jsx";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = await loginUser(email, password);
      console.log("Usuario logueado:", data);

      const userId = data.id || data.user?.id;
      if (!userId) {
        throw new Error("No se encontró el ID del usuario en la respuesta.");
      }

      localStorage.setItem("userId", userId);
      localStorage.setItem("token", data.token || "");

      navigate("/home", { replace: true });
      window.location.reload();
    } catch (err) {
      console.error("Error al iniciar sesión:", err);
      setError("Correo o contraseña incorrectos");
    }
  };

  return (
    <AuthBackground>
      <div
        style={{
          backgroundColor: '#FFE675',
          width: '100%',
          maxWidth: '24rem',
          padding: ' 1rem 2rem',
          borderRadius: '1.5rem',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          margin: '1rem 1.5rem'
        }}
        className="flex flex-col"
      >

        {/* HEADER */}
                <div style={{ marginBottom: "1rem", textAlign: "center" }}>
                  <h2
                    style={{
                      fontSize: "1.5rem",
                      fontWeight: "bold",
                      color: "#1f2937",
                      marginBottom: "1rem",
                    }}
                  >
                    KAIRO😀
                  </h2>


          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1.5rem' }}>
            <div
              style={{
                backgroundColor: '#facc15',
                border: '4px solid #000000',
                borderRadius: '9999px',
                padding: '0.75rem'
              }}
            >
              <span style={{ fontSize: '1.75rem' }}>👤</span>
            </div>
          </div>
        </div>

        {error && (
          <p style={{ color: '#ef4444', textAlign: 'center', fontSize: '1rem', marginBottom: '0.75rem' }}>
            {error}
          </p>
        )}

        {/* FORMULARIO */}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>

          {/* EMAIL */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <label style={{ fontSize: '1rem', fontWeight: '500', color: '#374151' }}>
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                width: '92%',
                padding: '1rem',
                fontSize: '1rem',
                border: '3px solid #d1d5db',
                borderRadius: '0.75rem',
                backgroundColor: '#f7f7f7',
                transition: 'all 0.3s'
              }}
            />
          </div>

          {/* PASSWORD */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            <label style={{ fontSize: '1rem', fontWeight: '500', color: '#374151' }}>
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{
                width: '92%',
                padding: '1rem',
                fontSize: '1rem',
                border: '3px solid #d1d5db',
                borderRadius: '0.75rem',
                backgroundColor: '#f7f7f7',
                transition: 'all 0.3s'
              }}
            />
          </div>

          {/* BOTÓN */}
          <button
            type="submit"
            style={{
              width: '100%',
              backgroundColor: '#000000',
              color: '#ffffff',
              padding: '1rem',
              fontSize: '1.1rem',
              borderRadius: '0.75rem',
              fontWeight: 'bold',
              border: 'none',
              cursor: 'pointer',
              marginTop: '0.5rem'
            }}
          >
            Login
          </button>

        </form>

        {/* REGISTER */}
        <div
          style={{
            textAlign: 'center',
            borderTop: '1px solid #e5e7eb',
            paddingTop: '1.5rem',
            marginTop: '1.5rem',
            display: 'flex',
            flexDirection: 'column',
            gap: '0.5rem'
          }}
        >
          <p style={{ color: '#6b7280', fontSize: '0.85rem' }}>
            Don't have an account?
          </p>
          <button
            style={{
              fontWeight: '600',
              color: '#2563eb',
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              textDecoration: 'underline',
              fontSize: '1rem'
            }}
            onClick={() => navigate("/register")}
          >
            Register
          </button>
        </div>

      </div>
    </AuthBackground>

  );
}

export default LoginPage;