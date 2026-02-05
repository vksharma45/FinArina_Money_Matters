import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { usePortfolio } from '../context/PortfolioContext'
import { assetAPI, assetGroupAPI } from '../services/api'
import Card from '../components/common/Card'
import Modal from '../components/common/Modal'
import AssetHistoryChart from '../components/charts/AssetHistoryChart'
import { Plus, Trash2, ShoppingCart, TrendingUp, Users } from 'lucide-react'
import toast from 'react-hot-toast'

const Assets = () => {
  const { portfolioId } = useParams()
  const { selectedPortfolio, stockCategories } = usePortfolio()
  const [assets, setAssets] = useState([])
  const [wishlist, setWishlist] = useState([])
  const [allGroups, setAllGroups] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isBuyModalOpen, setIsBuyModalOpen] = useState(false)
  const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false)
  const [isGroupModalOpen, setIsGroupModalOpen] = useState(false)
  const [selectedAsset, setSelectedAsset] = useState(null)
  const [assetHistory, setAssetHistory] = useState([])
  const [assetGroups, setAssetGroups] = useState([])
  const [selectedGroupIds, setSelectedGroupIds] = useState([])

  const [formData, setFormData] = useState({
    assetName: '',
    assetType: 'STOCK',
    quantity: '',
    buyPrice: '',
    currentPrice: '',
    isWishlist: false,
    stockCategoryId: '',
  })

  const [buyData, setBuyData] = useState({
    buyPrice: '',
    quantity: '',
    remarks: '',
  })

  useEffect(() => {
    if (portfolioId) {
      loadAssets()
      loadAllGroups()
    }
  }, [portfolioId])

  const loadAssets = async () => {
    try {
      setLoading(true)
      const [assetsRes, wishlistRes] = await Promise.all([
        assetAPI.getAll(portfolioId),
        assetAPI.getWishlist(portfolioId),
      ])

      const allAssets = assetsRes.data.data || []
      setAssets(allAssets.filter(a => !a.isWishlist))
      setWishlist(allAssets.filter(a => a.isWishlist))
    } catch (error) {
      toast.error('Failed to load assets')
    } finally {
      setLoading(false)
    }
  }

  const loadAllGroups = async () => {
    try {
      const response = await assetGroupAPI.getAll()
      setAllGroups(response.data.data || [])
    } catch (error) {
      console.error('Failed to load groups')
    }
  }

  const loadAssetHistory = async (assetId) => {
    try {
      const response = await assetAPI.getHistory(assetId)
      setAssetHistory(response.data.data || [])
    } catch (error) {
      toast.error('Failed to load asset history')
      console.error(error)
    }
  }

  const loadAssetGroups = async (assetId) => {
    try {
      const response = await assetAPI.getGroups(assetId)
      const groups = response.data.data || []
      setAssetGroups(groups)
      setSelectedGroupIds(groups.map(g => g.groupId))
    } catch (error) {
      toast.error('Failed to load asset groups')
      console.error(error)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const payload = {
        ...formData,
        quantity: parseFloat(formData.quantity),
        buyPrice: formData.isWishlist ? null : parseFloat(formData.buyPrice),
        currentPrice: parseFloat(formData.currentPrice),
        stockCategoryId: formData.assetType === 'STOCK' ? parseInt(formData.stockCategoryId) : null,
      }

      await assetAPI.create(portfolioId, payload)
      await loadAssets()
      setIsModalOpen(false)
      resetForm()
      toast.success('Asset created successfully')
    } catch (error) {
      toast.error(error.message)
    }
  }

  const handleBuyAsset = async (e) => {
    e.preventDefault()
    try {
      await assetAPI.buy(selectedAsset.assetId, {
        buyPrice: parseFloat(buyData.buyPrice),
        quantity: buyData.quantity ? parseFloat(buyData.quantity) : null,
        remarks: buyData.remarks || 'Converted from wishlist',
      })
      await loadAssets()
      setIsBuyModalOpen(false)
      setSelectedAsset(null)
      setBuyData({ buyPrice: '', quantity: '', remarks: '' })
      toast.success('Asset purchased successfully')
    } catch (error) {
      toast.error(error.message)
    }
  }

  const handleDelete = async (id, name) => {
    if (window.confirm(`Delete "${name}"?`)) {
      try {
        await assetAPI.delete(id)
        await loadAssets()
        toast.success('Asset deleted')
      } catch (error) {
        toast.error(error.message)
      }
    }
  }

  const handleViewHistory = async (asset) => {
    setSelectedAsset(asset)
    await loadAssetHistory(asset.assetId)
    setIsHistoryModalOpen(true)
  }

  const handleManageGroups = async (asset) => {
    setSelectedAsset(asset)
    await loadAssetGroups(asset.assetId)
    setIsGroupModalOpen(true)
  }

  const handleSaveGroups = async () => {
    try {
      // Use PUT to replace all groups for this asset
      await assetAPI.replaceGroups(selectedAsset.assetId, {
        groupIds: selectedGroupIds
      })
      await loadAssets()
      setIsGroupModalOpen(false)
      setSelectedAsset(null)
      setSelectedGroupIds([])
      toast.success('Groups updated successfully')
    } catch (error) {
      toast.error(error.message)
    }
  }

  const toggleGroupSelection = (groupId) => {
    if (selectedGroupIds.includes(groupId)) {
      setSelectedGroupIds(selectedGroupIds.filter(id => id !== groupId))
    } else {
      setSelectedGroupIds([...selectedGroupIds, groupId])
    }
  }

  const resetForm = () => {
    setFormData({
      assetName: '',
      assetType: 'STOCK',
      quantity: '',
      buyPrice: '',
      currentPrice: '',
      isWishlist: false,
      stockCategoryId: '',
    })
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
        title="Holdings"
        actions={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            <Plus size={18} />
            Add Asset
          </button>
        }
      >
        {assets.length === 0 ? (
          <div className="text-center" style={{ padding: '40px', color: '#6b7280' }}>
            No holdings yet
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Quantity</th>
                <th>Buy Price</th>
                <th>Current Price</th>
                <th>Invested</th>
                <th>Current Value</th>
                <th>Returns</th>
                <th>Groups</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {assets.map((asset) => (
                <tr key={asset.assetId}>
                  <td>{asset.assetName}</td>
                  <td><span className="badge badge-info">{asset.assetType}</span></td>
                  <td>{asset.quantity}</td>
                  <td>{formatCurrency(asset.buyPrice)}</td>
                  <td>{formatCurrency(asset.currentPrice)}</td>
                  <td>{formatCurrency(asset.investedValue)}</td>
                  <td>{formatCurrency(asset.currentValue)}</td>
                  <td style={{ color: asset.percentageReturn >= 0 ? '#10b981' : '#ef4444' }}>
                    {asset.percentageReturn?.toFixed(2)}%
                  </td>
                  <td>
                    {asset.groupNames && asset.groupNames.length > 0 ? (
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                        {asset.groupNames.map((name, idx) => (
                          <span key={idx} className="badge badge-info" style={{ fontSize: '11px' }}>
                            {name}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <span style={{ color: '#9ca3af', fontSize: '12px' }}>None</span>
                    )}
                  </td>
                  <td>
                    <div className="flex gap-2">
                      <button
                        className="btn btn-primary"
                        style={{ padding: '6px 12px' }}
                        onClick={() => handleManageGroups(asset)}
                        title="Manage Groups"
                      >
                        <Users size={14} />
                      </button>
                      <button
                        className="btn btn-secondary"
                        style={{ padding: '6px 12px' }}
                        onClick={() => handleViewHistory(asset)}
                        title="View History"
                      >
                        <TrendingUp size={14} />
                      </button>
                      <button
                        className="btn btn-danger"
                        style={{ padding: '6px 12px' }}
                        onClick={() => handleDelete(asset.assetId, asset.assetName)}
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>

      {wishlist.length > 0 && (
        <Card title="Wishlist">
          <table className="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Quantity</th>
                <th>Current Price</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {wishlist.map((asset) => (
                <tr key={asset.assetId}>
                  <td>{asset.assetName}</td>
                  <td><span className="badge badge-warning">{asset.assetType}</span></td>
                  <td>{asset.quantity}</td>
                  <td>{formatCurrency(asset.currentPrice)}</td>
                  <td className="flex gap-2">
                    <button
                      className="btn btn-success"
                      style={{ padding: '6px 12px' }}
                      onClick={() => {
                        setSelectedAsset(asset)
                        setBuyData({ ...buyData, quantity: asset.quantity.toString() })
                        setIsBuyModalOpen(true)
                      }}
                    >
                      <ShoppingCart size={14} />
                      Buy
                    </button>
                    <button
                      className="btn btn-danger"
                      style={{ padding: '6px 12px' }}
                      onClick={() => handleDelete(asset.assetId, asset.assetName)}
                    >
                      <Trash2 size={14} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      {/* Create Asset Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Add New Asset"
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Asset Name</label>
            <input
              type="text"
              className="form-input"
              value={formData.assetName}
              onChange={(e) => setFormData({ ...formData, assetName: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Type</label>
            <select
              className="form-select"
              value={formData.assetType}
              onChange={(e) => setFormData({ ...formData, assetType: e.target.value })}
              required
            >
              <option value="STOCK">Stock</option>
              <option value="MUTUAL_FUND">Mutual Fund</option>
              <option value="BOND">Bond</option>
              <option value="ETF">ETF</option>
              <option value="CASH">Cash</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          {formData.assetType === 'STOCK' && (
            <div className="form-group">
              <label className="form-label">Stock Category</label>
              <select
                className="form-select"
                value={formData.stockCategoryId}
                onChange={(e) => setFormData({ ...formData, stockCategoryId: e.target.value })}
                required
              >
                <option value="">Select category...</option>
                {stockCategories.map((cat) => (
                  <option key={cat.categoryId} value={cat.categoryId}>
                    {cat.categoryName}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div className="form-group">
            <label className="form-label">
              <input
                type="checkbox"
                checked={formData.isWishlist}
                onChange={(e) => setFormData({ ...formData, isWishlist: e.target.checked })}
                style={{ marginRight: '8px' }}
              />
              Add to Wishlist (not purchased yet)
            </label>
          </div>

          <div className="form-group">
            <label className="form-label">Quantity</label>
            <input
              type="number"
              step="0.0001"
              className="form-input"
              value={formData.quantity}
              onChange={(e) => setFormData({ ...formData, quantity: e.target.value })}
              required
            />
          </div>

          {!formData.isWishlist && (
            <div className="form-group">
              <label className="form-label">Buy Price</label>
              <input
                type="number"
                step="0.01"
                className="form-input"
                value={formData.buyPrice}
                onChange={(e) => setFormData({ ...formData, buyPrice: e.target.value })}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Current Price</label>
            <input
              type="number"
              step="0.01"
              className="form-input"
              value={formData.currentPrice}
              onChange={(e) => setFormData({ ...formData, currentPrice: e.target.value })}
              required
            />
          </div>

          <div className="flex gap-2">
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }}>
              Add Asset
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

      {/* Buy Asset Modal */}
      <Modal
        isOpen={isBuyModalOpen}
        onClose={() => setIsBuyModalOpen(false)}
        title={`Buy ${selectedAsset?.assetName}`}
      >
        <form onSubmit={handleBuyAsset}>
          <div className="form-group">
            <label className="form-label">Buy Price</label>
            <input
              type="number"
              step="0.01"
              className="form-input"
              value={buyData.buyPrice}
              onChange={(e) => setBuyData({ ...buyData, buyPrice: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Quantity (leave empty to use existing)</label>
            <input
              type="number"
              step="0.0001"
              className="form-input"
              value={buyData.quantity}
              onChange={(e) => setBuyData({ ...buyData, quantity: e.target.value })}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Remarks (optional)</label>
            <textarea
              className="form-textarea"
              value={buyData.remarks}
              onChange={(e) => setBuyData({ ...buyData, remarks: e.target.value })}
            />
          </div>

          <div className="flex gap-2">
            <button type="submit" className="btn btn-success" style={{ flex: 1 }}>
              Purchase
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsBuyModalOpen(false)}
            >
              Cancel
            </button>
          </div>
        </form>
      </Modal>

      {/* Asset History Modal with Chart */}
      <Modal
        isOpen={isHistoryModalOpen}
        onClose={() => setIsHistoryModalOpen(false)}
        title={`Price History - ${selectedAsset?.assetName}`}
      >
        <div style={{ marginBottom: '20px' }}>
          <AssetHistoryChart historyData={assetHistory} />
        </div>

        {assetHistory.length > 0 && (
          <div>
            <h3 style={{ fontSize: '16px', fontWeight: 600, marginBottom: '12px' }}>
              Transaction History
            </h3>
            <table className="table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Action</th>
                  <th>Price</th>
                  <th>Quantity</th>
                </tr>
              </thead>
              <tbody>
                {assetHistory.map((history) => (
                  <tr key={history.historyId}>
                    <td>{new Date(history.actionDate).toLocaleDateString()}</td>
                    <td>
                      <span className={`badge ${
                        history.actionType === 'BUY' ? 'badge-success' :
                        history.actionType === 'SELL' ? 'badge-danger' :
                        'badge-info'
                      }`}>
                        {history.actionType}
                      </span>
                    </td>
                    <td>{formatCurrency(history.priceAtThatTime)}</td>
                    <td>{history.quantityChanged || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Modal>

      {/* Manage Groups Modal */}
      <Modal
        isOpen={isGroupModalOpen}
        onClose={() => {
          setIsGroupModalOpen(false)
          setSelectedAsset(null)
          setSelectedGroupIds([])
        }}
        title={`Manage Groups - ${selectedAsset?.assetName}`}
      >
        <div style={{ marginBottom: '16px' }}>
          <p style={{ color: '#6b7280', fontSize: '14px', marginBottom: '12px' }}>
            Select which groups this asset belongs to:
          </p>

          {allGroups.length === 0 ? (
            <div style={{ padding: '20px', textAlign: 'center', color: '#6b7280', background: '#f9fafb', borderRadius: '6px' }}>
              No groups available. Create groups first in the Groups page.
            </div>
          ) : (
            <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
              {allGroups.map((group) => (
                <div
                  key={group.groupId}
                  style={{
                    padding: '12px',
                    border: '1px solid #e5e7eb',
                    borderRadius: '6px',
                    marginBottom: '8px',
                    cursor: 'pointer',
                    background: selectedGroupIds.includes(group.groupId) ? '#eff6ff' : 'white',
                    transition: 'all 0.2s'
                  }}
                  onClick={() => toggleGroupSelection(group.groupId)}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <input
                      type="checkbox"
                      checked={selectedGroupIds.includes(group.groupId)}
                      onChange={() => toggleGroupSelection(group.groupId)}
                      style={{ width: '18px', height: '18px', cursor: 'pointer' }}
                    />
                    <div style={{ flex: 1 }}>
                      <div style={{ fontWeight: 600, marginBottom: '4px' }}>
                        {group.groupName}
                      </div>
                      {group.description && (
                        <div style={{ fontSize: '12px', color: '#6b7280' }}>
                          {group.description}
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div style={{
          marginTop: '16px',
          padding: '12px',
          background: '#f9fafb',
          borderRadius: '6px',
          fontSize: '14px',
          color: '#374151'
        }}>
          <strong>Selected:</strong> {selectedGroupIds.length} group{selectedGroupIds.length !== 1 ? 's' : ''}
        </div>

        <div className="flex gap-2" style={{ marginTop: '20px' }}>
          <button
            className="btn btn-primary"
            style={{ flex: 1 }}
            onClick={handleSaveGroups}
            disabled={allGroups.length === 0}
          >
            Save Groups
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setIsGroupModalOpen(false)
              setSelectedAsset(null)
              setSelectedGroupIds([])
            }}
          >
            Cancel
          </button>
        </div>
      </Modal>
    </div>
  )
}

export default Assets
