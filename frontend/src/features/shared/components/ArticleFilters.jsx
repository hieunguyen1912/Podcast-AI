/**
 * Shared Article Filters Component
 * Reusable filters component for article management
 */

import React from 'react';
import { Filter, X } from 'lucide-react';

/**
 * ArticleFilters component
 * @param {Object} props
 * @param {boolean} props.showFilters - Whether filters are visible
 * @param {Function} props.onToggleFilters - Toggle filters visibility
 * @param {Object} props.filters - Current filter values
 * @param {Function} props.onFilterChange - Filter change handler
 * @param {Function} props.onClearFilters - Clear all filters handler
 * @param {Object} props.config - Configuration
 * @param {boolean} props.config.showStatusFilter - Show status filter (default: true)
 * @param {boolean} props.config.showCategoryFilter - Show category filter (default: true)
 * @param {boolean} props.config.showAuthorFilter - Show author filter (default: true)
 * @param {boolean} props.config.showSortOptions - Show sort options (default: true)
 * @param {boolean} props.config.hideDraftOption - Hide Draft option in status filter (default: false)
 */
function ArticleFilters({
  showFilters = false,
  onToggleFilters,
  filters = {},
  onFilterChange,
  onClearFilters,
  config = {}
}) {
  const {
    showStatusFilter = true,
    showCategoryFilter = true,
    showAuthorFilter = true,
    showSortOptions = true,
    hideDraftOption = false
  } = config;

  if (!showFilters) {
    return (
      <button
        onClick={onToggleFilters}
        className={`px-4 py-2 rounded-lg border transition-colors flex items-center gap-2 ${
          showFilters 
            ? 'bg-orange-500 text-white border-orange-500' 
            : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
        }`}
      >
        <Filter className="h-4 w-4" />
        Filters
      </button>
    );
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Filters</h3>
        <div className="flex items-center gap-2">
          <button
            onClick={onClearFilters}
            className="text-sm text-gray-600 hover:text-gray-900 flex items-center gap-1"
          >
            <X className="h-4 w-4" />
            Clear All
          </button>
          <button
            onClick={onToggleFilters}
            className="text-sm text-gray-600 hover:text-gray-900"
          >
            Hide
          </button>
        </div>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        {/* Status Filter */}
        {showStatusFilter && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Status
            </label>
            <select
              value={filters.status || ''}
              onChange={(e) => onFilterChange('status', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
            >
              <option value="">All Status</option>
              {!hideDraftOption && <option value="DRAFT">Draft</option>}
              <option value="PENDING_REVIEW">Pending Review</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>
        )}

        {/* Category Filter */}
        {showCategoryFilter && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Category
            </label>
            <input
              type="text"
              value={filters.categoryName || ''}
              onChange={(e) => onFilterChange('categoryName', e.target.value)}
              placeholder="Search category..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
            />
          </div>
        )}

        {/* Author Filter */}
        {showAuthorFilter && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Author
            </label>
            <input
              type="text"
              value={filters.authorName || ''}
              onChange={(e) => onFilterChange('authorName', e.target.value)}
              placeholder="Search author..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
            />
          </div>
        )}

        {/* Sort By */}
        {showSortOptions && (
          <>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Sort By
              </label>
              <select
                value={filters.sortBy || 'createdAt'}
                onChange={(e) => onFilterChange('sortBy', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
              >
                <option value="createdAt">Created Date</option>
                <option value="updatedAt">Updated Date</option>
                <option value="publishedAt">Published Date</option>
                <option value="title">Title</option>
              </select>
            </div>

            {/* Sort Direction */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Direction
              </label>
              <select
                value={filters.sortDirection || 'desc'}
                onChange={(e) => onFilterChange('sortDirection', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
              >
                <option value="desc">Descending</option>
                <option value="asc">Ascending</option>
              </select>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default ArticleFilters;



