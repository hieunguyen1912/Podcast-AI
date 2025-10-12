/**
 * Dashboard sidebar component
 * Reusable sidebar navigation for dashboard
 */

import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import '../../styles/components/dashboard/DashboardSidebar.css';

function DashboardSidebar({ activeModule, onModuleChange }) {
  const { user } = useAuth();

  // Dashboard modules configuration
  const modules = [
    {
      id: 'overview',
      name: 'Overview',
      icon: '📊'
    },
    {
      id: 'profile',
      name: 'Profile',
      icon: '👤'
    },
    {
      id: 'podcasts',
      name: 'My Podcasts',
      icon: '🎧'
    },
    {
      id: 'settings',
      name: 'Settings',
      icon: '⚙️'
    }
  ];

  return (
    <aside className="dashboard-sidebar" role="navigation">
      <div className="sidebar-header">
        <h2>Dashboard</h2>
        <p>Welcome back, {user?.firstName || user?.username || 'User'}!</p>
      </div>
      
      <nav className="sidebar-nav">
        <ul className="nav-list">
          {modules.map(module => (
            <li key={module.id} className="nav-item">
              <button
                className={`nav-link ${activeModule === module.id ? 'active' : ''}`}
                onClick={() => onModuleChange(module.id)}
                aria-current={activeModule === module.id ? 'page' : undefined}
              >
                <span className="nav-icon">{module.icon}</span>
                <span className="nav-text">{module.name}</span>
              </button>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}

export default DashboardSidebar;
