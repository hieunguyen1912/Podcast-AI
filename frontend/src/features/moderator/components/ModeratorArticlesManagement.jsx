/**
 * Moderator Articles Management Component
 * Comprehensive article management for MODERATOR using shared components
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { RefreshCw, Search as SearchIcon, FileText } from 'lucide-react';
import { Alert, Spinner, ConfirmModal } from '../../../components/common';
import { 
  ArticleManagementTable, 
  ArticleFilters, 
  ArticleTabs,
  ArticleApprovalActions,
  ArticleEditModal
} from '../../shared/components';
import articleService from '../api';
import adminService from '../../admin/api';
import ArticleViewModal from './ArticleViewModal';

/**
 * ModeratorArticlesManagement component
 * @param {Object} props
 * @param {Function} props.onStatsChange - Callback when stats change
 */
function ModeratorArticlesManagement({ onStatsChange }) {
  const navigate = useNavigate();
  
  // Tab state
  const [activeTab, setActiveTab] = useState('pending');
  
  // Data state
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize] = useState(10);
  
  // Filter state
  const [filters, setFilters] = useState({
    status: '',
    categoryName: '',
    authorName: '',
    sortBy: 'createdAt',
    sortDirection: 'desc'
  });
  const [showFilters, setShowFilters] = useState(false);
  
  // Modal states
  const [viewingArticle, setViewingArticle] = useState(null);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [editingArticle, setEditingArticle] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [approveConfirmation, setApproveConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '' });
  const [rejectConfirmation, setRejectConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '', reason: '' });
  const [deleteConfirmation, setDeleteConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '' });

  // Load articles based on active tab and filters
  useEffect(() => {
    loadArticles();
  }, [activeTab, currentPage, pageSize, filters]);

  const loadArticles = async () => {
    setLoading(true);
    setError(null);
    
    try {
      let result;
      const params = {
        page: currentPage,
        size: pageSize,
        sortBy: filters.sortBy,
        sortDirection: filters.sortDirection
      };

      // Add filters if provided
      if (filters.status) params.status = filters.status;
      if (filters.categoryName) params.categoryName = filters.categoryName;
      if (filters.authorName) params.authorName = filters.authorName;

      switch (activeTab) {
        case 'pending':
          result = await adminService.getPendingArticles(params);
          break;
        case 'approved':
          result = await adminService.getApprovedArticles(params);
          break;
        case 'rejected':
          result = await adminService.getRejectedArticles(params);
          break;
        default:
          result = await adminService.getAllArticles(params);
      }
      
      if (result.success) {
        const data = result.data?.data || result.data || {};
        const articleList = data.content || data.items || data || [];
        setArticles(Array.isArray(articleList) ? articleList : []);
        setTotalPages(data.totalPages || 0);
        setTotalElements(data.totalElements || 0);
      } else {
        setError(result.error || 'Failed to load articles');
        setArticles([]);
      }
    } catch (err) {
      console.error('Error loading articles:', err);
      setError('An unexpected error occurred');
      setArticles([]);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setCurrentPage(0);
    // Update status filter based on tab
    if (tab === 'approved') {
      setFilters(prev => ({ ...prev, status: 'APPROVED' }));
    } else if (tab === 'rejected') {
      setFilters(prev => ({ ...prev, status: 'REJECTED' }));
    } else if (tab === 'pending') {
      setFilters(prev => ({ ...prev, status: 'PENDING_REVIEW' }));
    } else {
      setFilters(prev => ({ ...prev, status: '' }));
    }
  };

  const handleFilterChange = (name, value) => {
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
    setCurrentPage(0);
  };

  const clearFilters = () => {
    setFilters({
      status: '',
      categoryName: '',
      authorName: '',
      sortBy: 'createdAt',
      sortDirection: 'desc'
    });
    setCurrentPage(0);
  };

  const handleView = (article) => {
    setViewingArticle(article.id);
    setIsViewModalOpen(true);
  };

  const handleEdit = (article) => {
    setEditingArticle(article);
    setIsEditModalOpen(true);
  };

  const handleEditSuccess = () => {
    loadArticles();
    setIsEditModalOpen(false);
    setEditingArticle(null);
    if (onStatsChange) onStatsChange();
  };

  const handleApproveClick = (articleId, articleTitle) => {
    setApproveConfirmation({
      isOpen: true,
      articleId,
      articleTitle
    });
  };

  const handleApproveConfirm = async () => {
    if (!approveConfirmation.articleId) return;
    
    try {
      const result = await articleService.approveArticle(approveConfirmation.articleId);
      
      if (result.success) {
        await loadArticles();
        setApproveConfirmation({ isOpen: false, articleId: null, articleTitle: '' });
        if (onStatsChange) onStatsChange();
      } else {
        setError(result.error || 'Failed to approve article');
      }
    } catch (err) {
      console.error('Error approving article:', err);
      setError('An unexpected error occurred');
    }
  };

  const handleRejectClick = (articleId, articleTitle) => {
    setRejectConfirmation({
      isOpen: true,
      articleId,
      articleTitle,
      reason: ''
    });
  };

  const handleRejectConfirm = async () => {
    if (!rejectConfirmation.articleId) return;
    
    const reason = rejectConfirmation.reason?.trim() || '';
    if (reason.length < 10) {
      setError('Rejection reason must be at least 10 characters');
      return;
    }
    if (reason.length > 1000) {
      setError('Rejection reason must not exceed 1000 characters');
      return;
    }
    
    try {
      const result = await articleService.rejectArticle(rejectConfirmation.articleId, reason);
      
      if (result.success) {
        await loadArticles();
        setRejectConfirmation({ isOpen: false, articleId: null, articleTitle: '', reason: '' });
        if (onStatsChange) onStatsChange();
        setError(null);
      } else {
        setError(result.error || 'Failed to reject article');
      }
    } catch (err) {
      console.error('Error rejecting article:', err);
      setError('An unexpected error occurred');
    }
  };

  const handleDeleteClick = (articleId, articleTitle) => {
    setDeleteConfirmation({
      isOpen: true,
      articleId,
      articleTitle
    });
  };

  const handleDeleteConfirm = async () => {
    if (!deleteConfirmation.articleId) return;
    
    try {
      const result = await adminService.deleteArticle(deleteConfirmation.articleId);
      
      if (result.success) {
        await loadArticles();
        setDeleteConfirmation({ isOpen: false, articleId: null, articleTitle: '' });
        if (onStatsChange) onStatsChange();
      } else {
        setError(result.error || 'Failed to delete article');
      }
    } catch (err) {
      console.error('Error deleting article:', err);
      setError('An unexpected error occurred');
    }
  };

  // Helper functions
  const getAuthorName = (article) => {
    if (article.authorName) return article.authorName;
    if (typeof article.author === 'string') return article.author;
    if (article.author && typeof article.author === 'object') {
      return article.author.username || 
             `${article.author.firstName || ''} ${article.author.lastName || ''}`.trim() || 
             article.author.email ||
             'Anonymous';
    }
    return 'Anonymous';
  };

  const getCategoryName = (article) => {
    return article.categoryName || article.category?.name || 'Uncategorized';
  };

  const getImageUrl = (url) => {
    if (!url) return null;
    if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('blob:')) {
      return url;
    }
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';
    const cleanBaseURL = baseURL.replace(/\/api\/v1$/, '');
    const cleanUrl = url.startsWith('/') ? url : `/${url}`;
    return `${cleanBaseURL}${cleanUrl}`;
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return '-';
    }
  };

  const tabs = [
    { id: 'all', label: 'All Articles' },
    { id: 'pending', label: 'Pending Review' },
    { id: 'approved', label: 'Approved' },
    { id: 'rejected', label: 'Rejected' }
  ];

  return (
    <div className="space-y-4">
      {/* Header with Tabs */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-gray-900">Articles Management</h2>
          <div className="flex items-center gap-2">
            {/* Only show toggle button when filters are hidden */}
            {!showFilters && (
              <ArticleFilters
                showFilters={showFilters}
                onToggleFilters={() => setShowFilters(!showFilters)}
                filters={filters}
                onFilterChange={handleFilterChange}
                onClearFilters={clearFilters}
                config={{
                  showStatusFilter: activeTab === 'all',
                  showCategoryFilter: true,
                  showAuthorFilter: true,
                  showSortOptions: true,
                  hideDraftOption: true // MODERATOR cannot see drafts (only authors can)
                }}
              />
            )}
            <button
              onClick={loadArticles}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center gap-2"
            >
              <RefreshCw className="h-4 w-4" />
              Refresh
            </button>
          </div>
        </div>

        {/* Tabs */}
        <ArticleTabs
          tabs={tabs}
          activeTab={activeTab}
          onTabChange={handleTabChange}
        />
      </div>

      {/* Filters - Show full filters when showFilters is true */}
      {showFilters && (
        <ArticleFilters
          showFilters={showFilters}
          onToggleFilters={() => setShowFilters(!showFilters)}
          filters={filters}
          onFilterChange={handleFilterChange}
          onClearFilters={clearFilters}
          config={{
            showStatusFilter: activeTab === 'all',
            showCategoryFilter: true,
            showAuthorFilter: true,
            showSortOptions: true,
            hideDraftOption: true // MODERATOR cannot see drafts (only authors can)
          }}
        />
      )}

      {/* Error Message */}
      {error && (
        <Alert variant="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Articles Table */}
      <ArticleManagementTable
        articles={articles}
        loading={loading}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDeleteClick}
        onApprove={handleApproveClick}
        onReject={handleRejectClick}
        config={{
          showAuthor: true,
          showCategory: true,
          showStatus: true,
          showImage: true,
          showDate: true,
          showApproveActions: true,
          showEditAction: true,
          showDeleteAction: true,
          dateField: activeTab === 'approved' ? 'publishedAt' : 'updatedAt',
          dateLabel: activeTab === 'approved' ? 'Published' : 'Updated'
        }}
        getAuthorName={getAuthorName}
        getCategoryName={getCategoryName}
        getImageUrl={getImageUrl}
        formatDate={formatDate}
      />

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between bg-white rounded-lg border border-gray-200 p-4">
          <div className="text-sm text-gray-600">
            Showing {articles.length} of {totalElements} article{totalElements !== 1 ? 's' : ''}
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
              disabled={currentPage === 0 || loading}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Previous
            </button>
            <span className="text-sm text-gray-600 px-4">
              Page {currentPage + 1} of {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
              disabled={currentPage >= totalPages - 1 || loading}
              className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Next
            </button>
          </div>
        </div>
      )}

      {/* Article View Modal */}
      <ArticleViewModal
        isOpen={isViewModalOpen}
        onClose={() => {
          setIsViewModalOpen(false);
          setViewingArticle(null);
        }}
        articleId={viewingArticle}
        useAdminApi={true}
      />

      {/* Article Edit Modal */}
      <ArticleEditModal
        isOpen={isEditModalOpen}
        onClose={() => {
          setIsEditModalOpen(false);
          setEditingArticle(null);
        }}
        article={editingArticle}
        onSuccess={handleEditSuccess}
        onUpdate={(id, data, featuredImageFile) => adminService.updateArticle(id, data, featuredImageFile)}
      />

      {/* Approval Actions */}
      <ArticleApprovalActions
        approveConfirmation={approveConfirmation}
        onApproveConfirm={handleApproveConfirm}
        onApproveCancel={() => setApproveConfirmation({ isOpen: false, articleId: null, articleTitle: '' })}
        rejectConfirmation={rejectConfirmation}
        onRejectConfirm={handleRejectConfirm}
        onRejectCancel={() => setRejectConfirmation({ isOpen: false, articleId: null, articleTitle: '', reason: '' })}
        onRejectReasonChange={(reason) => setRejectConfirmation(prev => ({ ...prev, reason }))}
        error={error}
      />

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={deleteConfirmation.isOpen}
        onClose={() => setDeleteConfirmation({ isOpen: false, articleId: null, articleTitle: '' })}
        onConfirm={handleDeleteConfirm}
        title="Delete Article"
        message={
          <div className="space-y-2">
            <p className="text-gray-700">
              Are you sure you want to delete <strong>"{deleteConfirmation.articleTitle}"</strong>?
            </p>
            <p className="text-sm text-red-600 font-medium">
              This action cannot be undone.
            </p>
          </div>
        }
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
      />
    </div>
  );
}

export default ModeratorArticlesManagement;



