import { createRoot } from 'react-dom/client'
import './App.css'
import App from './App.tsx'
import { BrowserRouter } from 'react-router'
import { AuthProvider } from './services/AuthProvider.tsx'
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "./services/QueryClient.ts";


createRoot(document.getElementById('root')!).render(
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <App/>
        </AuthProvider>
      </QueryClientProvider>
    </BrowserRouter>
)
