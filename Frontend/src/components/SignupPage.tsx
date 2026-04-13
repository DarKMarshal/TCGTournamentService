import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { AccountResponse } from "../types/models";

export default function SignupPage() {
  const [username, setUsername] = useState("");
  const [playerId, setPlayerId] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const { setAccount } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");

    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    const id = parseInt(playerId, 10);
    if (isNaN(id) || id < 0 || playerId.length > 7) {
      setError("Player ID must be at most 7 digits");
      return;
    }

    try {
      const res = await fetch("/api/accounts/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username,
          playerId: parseInt(playerId, 10),
          dateOfBirth,
          password,
        }),
      });

      if (!res.ok) {
        const body = await res.json();
        setError(body.error || "Signup failed");
        return;
      }

      const account: AccountResponse = await res.json();
      setAccount(account);
      navigate("/");
    } catch {
      setError("Network error. Please try again.");
    }
  };

  return (
    <div className="auth-page">
      <h2>Sign Up</h2>
      <form className="auth-form" onSubmit={handleSubmit}>
        <label>
          Username
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <label>
          Player ID
          <input
            type="number"
            value={playerId}
            onChange={(e) => setPlayerId(e.target.value)}
            max={9999999}
            required
          />
        </label>
        <label>
          Date of Birth
          <input
            type="date"
            value={dateOfBirth}
            onChange={(e) => setDateOfBirth(e.target.value)}
            required
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <label>
          Confirm Password
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </label>
        {error && <p className="auth-error">{error}</p>}
        <button type="submit">Sign Up</button>
      </form>
    </div>
  );
}
