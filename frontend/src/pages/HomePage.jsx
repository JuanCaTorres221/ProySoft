import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDiaries, createDiary, deleteDiary, getEmotions, updateDiary } from "../services/api";
import "../App.css";

export default function HomePage() {
  const [diaries, setDiaries] = useState([]);
  const [emotions, setEmotions] = useState([]);
  const [newDiary, setNewDiary] = useState({ name: "", emotionId: "" });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const userId = localStorage.getItem("userId");
  const [editingDiaryId, setEditingDiaryId] = useState(null);
  const [editingEmotionId, setEditingEmotionId] = useState("");//aqui


  useEffect(() => {
    console.log(" useEffect ejecutado con userId:", userId);
    if (!userId) {
        console.log(" No hay userId, no se cargan datos");
        setLoading(false); // evita quedarse en "Cargando..."
        return;
      }
    const fetchData = async () => {
      try {
          console.log(" Cargando datos...");
        const [diariesData, emotionsData] = await Promise.all([
          getDiaries(userId),
          getEmotions(),
        ]);
        console.log("Datos cargados:", diariesData, emotionsData);
        setDiaries(diariesData);
        setEmotions(emotionsData);
      } catch (err) {
        console.error("Error al cargar datos:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [userId]);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login", { replace: true });
    window.location.reload();
  };


  const handleCreateDiary = async () => {
    if (!newDiary.name || !newDiary.emotionId) {
      alert("Por favor ingresa un nombre y selecciona una emoción.");
      return;
    }

    try {
      const diaryData = {
        userId: parseInt(userId),
        emotionId: parseInt(newDiary.emotionId),
        notes: newDiary.name,
        entryDate: new Date().toLocaleDateString("en-CA"),
      };


      const created = await createDiary(diaryData);
      setDiaries((prev) => [...prev, created]);
      setNewDiary({ name: "", emotionId: "" });
    } catch (error) {
      console.error("Error al crear diario:", error);
      alert("Ocurrió un error al crear el diario.");
    }

    window.location.reload();
  };


  const handleDelete = async (id) => {
    if (!window.confirm("¿Seguro que deseas eliminar este diario?")) return;
    try {
      await deleteDiary(id);
      setDiaries(diaries.filter((d) => d.id !== id));
    } catch (err) {
      console.error("Error al eliminar diario:", err);
    }
  };

const handleUpdateEmotion = async (id) => {
  try {
    const diary = diaries.find((d) => d.id === id);
    if (!diary) return;

    const updatedData = {
      userId: parseInt(userId),
      emotionId: parseInt(editingEmotionId),
      notes: diary.notes,
      entryDate: diary.entryDate,
    };

    const updatedDiary = await updateDiary(id, updatedData);

    setDiaries((prev) =>
      prev.map((d) => (d.id === id ? updatedDiary : d))
    );

    setEditingDiaryId(null);
    setEditingEmotionId("");
    window.location.reload();
  } catch (error) {
    console.error("Error al actualizar emoción:", error);
    alert("No se pudo actualizar el diario.");
  }
};


  if (loading) return <p>Cargando...</p>;

  return (
    <div className="container">
      <header className="header">
        <h1>Mis Diarios</h1>
        <button onClick={handleLogout} className="logout-button">
          Cerrar sesión
        </button>
        <button onClick={() => navigate("/stats")} className="stats-button">
          Ver estadísticas
        </button>

      </header>

      <div className="create-diary">
        <h2>Crear nuevo diario</h2>
        <input
          type="text"
          placeholder="Nombre o nota del diario"
          value={newDiary.name}
          onChange={(e) => setNewDiary({ ...newDiary, name: e.target.value })}
        />
        <select
          value={newDiary.emotionId}
          onChange={(e) =>
            setNewDiary({ ...newDiary, emotionId: e.target.value })
          }
        >
          <option value="">Selecciona una emoción</option>
          {emotions.map((emotion) => (
            <option key={emotion.id} value={emotion.id}>
              {emotion.name}
            </option>
          ))}
        </select>
        <button onClick={handleCreateDiary}>Crear Diario</button>
      </div>

      <div className="diary-list">
        <h2>Diarios existentes</h2>
        {diaries.length === 0 ? (
          <p>No hay diarios aún.</p>
        ) : (
          <ul>
            {diaries.map((diary) => (
              <li key={diary.id} className="diary-item">
                <strong>{diary.notes}</strong> <br />
                Emoción:{" "}
                {editingDiaryId === diary.id ? (
                  <select
                    value={editingEmotionId}
                    onChange={(e) => setEditingEmotionId(e.target.value)}
                  >
                    <option value="">Selecciona una emoción</option>
                    {emotions.map((emotion) => (
                      <option key={emotion.id} value={emotion.id}>
                        {emotion.name}
                      </option>
                    ))}
                  </select>
                ) : (
                  diary.emotion?.name || "N/A"
                )}{" "}
                <br />
                Fecha: {diary.entryDate}
                <div>
                  {editingDiaryId === diary.id ? (
                    <>
                      <button onClick={() => handleUpdateEmotion(diary.id)}>Guardar</button>
                      <button
                        onClick={() => {
                          setEditingDiaryId(null);
                          setEditingEmotionId("");
                        }}
                      >
                        Cancelar
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        onClick={() => {
                          setEditingDiaryId(diary.id);
                          setEditingEmotionId(diary.emotion?.id || "");
                        }}
                      >
                        Editar
                      </button>
                      <button onClick={() => handleDelete(diary.id)}>Eliminar</button>
                    </>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
