import { useEffect, useRef, useCallback, useState } from "react";
import { Client } from "@stomp/stompjs";

/**
 * Build an absolute WebSocket URL that works both through the
 * Vite dev-proxy and in production.  Spring's SockJS endpoint
 * also exposes a raw WebSocket at  …/websocket.
 */
function buildBrokerURL(): string {
  const proto = window.location.protocol === "https:" ? "wss:" : "ws:";
  return `${proto}//${window.location.host}/ws/websocket`;
}

/**
 * Hook that manages a STOMP-over-WebSocket connection.
 *
 * Returns:
 *  - connected: whether the client is currently connected
 *  - subscribe(destination, callback): subscribe to a topic, returns unsubscribe fn
 *  - publish(destination, body?): send a message to the server
 */
export function useStompClient() {
  const clientRef = useRef<Client | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const client = new Client({
      brokerURL: buildBrokerURL(),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("STOMP connected");
        setConnected(true);
      },
      onDisconnect: () => {
        console.log("STOMP disconnected");
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error("STOMP error:", frame.headers["message"], frame.body);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

  const subscribe = useCallback(
    <T,>(destination: string, callback: (body: T) => void) => {
      const client = clientRef.current;
      if (!client?.connected) return () => {};

      const sub = client.subscribe(destination, (message) => {
        callback(JSON.parse(message.body) as T);
      });

      return () => sub.unsubscribe();
    },
    [connected], // eslint-disable-line react-hooks/exhaustive-deps
  );

  const publish = useCallback(
    (destination: string, body: Record<string, unknown> = {}) => {
      const client = clientRef.current;
      if (!client?.connected) {
        console.warn("STOMP not connected — cannot publish to", destination);
        return;
      }
      client.publish({ destination, body: JSON.stringify(body) });
    },
    [],
  );

  return { connected, subscribe, publish };
}
