import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../services/api";

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

      // Si el backend devuelve data.user.id, usa eso
      const userId = data.id || data.user?.id;

      if (!userId) {
        throw new Error("No se encontró el ID del usuario en la respuesta.");
      }

      // Guardar datos en localStorage
      localStorage.setItem("userId", userId);
      localStorage.setItem("token", data.token || "");

      // Redirigir al home
      navigate("/home", { replace: true });
      window.location.reload(); // 🔹 fuerza recarga para que App.jsx detecte login
    } catch (err) {
      console.error("Error al iniciar sesión:", err);
      setError("Correo o contraseña incorrectos");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-2xl shadow-md w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Iniciar Sesión</h2>
        {error && <p className="text-red-500 text-center mb-4">{error}</p>}
        <form onSubmit={handleSubmit}>
          <input
            type="email"
            placeholder="Correo electrónico"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-2 mb-4 border rounded-lg"
            required
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-2 mb-4 border rounded-lg"
            required
          />
          <button
            type="submit"
            className="w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg transition"
          >
            Entrar
          </button>
        </form>

        <p className="text-center mt-4 text-sm">
          ¿No tienes cuenta?{" "}
          <button
            onClick={() => navigate("/register")}
            className="text-blue-600 hover:underline"
          >
            Regístrate
          </button>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
