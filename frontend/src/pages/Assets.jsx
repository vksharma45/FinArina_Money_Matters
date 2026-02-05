import React, { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { usePortfolio } from '../context/PortfolioContext'
import { assetAPI } from '../services/api'
import Card from '../components/common/Card'
import Modal from '../components/common/Modal'
import { Plus, Trash2, Edit, ShoppingCart, History } from 'lucide-react'
import toast from 'react-hot-toast'

const Assets = () => {
  const { portfolioId } = useParams()
  const { selectedPortfolio, stockCategories } = usePortfolio()
  const [assets, setAssets] = useState([])
  const [wishlist, setWishlist] = useState([])
  const [loading, setLoading] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isBuyModalOpen, setIsBuyModalOpen] = useState(false)
  const [selectedAsset, setSelectedAsset] = useState(null)
  
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
    </div>
  )
}

export default Assets
