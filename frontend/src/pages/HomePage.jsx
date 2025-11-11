export default function HomePage() {
  return (
    <div className="h-screen flex flex-col items-center justify-center bg-gray-50">
      <h1 className="text-3xl font-bold text-gray-800 mb-4">Bienvenido al Diario de Emociones 😊</h1>
      <button className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition">
        Cerrar sesión
      </button>
    </div>
  );
}
