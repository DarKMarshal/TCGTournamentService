import { useEffect, useState } from "react";
import type { EventSummary } from "../types/models";

interface Props {
  connected: boolean;
  subscribe: <T>(dest: string, cb: (body: T) => void) => () => void;
  publish: (dest: string, body?: Record<string, unknown>) => void;
  onSelect: (eventId: string) => void;
}

export default function EventPicker({ connected, subscribe, publish, onSelect }: Props) {
  const [events, setEvents] = useState<EventSummary[]>([]);
  const [selected, setSelected] = useState("");

  // Subscribe to event list and request it once connected
  useEffect(() => {
    if (!connected) return;

    const unsub = subscribe<EventSummary[]>("/topic/events", setEvents);
    publish("/app/events");

    return unsub;
  }, [connected, subscribe, publish]);

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const id = e.target.value;
    setSelected(id);
    if (id) onSelect(id);
  };

  return (
    <div className="event-picker">
      <label htmlFor="event-select">Select Event</label>
      <select
        id="event-select"
        value={selected}
        onChange={handleChange}
        disabled={events.length === 0}
      >
        <option value="">
          {events.length === 0 ? "No events available" : "-- Choose an event --"}
        </option>
        {events.map((ev) => (
          <option key={ev.id} value={ev.id}>
            {ev.name}
          </option>
        ))}
      </select>
    </div>
  );
}
