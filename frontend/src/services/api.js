const API_URL = "http://localhost:8080";

export async function loginUser(email, password) {
  const response = await fetch(`${API_URL}/api/users/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) {
    throw new Error("Error al iniciar sesión");
  }

  return await response.json();
}

export async function registerUser(name, email, password) {
  try {
    const response = await fetch("http://localhost:8080/api/users/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password }),
    });

    // Si no es 200–299, lanzamos error
    if (!response.ok) {
      const text = await response.text();
      throw new Error(`Error del servidor: ${text}`);
    }

    // Intentar parsear respuesta a JSON (si existe)
    const text = await response.text();
    if (!text) return {}; // en caso de que el backend no devuelva cuerpo

    try {
      return JSON.parse(text);
    } catch {
      return { message: text };
    }

  } catch (error) {
    console.error("Error en registerUser:", error);
    throw error;
  }
}

