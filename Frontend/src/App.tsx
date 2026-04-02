import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "./components/HomePage";
import EventResultsPage from "./components/EventResultsPage";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <header className="app-header">
          <h1>Your Elite Four</h1>
        </header>

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/event/:eventId" element={<EventResultsPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
