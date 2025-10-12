/**
 * Dashboard page component
 * Main dashboard with sidebar navigation and module content
 */

import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useLocation } from 'react-router-dom';
import DashboardSidebar from '../components/dashboard/DashboardSidebar';
import DashboardOverview from '../components/dashboard/DashboardOverview';
import ProfileModule from '../components/dashboard/ProfileModule';
import PodcastManagement from '../components/dashboard/PodcastManagement';
import SettingsModule from '../components/dashboard/SettingsModule';
import '../styles/pages/DashboardPage.css';

function DashboardPage() {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();
  const [activeModule, setActiveModule] = useState('overview');

  // Set active module based on URL path
  useEffect(() => {
    if (location.pathname === '/me') {
      setActiveModule('profile');
    } else {
      setActiveModule('overview');
    }
  }, [location.pathname]);

  // Dashboard modules configuration
  const modules = {
    overview: DashboardOverview,
    profile: ProfileModule,
    podcasts: PodcastManagement,
    settings: SettingsModule
  };

  const ActiveComponent = modules[activeModule];

  if (!isAuthenticated) {
    return (
      <div className="dashboard-page">
        <div className="dashboard-container">
          <div className="dashboard-error">
            <h2>Access Denied</h2>
            <p>Please login to access your dashboard.</p>
            <a href="/login" className="btn btn-primary">Login</a>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      <div className="dashboard-container">
        {/* Sidebar Navigation */}
        <DashboardSidebar 
          activeModule={activeModule}
          onModuleChange={setActiveModule}
        />

        {/* Main Content Area */}
        <main className="dashboard-main" role="main">
          <div className="dashboard-header">
            <h1>
              {activeModule === 'overview' && 'Overview'}
              {activeModule === 'profile' && 'Profile'}
              {activeModule === 'podcasts' && 'My Podcasts'}
              {activeModule === 'settings' && 'Settings'}
            </h1>
            <div className="dashboard-actions">
              {/* Quick actions can be added here */}
            </div>
          </div>
          
          <div className="dashboard-content">
            {ActiveComponent && <ActiveComponent />}
            
          </div>
        </main>
      </div>
    </div>
  );
}

export default DashboardPage;
