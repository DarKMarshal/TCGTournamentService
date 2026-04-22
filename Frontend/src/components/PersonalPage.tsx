import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { PersonalDataDTO } from "../types/models";
import { authFetch } from "../utils/authFetch";

export default function PersonalPage() {
  const { account, isOrganizer, isAdmin } = useAuth();
  const [data, setData] = useState<PersonalDataDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!account) {
      setLoading(false);
      return;
    }

    authFetch(`/api/personal/${account.playerId}`)
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Server responded with status ${res.status}`);
        }
        return res.json();
      })
      .then((d: PersonalDataDTO) => {
        setData(d);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to fetch personal data:", err);
        setLoading(false);
      });
  }, [account]);

  if (!account) {
    return <p className="empty-state">Please log in to view your personal page.</p>;
  }

  if (loading) {
    return <p className="empty-state">Loading…</p>;
  }

  const playerFound = data?.player != null;

  return (
    <div className="personal-container">
      <h2>Personal</h2>

      {/* Player info */}
      <div className="personal-info">
        <p><strong>Username:</strong> {account.username}</p>
        <p><strong>Player ID:</strong> {account.playerId}</p>
        {playerFound && (
          <p><strong>Championship Points:</strong> {data!.player!.championshipPoints}</p>
        )}
      </div>

      {/* Results list */}
      <div className="personal-section">
        <h3>Results</h3>
        {playerFound && data!.results.length > 0 ? (
          <table className="results-table">
            <thead>
              <tr>
                <th>Event</th>
                <th>Division</th>
                <th className="cell-center">Placement</th>
                <th className="cell-center">Points Earned</th>
              </tr>
            </thead>
            <tbody>
              {data!.results.map((r, idx) => (
                <tr key={`${r.eventId}-${r.ageDivision}-${idx}`}>
                  <td>
                    <Link to={`/event/${r.eventId}`} className="personal-event-link">
                      {r.eventName}
                    </Link>
                  </td>
                  <td>{r.ageDivision}</td>
                  <td className="cell-center">{r.placement}</td>
                  <td className="cell-center">{r.pointsEarned}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p className="empty-state">No Results Found!</p>
        )}
      </div>

      {/* Organizer section: uploaded events */}
      {isOrganizer && (
        <div className="personal-section">
          <h3>Your Uploaded Events</h3>
          {data && data.uploadedEvents.length > 0 ? (
            <ul className="personal-event-list">
              {data.uploadedEvents.map((ev) => (
                <li key={ev.id}>
                  <Link to={`/event/${ev.id}`} className="personal-event-link">
                    {ev.name}
                  </Link>
                </li>
              ))}
            </ul>
          ) : (
            <p className="empty-state">No uploaded events.</p>
          )}
          <Link to="/upload" className="personal-upload-btn">
            File Upload
          </Link>
        </div>
      )}

      {/* Admin section */}
      {isAdmin && (
        <div className="personal-section">
          <h3>Administration</h3>
          <Link to="/admin" className="personal-admin-link">
            Admin Panel
          </Link>
        </div>
      )}
    </div>
  );
}
