import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import type { AccountResponse } from "../types/models";
import { authFetch } from "../utils/authFetch";

export default function AdminPanel() {
  const { isAdmin } = useAuth();
  const [accounts, setAccounts] = useState<AccountResponse[]>([]);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!isAdmin) return;
    fetchAccounts();
  }, [isAdmin]);

  const fetchAccounts = async () => {
    try {
      const res = await authFetch("/api/accounts");
      if (res.ok) {
        setAccounts(await res.json());
      }
    } catch {
      setError("Failed to load accounts");
    }
  };

  const handleRoleChange = async (accountId: number, newRole: string) => {
    setError("");
    setSuccess("");
    try {
      const res = await authFetch(`/api/accounts/${accountId}/role`, {
        method: "PUT",
        body: JSON.stringify({ accountId, role: newRole }),
      });

      if (!res.ok) {
        const body = await res.json();
        setError(body.error || "Failed to update role");
        return;
      }

      setSuccess(`Role updated successfully`);
      fetchAccounts();
    } catch {
      setError("Network error. Please try again.");
    }
  };

  if (!isAdmin) {
    return (
      <div className="auth-page">
        <h2>Admin Panel</h2>
        <p className="auth-error">Access denied. Admin privileges required.</p>
      </div>
    );
  }

  return (
    <div className="admin-panel">
      <h2>Admin Panel</h2>
      {error && <p className="auth-error">{error}</p>}
      {success && <p className="auth-success">{success}</p>}
      <table className="results-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Player ID</th>
            <th>Date of Birth</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {accounts.map((acc) => (
            <tr key={acc.id}>
              <td>{acc.id}</td>
              <td>{acc.username}</td>
              <td>{acc.playerId}</td>
              <td>{acc.dateOfBirth}</td>
              <td>{acc.role}</td>
              <td>
                <select
                  value={acc.role}
                  onChange={(e) => handleRoleChange(acc.id, e.target.value)}
                >
                  <option value="PLAYER">Player</option>
                  <option value="ORGANIZER">Organizer</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {accounts.length === 0 && <p className="empty-state">No accounts found.</p>}
    </div>
  );
}
