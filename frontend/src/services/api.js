import axios from 'axios'

const API_BASE_URL = "http://localhost:8080/api"

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || error.message || 'An error occurred'
    return Promise.reject(new Error(message))
  }
)

// Portfolio APIs
export const portfolioAPI = {
  getAll: () => api.get('/portfolios'),
  getById: (id) => api.get(`/portfolios/${id}`),
  getSummary: (id) => api.get(`/portfolios/${id}/summary`),
  create: (data) => api.post('/portfolios', data),
  delete: (id) => api.delete(`/portfolios/${id}`),
}

// Asset APIs
export const assetAPI = {
  getAll: (portfolioId) => api.get(`/portfolios/${portfolioId}/assets`),
  getWishlist: (portfolioId) => api.get(`/portfolios/${portfolioId}/wishlist`),
  getById: (id) => api.get(`/assets/${id}`),
  getPerformance: (id) => api.get(`/assets/${id}/performance`),
  getHistory: (id) => api.get(`/assets/${id}/history`),
  create: (portfolioId, data) => api.post(`/portfolios/${portfolioId}/assets`, data),
  update: (id, data) => api.put(`/assets/${id}`, data),
  buy: (id, data) => api.post(`/assets/${id}/buy`, data),
  delete: (id) => api.delete(`/assets/${id}`),
  getGroups: (id) => api.get(`/assets/${id}/groups`),
  addGroups: (id, data) => api.post(`/assets/${id}/groups`, data),
  replaceGroups: (id, data) => api.put(`/assets/${id}/groups`, data),
  removeFromGroup: (assetId, groupId) => api.delete(`/assets/${assetId}/groups/${groupId}`),
}

// Asset Group APIs
export const assetGroupAPI = {
  getAll: () => api.get('/asset-groups'),
  getById: (id) => api.get(`/asset-groups/${id}`),
  getPerformance: (id, portfolioId) => api.get(`/asset-groups/${id}/performance?portfolioId=${portfolioId}`),
  getAllPerformance: (portfolioId) => api.get(`/portfolios/${portfolioId}/asset-groups/performance`),
  create: (data) => api.post('/asset-groups', data),
  update: (id, data) => api.put(`/asset-groups/${id}`, data),
  delete: (id) => api.delete(`/asset-groups/${id}`),
}

// Stock Category APIs
export const stockCategoryAPI = {
  getAll: () => api.get('/stock-categories'),
  getById: (id) => api.get(`/stock-categories/${id}`),
  getPerformance: (portfolioId) => api.get(`/stock-categories/performance/portfolio/${portfolioId}`),
  getPerformanceById: (categoryId, portfolioId) => 
    api.get(`/stock-categories/${categoryId}/performance?portfolioId=${portfolioId}`),
  create: (data) => api.post('/stock-categories', data),
}

// Credit Card APIs
export const creditCardAPI = {
  getAll: (portfolioId) => api.get(`/portfolios/${portfolioId}/credit-cards`),
  getById: (id) => api.get(`/credit-cards/${id}`),
  getUpcomingDue: (portfolioId) => api.get(`/portfolios/${portfolioId}/credit-cards/upcoming-due`),
  getOverdue: (portfolioId) => api.get(`/portfolios/${portfolioId}/credit-cards/overdue`),
  create: (portfolioId, data) => api.post(`/portfolios/${portfolioId}/credit-cards`, data),
  update: (id, data) => api.put(`/credit-cards/${id}`, data),
  delete: (id) => api.delete(`/credit-cards/${id}`),
}

export default api
