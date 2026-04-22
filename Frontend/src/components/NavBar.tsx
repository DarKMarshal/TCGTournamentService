import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function NavBar() {
  const { account, logout, isAdmin, isOrganizer } = useAuth();

  return (
    <nav className="nav-bar">
      <NavLink to="/" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`} end>
        Events
      </NavLink>
      <NavLink to="/leaderboard" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
        Leaderboard
      </NavLink>

      <div className="nav-spacer" />

      {account ? (
        <>
          {isAdmin && (
            <NavLink to="/admin" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
              Admin
            </NavLink>
          )}
          {isOrganizer && (
            <NavLink to="/upload" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
              File Upload
            </NavLink>
          )}
            <NavLink to="/personal" className="nav-link nav-user">
                {account.username} <span className="nav-role">({account.role})</span>
            </NavLink>
          <button className="nav-link nav-logout" onClick={logout}>
            Log Out
          </button>
        </>
      ) : (
        <>
          <NavLink to="/login" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Log In
          </NavLink>
          <NavLink to="/signup" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Sign Up
          </NavLink>
        </>
      )}
    </nav>
  );
}
