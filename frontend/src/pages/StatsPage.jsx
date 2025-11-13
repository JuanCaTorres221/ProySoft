import React, { useEffect, useState } from "react";
import { getDiaries } from "../services/api";
import { useNavigate } from "react-router-dom";
import {
  LineChart, Line, BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend
} from "recharts";

import "../App.css";

export default function StatsPage() {
  const [diaries, setDiaries] = useState([]);
  const [monthlyStats, setMonthlyStats] = useState({});
  const [correlation, setCorrelation] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");




  const monthlyChartData = Object.entries(monthlyStats)
    .map(([month, items]) => ({
      month,
      count: items.length,
      dateObj: new Date(month + "-01")
    }))
    .sort((a, b) => a.dateObj - b.dateObj);



  useEffect(() => {
    if (!userId) {
      navigate("/login");
      return;
    }

    const fetchData = async () => {
      try {
        const data = await getDiaries(userId);
        setDiaries(data);
        computeStats(data);
      } catch (error) {
        console.error("Error cargando diarios:", error);
      }
    };

    fetchData();
  }, [userId]);

  const computeStats = (data) => {
    const parsed = data.map((d) => ({
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



    // --- Datos para gráficas mensuales ---
    const monthlyChartData = Object.entries(monthlyStats).map(([month, items]) => ({
      month,
      count: items.length
    }));


    const emotions = parsed.map((d) => d.emotionValue);
    const noteLengths = parsed.map((d) => d.notes?.length || 0);
    const corr = computeCorrelation(emotions, noteLengths);
    setCorrelation(corr);
  };

  const computeCorrelation = (arr1, arr2) => {
    if (arr1.length < 2) return null;
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


  const emotionCounts = diaries.reduce((acc, diary) => {
    const emotionName = emotionLabels[diary.emotion?.id] || "Desconocido";
    acc[emotionName] = (acc[emotionName] || 0) + 1;
    return acc;
  }, {});

  const barData = Object.entries(emotionCounts).map(([emotion, count]) => ({
    emotion,
    count
  }));

    // --- ORDENAR DIARIOS POR FECHA PARA EL LINE CHART ---
    const sortedDiaries = [...diaries].sort(
    (a, b) => new Date(a.entryDate) - new Date(b.entryDate)
    );


  return (
    <div className="container">
      <header className="header">
        <h1>Estadísticas de tus diarios</h1>
        <button onClick={() => navigate("/home")}>Volver</button>
      </header>

      <section>
        <h2>📊 Entradas por emoción</h2>
        <BarChart width={600} height={350} data={barData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="emotion" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="count" fill="#8884d8" />
        </BarChart>
      </section>



      <section>

        <section>
          <h2>📊 Gráfica Mensual</h2>
          <BarChart width={600} height={300} data={monthlyChartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="month" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="count" fill="#8884d8" name="Entradas por mes" />
          </BarChart>
        </section>



        <ul>
          {Object.entries(monthlyStats).map(([month, items]) => (
            <li key={month}>
              Mes {month}: {items.length} entradas
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h2>📈 Tendencia de emociones</h2>
        <LineChart width={600} height={300} data={sortedDiaries}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="entryDate" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line
            type="monotone"
            dataKey="emotion.id"
            stroke="#8884d8"
            name="Emoción (ID)"
          />
        </LineChart>
      </section>

    </div>
  );
}