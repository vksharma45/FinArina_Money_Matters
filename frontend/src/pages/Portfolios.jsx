import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { usePortfolio } from '../context/PortfolioContext'
import Card from '../components/common/Card'
import Modal from '../components/common/Modal'
import { Plus, Trash2, ArrowRight } from 'lucide-react'
import toast from 'react-hot-toast'

const Portfolios = () => {
  const navigate = useNavigate()
  const { portfolios, createPortfolio, deletePortfolio, setSelectedPortfolio } = usePortfolio()
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [formData, setFormData] = useState({
    portfolioName: '',
    initialInvestment: '',
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const portfolio = await createPortfolio({
        portfolioName: formData.portfolioName,
        initialInvestment: parseFloat(formData.initialInvestment),
      })
      setIsModalOpen(false)
      setFormData({ portfolioName: '', initialInvestment: '' })
      setSelectedPortfolio(portfolio)
    } catch (error) {
      // Error already handled by context
    }
  }

  const handleDelete = async (id, name) => {
    if (window.confirm(`Are you sure you want to delete "${name}"? This will delete all assets, groups, and cards in this portfolio.`)) {
      try {
        await deletePortfolio(id)
      } catch (error) {
        // Error already handled
      }
    }
  }

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value || 0)
  }

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })
  }

  return (
    <div className="container" style={{ paddingTop: '40px' }}>
      <Card
        title="My Portfolios"
        actions={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} />
            New Portfolio
          </button>
        }
      >
        {portfolios.length === 0 ? (
          <div className="text-center" style={{ padding: '40px', color: '#6b7280' }}>
            <p>No portfolios yet. Create one to get started!</p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
            {portfolios.map((portfolio) => (
              <div
                key={portfolio.portfolioId}
                style={{
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  padding: '20px',
                  cursor: 'pointer',
                  transition: 'all 0.2s',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)'
                  e.currentTarget.style.transform = 'translateY(-2px)'
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.boxShadow = 'none'
                  e.currentTarget.style.transform = 'translateY(0)'
                }}
              >
                <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '12px' }}>
                  {portfolio.portfolioName}
                </h3>
                <div style={{ color: '#6b7280', fontSize: '14px', marginBottom: '4px' }}>
                  Initial Investment
                </div>
                <div style={{ fontSize: '24px', fontWeight: 700, marginBottom: '12px' }}>
                  {formatCurrency(portfolio.initialInvestment)}
                </div>
                <div style={{ color: '#6b7280', fontSize: '12px', marginBottom: '16px' }}>
                  Created {formatDate(portfolio.createdDate)}
                </div>
                <div className="flex gap-2">
                  <button
                    className="btn btn-primary"
                    style={{ flex: 1 }}
                    onClick={() => {
                      setSelectedPortfolio(portfolio)
                      navigate(`/portfolios/${portfolio.portfolioId}/assets`)
                    }}
                  >
                    View Assets
                    <ArrowRight size={16} />
                  </button>
                  <button
                    className="btn btn-danger"
                    onClick={(e) => {
                      e.stopPropagation()
                      handleDelete(portfolio.portfolioId, portfolio.portfolioName)
                    }}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Create New Portfolio"
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Portfolio Name</label>
            <input
              type="text"
              className="form-input"
              value={formData.portfolioName}
              onChange={(e) =>
                setFormData({ ...formData, portfolioName: e.target.value })
              }
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Initial Investment</label>
            <input
              type="number"
              step="0.01"
              min="0"
              className="form-input"
              value={formData.initialInvestment}
              onChange={(e) =>
                setFormData({ ...formData, initialInvestment: e.target.value })
              }
              required
            />
          </div>

          <div className="flex gap-2">
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
              Create Portfolio
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </button>
          </div>
        </form>
      </Modal>
    </div>
  )
}

export default Portfolios
