/**
 * Profile module component for dashboard
 * Extracted from ProfilePage to be used as a dashboard module
 */

import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { profileService } from '../../services/profileService';
import '../../styles/components/dashboard/ProfileModule.css';

function ProfileModule() {
  const { user: authUser, isAuthenticated, updateProfile } = useAuth();
  const [profile, setProfile] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    dateOfBirth: ''
  });

  // Load profile data on component mount
  useEffect(() => {
    const loadProfile = async () => {
      if (!isAuthenticated) {
        setError('Please login to view profile');
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError(null);
        
        const result = await profileService.getProfile();
        
        if (result.success) {
          setProfile(result.data);
          setEditForm({
            username: result.data.username || '',
            email: result.data.email || '',
            firstName: result.data.firstName || '',
            lastName: result.data.lastName || '',
            phoneNumber: result.data.phoneNumber || '',
            dateOfBirth: result.data.dateOfBirth || ''
          });
        } else {
          setError(result.error || 'Failed to load profile');
        }
      } catch (error) {
        console.error('Profile loading error:', error);
        setError('An unexpected error occurred');
      } finally {
        setIsLoading(false);
      }
    };

    loadProfile();
  }, [isAuthenticated]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSave = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const result = await updateProfile(editForm);
      
      if (result.success) {
        // Reload profile data
        const profileResult = await profileService.getProfile();
        if (profileResult.success) {
          setProfile(profileResult.data);
        }
        setIsEditing(false);
      } else {
        setError(result.error || 'Failed to update profile');
      }
    } catch (error) {
      console.error('Profile update error:', error);
      setError('An unexpected error occurred');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCancel = () => {
    setEditForm({
      username: profile?.username || '',
      email: profile?.email || '',
      firstName: profile?.firstName || '',
      lastName: profile?.lastName || '',
      phoneNumber: profile?.phoneNumber || '',
      dateOfBirth: profile?.dateOfBirth || ''
    });
    setIsEditing(false);
    setError(null);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getStatusBadge = (status) => {
    const statusClasses = {
      'ACTIVE': 'status-active',
      'INACTIVE': 'status-inactive',
      'PENDING': 'status-pending',
      'SUSPENDED': 'status-suspended'
    };
    
    return (
      <span className={`status-badge ${statusClasses[status] || 'status-default'}`}>
        {status}
      </span>
    );
  };

  const getRoleBadge = (role) => {
    const roleClasses = {
      'USER': 'role-user',
      'ADMIN': 'role-admin',
      'MODERATOR': 'role-moderator'
    };
    
    return (
      <span className={`role-badge ${roleClasses[role] || 'role-default'}`}>
        {role}
      </span>
    );
  };

  if (isLoading) {
    return (
      <div className="profile-module">
        <div className="module-loading">
          <div className="loading-spinner"></div>
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="profile-module">
        <div className="module-error">
          <h3>Error</h3>
          <p>{error}</p>
          <button 
            onClick={() => window.location.reload()} 
            className="btn btn-primary"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-module">
      <div className="module-header">
        <h2>Profile Information</h2>
        <p>Manage your account information and preferences</p>
      </div>

      <div className="profile-content">
        {/* Avatar Section */}
        <div className="profile-avatar-section">
          <div className="avatar-container">
            {profile?.avatarUrl ? (
              <img 
                src={profile.avatarUrl} 
                alt="Profile Avatar" 
                className="profile-avatar"
              />
            ) : (
              <div className="profile-avatar-placeholder">
                {profile?.firstName?.charAt(0)?.toUpperCase() || 
                 profile?.username?.charAt(0)?.toUpperCase() || 'U'}
              </div>
            )}
          </div>
          <button className="btn btn-outline btn-sm">
            Change Avatar
          </button>
        </div>

        {/* Profile Information */}
        <div className="profile-info">
          <div className="profile-section">
            <h3>Basic Information</h3>
            
              {isEditing ? (
                <div className="edit-form">
                  <div className="form-group">
                    <label htmlFor="username">Username *</label>
                    <input
                      type="text"
                      id="username"
                      name="username"
                      value={editForm.username}
                      onChange={handleInputChange}
                      className="form-input"
                      required
                      minLength="3"
                      maxLength="50"
                      pattern="^[a-zA-Z0-9_]+$"
                      title="Username can only contain letters, numbers, and underscores"
                    />
                    <small className="form-help">3-50 characters, letters, numbers, and underscores only</small>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="email">Email *</label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      value={editForm.email}
                      onChange={handleInputChange}
                      className="form-input"
                      required
                      maxLength="100"
                    />
                    <small className="form-help">Valid email address required</small>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="firstName">First Name</label>
                    <input
                      type="text"
                      id="firstName"
                      name="firstName"
                      value={editForm.firstName}
                      onChange={handleInputChange}
                      className="form-input"
                      maxLength="50"
                    />
                    <small className="form-help">Maximum 50 characters</small>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="lastName">Last Name</label>
                    <input
                      type="text"
                      id="lastName"
                      name="lastName"
                      value={editForm.lastName}
                      onChange={handleInputChange}
                      className="form-input"
                      maxLength="50"
                    />
                    <small className="form-help">Maximum 50 characters</small>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="phoneNumber">Phone Number</label>
                    <input
                      type="tel"
                      id="phoneNumber"
                      name="phoneNumber"
                      value={editForm.phoneNumber}
                      onChange={handleInputChange}
                      className="form-input"
                      maxLength="20"
                      pattern="^[+]?[0-9\\s\\-()]*$"
                      title="Phone number format is invalid"
                    />
                    <small className="form-help">Optional, maximum 20 characters</small>
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="dateOfBirth">Date of Birth</label>
                    <input
                      type="date"
                      id="dateOfBirth"
                      name="dateOfBirth"
                      value={editForm.dateOfBirth}
                      onChange={handleInputChange}
                      className="form-input"
                    />
                    <small className="form-help">Optional birth date</small>
                  </div>
                
                <div className="form-actions">
                  <button 
                    onClick={handleSave} 
                    className="btn btn-primary"
                    disabled={isLoading}
                  >
                    {isLoading ? 'Saving...' : 'Save Changes'}
                  </button>
                  <button 
                    onClick={handleCancel} 
                    className="btn btn-outline"
                  >
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
                <div className="profile-details">
                  <div className="detail-row">
                    <span className="detail-label">Username:</span>
                    <span className="detail-value">{profile?.username || 'N/A'}</span>
                  </div>
                  
                  <div className="detail-row">
                    <span className="detail-label">Email:</span>
                    <span className="detail-value">{profile?.email || 'N/A'}</span>
                  </div>
                  
                  <div className="detail-row">
                    <span className="detail-label">First Name:</span>
                    <span className="detail-value">{profile?.firstName || 'N/A'}</span>
                  </div>
                  
                  <div className="detail-row">
                    <span className="detail-label">Last Name:</span>
                    <span className="detail-value">{profile?.lastName || 'N/A'}</span>
                  </div>
                  
                  <div className="detail-row">
                    <span className="detail-label">Phone Number:</span>
                    <span className="detail-value">{profile?.phoneNumber || 'N/A'}</span>
                  </div>
                  
                  <div className="detail-row">
                    <span className="detail-label">Date of Birth:</span>
                    <span className="detail-value">
                      {profile?.dateOfBirth ? formatDate(profile.dateOfBirth) : 'N/A'}
                    </span>
                  </div>
                
                <div className="detail-row">
                  <span className="detail-label">Email Verified:</span>
                  <span className="detail-value">
                    {profile?.emailVerified ? (
                      <span className="verified-badge">✓ Verified</span>
                    ) : (
                      <span className="unverified-badge">✗ Not Verified</span>
                    )}
                  </span>
                </div>
                
                <div className="detail-row">
                  <span className="detail-label">Role:</span>
                  <span className="detail-value">
                    {getRoleBadge(profile?.role)}
                  </span>
                </div>
                
                <div className="detail-row">
                  <span className="detail-label">Status:</span>
                  <span className="detail-value">
                    {getStatusBadge(profile?.status)}
                  </span>
                </div>
                
                <div className="detail-row">
                  <span className="detail-label">Member Since:</span>
                  <span className="detail-value">
                    {formatDate(profile?.createdAt)}
                  </span>
                </div>
                
                <div className="detail-row">
                  <span className="detail-label">Last Updated:</span>
                  <span className="detail-value">
                    {formatDate(profile?.updatedAt)}
                  </span>
                </div>
                
                <div className="profile-actions">
                  <button 
                    onClick={() => setIsEditing(true)} 
                    className="btn btn-primary"
                  >
                    Edit Profile
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProfileModule;
