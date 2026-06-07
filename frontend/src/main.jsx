import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

import { registerSW } from 'virtual:pwa-register'

function isExtensionNewTab() {
  if (typeof window === 'undefined') return false
  // `chrome-extension://` pages don't benefit from our PWA SW and can run into
  // confusing caching during extension development.
  return window.location.protocol === 'chrome-extension:'
}

// Register service worker only in production builds.
// In `vite dev`, a SW can easily break refresh/navigation.
if (import.meta.env.PROD && !isExtensionNewTab()) {
  const updateSW = registerSW({
    immediate: true,
    onNeedRefresh() {
      // Auto-apply updates so users get the latest offline/auth fixes.
      updateSW(true)
    },
  })
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
