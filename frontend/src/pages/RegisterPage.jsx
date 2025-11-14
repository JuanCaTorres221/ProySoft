import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/api";
import AuthBackground from "../components/AuthBackground";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    try {
      await registerUser(name, email, password);
      setSuccess("User successfully registered. Redirecting...");
      setTimeout(() => navigate("/login"), 1500);
    } catch (err) {
      setError("Error registering user. Try again.");
    }
  };

  return (
    <AuthBackground >
      {/* CONTENEDOR PRINCIPAL IDENTICO AL LOGIN */}
      <div
        style={{
          backgroundColor: '#FFE675',
                    width: '100%',
                    maxWidth: '24rem',
                    padding: ' 1rem 2rem',
                    borderRadius: '1.5rem',
                    boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
                    margin: '1rem 1rem'
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
            REGISTRO
          </h2>
        </div>

        {error && (
          <p style={{ color: "#ef4444", textAlign: "center", fontSize: "1.125rem", marginBottom: "0.75rem" }}>
            {error}
          </p>
        )}
        {success && (
          <p style={{ color: "#22c55e", textAlign: "center", fontSize: "1.125rem", marginBottom: "0.75rem" }}>
            {success}
          </p>
        )}

        {/* FORMULARIO */}
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>

          {/* NAME */}
          <div style={{ display: "flex", flexDirection: "column", gap: "0.7rem" }}>
            <label style={{ fontSize: "1.125rem", fontWeight: "500", color: "#374151" }}>
              User Name
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              style={{
                width: "92%",
                padding: " 1rem 1.25rem",
                fontSize: "1.125rem",
                border: "3px solid #d1d5db",
                borderRadius: "1rem",
                backgroundColor: "#f7f7f7",
                transition: "all 0.3s",
              }}
              onFocus={(e) => {
                e.target.style.backgroundColor = "#ffffff";
                e.target.style.borderColor = "#000000";
                e.target.style.boxShadow = "0 20px 25px -5px rgba(0, 0, 0, 0.1)";
              }}
              onBlur={(e) => {
                e.target.style.backgroundColor = "#f7f7f7";
                e.target.style.borderColor = "#d1d5db";
                e.target.style.boxShadow = "none";
              }}
              placeholder="Enter your name"
              required
            />
          </div>

          {/* EMAIL */}
          <div style={{ display: "flex", flexDirection: "column", gap: "0.7rem" }}>
            <label style={{ fontSize: "1.125rem", fontWeight: "500", color: "#374151" }}>
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{
                width: "92%",
                padding: "1.25rem",
                fontSize: "1.125rem",
                border: "3px solid #d1d5db",
                borderRadius: "1rem",
                backgroundColor: "#f7f7f7",
                transition: "all 0.3s",
              }}
              onFocus={(e) => {
                e.target.style.backgroundColor = "#ffffff";
                e.target.style.borderColor = "#000000";
                e.target.style.boxShadow = "0 20px 25px -5px rgba(0, 0, 0, 0.1)";
              }}
              onBlur={(e) => {
                e.target.style.backgroundColor = "#f7f7f7";
                e.target.style.borderColor = "#d1d5db";
                e.target.style.boxShadow = "none";
              }}
              placeholder="Enter your email"
              required
            />
          </div>

          {/* PASSWORD */}
          <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
            <label style={{ fontSize: "1.125rem", fontWeight: "500", color: "#374151" }}>
              Password
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{
                width: "92%",
                padding: "1.25rem",
                fontSize: "1.125rem",
                border: "3px solid #d1d5db",
                borderRadius: "1rem",
                backgroundColor: "#f7f7f7",
                transition: "all 0.3s",
              }}
              onFocus={(e) => {
                e.target.style.backgroundColor = "#ffffff";
                e.target.style.borderColor = "#000000";
                e.target.style.boxShadow = "0 20px 25px -5px rgba(0, 0, 0, 0.1)";
              }}
              onBlur={(e) => {
                e.target.style.backgroundColor = "#f7f7f7";
                e.target.style.borderColor = "#d1d5db";
                e.target.style.boxShadow = "none";
              }}
              placeholder="Enter your password"
              required
            />
          </div>

          {/* BOTÓN */}
          <div style={{ paddingTop: "1rem" }}>
            <button
              type="submit"
              style={{
                width: "92%",
                backgroundColor: "#000000",
                color: "#ffffff",
                padding: "1.25rem",
                fontSize: "1.25rem",
                borderRadius: "1rem",
                fontWeight: "bold",
                border: "none",
                cursor: "pointer",
                transition: "all 0.3s",
              }}
              onMouseOver={(e) => (e.target.style.backgroundColor = "#111827")}
              onMouseOut={(e) => (e.target.style.backgroundColor = "#000000")}
            >
              Create Account
            </button>
          </div>
        </form>

        {/* LOGIN SECTION */}
        <div
          style={{
            textAlign: "center",
            borderTop: "1px solid #e5e7eb",
            marginTop: "5px",
            display: "flex",
            flexDirection: "column",
            gap: "0.75rem",
          }}
        >
          <p style={{ color: "#6b7280", fontSize: "1.125rem" }}>
            Already have an account?
          </p>
          <button
            style={{
              fontWeight: "600",
              color: "#2563eb",
              background: "none",
              border: "none",
              cursor: "pointer",
              textDecoration: "underline",
              fontSize: "1.125rem",
              transition: "color 0.3s"
            }}
            onMouseOver={(e) => (e.target.style.color = "#1e40af")}
            onMouseOut={(e) => (e.target.style.color = "#2563eb")}
            onClick={() => navigate("/login")}
          >
            Login
          </button>
        </div>
      </div>
    </AuthBackground>
  );
}
