import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getDiaries, createDiary, deleteDiary, getEmotions, updateDiary } from "../services/api";

export default function HomePage() {
  const [diaries, setDiaries] = useState([]);
  const [emotions, setEmotions] = useState([]);
  const [newDiary, setNewDiary] = useState({ name: "", emotionId: "" });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const userId = localStorage.getItem("userId");
  const [editingDiaryId, setEditingDiaryId] = useState(null);
  const [editingEmotionId, setEditingEmotionId] = useState("");

  // Emociones con colores
  const emotionColors = {
    "Feliz": "#FACC5C",
    "Calmado": "#CFE4FF",
    "Motivado": "#FC81C1",
    "Sorprendido": "#34D399",
    "Triste": "#459BFF",
    "Enojado": "#F03C3C",
    "Cansado": "#A8A8A8",
    "Ansioso": "#EB9300"
  };

  useEffect(() => {
    console.log(" useEffect ejecutado con userId:", userId);
    if (!userId) {
      console.log(" No hay userId, no se cargan datos");
      setLoading(false);
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

  const handleEmotionSelect = (emotionId) => {
    setNewDiary({ ...newDiary, emotionId });
  };

  if (loading) return <p>Cargando...</p>;

  // Obtener fecha actual formateada
  const currentDate = new Date();
  const formattedDate = currentDate.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
  const formattedTime = currentDate.toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true
  });

  return (
    <div className="min-h-screen bg-[#FFE675] flex items-center justify-center p-4">
      <div
        style={{
          backgroundColor: '#FFFFFF',
          width: '100%',
          maxWidth: '900px',
          padding: '2rem',
          borderRadius: '2rem',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
          margin: '2rem 1rem'
        }}
        className="flex flex-col"
      >
        {/* Header con fecha, hora y botones */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '2rem',
          flexWrap: 'wrap',
          gap: '1rem'
        }}>
          <div style={{ textAlign: 'left' }}>
            <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#1F2937' }}>
              {formattedDate}
            </div>
            <div style={{ fontSize: '1rem', color: '#6B7280', marginTop: '0.25rem' }}>
              {formattedTime}
            </div>
          </div>

          <h1 style={{ fontSize: "2rem", fontWeight: "bold", color: "#1F2937" }}>MIS DIARIOS 🏠</h1>

          <div style={{ display: "flex", gap: "1rem" }}>
            <button
              onClick={handleLogout}
              style={{
                backgroundColor: '#9C0000',
                color: '#FFFFFF',
                padding: '0.75rem 1.5rem',
                borderRadius: '0.75rem',
                fontWeight: '600',
                border: 'none',
                cursor: 'pointer',
                fontSize: '0.875rem',
                transition: 'all 0.3s ease',
                transform: 'scale(1)'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = '#7A0000';
                e.target.style.transform = 'scale(1.05)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = '#9C0000';
                e.target.style.transform = 'scale(1)';
              }}
            >
              Cerrar sesión
            </button>

            <button
              onClick={() => navigate("/stats")}
              style={{
                backgroundColor: '#F8FAFC',
                color: '#374151',
                padding: '0.75rem 1.5rem',
                borderRadius: '0.75rem',
                fontWeight: '600',
                border: '1px solid #E5E7EB',
                cursor: 'pointer',
                fontSize: '0.875rem',
                transition: 'all 0.3s ease',
                transform: 'scale(1)'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = '#E5E7EB';
                e.target.style.transform = 'scale(1.05)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = '#F8FAFC';
                e.target.style.transform = 'scale(1)';
              }}
            >
              Ver estadísticas
            </button>
          </div>
        </div>

        {/* Línea separadora */}
        <div style={{ height: '1px', backgroundColor: '#E5E7EB', margin: '0 0 2rem 0' }}></div>

        {/* Sección de Registrar Estado de Ánimo */}
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{
            fontSize: '1.5rem',
            fontWeight: 'bold',
            color: '#1F2937',
            textAlign: 'center',
            marginBottom: '1.5rem'
          }}>
            Registrar Estado de Ánimo
          </h1>

          {/* Indicador de emoción seleccionada */}
          {newDiary.emotionId && (
            <div style={{
              textAlign: 'center',
              marginBottom: '1rem',
              padding: '0.75rem',
              backgroundColor: '#F0FDF4',
              borderRadius: '0.75rem',
              border: '2px solid #BBF7D0'
            }}>
              <span style={{ fontWeight: '600', color: '#065F46', fontSize: '1rem' }}>
                 Emoción seleccionada: {emotions.find(e => e.id.toString() === newDiary.emotionId)?.name}
              </span>
            </div>
          )}

          {/* Campo de notas */}
          <textarea
            placeholder="Escribe cómo te sientes..."
            value={newDiary.name}
            onChange={(e) => setNewDiary({ ...newDiary, name: e.target.value })}
            style={{
              width: '92%',
              padding: '1rem',
              border: '2px solid #E5E7EB',
              borderRadius: '1rem',
              backgroundColor: '#F8FAFC',
              fontSize: '1rem',
              resize: 'none',
              minHeight: '80px',
              margin: ' 1rem'
            }}
          />

          {/* Botones de emociones */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))',
            gap: '0.5rem',
            marginBottom: '1.5rem'
          }}>
            {emotions.map((emotion) => (
              <button
                key={emotion.id}
                style={{
                  backgroundColor: newDiary.emotionId === emotion.id.toString()
                    ? emotionColors[emotion.name]
                    : `${emotionColors[emotion.name]}80`,
                  color: '#1F2937',
                  padding: '0.75rem 0.5rem',
                  borderRadius: '0.75rem',
                  border: newDiary.emotionId === emotion.id.toString()
                    ? '3px solid #000000'
                    : '2px solid transparent',
                  fontSize: '0.875rem',
                  fontWeight: '500',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  transform: 'scale(1)',
                  position: 'relative'
                }}
                onMouseOver={(e) => {
                  if (newDiary.emotionId !== emotion.id.toString()) {
                    e.target.style.transform = 'scale(1.05)';
                    e.target.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
                  }
                }}
                onMouseOut={(e) => {
                  if (newDiary.emotionId !== emotion.id.toString()) {
                    e.target.style.transform = 'scale(1)';
                    e.target.style.boxShadow = 'none';
                  }
                }}
                onClick={() => handleEmotionSelect(emotion.id.toString())}
              >
                {emotion.name}
                {/* Indicador de selección */}
                {newDiary.emotionId === emotion.id.toString() && (
                  <span style={{
                    position: 'absolute',
                    top: '-5px',
                    right: '-5px',
                    backgroundColor: '#10B981',
                    color: 'white',
                    borderRadius: '50%',
                    width: '20px',
                    height: '20px',
                    fontSize: '12px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 'bold'
                  }}>
                    ✓
                  </span>
                )}
              </button>
            ))}
          </div>

          {/* Botón para limpiar seleccion */}
          {newDiary.emotionId && (
            <button
              onClick={() => setNewDiary({ ...newDiary, emotionId: "" })}
              style={{
                backgroundColor: '#F3F4F6',
                color: '#374151',
                padding: '0.5rem 1rem',
                borderRadius: '0.5rem',
                border: '1px solid #D1D5DB',
                cursor: 'pointer',
                fontSize: '0.75rem',
                marginBottom: '1rem',
                transition: 'all 0.3s ease'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = '#E5E7EB';
                e.target.style.transform = 'scale(1.05)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = '#F3F4F6';
                e.target.style.transform = 'scale(1)';
              }}
            >
              Limpiar selección
            </button>
          )}

          {/* Botones de acción */}
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <button
              onClick={handleCreateDiary}
              style={{
                flex: 1,
                backgroundColor: '#000000',
                color: '#ffffff',
                padding: '0.75rem',
                borderRadius: '1rem',
                fontWeight: '600',
                border: 'none',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                transform: 'scale(1)'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = '#374151';
                e.target.style.transform = 'scale(1.03)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = '#000000';
                e.target.style.transform = 'scale(1)';
              }}
            >
              Crear
            </button>

            <button
              onClick={() => setNewDiary({ name: "", emotionId: "" })}
              style={{
                flex: 1,
                backgroundColor: '#F3F4F6',
                color: '#374151',
                padding: '0.75rem',
                borderRadius: '1rem',
                fontWeight: '600',
                border: 'none',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                transform: 'scale(1)'
              }}
              onMouseOver={(e) => {
                e.target.style.backgroundColor = '#E5E7EB';
                e.target.style.transform = 'scale(1.03)';
              }}
              onMouseOut={(e) => {
                e.target.style.backgroundColor = '#F3F4F6';
                e.target.style.transform = 'scale(1)';
              }}
            >
              Cancelar
            </button>
          </div>
        </div>

        {/* Línea separadora */}
        <div style={{ height: '1px', backgroundColor: '#E5E7EB', margin: '2rem 0' }}></div>

        {/* LISTA DE DIARIOS EXISTENTES */}
        <div style={{ width: "100%" }}>
          <h2 style={{
            fontWeight: "bold",
            marginBottom: "1.5rem",
            fontSize: "1.5rem",
            color: "#1F2937"
          }}>
            Diarios existentes
          </h2>

          {diaries.length === 0 ? (
            <div style={{
              textAlign: 'center',
              padding: '2rem',
              backgroundColor: '#F8FAFC',
              borderRadius: '1rem',
              border: '2px dashed #E5E7EB'
            }}>
              <p style={{ color: '#6B7280', fontSize: '1rem' }}>
                No hay diarios registrados aún. ¡Crea tu primer diario!
              </p>
            </div>
          ) : (
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
                gap: "1rem"
              }}
            >
              {diaries.map((diary) => (
                <div
                  key={diary.id}
                  style={{
                    backgroundColor: "#F8FAFC",
                    borderRadius: "1rem",
                    padding: "1.5rem",
                    border: "2px solid #E5E7EB",
                    boxShadow: "0 4px 10px rgba(0,0,0,0.1)",
                    transition: 'all 0.3s ease'
                  }}
                  onMouseOver={(e) => {
                    e.currentTarget.style.transform = 'translateY(-2px)';
                    e.currentTarget.style.boxShadow = '0 8px 20px rgba(0,0,0,0.15)';
                  }}
                  onMouseOut={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = '0 4px 10px rgba(0,0,0,0.1)';
                  }}
                >
                  <div style={{
                    fontWeight: "bold",
                    marginBottom: "0.5rem",
                    fontSize: "1.1rem",
                    color: "#1F2937"
                  }}>
                    {diary.notes}
                  </div>

                  <div style={{ marginBottom: "0.5rem" }}>
                    <strong style={{ color: "#374151" }}>Emoción:</strong>{" "}
                    {editingDiaryId === diary.id ? (
                      <select
                        value={editingEmotionId}
                        onChange={(e) => setEditingEmotionId(e.target.value)}
                        style={{
                          padding: '0.25rem',
                          borderRadius: '0.5rem',
                          border: '1px solid #D1D5DB',
                          backgroundColor: 'white'
                        }}
                      >
                        <option value="">Selecciona una emoción</option>
                        {emotions.map((emotion) => (
                          <option key={emotion.id} value={emotion.id}>
                            {emotion.name}
                          </option>
                        ))}
                      </select>
                    ) : (
                      <span style={{
                        color: emotionColors[diary.emotion?.name] || '#374151',
                        fontWeight: '500'
                      }}>
                        {diary.emotion?.name || "N/A"}
                      </span>
                    )}
                  </div>

                  <div style={{ marginBottom: "0.75rem" }}>
                    <strong style={{ color: "#374151" }}>Fecha:</strong>{" "}
                    <span style={{ color: "#6B7280" }}>{diary.entryDate}</span>
                  </div>

                  <div style={{ display: "flex", gap: "0.5rem" }}>
                    {editingDiaryId === diary.id ? (
                      <>
                        <button
                          onClick={() => handleUpdateEmotion(diary.id)}
                          style={{
                            backgroundColor: '#10B981',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '0.5rem',
                            border: 'none',
                            cursor: 'pointer',
                            fontSize: '0.875rem',
                            transition: 'all 0.3s ease'
                          }}
                          onMouseOver={(e) => {
                            e.target.style.backgroundColor = '#059669';
                            e.target.style.transform = 'scale(1.05)';
                          }}
                          onMouseOut={(e) => {
                            e.target.style.backgroundColor = '#10B981';
                            e.target.style.transform = 'scale(1)';
                          }}
                        >
                          Guardar
                        </button>
                        <button
                          onClick={() => {
                            setEditingDiaryId(null);
                            setEditingEmotionId("");
                          }}
                          style={{
                            backgroundColor: '#EF4444',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '0.5rem',
                            border: 'none',
                            cursor: 'pointer',
                            fontSize: '0.875rem',
                            transition: 'all 0.3s ease'
                          }}
                          onMouseOver={(e) => {
                            e.target.style.backgroundColor = '#DC2626';
                            e.target.style.transform = 'scale(1.05)';
                          }}
                          onMouseOut={(e) => {
                            e.target.style.backgroundColor = '#EF4444';
                            e.target.style.transform = 'scale(1)';
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
                          style={{
                            backgroundColor: '#3B82F6',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '0.5rem',
                            border: 'none',
                            cursor: 'pointer',
                            fontSize: '0.875rem',
                            transition: 'all 0.3s ease'
                          }}
                          onMouseOver={(e) => {
                            e.target.style.backgroundColor = '#2563EB';
                            e.target.style.transform = 'scale(1.05)';
                          }}
                          onMouseOut={(e) => {
                            e.target.style.backgroundColor = '#3B82F6';
                            e.target.style.transform = 'scale(1)';
                          }}
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => handleDelete(diary.id)}
                          style={{
                            backgroundColor: '#EF4444',
                            color: 'white',
                            padding: '0.5rem 1rem',
                            borderRadius: '0.5rem',
                            border: 'none',
                            cursor: 'pointer',
                            fontSize: '0.875rem',
                            transition: 'all 0.3s ease'
                          }}
                          onMouseOver={(e) => {
                            e.target.style.backgroundColor = '#DC2626';
                            e.target.style.transform = 'scale(1.05)';
                          }}
                          onMouseOut={(e) => {
                            e.target.style.backgroundColor = '#EF4444';
                            e.target.style.transform = 'scale(1)';
                          }}
                        >
                          Eliminar
                        </button>
                      </>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}