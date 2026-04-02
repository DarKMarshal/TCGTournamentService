import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { EventSummary } from "../types/models";

const API_BASE = "http://localhost:8080";

export default function HomePage() {
  const [events, setEvents] = useState<EventSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetch(`${API_BASE}/api/events`)
      .then((res) => res.json())
      .then((data: EventSummary[]) => {
        setEvents(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Failed to fetch events:", err);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <p className="empty-state">Loading events…</p>;
  }

  if (events.length === 0) {
    return <p className="empty-state">No events available.</p>;
  }

  return (
    <div className="events-grid">
      {events.map((ev) => (
        <div
          key={ev.id}
          className="event-card"
          onClick={() => navigate(`/event/${ev.id}`)}
        >
          <h3 className="event-card-name">{ev.name}</h3>
          {Object.keys(ev.winners).length > 0 ? (
            Object.entries(ev.winners).map(([division, winner]) => (
              <p key={division} className="event-card-winner">
                {division}: {winner}
              </p>
            ))
          ) : (
            <p className="event-card-winner">No results yet</p>
          )}
        </div>
      ))}
    </div>
  );
}
