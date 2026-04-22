import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from "react";
import type { AccountResponse } from "../types/models";
import { authFetch } from "../utils/authFetch";

interface AuthContextType {
  account: AccountResponse | null;
  setAccount: (account: AccountResponse | null) => void;
  logout: () => void;
  isAdmin: boolean;
  isOrganizer: boolean;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [account, setAccountState] = useState<AccountResponse | null>(() => {
    const stored = localStorage.getItem("account");
    return stored ? JSON.parse(stored) : null;
  });

  useEffect(() => {
    if (account) {
      localStorage.setItem("account", JSON.stringify(account));
    } else {
      localStorage.removeItem("account");
    }
  }, [account]);

  // Validate stored session against backend on initial load
  useEffect(() => {
    if (!account) return;
    authFetch(`/api/accounts/${account.id}`)
      .then((res) => {
        if (res.status === 401) {
          // Token expired or invalid – clear local session
          setAccountState(null);
          localStorage.removeItem("token");
          return null;
        }
        if (!res.ok) {
          return null;
        }
        return res.json();
      })
      .then((data: AccountResponse | null) => {
        if (data && data.role !== account.role) {
          // Role changed on backend – update local state
          setAccountState(data);
        }
      })
      .catch(() => {
        // Network error – keep existing session to allow offline-like usage
      });
    // Only run on mount
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const setAccount = useCallback((acc: AccountResponse | null) => {
    if (acc?.token) {
      localStorage.setItem("token", acc.token);
    }
    setAccountState(acc);
  }, []);

  const logout = useCallback(() => {
    setAccountState(null);
    localStorage.removeItem("token");
  }, []);

  const isAdmin = account?.role === "ADMIN";
  const isOrganizer = account?.role === "ORGANIZER" || isAdmin;
  const isAuthenticated = account !== null;

  return (
    <AuthContext.Provider value={{ account, setAccount, logout, isAdmin, isOrganizer, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
