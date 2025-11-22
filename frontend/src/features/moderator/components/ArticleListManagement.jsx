/**
 * Article List Management Component
 * Displays and manages lists of articles for MODERATOR role
 */

import React, { useState, useEffect } from 'react';
import { 
  Edit, 
  Eye, 
  Send, 
  RefreshCw,
  Search as SearchIcon,
  FileText,
  Clock,
  CheckCircle,
  XCircle,
  Trash2,
  Volume2
} from 'lucide-react';
import { Button, Input, Spinner, Alert, StatusBadge, ConfirmModal, PermissionGuard, Modal } from '../../../components/common';
import { PERMISSIONS } from '../../../constants/permissions';
import articleService from '../api';
import { formatNewsTime } from '../../../utils/formatTime';
import ArticleViewModal from './ArticleViewModal';
import AudioGenerationModal from './AudioGenerationModal';
import { useAuth } from '../../../context/AuthContext';

/**
 * ArticleListManagement component
 * @param {Object} props
 * @param {string} props.filter - Filter type: 'all', 'drafts', 'submitted', 'approved', 'rejected'
 * @param {Function} props.onEdit - Callback when edit button is clicked
 * @param {Function} props.onView - Callback when view button is clicked
 */
function ArticleListManagement({ filter = 'all', onEdit, onView }) {
  const { user } = useAuth();
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewingArticle, setViewingArticle] = useState(null);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [deleteConfirmation, setDeleteConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '' });
  const [audioGenerationModal, setAudioGenerationModal] = useState({ isOpen: false, articleId: null, articleTitle: '' });
  const [approveConfirmation, setApproveConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '' });
  const [rejectConfirmation, setRejectConfirmation] = useState({ isOpen: false, articleId: null, articleTitle: '', reason: '' });
  
  // Check if current user is the author of an article
  const isArticleAuthor = (article) => {
    if (!user || !article) return false;
    const articleAuthorId = article.authorId || (article.author && typeof article.author === 'object' ? article.author.id : null);
    return articleAuthorId === user.id;
  };

  // Helper function to extract author name from various formats
  // Supports both NewsArticleSummaryResponse (authorName) and NewsArticleResponse (author object)
  const getAuthorName = (article) => {
    // Handle authorName (string) from NewsArticleSummaryResponse
    if (article.authorName) {
      return article.authorName;
    }
    // Handle author as string
    if (typeof article.author === 'string') {
      return article.author;
    }
    // Handle author as object from NewsArticleResponse
    if (article.author && typeof article.author === 'object') {
      return article.author.username || 
             `${article.author.firstName || ''} ${article.author.lastName || ''}`.trim() || 
             article.author.email ||
             'Anonymous';
    }
    return 'Anonymous';
  };

  // Helper function to extract category name from various formats
  // Supports both NewsArticleSummaryResponse (categoryName) and NewsArticleResponse (category object)
  const getCategoryName = (article) => {
    // Handle categoryName (string) from NewsArticleSummaryResponse
    if (article.categoryName) {
      return article.categoryName;
    }
    // Handle category as object from NewsArticleResponse
    if (article.category && typeof article.category === 'object') {
      return article.category.name || '-';
    }
    return '-';
  };

  const getImageUrl = (url) => {
    if (!url) return null;
    
    // If already absolute URL (http/https/blob), return as is
    if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('blob:')) {
      return url;
    }
    
    // If relative URL, prepend base URL
    const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';
    // Remove /api/v1 if present in baseURL since relative URLs might already include it
    const cleanBaseURL = baseURL.replace(/\/api\/v1$/, '');
    
    // Ensure URL starts with /
    const cleanUrl = url.startsWith('/') ? url : `/${url}`;
    
    return `${cleanBaseURL}${cleanUrl}`;
  };

  // Load articles based on filter
  useEffect(() => {
    loadArticles();
  }, [filter]);

  const loadArticles = async () => {
    setLoading(true);
    setError(null);
    
    try {
      let result;
      
      switch (filter) {
        case 'drafts':
          result = await articleService.getMyDrafts();
          break;
        case 'submitted':
          result = await articleService.getMySubmitted();
          break;
        case 'approved':
          result = await articleService.getMyApproved();
          break;
        case 'rejected':
          result = await articleService.getMyRejected();
          break;
        default:
          result = await articleService.getMyAll();
      }
      
      if (result.success) {
        // Handle different response structures
        let articleData = [];
        
        // Check if result.data is the backend response object with nested data
        const responseData = result.data?.data || result.data;
        
        if (Array.isArray(responseData)) {
          // Direct array response
          articleData = responseData;
        } else if (responseData?.content && Array.isArray(responseData.content)) {
          // Paginated response structure: {content: [...], page: 0, size: 10, ...}
          articleData = responseData.content;
        } else if (responseData?.items && Array.isArray(responseData.items)) {
          // Alternative structure
          articleData = responseData.items;
        }
        
        setArticles(articleData);
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

  const handleSubmit = async (articleId) => {
    if (!window.confirm('Are you sure you want to submit this article for review?')) {
      return;
    }
    
    try {
      const result = await articleService.submitArticle(articleId);
      
      if (result.success) {
        // Reload articles
        loadArticles();
      } else {
        alert(result.error || 'Failed to submit article');
      }
    } catch (err) {
      console.error('Error submitting article:', err);
      alert('An unexpected error occurred while submitting');
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
      const result = await articleService.deleteArticle(deleteConfirmation.articleId);
      
      if (result.success) {
        // Reload articles
        loadArticles();
        // Close confirmation modal
        setDeleteConfirmation({ isOpen: false, articleId: null, articleTitle: '' });
      } else {
        alert(result.error || 'Failed to delete article');
      }
    } catch (err) {
      console.error('Error deleting article:', err);
      alert('An unexpected error occurred while deleting');
    }
  };

  const handleDeleteCancel = () => {
    setDeleteConfirmation({ isOpen: false, articleId: null, articleTitle: '' });
  };

  const handleGenerateAudioClick = (articleId, articleTitle) => {
    setAudioGenerationModal({
      isOpen: true,
      articleId,
      articleTitle
    });
  };

  const handleAudioGenerationSuccess = () => {
    // Optionally reload articles or show success message
    // loadArticles();
  };

  const handleAudioGenerationClose = () => {
    setAudioGenerationModal({ isOpen: false, articleId: null, articleTitle: '' });
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
        // Show success message
        alert('Article approved successfully');
      } else {
        alert(result.error || 'Failed to approve article');
      }
    } catch (err) {
      console.error('Error approving article:', err);
      alert('An unexpected error occurred');
    }
  };

  const handleApproveCancel = () => {
    setApproveConfirmation({ isOpen: false, articleId: null, articleTitle: '' });
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
    
    // Validate rejection reason
    const reason = rejectConfirmation.reason?.trim() || '';
    if (!reason || reason.length < 10) {
      alert('Rejection reason must be at least 10 characters');
      return;
    }
    
    if (reason.length > 1000) {
      alert('Rejection reason must not exceed 1000 characters');
      return;
    }
    
    try {
      const result = await articleService.rejectArticle(rejectConfirmation.articleId, reason);
      
      if (result.success) {
        await loadArticles();
        setRejectConfirmation({ isOpen: false, articleId: null, articleTitle: '', reason: '' });
        // Show success message
        alert('Article rejected successfully');
      } else {
        alert(result.error || 'Failed to reject article');
      }
    } catch (err) {
      console.error('Error rejecting article:', err);
      alert('An unexpected error occurred');
    }
  };

  const handleRejectCancel = () => {
    setRejectConfirmation({ isOpen: false, articleId: null, articleTitle: '', reason: '' });
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      DRAFT: {
        icon: FileText,
        bgColor: 'bg-gray-100',
        textColor: 'text-gray-700',
        label: 'Draft'
      },
      PENDING_REVIEW: {
        icon: Clock,
        bgColor: 'bg-yellow-100',
        textColor: 'text-yellow-700',
        label: 'Pending Review'
      },
      APPROVED: {
        icon: CheckCircle,
        bgColor: 'bg-green-100',
        textColor: 'text-green-700',
        label: 'Approved'
      },
      REJECTED: {
        icon: XCircle,
        bgColor: 'bg-red-100',
        textColor: 'text-red-700',
        label: 'Rejected'
      },
      PUBLISHED: {
        icon: CheckCircle,
        bgColor: 'bg-blue-100',
        textColor: 'text-blue-700',
        label: 'Published'
      }
    };

    const config = statusConfig[status] || statusConfig.DRAFT;
    const Icon = config.icon;

    return (
      <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${config.bgColor} ${config.textColor}`}>
        <Icon className="h-3 w-3 mr-1" />
        {config.label}
      </span>
    );
  };

  const filteredArticles = articles.filter(article => {
    if (!searchTerm) return true;
    
    const searchLower = searchTerm.toLowerCase();
    const authorName = getAuthorName(article);
    const categoryName = getCategoryName(article);
    
    return (
      article.title?.toLowerCase().includes(searchLower) ||
      authorName.toLowerCase().includes(searchLower) ||
      categoryName.toLowerCase().includes(searchLower)
    );
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Header with Search and Refresh */}
      <div className="flex items-center justify-between gap-4">
        <div className="flex-1 max-w-md">
          <div className="relative">
            <SearchIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search articles..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent"
            />
          </div>
        </div>
        
        <button
          onClick={loadArticles}
          className="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors flex items-center"
        >
          <RefreshCw className="h-5 w-5 mr-2" />
          Refresh
        </button>
      </div>

      {/* Error Message */}
      {error && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg" role="alert">
          <p className="text-sm text-red-700">{error}</p>
        </div>
      )}

      {/* Articles List */}
      {filteredArticles.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <FileText className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No Articles Found</h3>
          <p className="text-gray-600">
            {searchTerm 
              ? 'No articles match your search criteria' 
              : `You don't have any ${filter === 'all' ? 'articles' : filter} yet`
            }
          </p>
        </div>
      ) : (
        <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Title
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Author
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Category
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Featured Image
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredArticles.map((article) => (
                  <tr key={article.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-900 line-clamp-2">
                          {article.title}
                        </p>
                        {article.excerpt && (
                          <p className="text-xs text-gray-500 mt-1 line-clamp-1">
                            {article.excerpt}
                          </p>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <p className="text-sm text-gray-900">{getAuthorName(article)}</p>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm text-gray-900">
                        {getCategoryName(article)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {getStatusBadge(article.status)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {(article.featuredImage || article.imageUrl) ? (
                        <img 
                          src={getImageUrl(article.featuredImage || article.imageUrl)} 
                          alt={article.title}
                          className="w-20 h-20 object-cover rounded border border-gray-200"
                          onError={(e) => {
                            e.target.style.display = 'none';
                          }}
                        />
                      ) : (
                        <div className="w-20 h-20 bg-gray-100 rounded border border-gray-200 flex items-center justify-center">
                          <span className="text-xs text-gray-400">No image</span>
                        </div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => {
                            setViewingArticle(article);
                            setIsViewModalOpen(true);
                          }}
                          className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
                          title="View article"
                        >
                          <Eye className="h-4 w-4" />
                        </button>
                        
                        {/* Edit button - requires ARTICLE_UPDATE permission */}
                        <PermissionGuard 
                          requiredPermissions={[PERMISSIONS.ARTICLE_UPDATE]}
                          fallback={null}
                        >
                          {onEdit && (article.status === 'DRAFT' || article.status === 'REJECTED') && (
                            <button
                              onClick={() => onEdit(article)}
                              className="p-2 text-blue-600 hover:text-blue-900 hover:bg-blue-50 rounded-lg transition-colors"
                              title="Edit article"
                            >
                              <Edit className="h-4 w-4" />
                            </button>
                          )}
                        </PermissionGuard>
                        
                        {/* Generate Audio button - only for approved articles and only if user is the author */}
                        {/* AUTHOR has ARTICLE_TTS permission, MODERATOR does not */}
                        <PermissionGuard 
                          requiredPermissions={[PERMISSIONS.ARTICLE_TTS]}
                          fallback={null}
                        >
                          {article.status === 'APPROVED' && isArticleAuthor(article) && (
                            <button
                              onClick={() => handleGenerateAudioClick(article.id, article.title)}
                              className="p-2 text-orange-600 hover:text-orange-900 hover:bg-orange-50 rounded-lg transition-colors"
                              title="Generate audio (Author only)"
                            >
                              <Volume2 className="h-4 w-4" />
                            </button>
                          )}
                        </PermissionGuard>
                        
                        {/* Submit button - only for AUTHOR (who creates articles) */}
                        {/* MODERATOR doesn't create articles, so they don't need submit */}
                        <PermissionGuard 
                          requiredPermissions={[PERMISSIONS.ARTICLE_CREATE]}
                          fallback={null}
                        >
                          {article.status === 'DRAFT' && (
                            <button
                              onClick={() => handleSubmit(article.id)}
                              className="p-2 text-orange-600 hover:text-orange-900 hover:bg-orange-50 rounded-lg transition-colors"
                              title="Submit for review"
                            >
                              <Send className="h-4 w-4" />
                            </button>
                          )}
                        </PermissionGuard>
                        
                        {/* Approve/Reject buttons - requires ARTICLE_APPROVE permission (MODERATOR/ADMIN) */}
                        <PermissionGuard 
                          requiredPermissions={[PERMISSIONS.ARTICLE_APPROVE]}
                          fallback={null}
                        >
                          {(article.status === 'PENDING_REVIEW' || article.status === 'SUBMITTED') && (
                            <>
                              <button
                                onClick={() => handleApproveClick(article.id, article.title)}
                                className="p-2 text-green-600 hover:text-green-900 hover:bg-green-50 rounded-lg transition-colors"
                                title="Approve article"
                              >
                                <CheckCircle className="h-4 w-4" />
                              </button>
                              
                              <button
                                onClick={() => handleRejectClick(article.id, article.title)}
                                className="p-2 text-red-600 hover:text-red-900 hover:bg-red-50 rounded-lg transition-colors"
                                title="Reject article"
                              >
                                <XCircle className="h-4 w-4" />
                              </button>
                            </>
                          )}
                        </PermissionGuard>
                        
                        {/* Delete button - requires ARTICLE_DELETE permission */}
                        <PermissionGuard 
                          requiredPermissions={[PERMISSIONS.ARTICLE_DELETE]}
                          fallback={null}
                        >
                          {article.status === 'DRAFT' && (
                            <button
                              onClick={() => handleDeleteClick(article.id, article.title)}
                              className="p-2 text-red-600 hover:text-red-900 hover:bg-red-50 rounded-lg transition-colors"
                              title="Delete article"
                            >
                              <Trash2 className="h-4 w-4" />
                            </button>
                          )}
                        </PermissionGuard>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Summary */}
      {!error && filteredArticles.length > 0 && (
        <div className="text-sm text-gray-600">
          Showing {filteredArticles.length} of {articles.length} article{articles.length !== 1 ? 's' : ''}
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
      />

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={deleteConfirmation.isOpen}
        onClose={handleDeleteCancel}
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

      {/* Approve Confirmation Modal */}
      <ConfirmModal
        isOpen={approveConfirmation.isOpen}
        onClose={handleApproveCancel}
        onConfirm={handleApproveConfirm}
        title="Approve Article"
        message={
          <div className="space-y-2">
            <p className="text-gray-700">
              Are you sure you want to approve <strong>"{approveConfirmation.articleTitle}"</strong>?
            </p>
            <p className="text-sm text-gray-600">
              The article will be published and visible to all users.
            </p>
          </div>
        }
        confirmText="Approve"
        cancelText="Cancel"
        variant="success"
      />

      {/* Reject Confirmation Modal */}
      {rejectConfirmation.isOpen && (
        <Modal
          isOpen={rejectConfirmation.isOpen}
          onClose={handleRejectCancel}
          title="Reject Article"
          size="md"
          footer={
            <>
              <button
                onClick={handleRejectCancel}
                className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleRejectConfirm}
                disabled={!rejectConfirmation.reason || rejectConfirmation.reason.trim().length < 10 || rejectConfirmation.reason.trim().length > 1000}
                className="px-4 py-2 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Reject Article
              </button>
            </>
          }
        >
          <div className="space-y-4">
            <p className="text-gray-700">
              Are you sure you want to reject <strong>"{rejectConfirmation.articleTitle}"</strong>?
            </p>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Rejection Reason <span className="text-red-500">*</span>
              </label>
              <textarea
                value={rejectConfirmation.reason}
                onChange={(e) => setRejectConfirmation({ ...rejectConfirmation, reason: e.target.value })}
                placeholder="Enter reason for rejection (minimum 10 characters)..."
                rows={4}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                minLength={10}
                maxLength={1000}
              />
              <div className="mt-1 text-sm text-gray-500">
                {rejectConfirmation.reason && rejectConfirmation.reason.trim().length > 0 && 
                 rejectConfirmation.reason.trim().length < 10 
                  ? `Minimum 10 characters required (${rejectConfirmation.reason.trim().length}/10)`
                  : `${rejectConfirmation.reason?.length || 0}/1000 characters`}
              </div>
              {rejectConfirmation.reason && rejectConfirmation.reason.trim().length > 0 && 
               rejectConfirmation.reason.trim().length < 10 && (
                <p className="mt-1 text-sm text-red-600">
                  Rejection reason must be at least 10 characters
                </p>
              )}
            </div>
          </div>
        </Modal>
      )}

      {/* Approve Confirmation Modal */}
      <ConfirmModal
        isOpen={approveConfirmation.isOpen}
        onClose={handleApproveCancel}
        onConfirm={handleApproveConfirm}
        title="Approve Article"
        message={
          <div className="space-y-2">
            <p className="text-gray-700">
              Are you sure you want to approve <strong>"{approveConfirmation.articleTitle}"</strong>?
            </p>
            <p className="text-sm text-gray-600">
              The article will be published and visible to all users.
            </p>
          </div>
        }
        confirmText="Approve"
        cancelText="Cancel"
        variant="success"
      />

      {/* Reject Confirmation Modal */}
      {rejectConfirmation.isOpen && (
        <Modal
          isOpen={rejectConfirmation.isOpen}
          onClose={handleRejectCancel}
          title="Reject Article"
          size="md"
          footer={
            <>
              <button
                onClick={handleRejectCancel}
                className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleRejectConfirm}
                disabled={!rejectConfirmation.reason || rejectConfirmation.reason.trim().length < 10 || rejectConfirmation.reason.trim().length > 1000}
                className="px-4 py-2 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Reject Article
              </button>
            </>
          }
        >
          <div className="space-y-4">
            <p className="text-gray-700">
              Are you sure you want to reject <strong>"{rejectConfirmation.articleTitle}"</strong>?
            </p>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Rejection Reason <span className="text-red-500">*</span>
              </label>
              <textarea
                value={rejectConfirmation.reason}
                onChange={(e) => setRejectConfirmation({ ...rejectConfirmation, reason: e.target.value })}
                placeholder="Enter reason for rejection (minimum 10 characters)..."
                rows={4}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500"
                minLength={10}
                maxLength={1000}
              />
              <div className="mt-1 text-sm text-gray-500">
                {rejectConfirmation.reason && rejectConfirmation.reason.trim().length > 0 && 
                 rejectConfirmation.reason.trim().length < 10 
                  ? `Minimum 10 characters required (${rejectConfirmation.reason.trim().length}/10)`
                  : `${rejectConfirmation.reason?.length || 0}/1000 characters`}
              </div>
              {rejectConfirmation.reason && rejectConfirmation.reason.trim().length > 0 && 
               rejectConfirmation.reason.trim().length < 10 && (
                <p className="mt-1 text-sm text-red-600">
                  Rejection reason must be at least 10 characters
                </p>
              )}
            </div>
          </div>
        </Modal>
      )}

      {/* Audio Generation Modal */}
      <AudioGenerationModal
        isOpen={audioGenerationModal.isOpen}
        onClose={handleAudioGenerationClose}
        articleId={audioGenerationModal.articleId}
        articleTitle={audioGenerationModal.articleTitle}
        onSuccess={handleAudioGenerationSuccess}
      />
    </div>
  );
}

export default ArticleListManagement;

