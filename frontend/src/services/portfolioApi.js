import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api"
});

export const getPortfolios = () => API.get("/portfolios");

export const getPortfolioById = (id) =>
  API.get(`/portfolios/${id}`);

export const getPortfolioSummary = (id) =>
  API.get(`/portfolios/${id}/summary`);

export const createPortfolio = (data) =>
  API.post("/portfolios", data);

export const deletePortfolio = (id) =>
  API.delete(`/portfolios/${id}`);
