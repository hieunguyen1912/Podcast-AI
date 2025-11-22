/**
 * Author Overview Component
 * Dashboard overview for AUTHOR role
 */

import React from 'react';
import { 
  FileText, 
  Clock, 
  CheckCircle, 
  XCircle, 
  TrendingUp,
  Eye,
  FilePlus
} from 'lucide-react';
import { PermissionGuard } from '../../../components/common';

/**
 * AuthorOverview component
 * @param {Object} props
 * @param {Object} props.stats - Statistics for articles
 * @param {Function} props.onNavigate - Callback to navigate to different modules
 */
function AuthorOverview({ stats = {}, onNavigate }) {
  const statCards = [
    {
      id: 'drafts',
      title: 'Drafts',
      value: stats.drafts || 0,
      icon: FileText,
      color: 'gray',
      bgColor: 'bg-gray-50',
      iconColor: 'text-gray-600',
      borderColor: 'border-gray-200'
    },
    {
      id: 'submitted',
      title: 'Pending Review',
      value: stats.submitted || 0,
      icon: Clock,
      color: 'yellow',
      bgColor: 'bg-yellow-50',
      iconColor: 'text-yellow-600',
      borderColor: 'border-yellow-200'
    },
    {
      id: 'approved',
      title: 'Approved',
      value: stats.approved || 0,
      icon: CheckCircle,
      color: 'green',
      bgColor: 'bg-green-50',
      iconColor: 'text-green-600',
      borderColor: 'border-green-200'
    },
    {
      id: 'rejected',
      title: 'Rejected',
      value: stats.rejected || 0,
      icon: XCircle,
      color: 'red',
      bgColor: 'bg-red-50',
      iconColor: 'text-red-600',
      borderColor: 'border-red-200'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg p-8 text-white">
        <h1 className="text-3xl font-bold mb-2">Welcome to Author Dashboard</h1>
        <p className="text-blue-100">
          Create and manage your articles. Track your submissions and published content.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.id}
              className={`${stat.bgColor} border ${stat.borderColor} rounded-lg p-6 cursor-pointer hover:shadow-md transition-all`}
              onClick={() => onNavigate && onNavigate(stat.id)}
            >
              <div className="flex items-center justify-between mb-4">
                <div className={`p-3 rounded-lg bg-white`}>
                  <Icon className={`h-6 w-6 ${stat.iconColor}`} />
                </div>
              </div>
              <p className="text-sm font-medium text-gray-600 mb-1">{stat.title}</p>
              <p className="text-3xl font-bold text-gray-900">{stat.value}</p>
            </div>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Only show Create Article button if user has ARTICLE_CREATE permission */}
          <PermissionGuard 
            requiredPermissions={['PERMISSION_ARTICLE_CREATE']}
            fallback={null}
          >
            <button
              onClick={() => onNavigate && onNavigate('create')}
              className="flex items-center justify-center gap-3 px-6 py-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
              <FileText className="h-5 w-5" />
              <span className="font-medium">Create New Article</span>
            </button>
          </PermissionGuard>
          
          <button
            onClick={() => onNavigate && onNavigate('all')}
            className="flex items-center justify-center gap-3 px-6 py-4 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
          >
            <Eye className="h-5 w-5" />
            <span className="font-medium">View My Articles</span>
          </button>
        </div>
      </div>

      {/* Performance Insights */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-gray-900">Performance Insights</h2>
          <TrendingUp className="h-5 w-5 text-blue-500" />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="text-center p-4 bg-blue-50 rounded-lg">
            <p className="text-2xl font-bold text-blue-900">{stats.approved || 0}</p>
            <p className="text-sm text-gray-600 mt-1">Published Articles</p>
          </div>
          <div className="text-center p-4 bg-yellow-50 rounded-lg">
            <p className="text-2xl font-bold text-yellow-900">{stats.submitted || 0}</p>
            <p className="text-sm text-gray-600 mt-1">Under Review</p>
          </div>
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <p className="text-2xl font-bold text-gray-900">{stats.drafts || 0}</p>
            <p className="text-sm text-gray-600 mt-1">Draft Articles</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AuthorOverview;

