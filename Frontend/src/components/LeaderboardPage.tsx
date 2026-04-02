import { useEffect, useState } from "react";
import type { LeaderboardDTO } from "../types/models";

const API_BASE = "http://localhost:8080";

const DIVISION_ORDER = ["Master", "Senior", "Junior"];

export default function LeaderboardPage() {
  const [leaderboards, setLeaderboards] = useState<LeaderboardDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState(0);

  useEffect(() => {
    fetch(`${API_BASE}/api/leaderboard`)
      .then((res) => res.json())
      .then((data: LeaderboardDTO[]) => {
        setLeaderboards(data);
        setActiveTab(0);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to fetch leaderboard:", err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <p className="empty-state">Loading leaderboard…</p>;
  }

  if (leaderboards.length === 0) {
    return <p className="empty-state">No leaderboard data available.</p>;
  }

  const sorted = [...leaderboards].sort((a, b) => {
    const aIdx = DIVISION_ORDER.indexOf(a.ageDivision);
    const bIdx = DIVISION_ORDER.indexOf(b.ageDivision);
    return (aIdx === -1 ? 999 : aIdx) - (bIdx === -1 ? 999 : bIdx);
  });

  const current = sorted[activeTab];
  const players = [...current.players].sort((a, b) => b.championshipPoints - a.championshipPoints);

  return (
    <div className="leaderboard-container">
      <h2>Championship Leaderboard</h2>

      <div className="division-tabs">
        {sorted.map((board, idx) => (
          <button
            key={board.ageDivision}
            className={`division-tab ${idx === activeTab ? "active" : ""}`}
            onClick={() => setActiveTab(idx)}
          >
            {board.ageDivision}
          </button>
        ))}
      </div>

      <div className="division-section">
        {players.length === 0 ? (
          <p className="empty-state">No players in this division.</p>
        ) : (
          <table className="results-table">
            <thead>
              <tr>
                <th className="cell-center">Rank</th>
                <th>Player</th>
                <th className="cell-center">Championship Points</th>
              </tr>
            </thead>
            <tbody>
              {players.map((player, index) => (
                <tr key={player.id}>
                  <td className="cell-center">{index + 1}</td>
                  <td>{player.name}</td>
                  <td className="cell-center">{player.championshipPoints}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
