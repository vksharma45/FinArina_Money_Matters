import React, { useState, useEffect } from 'react'
import { usePortfolio } from '../context/PortfolioContext'
import { portfolioAPI, assetGroupAPI, creditCardAPI } from '../services/api'
import Card from '../components/common/Card'
import { TrendingUp, TrendingDown, Wallet, AlertCircle } from 'lucide-react'
import toast from 'react-hot-toast'

const Dashboard = () => {
  const { selectedPortfolio } = usePortfolio()
  const [summary, setSummary] = useState(null)
  const [groupPerformance, setGroupPerformance] = useState([])
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (selectedPortfolio) {
      loadDashboardData()
    }
  }, [selectedPortfolio])

  const loadDashboardData = async () => {
    if (!selectedPortfolio) return

    try {
      setLoading(true)

      const [summaryRes, groupsRes, alertsRes] = await Promise.all([
        portfolioAPI.getSummary(selectedPortfolio.portfolioId),
        assetGroupAPI.getAllPerformance(selectedPortfolio.portfolioId),
        creditCardAPI.getUpcomingDue(selectedPortfolio.portfolioId),
      ])

      setSummary(summaryRes.data.data)
      setGroupPerformance(groupsRes.data.data || [])
      setAlerts(alertsRes.data.data || [])
    } catch (error) {
      toast.error('Failed to load dashboard data')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div className="spinner" />

  if (!selectedPortfolio) {
    return (
      <div className="container" style={{ paddingTop: '40px' }}>
        <Card>
          <div className="text-center" style={{ padding: '40px' }}>
            <Wallet size={48} style={{ margin: '0 auto 16px', color: '#9ca3af' }} />
            <h2 style={{ marginBottom: '8px' }}>No Portfolio Selected</h2>
            <p style={{ color: '#6b7280' }}>Create a portfolio to get started</p>
          </div>
        </Card>
      </div>
    )
  }

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
    }).format(value || 0)
  }

  const formatPercent = (value) => {
    return `${(value || 0).toFixed(2)}%`
  }

  return (
    <div className="container" style={{ paddingTop: '40px' }}>
      <h1 style={{ marginBottom: '24px', fontSize: '28px', fontWeight: 700 }}>
        Dashboard
      </h1>

      {/* Summary Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '24px' }}>
        <Card>
          <div style={{ marginBottom: '8px', color: '#6b7280', fontSize: '14px' }}>
            Total Invested
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700 }}>
            {formatCurrency(summary?.totalInvestedAmount)}
          </div>
        </Card>

        <Card>
          <div style={{ marginBottom: '8px', color: '#6b7280', fontSize: '14px' }}>
            Current Value
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700 }}>
            {formatCurrency(summary?.currentPortfolioValue)}
          </div>
        </Card>

        <Card>
          <div style={{ marginBottom: '8px', color: '#6b7280', fontSize: '14px' }}>
            Total Returns
          </div>
          <div style={{ fontSize: '28px', fontWeight: 700, color: summary?.absoluteReturn >= 0 ? '#10b981' : '#ef4444' }}>
            {summary?.absoluteReturn >= 0 ? <TrendingUp size={24} style={{ display: 'inline', marginRight: '8px' }} /> : <TrendingDown size={24} style={{ display: 'inline', marginRight: '8px' }} />}
            {formatCurrency(summary?.absoluteReturn)}
          </div>
          <div style={{ fontSize: '14px', color: summary?.percentageReturn >= 0 ? '#10b981' : '#ef4444' }}>
            {formatPercent(summary?.percentageReturn)}
          </div>
        </Card>
      </div>

      {/* Asset Allocation */}
      {summary?.assetAllocation && Object.keys(summary.assetAllocation).length > 0 && (
        <Card title="Asset Allocation">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
            {Object.entries(summary.assetAllocation).map(([type, percentage]) => (
              <div key={type} style={{ padding: '16px', background: '#f9fafb', borderRadius: '8px' }}>
                <div style={{ fontSize: '14px', color: '#6b7280', marginBottom: '4px' }}>
                  {type.replace('_', ' ')}
                </div>
                <div style={{ fontSize: '24px', fontWeight: 600 }}>
                  {formatPercent(percentage)}
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Group Performance */}
      {groupPerformance.length > 0 && (
        <Card title="Group Performance">
          <table className="table">
            <thead>
              <tr>
                <th>Group Name</th>
                <th>Assets</th>
                <th>Invested</th>
                <th>Current Value</th>
                <th>Returns</th>
              </tr>
            </thead>
            <tbody>
              {groupPerformance.map((group) => (
                <tr key={group.groupId}>
                  <td>{group.groupName}</td>
                  <td>{group.holdingCount}</td>
                  <td>{formatCurrency(group.totalInvested)}</td>
                  <td>{formatCurrency(group.currentValue)}</td>
                  <td style={{ color: group.percentageReturn >= 0 ? '#10b981' : '#ef4444' }}>
                    {formatPercent(group.percentageReturn)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      {/* Credit Card Alerts */}
      {alerts.length > 0 && (
        <Card title="Payment Alerts">
          {alerts.map((card) => (
            <div key={card.cardId} style={{
              padding: '12px',
              background: card.dueStatus === 'OVERDUE' ? '#fee2e2' : '#fef3c7',
              borderRadius: '8px',
              marginBottom: '8px',
              display: 'flex',
              alignItems: 'start',
              gap: '12px'
            }}>
              <AlertCircle size={20} style={{ color: card.dueStatus === 'OVERDUE' ? '#ef4444' : '#f59e0b', marginTop: '2px' }} />
              <div>
                <div style={{ fontWeight: 600, marginBottom: '4px' }}>{card.cardName}</div>
                <div style={{ fontSize: '14px', color: '#374151' }}>{card.alertMessage}</div>
              </div>
            </div>
          ))}
        </Card>
      )}
    </div>
  )
}

export default Dashboard
