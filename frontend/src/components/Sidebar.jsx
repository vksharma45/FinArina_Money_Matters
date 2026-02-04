import { Link } from "react-router-dom";

function Sidebar() {
  return (
    <div
      style={{
        width: "220px",
        background: "linear-gradient(180deg, #a1c4fd, #c2e9fb)", // Dark blue color for sidebar
//         borderRight: "1px solid #ddd",
        padding: "20px",
        color: "white",
//         height: "100vh", // Full height
//         position: "fixed", // Keeps sidebar fixed
      }}
    >
      <h3 style={{ color: "#ecf0f1", marginBottom: "20px" }}>Menu</h3>

      <p>
        <Link
          to="/"
          style={{
            display: "block",
            padding: "10px",
            backgroundColor: "#1e3a8a",
            color: "white",
            borderRadius: "8px",
            marginBottom: "10px",
            textDecoration: "none",
            fontWeight: "bold",
            textAlign: "center",
            transition: "background-color 0.3s",
          }}
        >
          Dashboard
        </Link>
      </p>
      <p>
        <Link
          to="/portfolios"
          style={{
            display: "block",
            padding: "10px",
            backgroundColor: "#1e3a8a",
            color: "white",
            borderRadius: "8px",
            marginBottom: "10px",
            textDecoration: "none",
            fontWeight: "bold",
            textAlign: "center",
            transition: "background-color 0.3s",
          }}
        >
          Portfolios
        </Link>
      </p>
      <p>
        <button
          style={{
            display: "block",
            padding: "10px",
            backgroundColor: "#1e3a8a",
            color: "white",
            borderRadius: "8px",
            marginBottom: "10px",
            width: "100%",
            fontWeight: "bold",
            border: "none",
            cursor: "pointer",
          }}
        >
          Groups
        </button>
      </p>
      <p>
        <button
          style={{
            display: "block",
            padding: "10px",
            backgroundColor: "#1e3a8a",
            color: "white",
            borderRadius: "8px",
            marginBottom: "10px",
            width: "100%",
            fontWeight: "bold",
            border: "none",
            cursor: "pointer",
          }}
        >
          Cards
        </button>
      </p>
    </div>
  );
}

export default Sidebar;
