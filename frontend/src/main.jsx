import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css'; // Your CSS file (optional)
import App from './App'; // Your main App component

// Get the root element in the HTML (this is the div with id="root")
const root = ReactDOM.createRoot(document.getElementById('root'));

// Render the app to the root element
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
