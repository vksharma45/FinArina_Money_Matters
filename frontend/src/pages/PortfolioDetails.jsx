import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  getPortfolioById,
  getPortfolioSummary,
  deletePortfolio
} from "../services/portfolioApi";

function PortfolioDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [portfolio, setPortfolio] = useState(null);
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    getPortfolioById(id).then((res) =>
      setPortfolio(res.data.data)
    );

    getPortfolioSummary(id).then((res) =>
      setSummary(res.data.data)
    );
  }, [id]);

  if (!portfolio) return <p>Loading...</p>;

  const handleDelete = () => {
    deletePortfolio(id).then(() => navigate("/portfolios"));
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>{portfolio.portfolioName}</h2>
      <p>Created: {portfolio.createdDate}</p>
      <p>Initial Investment: ₹{portfolio.initialInvestment}</p>

      <hr />

      {summary && (
        <>
          <h3>Summary</h3>
          <div style={{ display: "flex", gap: "20px" }}>
            <Card label="Total Invested" value={summary.totalInvestedAmount} />
            <Card label="Current Value" value={summary.currentPortfolioValue} />
            <Card label="Return" value={summary.absoluteReturn} />
            <Card label="Return %" value={summary.percentageReturn + "%"} />
          </div>
        </>
      )}

      <button
        style={{ marginTop: "20px", background: "red", color: "white" }}
        onClick={handleDelete}
      >
        Delete Portfolio
      </button>
    </div>
  );
}

function Card({ label, value }) {
  return (
    <div
      style={{
        background: "white",
        padding: "20px",
        borderRadius: "8px",
        boxShadow: "0 2px 6px rgba(0,0,0,0.08)"
      }}
    >
      <h4>{label}</h4>
      <p>₹{value}</p>
    </div>
  );
}

export default PortfolioDetails;
