/**
 * Register page component - Modern Minimalist + Soft Neumorphism Design
 * Clean registration form with floating elements and subtle interactions
 */

import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { isValidEmail, validatePassword } from '../utils/validation';
import '../styles/pages/AuthPage.css';

function RegisterPage() {
  const { register, error, isLoading } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    setIsLoaded(true);
  }, []);

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
    
    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    } else if (formData.name.trim().length < 2) {
      errors.name = 'Name must be at least 2 characters';
    }
    
    if (!formData.email) {
      errors.email = 'Email is required';
    } else if (!isValidEmail(formData.email)) {
      errors.email = 'Please enter a valid email address';
    }
    
    const passwordValidation = validatePassword(formData.password);
    if (!passwordValidation.isValid) {
      errors.password = passwordValidation.message;
    }
    
    if (!formData.confirmPassword) {
      errors.confirmPassword = 'Please confirm your password';
    } else if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    const result = await register({
      name: formData.name.trim(),
      email: formData.email,
      password: formData.password
    });
    
    if (result.success) {
      window.location.href = '/';
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
            <h1 className="text-heading-1 auth-title">Create Account</h1>
            <p className="text-body-large auth-subtitle">
              Join thousands of creators who trust AI to enhance their content
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
              <label htmlFor="name" className="form-label text-caption">
                Full Name
              </label>
              <div className="input-wrapper neumorphism">
                <div className="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </div>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  className={`form-input ${validationErrors.name ? 'error' : ''}`}
                  placeholder="Enter your full name"
                  required
                  aria-describedby={validationErrors.name ? 'name-error' : undefined}
                />
              </div>
              {validationErrors.name && (
                <span id="name-error" className="error-text">
                  {validationErrors.name}
                </span>
              )}
            </div>

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
                  placeholder="Create a password"
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

            <div className="form-group">
              <label htmlFor="confirmPassword" className="form-label text-caption">
                Confirm Password
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
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  className={`form-input ${validationErrors.confirmPassword ? 'error' : ''}`}
                  placeholder="Confirm your password"
                  required
                  aria-describedby={validationErrors.confirmPassword ? 'confirm-password-error' : undefined}
                />
              </div>
              {validationErrors.confirmPassword && (
                <span id="confirm-password-error" className="error-text">
                  {validationErrors.confirmPassword}
                </span>
              )}
            </div>

            <div className="form-group">
              <label className="checkbox-wrapper neumorphism">
                <input type="checkbox" name="terms" required className="checkbox-input" />
                <span className="checkbox-custom"></span>
                <span className="checkbox-text text-caption">
                  I agree to the{' '}
                  <Link to="/terms" className="auth-link">Terms of Service</Link>
                  {' '}and{' '}
                  <Link to="/privacy" className="auth-link">Privacy Policy</Link>
                </span>
              </label>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-large btn-full"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <div className="btn-spinner"></div>
                  Creating Account...
                </>
              ) : (
                <>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="8.5" cy="7" r="4"/>
                    <line x1="20" y1="8" x2="20" y2="14"/>
                    <line x1="23" y1="11" x2="17" y2="11"/>
                  </svg>
                  Create Account
                </>
              )}
            </button>
          </form>

          {/* Footer */}
          <div className="auth-footer">
            <p className="text-body">
              Already have an account?{' '}
              <Link to="/login" className="auth-link">
                Sign in here
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;
