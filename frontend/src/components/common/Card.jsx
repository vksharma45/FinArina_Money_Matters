import React from 'react'

const Card = ({ title, children, actions }) => {
  return (
    <div className="card">
      {(title || actions) && (
        <div className="flex-between mb-4">
          {title && <h2 style={{ fontSize: '18px', fontWeight: 600 }}>{title}</h2>}
          {actions && <div className="flex gap-2">{actions}</div>}
        </div>
      )}
      {children}
    </div>
  )
}

export default Card
