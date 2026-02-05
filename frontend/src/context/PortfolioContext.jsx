import React, { createContext, useContext, useState, useEffect } from 'react'
import { portfolioAPI, stockCategoryAPI } from '../services/api'
import toast from 'react-hot-toast'

const PortfolioContext = createContext()

export const usePortfolio = () => {
  const context = useContext(PortfolioContext)
  if (!context) {
    throw new Error('usePortfolio must be used within a PortfolioProvider')
  }
  return context
}

export const PortfolioProvider = ({ children }) => {
  const [portfolios, setPortfolios] = useState([])
  const [selectedPortfolio, setSelectedPortfolio] = useState(null)
  const [stockCategories, setStockCategories] = useState([])
  const [loading, setLoading] = useState(false)

  // Load portfolios on mount
  useEffect(() => {
    loadPortfolios()
    loadStockCategories()
  }, [])

  const loadPortfolios = async () => {
    try {
      setLoading(true)
      const response = await portfolioAPI.getAll()
      setPortfolios(response.data.data || [])
      
      // Auto-select first portfolio if none selected
      if (!selectedPortfolio && response.data.data?.length > 0) {
        setSelectedPortfolio(response.data.data[0])
      }
    } catch (error) {
      toast.error('Failed to load portfolios')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  const loadStockCategories = async () => {
    try {
      const response = await stockCategoryAPI.getAll()
      setStockCategories(response.data.data || [])
    } catch (error) {
      console.error('Failed to load stock categories:', error)
    }
  }

  const createPortfolio = async (data) => {
    try {
      const response = await portfolioAPI.create(data)
      await loadPortfolios()
      toast.success('Portfolio created successfully')
      return response.data.data
    } catch (error) {
      toast.error(error.message)
      throw error
    }
  }

  const deletePortfolio = async (id) => {
    try {
      await portfolioAPI.delete(id)
      await loadPortfolios()
      if (selectedPortfolio?.portfolioId === id) {
        setSelectedPortfolio(portfolios[0] || null)
      }
      toast.success('Portfolio deleted successfully')
    } catch (error) {
      toast.error(error.message)
      throw error
    }
  }

  const createStockCategory = async (data) => {
    try {
      const response = await stockCategoryAPI.create(data)
      await loadStockCategories()
      toast.success('Stock category created successfully')
      return response.data.data
    } catch (error) {
      toast.error(error.message)
      throw error
    }
  }

  const value = {
    portfolios,
    selectedPortfolio,
    setSelectedPortfolio,
    stockCategories,
    loading,
    loadPortfolios,
    createPortfolio,
    deletePortfolio,
    createStockCategory,
  }

  return (
    <PortfolioContext.Provider value={value}>
      {children}
    </PortfolioContext.Provider>
  )
}
