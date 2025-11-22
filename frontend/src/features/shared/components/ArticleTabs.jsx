/**
 * Shared Article Tabs Component
 * Reusable tabs component for article management
 */

import React from 'react';

/**
 * ArticleTabs component
 * @param {Object} props
 * @param {Array} props.tabs - Array of tab objects: { id, label }
 * @param {string} props.activeTab - Currently active tab ID
 * @param {Function} props.onTabChange - Tab change handler
 */
function ArticleTabs({ tabs = [], activeTab, onTabChange }) {
  if (!tabs || tabs.length === 0) return null;

  return (
    <div className="flex border-b border-gray-200">
      {tabs.map(tab => (
        <button
          key={tab.id}
          onClick={() => onTabChange(tab.id)}
          className={`px-4 py-2 font-medium text-sm transition-colors border-b-2 ${
            activeTab === tab.id
              ? 'border-orange-500 text-orange-600'
              : 'border-transparent text-gray-600 hover:text-gray-900'
          }`}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
}

export default ArticleTabs;



