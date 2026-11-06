import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { BrowserRouter } from 'react-router'
import { AuthProvider } from './services/AuthProvider.tsx'

createRoot(document.getElementById('root')!).render(
  // Temporarily disabling StrictMode to avoid double rendering of components during development
  // <StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App/>
      </AuthProvider>
    </BrowserRouter>,
  //</StrictMode>
)
