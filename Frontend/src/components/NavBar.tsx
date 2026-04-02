import { NavLink } from "react-router-dom";

export default function NavBar() {
  return (
    <nav className="nav-bar">
      <NavLink to="/" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`} end>
        Events
      </NavLink>
      <NavLink to="/upload" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
        File Upload
      </NavLink>
      <NavLink to="/leaderboard" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
        Leaderboard
      </NavLink>
    </nav>
  );
}
