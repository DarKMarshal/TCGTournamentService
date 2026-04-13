import { createContext, useContext, useState, useEffect, type ReactNode } from "react";
import type { AccountResponse } from "../types/models";

interface AuthContextType {
  account: AccountResponse | null;
  setAccount: (account: AccountResponse | null) => void;
  logout: () => void;
  isAdmin: boolean;
  isOrganizer: boolean;
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

  const setAccount = (acc: AccountResponse | null) => {
    setAccountState(acc);
  };

  const logout = () => {
    setAccountState(null);
  };

  const isAdmin = account?.role === "ADMIN";
  const isOrganizer = account?.role === "ORGANIZER" || isAdmin;

  return (
    <AuthContext.Provider value={{ account, setAccount, logout, isAdmin, isOrganizer }}>
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
