export default function AuthBackground({ children }) {
  return (
    <div
      className="flex items-center justify-center min-h-screen bg-[#FFE675] relative overflow-hidden"
      style={{
        width: '100%',
        minHeight: '100vh',
        margin: 0,
        padding: 0
      }}
    >
      {/* Emoji gigante del fondo */}
      <div className="absolute -bottom-40 -right-40 opacity-15">
        <span className="text-[50rem] leading-none select-none">😄</span>
      </div>

      {/* Contenido */}
      <div className="relative z-10 w-full flex justify-center items-center min-h-screen">
        {children}
      </div>
    </div>
  );
}