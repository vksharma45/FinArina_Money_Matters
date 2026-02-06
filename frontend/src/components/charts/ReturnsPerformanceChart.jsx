import React from 'react'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Cell } from 'recharts'

const ReturnsPerformanceChart = ({ assets }) => {
  if (!assets || assets.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
        No asset data available
      </div>
    )
  }

  // Transform data for chart - only holdings, not wishlist
  const chartData = assets
    .filter(asset => !asset.isWishlist && asset.percentageReturn != null)
    .map((asset) => ({
      name: asset.assetName.length > 15 
        ? asset.assetName.substring(0, 15) + '...' 
        : asset.assetName,
      fullName: asset.assetName,
      returns: parseFloat(asset.percentageReturn || 0),
      absoluteReturn: parseFloat(asset.absoluteReturn || 0),
      invested: parseFloat(asset.investedValue || 0),
      current: parseFloat(asset.currentValue || 0),
    }))
    .sort((a, b) => b.returns - a.returns) // Sort by highest returns
    .slice(0, 10) // Top 10 assets

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload
      return (
        <div
          style={{
            background: 'white',
            padding: '12px',
            border: '1px solid #e5e7eb',
            borderRadius: '6px',
            boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          }}
        >
          <p style={{ margin: 0, fontWeight: 600, color: '#111827' }}>
            {data.fullName}
          </p>
          <p style={{ margin: '4px 0', color: data.returns >= 0 ? '#10b981' : '#ef4444' }}>
            Returns: <strong>{data.returns.toFixed(2)}%</strong>
          </p>
          <p style={{ margin: '4px 0', color: '#6b7280', fontSize: '12px' }}>
            Invested: ${data.invested.toFixed(2)}
          </p>
          <p style={{ margin: '4px 0', color: '#6b7280', fontSize: '12px' }}>
            Current: ${data.current.toFixed(2)}
          </p>
          <p style={{ margin: '4px 0', color: data.absoluteReturn >= 0 ? '#10b981' : '#ef4444', fontSize: '12px' }}>
            Gain/Loss: ${data.absoluteReturn.toFixed(2)}
          </p>
        </div>
      )
    }
    return null
  }

  return (
    <div style={{ width: '100%', height: '400px' }}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={chartData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
          <XAxis
            dataKey="name"
            stroke="#6b7280"
            style={{ fontSize: '12px' }}
            angle={-45}
            textAnchor="end"
            height={100}
          />
          <YAxis
            stroke="#6b7280"
            style={{ fontSize: '12px' }}
            tickFormatter={(value) => `${value}%`}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend
            verticalAlign="top"
            height={36}
          />
          <Bar
            dataKey="returns"
            name="Returns (%)"
            radius={[8, 8, 0, 0]}
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.returns >= 0 ? '#10b981' : '#ef4444'} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export default ReturnsPerformanceChart
