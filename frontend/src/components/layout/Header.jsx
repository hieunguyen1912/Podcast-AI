/**
 * Header component - Modern Minimalist + Soft Neumorphism Design
 * Clean navigation with floating elements and subtle shadows
 */

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import '../../styles/components/Header.css';

function Header() {
  const { user, isAuthenticated, logout } = useAuth();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      // TODO: Implement search functionality
      console.log('Searching for:', searchQuery);
    }
  };

  const handleLogout = async () => {
    await logout();
    setIsMenuOpen(false);
  };

  return (
    <header className="header" role="banner">
      <div className="header-container">
        {/* Logo */}
        <div className="header-logo">
          <Link to="/" aria-label="PodcastAI Home" className="logo-link">
            <span className="logo-text">
              <span className="logo-podcast">Podcast</span>
              <span className="logo-ai">AI</span>
            </span>
          </Link>
        </div>

        {/* Navigation Links */}
        <nav className="header-nav">
          <Link to="/discover" className="nav-link">
            Discover
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="6,9 12,15 18,9"/>
            </svg>
          </Link>
          <Link to="/create" className="nav-link">
            Create
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="6,9 12,15 18,9"/>
            </svg>
          </Link>
          <Link to="/features" className="nav-link">
            Features
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="6,9 12,15 18,9"/>
            </svg>
          </Link>
          <Link to="/pricing" className="nav-link">Pricing</Link>
        </nav>

        {/* User Menu */}
        <div className="header-user">
          {isAuthenticated ? (
            <div className="user-menu">
              <button
                className="user-menu-trigger"
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                aria-expanded={isMenuOpen}
                aria-haspopup="true"
                aria-label="User menu"
              >
                <span className="user-avatar">
                  {user?.firstName?.charAt(0)?.toUpperCase() || user?.username?.charAt(0)?.toUpperCase() || 'U'}
                </span>
                <span className="user-name">
                  {user?.firstName || user?.username || 'User'}
                </span>
                <svg 
                  width="12" 
                  height="12" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2"
                  className={`menu-arrow ${isMenuOpen ? 'open' : ''}`}
                >
                  <polyline points="6,9 12,15 18,9"/>
                </svg>
              </button>

              {isMenuOpen && (
                <div className="user-menu-dropdown" role="menu">
                  <Link to="/dashboard" role="menuitem" onClick={() => setIsMenuOpen(false)} className="menu-item">
                    Dashboard
                  </Link>
                  <Link to="/me" role="menuitem" onClick={() => setIsMenuOpen(false)} className="menu-item">
                    Profile
                  </Link>
                  <Link to="/favorites" role="menuitem" onClick={() => setIsMenuOpen(false)} className="menu-item">
                    Favorites
                  </Link>
                  <div className="menu-divider"></div>
                  <button
                    onClick={handleLogout}
                    role="menuitem"
                    className="menu-item logout-button"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login" className="btn btn-outline">Log in</Link>
              <Link to="/register" className="btn btn-primary">Sign up</Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

export default Header;
