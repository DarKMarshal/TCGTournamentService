import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import type { EventDetails, Division } from "../types/models";

const API_BASE = "http://localhost:8080";

export default function EventResultsPage() {
  const { eventId } = useParams<{ eventId: string }>();
  const [details, setDetails] = useState<EventDetails | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState(0);

  useEffect(() => {
    if (!eventId) return;
    fetch(`${API_BASE}/api/events/${eventId}`)
      .then((res) => res.json())
      .then((data: EventDetails) => {
        setDetails(data);
        setActiveTab(0);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to fetch event details:", err);
        setLoading(false);
      });
  }, [eventId]);

  if (loading) {
    return <p className="empty-state">Loading event…</p>;
  }

  if (!details) {
    return <p className="empty-state">Event not found.</p>;
  }

  const DIVISION_ORDER = ["MASTERS", "SENIORS", "JUNIORS"];
  const divisions: Division[] = [...details.divisions].sort((a, b) => {
    const aIdx = DIVISION_ORDER.indexOf(a.ageDivision.toUpperCase());
    const bIdx = DIVISION_ORDER.indexOf(b.ageDivision.toUpperCase());
    return (aIdx === -1 ? 999 : aIdx) - (bIdx === -1 ? 999 : bIdx);
  });

  if (divisions.length === 0) {
    return (
      <div>
        <Link to="/" className="back-link">← Back to Events</Link>
        <h2>{details.name}</h2>
        <p className="empty-state">No results available for this event.</p>
      </div>
    );
  }

  const current = divisions[activeTab];

  return (
    <div>
      <Link to="/" className="back-link">← Back to Events</Link>
      <h2>{details.name}</h2>

      <div className="division-tabs">
        {divisions.map((div, idx) => (
          <button
            key={div.ageDivision}
            className={`division-tab ${idx === activeTab ? "active" : ""}`}
            onClick={() => setActiveTab(idx)}
          >
            {div.ageDivision}
          </button>
        ))}
      </div>

      <div className="division-section">
        <p className="tournament-type">Format: {current.tournamentType}</p>

        {current.results.length === 0 ? (
          <p className="empty-state">No results for this division.</p>
        ) : (
          <table className="results-table">
            <thead>
              <tr>
                <th className="cell-center">#</th>
                <th>Player</th>
                <th className="cell-center">Match Pts</th>
                <th className="cell-center">Opp Win %</th>
                <th className="cell-center">Opp Opp Win %</th>
                <th className="cell-center">CP Earned</th>
              </tr>
            </thead>
            <tbody>
              {current.results.map((r, i) => (
                <tr key={i}>
                  <td className="cell-center">{r.placement}</td>
                  <td>{r.player.name}</td>
                  <td className="cell-center">{r.matchPoints}</td>
                  <td className="cell-center">
                    {(r.opponentWinPercentage * 100).toFixed(1)}%
                  </td>
                  <td className="cell-center">
                    {(r.opponentOpponentWinPercentage * 100).toFixed(1)}%
                  </td>
                  <td className="cell-center">{r.championshipPointsEarned}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
