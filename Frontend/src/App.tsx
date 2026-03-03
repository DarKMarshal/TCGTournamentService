import { useCallback, useEffect, useState } from "react";
import { useStompClient } from "./hooks/useStompClient";
import EventPicker from "./components/EventPicker";
import ResultsTable from "./components/ResultsTable";
import FileUpload from "./components/FileUpload";
import type { EventDetails } from "./types/models";
import "./App.css";

function App() {
  const { connected, subscribe, publish } = useStompClient();
  const [eventDetails, setEventDetails] = useState<EventDetails | null>(null);

  // Subscribe to event details topic
  useEffect(() => {
    if (!connected) return;
    return subscribe<EventDetails>("/topic/event/details", setEventDetails);
  }, [connected, subscribe]);

  // When an event is selected, request its full details
  const handleEventSelect = useCallback(
    (eventId: string) => {
      setEventDetails(null); // clear while loading
      publish("/app/event/details", { eventId });
    },
    [publish],
  );

  return (
    <div className="app">
      <header className="app-header">
        <h1>TCG Tournament Service</h1>
        <span className="connection-badge" data-connected={connected}>
          {connected ? "● Connected" : "○ Disconnected"}
        </span>
      </header>

      <div className="toolbar">
        <EventPicker
          connected={connected}
          subscribe={subscribe}
          publish={publish}
          onSelect={handleEventSelect}
        />
        <FileUpload />
      </div>

      <main className="app-main">
        {eventDetails ? (
          <ResultsTable event={eventDetails} />
        ) : (
          <p className="empty-state">Select an event above to view results.</p>
        )}
      </main>
    </div>
  );
}

export default App;
