const API_URL = "http://localhost:8080/api";

// 🔹 Iniciar sesión
export async function loginUser(email, password) {
  const response = await fetch(`${API_URL}/users/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!response.ok) throw new Error("Error al iniciar sesión");
  const data = await response.json();

  localStorage.setItem("token", data.token || "");
  localStorage.setItem("userId", data.id);
  return data;
}

// 🔹 Registrar usuario
export async function registerUser(name, email, password) {
  const response = await fetch(`${API_URL}/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });

  if (!response.ok) throw new Error("Error al registrarse");
  return await response.json();
}

// 🔹 Obtener todos los diarios del usuario
export async function getDiaries(userId) {
  const response = await fetch(`${API_URL}/diaries/user/${userId}`);
  if (!response.ok) throw new Error("Error al obtener los diarios");
  return await response.json();
}


export async function createDiary(diaryData) {
  const response = await fetch(`${API_URL}/diaries`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(diaryData),
  });

  if (!response.ok) {
    // 👇 Nuevo: leemos el texto que devuelve el backend y lo mostramos
    const errorText = await response.text();
    console.error("Error al crear diario (backend):", errorText);
    throw new Error(errorText || "Error al crear diario");
  return await response.json();
  }
}

// 🔹 Eliminar un diario
export async function deleteDiary(id) {
  const response = await fetch(`${API_URL}/diaries/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) throw new Error("Error al eliminar diario");
  return true;
}

// 🔹 Obtener emociones predefinidas
export async function getEmotions() {
  // Puedes cambiar este endpoint si tu backend lo tiene
  // pero por ahora devolvemos una lista fija predefinida
  return [
    { id: 1, name: "Feliz", color: "amarillo", intensity: 5, description: "Me siento alegre y motivado" },
    { id: 2, name: "Triste", color: "azul", intensity: 3, description: "Me siento decaído o melancólico" },
    { id: 3, name: "Enojado", color: "rojo", intensity: 4, description: "Estoy molesto o frustrado" },
    { id: 4, name: "Ansioso", color: "naranja", intensity: 4, description: "Tengo nerviosismo o preocupación" },
    { id: 5, name: "Sorprendido", color: "verde", intensity: 3, description: "Algo inesperado me ha sorprendido" },
    { id: 6, name: "Calmado", color: "celeste", intensity: 2, description: "Estoy tranquilo y relajado" },
    { id: 7, name: "Cansado", color: "gris", intensity: 3, description: "Estoy agotado o con poca energía" },
    { id: 8, name: "Motivado", color: "violeta", intensity: 5, description: "Tengo energía y determinación" },
  ];
}

// 🔹 (Opcional) Crear una emoción desde el front si luego lo permites
export async function createEmotion(emotionData) {
  const response = await fetch(`${API_URL}/emotions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(emotionData),
  });

  if (!response.ok) throw new Error("Error al crear emoción");
  return await response.json();
}
