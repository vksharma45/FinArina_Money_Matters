import React, { useState, useEffect } from 'react'
import { usePortfolio } from '../context/PortfolioContext'
import { assetGroupAPI } from '../services/api'
import Card from '../components/common/Card'
import Modal from '../components/common/Modal'
import { Plus, Trash2 } from 'lucide-react'
import toast from 'react-hot-toast'

const AssetGroups = () => {
  const { selectedPortfolio } = usePortfolio()
  const [groups, setGroups] = useState([])
  const [performance, setPerformance] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [formData, setFormData] = useState({
    groupName: '',
    description: '',
  })

  useEffect(() => {
    loadGroups()
  }, [])

  useEffect(() => {
    if (selectedPortfolio) {
      loadPerformance()
    }
  }, [selectedPortfolio])

  const loadGroups = async () => {
    try {
      setLoading(true)
      const response = await assetGroupAPI.getAll()
      setGroups(response.data.data || [])
    } catch (error) {
      toast.error('Failed to load groups')
    } finally {
      setLoading(false)
    }
  }

  const loadPerformance = async () => {
    if (!selectedPortfolio) return
    
    try {
      const response = await assetGroupAPI.getAllPerformance(selectedPortfolio.portfolioId)
      setPerformance(response.data.data || [])
    } catch (error) {
      console.error('Failed to load performance')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await assetGroupAPI.create(formData)
      await loadGroups()
      setIsModalOpen(false)
      setFormData({ groupName: '', description: '' })
      toast.success('Group created successfully')
    } catch (error) {
      toast.error(error.message)
    }
  }

  const handleDelete = async (id, name) => {
    if (window.confirm(`Delete group "${name}"?`)) {
      try {
        await assetGroupAPI.delete(id)
        await loadGroups()
        await loadPerformance()
        toast.success('Group deleted')
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

  if (loading) return <div className="spinner" />

  return (
    <div className="container" style={{ paddingTop: '40px' }}>
      <Card
        title="Asset Groups"
        actions={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} />
            New Group
          </button>
        }
      >
        {groups.length === 0 ? (
          <div className="text-center" style={{ padding: '40px', color: '#6b7280' }}>
            No groups created yet
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Group Name</th>
                <th>Description</th>
                <th>Total Assets</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {groups.map((group) => (
                <tr key={group.groupId}>
                  <td style={{ fontWeight: 600 }}>{group.groupName}</td>
                  <td style={{ color: '#6b7280' }}>{group.description || '-'}</td>
                  <td>{group.assetCount}</td>
                  <td>{new Date(group.createdDate).toLocaleDateString()}</td>
                  <td>
                    <button
                      className="btn btn-danger"
                      style={{ padding: '6px 12px' }}
                      onClick={() => handleDelete(group.groupId, group.groupName)}
                    >
                      <Trash2 size={14} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>

      {selectedPortfolio && performance.length > 0 && (
        <Card title={`Performance - ${selectedPortfolio.portfolioName}`}>
          <table className="table">
            <thead>
              <tr>
                <th>Group</th>
                <th>Holdings</th>
                <th>Invested</th>
                <th>Current Value</th>
                <th>Returns</th>
              </tr>
            </thead>
            <tbody>
              {performance.map((perf) => (
                <tr key={perf.groupId}>
                  <td style={{ fontWeight: 600 }}>{perf.groupName}</td>
                  <td>{perf.holdingCount}</td>
                  <td>{formatCurrency(perf.totalInvested)}</td>
                  <td>{formatCurrency(perf.currentValue)}</td>
                  <td style={{ color: perf.percentageReturn >= 0 ? '#10b981' : '#ef4444' }}>
                    {perf.percentageReturn?.toFixed(2)}%
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Create Asset Group"
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Group Name</label>
            <input
              type="text"
              className="form-input"
              value={formData.groupName}
              onChange={(e) => setFormData({ ...formData, groupName: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Description (optional)</label>
            <textarea
              className="form-textarea"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
          </div>

          <div className="flex gap-2">
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
              Create Group
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

export default AssetGroups
