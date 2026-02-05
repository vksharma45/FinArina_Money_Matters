import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { usePortfolio } from '../../context/PortfolioContext'
import { Wallet, TrendingUp, Users, CreditCard } from 'lucide-react'

const Navbar = () => {
  const location = useLocation()
  const { selectedPortfolio, setSelectedPortfolio, portfolios } = usePortfolio()

  const isActive = (path) => location.pathname === path

  const navStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    height: '64px',
    background: 'white',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    zIndex: 100,
  }

  const containerStyle = {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '0 20px',
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  }

  const linkStyle = (active) => ({
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    padding: '8px 16px',
    borderRadius: '6px',
    color: active ? '#3b82f6' : '#64748b',
    textDecoration: 'none',
    fontWeight: 500,
    transition: 'all 0.2s',
    backgroundColor: active ? '#eff6ff' : 'transparent',
  })

  return (
    <nav style={navStyle}>
      <div style={containerStyle}>
        <Link to="/dashboard" style={{ textDecoration: 'none' }}>
          <h1 style={{ fontSize: '20px', fontWeight: 700, color: '#6CA6CD' }}>
            FinArena
          </h1>
        </Link>

        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <Link to="/dashboard" style={linkStyle(isActive('/dashboard'))}>
            <TrendingUp size={18} />
            Dashboard
          </Link>

          <Link to="/portfolios" style={linkStyle(isActive('/portfolios'))}>
            <Wallet size={18} />
            Portfolios
          </Link>

          <Link to="/groups" style={linkStyle(isActive('/groups'))}>
            <Users size={18} />
            Groups
          </Link>

          <Link to="/credit-cards" style={linkStyle(isActive('/credit-cards'))}>
            <CreditCard size={18} />
            Cards
          </Link>

          {portfolios.length > 0 && (
            <select
              value={selectedPortfolio?.portfolioId || ''}
              onChange={(e) => {
                const portfolio = portfolios.find(
                  (p) => p.portfolioId === parseInt(e.target.value)
                )
                setSelectedPortfolio(portfolio)
              }}
              style={{
                padding: '8px 12px',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                fontSize: '14px',
                marginLeft: '16px',
              }}
            >
              {portfolios.map((p) => (
                <option key={p.portfolioId} value={p.portfolioId}>
                  {p.portfolioName}
                </option>
              ))}
            </select>
          )}
        </div>
      </div>
    </nav>
  )
}

export default Navbar
