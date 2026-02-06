import React, { useState, useEffect } from 'react'
import { usePortfolio } from '../context/PortfolioContext'
import { creditCardAPI } from '../services/api'
import Card from '../components/common/Card'
import Modal from '../components/common/Modal'
import { Plus, Trash2, AlertCircle } from 'lucide-react'
import toast from 'react-hot-toast'

const CreditCards = () => {
  const { selectedPortfolio } = usePortfolio()
  const [cards, setCards] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [formData, setFormData] = useState({
    cardName: '',
    creditLimit: '',
    outstandingAmount: '',
    dueDate: '',
  })

  useEffect(() => {
    if (selectedPortfolio) {
      loadCards()
    }
  }, [selectedPortfolio])

  const loadCards = async () => {
    if (!selectedPortfolio) return

    try {
      setLoading(true)
      const response = await creditCardAPI.getAll(selectedPortfolio.portfolioId)
      setCards(response.data.data || [])
    } catch (error) {
      toast.error('Failed to load credit cards')
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await creditCardAPI.create(selectedPortfolio.portfolioId, {
        ...formData,
        creditLimit: parseFloat(formData.creditLimit),
        outstandingAmount: parseFloat(formData.outstandingAmount),
      })
      await loadCards()
      setIsModalOpen(false)
      setFormData({
        cardName: '',
        creditLimit: '',
        outstandingAmount: '',
        dueDate: '',
      })
      toast.success('Credit card added successfully')
    } catch (error) {
      toast.error(error.message)
    }
  }

  const handleDelete = async (id, name) => {
    if (window.confirm(`Delete "${name}"?`)) {
      try {
        await creditCardAPI.delete(id)
        await loadCards()
        toast.success('Credit card deleted')
      } catch (error) {
        toast.error(error.message)
      }
    }
  }

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value || 0)
  }

  const getStatusBadge = (status) => {
    const badges = {
      OK: 'badge-success',
      WARNING: 'badge-warning',
      OVERDUE: 'badge-danger',
    }
    return `badge ${badges[status] || 'badge-info'}`
  }

  if (loading) return <div className="spinner" />

  if (!selectedPortfolio) {
    return (
      <div className="container" style={{ paddingTop: '40px' }}>
        <Card>
          <div className="text-center" style={{ padding: '40px', color: '#6b7280' }}>
            Please select a portfolio
          </div>
        </Card>
      </div>
    )
  }

  return (
    <div className="container" style={{ paddingTop: '40px' }}>
      <Card
        title="Credit Cards"
        actions={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} />
            Add Card
          </button>
        }
      >
        {cards.length === 0 ? (
          <div className="text-center" style={{ padding: '40px', color: '#6b7280' }}>
            No credit cards tracked yet
          </div>
        ) : (
          <div style={{ display: 'grid', gap: '16px' }}>
            {cards.map((card) => (
              <div
                key={card.cardId}
                style={{
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  padding: '20px',
                }}
              >
                <div className="flex-between" style={{ marginBottom: '16px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>
                    {card.cardName}
                  </h3>
                  <div className="flex gap-2">
                    <span className={getStatusBadge(card.dueStatus)}>
                      {card.dueStatus}
                    </span>
                    <button
                      className="btn btn-danger"
                      style={{ padding: '6px 12px' }}
                      onClick={() => handleDelete(card.cardId, card.cardName)}
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>

                {card.dueStatus !== 'OK' && (
                  <div
                    style={{
                      padding: '12px',
                      background: card.dueStatus === 'OVERDUE' ? '#fee2e2' : '#fef3c7',
                      borderRadius: '6px',
                      marginBottom: '16px',
                      display: 'flex',
                      gap: '8px',
                    }}
                  >
                    <AlertCircle
                      size={16}
                      style={{
                        color: card.dueStatus === 'OVERDUE' ? '#ef4444' : '#f59e0b',
                        marginTop: '2px',
                      }}
                    />
                    <span style={{ fontSize: '14px' }}>{card.alertMessage}</span>
                  </div>
                )}

                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
                    gap: '16px',
                  }}
                >
                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Credit Limit
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600 }}>
                      {formatCurrency(card.creditLimit)}
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Outstanding
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600 }}>
                      {formatCurrency(card.outstandingAmount)}
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Available
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600, color: '#10b981' }}>
                      {formatCurrency(card.availableCredit)}
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Utilization
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600 }}>
                      {card.creditUtilization?.toFixed(1)}%
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Due Date
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600 }}>
                      {new Date(card.dueDate).toLocaleDateString()}
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '4px' }}>
                      Days Until Due
                    </div>
                    <div style={{ fontSize: '16px', fontWeight: 600 }}>
                      {card.daysUntilDue} days
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Add Credit Card"
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Card Name</label>
            <input
              type="text"
              className="form-input"
              value={formData.cardName}
              onChange={(e) => setFormData({ ...formData, cardName: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Credit Limit</label>
            <input
              type="number"
              step="0.01"
              className="form-input"
              value={formData.creditLimit}
              onChange={(e) => setFormData({ ...formData, creditLimit: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Outstanding Amount</label>
            <input
              type="number"
              step="0.01"
              className="form-input"
              value={formData.outstandingAmount}
              onChange={(e) =>
                setFormData({ ...formData, outstandingAmount: e.target.value })
              }
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Due Date</label>
            <input
              type="date"
              className="form-input"
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
              required
            />
          </div>

          <div className="flex gap-2">
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
              Add Card
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

export default CreditCards
