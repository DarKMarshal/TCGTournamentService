import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import HomePage from "./components/HomePage";
import EventResultsPage from "./components/EventResultsPage";
import FileUpload from "./components/FileUpload";
import LeaderboardPage from "./components/LeaderboardPage";
import SignupPage from "./components/SignupPage";
import LoginPage from "./components/LoginPage";
import AdminPanel from "./components/AdminPanel";
import PersonalPage from "./components/PersonalPage";
import NavBar from "./components/NavBar";
import "./App.css";

function RequireAuth({ children }: { children: React.ReactNode }) {
  const { account } = useAuth();
  if (!account) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function RequireOrganizer({ children }: { children: React.ReactNode }) {
  const { account, isOrganizer } = useAuth();
  if (!account) return <Navigate to="/login" replace />;
  if (!isOrganizer) return <Navigate to="/" replace />;
  return <>{children}</>;
}

function RequireAdmin({ children }: { children: React.ReactNode }) {
  const { account, isAdmin } = useAuth();
  if (!account) return <Navigate to="/login" replace />;
  if (!isAdmin) return <Navigate to="/" replace />;
  return <>{children}</>;
}

function RedirectIfAuthenticated({ children }: { children: React.ReactNode }) {
  const { account } = useAuth();
  if (account) return <Navigate to="/" replace />;
  return <>{children}</>;
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="app">
          <header className="app-header">
            <h1>Your Elite Four</h1>
          </header>
          <NavBar />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/event/:eventId" element={<EventResultsPage />} />
            <Route path="/upload" element={<RequireOrganizer><FileUpload /></RequireOrganizer>} />
            <Route path="/leaderboard" element={<LeaderboardPage />} />
            <Route path="/signup" element={<RedirectIfAuthenticated><SignupPage /></RedirectIfAuthenticated>} />
            <Route path="/login" element={<RedirectIfAuthenticated><LoginPage /></RedirectIfAuthenticated>} />
            <Route path="/personal" element={<RequireAuth><PersonalPage /></RequireAuth>} />
            <Route path="/admin" element={<RequireAdmin><AdminPanel /></RequireAdmin>} />
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
