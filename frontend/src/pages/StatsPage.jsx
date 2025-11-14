import React, { useEffect, useState } from "react";
import { getDiaries } from "../services/api";
import { useNavigate } from "react-router-dom";
import AuthBackground from "../components/AuthBackground";
import {
  LineChart, Line, BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, PieChart, Pie, Cell
} from "recharts";

export default function StatsPage() {
  const [diaries, setDiaries] = useState([]);
  const [monthlyStats, setMonthlyStats] = useState({});
  const [correlation, setCorrelation] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");

  // Colores para las gráficas
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D', '#FF6B6B', '#4ECDC4'];
  const PASTEL_COLORS = ['#FFB6C1', '#87CEEB', '#98FB98', '#DDA0DD', '#FFD700', '#FFA07A', '#20B2AA', '#DEB887'];

  const emotionLabels = {
    1: "Triste",
    2: "Enojado",
    3: "Cansado",
    4: "Ansioso",
    5: "Sorprendido",
    6: "Calmado",
    7: "Motivado",
    8: "Feliz"
  };




  useEffect(() => {
    if (!userId) {
      navigate("/login");
      return;
    }

    const fetchData = async () => {
      try {
        const data = await getDiaries(userId);
        setDiaries(data || []);
        computeStats(data || []);
      } catch (error) {
        console.error("Error cargando diarios:", error);
      }
    };

    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const computeStats = (data) => {
    const parsed = (data || []).map((d) => ({
      ...d,
      date: new Date(d.entryDate),
      emotionValue: d.emotion ? d.emotion.id : 0,
    }));

    const monthly = {};
    parsed.forEach((d) => {
      const month = `${d.date.getFullYear()}-${String(d.date.getMonth() + 1).padStart(2, "0")}`;
      if (!monthly[month]) monthly[month] = [];
      monthly[month].push(d);
    });

    setMonthlyStats(monthly);

    const emotions = parsed.map((d) => d.emotionValue);
    const noteLengths = parsed.map((d) => d.notes?.length || 0);
    const corr = computeCorrelation(emotions, noteLengths);
    setCorrelation(corr);
  };

  const computeCorrelation = (arr1, arr2) => {
    if (!arr1 || !arr2 || arr1.length < 2) return null;
    const n = arr1.length;
    const mean1 = arr1.reduce((a, b) => a + b, 0) / n;
    const mean2 = arr2.reduce((a, b) => a + b, 0) / n;
    const num = arr1.map((x, i) => (x - mean1) * (arr2[i] - mean2)).reduce((a, b) => a + b, 0);
    const den = Math.sqrt(
      arr1.map((x) => (x - mean1) ** 2).reduce((a, b) => a + b, 0) *
      arr2.map((x) => (x - mean2) ** 2).reduce((a, b) => a + b, 0)
    );
    return den === 0 ? null : num / den;
  };

  const emotionCounts = diaries.reduce((acc, diary) => {
    const emotionName = emotionLabels[diary.emotion?.id] || "Desconocido";
    acc[emotionName] = (acc[emotionName] || 0) + 1;
    return acc;
  }, {});

  const barData = Object.entries(emotionCounts).map(([emotion, count]) => ({
    emotion,
    count
  }));

  const pieData = Object.entries(emotionCounts).map(([emotion, count], index) => ({
    name: emotion,
    value: count,
    color: PASTEL_COLORS[index % PASTEL_COLORS.length]
  }));

  const monthlyChartData = Object.entries(monthlyStats)
    .map(([month, items]) => ({
      month: month.split('-')[1] + '/' + month.split('-')[0], // Formato MM/YYYY
      count: items.length,
      fullMonth: month
    }))
    .sort((a, b) => new Date(a.fullMonth + '-01') - new Date(b.fullMonth + '-01'));

  const sortedDiaries = [...diaries].sort(
    (a, b) => new Date(a.entryDate) - new Date(b.entryDate)
  );

  const summaryData = [
    { title: "Total Entradas", value: diaries.length, color: "bg-blue-500"}
  ];

  return (
    <AuthBackground>
      <div style={{ width: "100%", maxWidth: "1400px", margin: "0 auto", padding: "2rem" }}>
        {/* Header */}
        <div
          style={{
            backgroundColor: "#FFFFFF",
            width: "92%",
            maxWidth: "1200px",
            padding: "2rem",
            borderRadius: "1rem",
            boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.25)",
            margin: "2rem "
          }}
          className="flex flex-col"
        >
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold text-gray-800">Estadísticas</h1>
            <button
              onClick={() => navigate("/home")}
              style={{
                backgroundColor: "#000000",
                color: "#ffffff",
                padding: "0.75rem 1.5rem",
                borderRadius: "1rem",
                fontWeight: 600,
                border: "none",
                cursor: "pointer",
                transition: "all 0.3s"
              }}
              onMouseOver={(e) => { e.currentTarget.style.backgroundColor = "#111827";;
                  e.target.style.transform= 'scale(1.05)';}}
              onMouseOut={(e) => { e.currentTarget.style.backgroundColor = "#000000";}}
            >
              Volver al Inicio
            </button>
          </div>

          {/* Tarjetas de Resumen */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
            {summaryData.map((item, index) => (
              <div key={index}
                   style={{
                     backgroundColor: "#F8FAFC",
                     borderRadius: "1rem",
                     padding: "1.5rem",
                     border: `2px solid rgba(59,130,246,0.12)`,
                     textAlign: "center"
                   }}
              >
                <div className="text-center mb-4">
                  <h3 className="text-lg font-semibold text-gray-600">{item.title}</h3>
                  <p className="text-2xl font-bold text-gray-800 mt-2">{item.value}</p>
                </div>

                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div
                    style={{ width: item.percentage }}
                    className="h-2 rounded-full bg-blue-500"
                  />
                </div>
              </div>
            ))}
          </div>

          {/* Sección de Gráficas */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>

              {/* Gráfica de Barras - Emociones */}
              <div>
                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#1F2937', marginBottom: '1rem' }}>
                  Distribución de Emociones
                </h2>
                <div style={{ display: 'flex', justifyContent: 'center', backgroundColor: '#F8FAFC', padding: '1rem', borderRadius: '1rem' }}>
                  <BarChart width={600} height={300} data={barData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="emotion" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="count" fill="#8884d8" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </div>
              </div>

                <div style={{ height: '1px', backgroundColor: '#E5E7EB', margin: '2rem 0' }}></div>

              {/* Grafica Circular - Emociones */}
              <div>
                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#1F2937', marginBottom: '1rem' }}>
                  Porcentaje de Emociones
                </h2>
                <div style={{ display: 'flex', justifyContent: 'center', backgroundColor: '#F8FAFC', padding: '1rem', borderRadius: '1rem' }}>
                  <PieChart width={400} height={300}>
                    <Pie
                      data={pieData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} (${(percent * 100).toFixed(0)}%)`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </div>
              </div>

               <div style={{ height: '1px', backgroundColor: '#E5E7EB', margin: '2rem 0' }}></div>

              {/* Gráfica de Barras - Mensual */}
              <div>
                <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#1F2937', marginBottom: '1rem' }}>
                  Entradas por Mes
                </h2>
                <div style={{ display: 'flex', justifyContent: 'center', backgroundColor: '#F8FAFC', padding: '1rem', borderRadius: '1rem' }}>
                  <BarChart width={600} height={300} data={monthlyChartData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="count" fill="#00C49F" radius={[4, 4, 0, 0]} name="Entradas" />
                  </BarChart>
                </div>
              </div>

            </div>

          {/* Lista de meses */}
          <div style={{ margin: '2rem 2rem 3rem 2rem'}}>
            <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: '#1F2937', marginBottom: '1rem' }}>
                  Resumen por Mes
            </h2>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.75rem' }}>
                  {Object.entries(monthlyStats).map(([month, items]) => (
                    <div
                      key={month}
                      style={{
                        backgroundColor: '#F8FAFC',
                        borderRadius: '0.75rem',
                        padding: '1rem',
                        border: '1px solid #E5E7EB',
                        textAlign: 'center'
                      }}
                    >
                      <div style={{ fontSize: '0.875rem', fontWeight: '600', color: '#6B7280' }}>
                        Mes {month}
                      </div>
                      <div style={{ fontSize: '1.25rem', fontWeight: 'bold', color: '#3B82F6' }}>
                        {items.length} entradas
                      </div>
                    </div>
                  ))}
                </div>
          </div>
        </div>
      </div>
    </AuthBackground>
  );
}
