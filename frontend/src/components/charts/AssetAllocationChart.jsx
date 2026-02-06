import React from 'react'
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts'

const COLORS = {
  STOCK: '#3b82f6',
  MUTUAL_FUND: '#10b981',
  BOND: '#f59e0b',
  ETF: '#8b5cf6',
  CASH: '#06b6d4',
  OTHER: '#64748b',
}

const AssetAllocationChart = ({ assetAllocation }) => {
  if (!assetAllocation || Object.keys(assetAllocation).length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
        No allocation data available
      </div>
    )
  }

  // Transform data for recharts
  const data = Object.entries(assetAllocation).map(([type, percentage]) => ({
    name: type.replace('_', ' '),
    value: parseFloat(percentage),
    displayValue: `${parseFloat(percentage).toFixed(2)}%`,
  }))

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
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
            {payload[0].name}
          </p>
          <p style={{ margin: '4px 0 0 0', color: '#3b82f6', fontWeight: 600 }}>
            {payload[0].payload.displayValue}
          </p>
        </div>
      )
    }
    return null
  }

  const CustomLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
    const RADIAN = Math.PI / 180
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5
    const x = cx + radius * Math.cos(-midAngle * RADIAN)
    const y = cy + radius * Math.sin(-midAngle * RADIAN)

    if (percent < 0.05) return null // Don't show label if less than 5%

    return (
      <text
        x={x}
        y={y}
        fill="white"
        textAnchor={x > cx ? 'start' : 'end'}
        dominantBaseline="central"
        style={{ fontSize: '14px', fontWeight: 600 }}
      >
        {`${(percent * 100).toFixed(1)}%`}
      </text>
    )
  }

  return (
    <div style={{ width: '100%', height: '400px' }}>
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={CustomLabel}
            outerRadius={120}
            fill="#8884d8"
            dataKey="value"
          >
            {data.map((entry, index) => {
              const type = Object.keys(assetAllocation)[index]
              return <Cell key={`cell-${index}`} fill={COLORS[type] || COLORS.OTHER} />
            })}
          </Pie>
          <Tooltip content={<CustomTooltip />} />
          <Legend
            verticalAlign="bottom"
            height={36}
            formatter={(value, entry) => (
              <span style={{ color: '#374151', fontSize: '14px' }}>
                {value} ({entry.payload.displayValue})
              </span>
            )}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}

export default AssetAllocationChart
