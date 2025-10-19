/**
 * Login page component - Modern Minimalist + Soft Neumorphism Design
 * Clean authentication form with floating elements and subtle interactions
 */

import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { isValidEmail } from '../utils/validation';
import '../styles/pages/AuthPage.css';

function LoginPage() {
  const { login, error, isLoading, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [isLoaded, setIsLoaded] = useState(false);

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
    setIsLoaded(true);
  }, [isAuthenticated, navigate]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear validation error when user starts typing
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.email) {
      errors.email = 'Email is required';
    } else if (!isValidEmail(formData.email)) {
      errors.email = 'Please enter a valid email address';
    }
    
    if (!formData.password) {
      errors.password = 'Password is required';
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    const result = await login(formData);
    if (result.success) {
      navigate('/');
    }
  };

  return (
    <div className="auth-page">
      {/* Background Elements */}
      <div className="auth-background">
        <div className="floating-shape shape-1"></div>
        <div className="floating-shape shape-2"></div>
        <div className="floating-shape shape-3"></div>
      </div>

      <div className="auth-container">
        <div className={`auth-card neumorphism-strong ${isLoaded ? 'loaded' : ''}`}>
          {/* Header */}
          <div className="auth-header">
            <div className="auth-logo">
              <span className="logo-text">
                <span className="logo-podcast">Podcast</span>
                <span className="logo-ai">AI</span>
              </span>
            </div>
            <h1 className="text-heading-1 auth-title">Welcome Back</h1>
            <p className="text-body-large auth-subtitle">
              Sign in to continue your podcast journey
            </p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="auth-form" noValidate>
            {error && (
              <div className="error-message neumorphism" role="alert">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="15" y1="9" x2="9" y2="15"/>
                  <line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                {error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="email" className="form-label text-caption">
                Email Address
              </label>
              <div className="input-wrapper neumorphism">
                <div className="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                </div>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  className={`form-input ${validationErrors.email ? 'error' : ''}`}
                  placeholder="Enter your email"
                  required
                  aria-describedby={validationErrors.email ? 'email-error' : undefined}
                />
              </div>
              {validationErrors.email && (
                <span id="email-error" className="error-text">
                  {validationErrors.email}
                </span>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="password" className="form-label text-caption">
                Password
              </label>
              <div className="input-wrapper neumorphism">
                <div className="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <circle cx="12" cy="16" r="1"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </div>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  className={`form-input ${validationErrors.password ? 'error' : ''}`}
                  placeholder="Enter your password"
                  required
                  aria-describedby={validationErrors.password ? 'password-error' : undefined}
                />
              </div>
              {validationErrors.password && (
                <span id="password-error" className="error-text">
                  {validationErrors.password}
                </span>
              )}
            </div>

            <div className="form-options">
              <label className="checkbox-wrapper neumorphism">
                <input type="checkbox" name="remember" className="checkbox-input" />
                <span className="checkbox-custom"></span>
                <span className="checkbox-text text-caption">Remember me</span>
              </label>
              <Link to="/forgot-password" className="forgot-link text-caption">
                Forgot password?
              </Link>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-large btn-full"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <div className="btn-spinner"></div>
                  Signing In...
                </>
              ) : (
                <>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                    <polyline points="10,17 15,12 10,7"/>
                    <line x1="15" y1="12" x2="3" y2="12"/>
                  </svg>
                  Sign In
                </>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="auth-footer">
            <p className="text-body">
              Don't have an account?{' '}
              <Link to="/register" className="auth-link">
                Sign up here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
