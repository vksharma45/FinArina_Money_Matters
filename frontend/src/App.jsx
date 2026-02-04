import { Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import PortfolioList from "./pages/PortfolioList";
import PortfolioDetails from "./pages/PortfolioDetails";

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/portfolios" element={<PortfolioList />} />
        <Route path="/portfolio/:id" element={<PortfolioDetails />} />
      </Routes>
    </Layout>
  );
}

export default App;
