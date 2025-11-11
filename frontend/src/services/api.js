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
  const response = await fetch("http://localhost:8080/api/users/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });

  const text = await response.text();
  console.log("Respuesta del servidor:", text);

  if (!response.ok) {
    throw new Error(`Error del servidor: ${text}`);
  }

  return text ? JSON.parse(text) : {};
}

