/**
 * Main App component
 * Root component that sets up the application structure and providers
 */

import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import ProtectedRoute from './routes/ProtectedRoute.jsx';
import { routes } from './routes/index.js';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Header />
          
          <main className="main-content" role="main">
            <Suspense fallback={
              <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Loading...</p>
              </div>
            }>
              <Routes>
                {routes.map(route => {
                  const RouteComponent = route.element;
                  
                  if (route.protected) {
                    return (
                      <Route
                        key={route.path}
                        path={route.path}
                        element={
                          <ProtectedRoute>
                            <RouteComponent />
                          </ProtectedRoute>
                        }
                      />
                    );
                  }
                  
                  return (
                    <Route
                      key={route.path}
                      path={route.path}
                      element={<RouteComponent />}
                    />
                  );
                })}
              </Routes>
            </Suspense>
          </main>
          
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;