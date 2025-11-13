import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import HomePage from "./pages/HomePage";

function App() {
  const isAuthenticated = !!localStorage.getItem("userId");

  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            isAuthenticated ? <Navigate to="/home" replace /> : <Navigate to="/login" replace />
          }
        />
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/home" replace /> : <LoginPage />}
        />
        <Route
          path="/register"
          element={isAuthenticated ? <Navigate to="/home" replace /> : <RegisterPage />}
        />
        <Route
          path="/home"
          element={isAuthenticated ? <HomePage /> : <Navigate to="/login" replace />}
        />
      </Routes>
    </Router>
  );
}

export default App;
