import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function PortfolioList() {
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    axios.get("http://localhost:8080/api/portfolios")
      .then((res) => {
        setPortfolios(res.data.data || []);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error:", err);
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Loading portfolios...</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h2>My Portfolios</h2>

      {portfolios.length === 0 ? (
        <p>No portfolios found</p>
      ) : (
        portfolios.map((p) => (
          <div
            key={p.portfolioId}
            onClick={() => navigate(`/portfolio/${p.portfolioId}`)}  // IMPORTANT
            style={{
              background: "white",
              padding: "20px",
              marginBottom: "15px",
              borderRadius: "8px",
              cursor: "pointer",
              boxShadow: "0 2px 6px rgba(0,0,0,0.08)"
            }}
          >
            <h3>{p.portfolioName}</h3>
            <p>Created: {p.createdDate}</p>
            <p>Initial: ₹{p.initialInvestment}</p>
          </div>
        ))
      )}
    </div>
  );
}

export default PortfolioList;
