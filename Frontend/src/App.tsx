import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./components/HomePage";
import EventResultsPage from "./components/EventResultsPage";
import FileUpload from "./components/FileUpload";
import LeaderboardPage from "./components/LeaderboardPage";
import NavBar from "./components/NavBar";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <header className="app-header">
          <h1>Your Elite Four</h1>
        </header>

        <NavBar />

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/event/:eventId" element={<EventResultsPage />} />
          <Route path="/upload" element={<FileUpload />} />
          <Route path="/leaderboard" element={<LeaderboardPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
