import React from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const AssetHistoryChart = ({ historyData }) => {
  if (!historyData || historyData.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '40px', color: '#6b7280' }}>
        No history data available
      </div>
    )
  }

  // Transform data for chart
  const chartData = historyData.map((item) => ({
    date: new Date(item.actionDate).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    }),
    fullDate: new Date(item.actionDate).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    }),
    price: parseFloat(item.priceAtThatTime || 0),
    quantity: parseFloat(item.quantityChanged || 0),
    action: item.actionType,
  }))

  const CustomTooltip = ({ active, payload, label }) => {
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
            {payload[0].payload.fullDate}
          </p>
          <p style={{ margin: '4px 0', color: '#3b82f6' }}>
            Action: <strong>{payload[0].payload.action}</strong>
          </p>
          <p style={{ margin: '4px 0', color: '#10b981' }}>
            Price: <strong>${payload[0].value.toFixed(2)}</strong>
          </p>
          {payload[0].payload.quantity !== 0 && (
            <p style={{ margin: '4px 0', color: '#f59e0b' }}>
              Quantity: <strong>{payload[0].payload.quantity}</strong>
            </p>
          )}
        </div>
      )
    }
    return null
  }

  return (
    <div style={{ width: '100%', height: '400px' }}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={chartData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
          <XAxis
            dataKey="date"
            stroke="#6b7280"
            style={{ fontSize: '12px' }}
          />
          <YAxis
            stroke="#6b7280"
            style={{ fontSize: '12px' }}
            tickFormatter={(value) => `$${value}`}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend
            verticalAlign="top"
            height={36}
            iconType="line"
          />
          <Line
            type="monotone"
            dataKey="price"
            stroke="#3b82f6"
            strokeWidth={2}
            dot={{ fill: '#3b82f6', r: 4 }}
            activeDot={{ r: 6 }}
            name="Price"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

export default AssetHistoryChart
