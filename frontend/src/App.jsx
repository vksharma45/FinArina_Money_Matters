import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { PortfolioProvider } from './context/PortfolioContext'
import Navbar from './components/common/Navbar'
import Dashboard from './pages/Dashboard'
import Portfolios from './pages/Portfolios'
import Assets from './pages/Assets'
import AssetGroups from './pages/AssetGroups'
import CreditCards from './pages/CreditCards'

function App() {
  return (
    <Router>
      <PortfolioProvider>
        <div className="app">
          <Navbar />
          <main style={{ paddingTop: '80px', minHeight: '100vh' }}>
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/portfolios" element={<Portfolios />} />
              <Route path="/portfolios/:portfolioId/assets" element={<Assets />} />
              <Route path="/groups" element={<AssetGroups />} />
              <Route path="/credit-cards" element={<CreditCards />} />
            </Routes>
          </main>
          <Toaster position="top-right" />
        </div>
      </PortfolioProvider>
    </Router>
  )
}

export default App
