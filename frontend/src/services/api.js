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


export const updateDiary = async (id, updatedData) => {
  try {
    const response = await fetch(`http://localhost:8080/api/diaries/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedData),
    });

    if (!response.ok) {
      throw new Error("Error al actualizar el diario");
    }

    return await response.json();
  } catch (error) {
    console.error("Error en updateDiary:", error);
    throw error;
  }
};





// 🔹 Obtener emociones predefinidas
// 🔹 Obtener emociones predefinidas
export async function getEmotions() {
  return [
    { id: 1, name: "Triste", color: "azul", intensity: 2, description: "Sentimiento de pena o melancolía" },
    { id: 2, name: "Enojado", color: "rojo", intensity: 4, description: "Sentimiento de ira o frustración" },
    { id: 3, name: "Cansado", color: "gris", intensity: 2, description: "Sensación de agotamiento físico o mental" },
    { id: 4, name: "Ansioso", color: "naranja", intensity: 3, description: "Estado de inquietud o preocupación" },
    { id: 5, name: "Sorprendido", color: "verde", intensity: 4, description: "Reacción ante algo inesperado" },
    { id: 6, name: "Calmado", color: "dorado", intensity: 5, description: "Sensación de calma y tranquilidad" },
    { id: 7, name: "Motivado", color: "violeta", intensity: 4, description: "Deseo de lograr algo con entusiasmo" },
    { id: 8, name: "Feliz", color: "amarillo", intensity: 5, description: "Sentimiento de alegría y bienestar" }
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
