/**
 * Shared Article Management Table Component
 * Reusable table component for displaying articles with actions
 * Used by both ADMIN and MODERATOR
 */

import React from 'react';
import { 
  Eye, 
  Edit, 
  Trash2, 
  CheckCircle, 
  XCircle,
  Send,
  Volume2,
  Clock,
  User,
  FileText
} from 'lucide-react';
import { StatusBadge, PermissionGuard } from '../../../components/common';
import { PERMISSIONS } from '../../../constants/permissions';

/**
 * ArticleManagementTable component
 * @param {Object} props
 * @param {Array} props.articles - Array of articles to display
 * @param {boolean} props.loading - Loading state
 * @param {Function} props.onView - View article handler
 * @param {Function} props.onEdit - Edit article handler
 * @param {Function} props.onDelete - Delete article handler
 * @param {Function} props.onApprove - Approve article handler
 * @param {Function} props.onReject - Reject article handler
 * @param {Function} props.onSubmit - Submit article handler (for authors)
 * @param {Function} props.onGenerateAudio - Generate audio handler (for authors)
 * @param {Object} props.config - Configuration object
 * @param {boolean} props.config.showAuthor - Show author column (default: true)
 * @param {boolean} props.config.showCategory - Show category column (default: true)
 * @param {boolean} props.config.showStatus - Show status column (default: true)
 * @param {boolean} props.config.showImage - Show image column (default: true)
 * @param {boolean} props.config.showDate - Show date column (default: true)
 * @param {boolean} props.config.showApproveActions - Show approve/reject buttons (default: false)
 * @param {boolean} props.config.showEditAction - Show edit button (default: true)
 * @param {boolean} props.config.showDeleteAction - Show delete button (default: true)
 * @param {boolean} props.config.showSubmitAction - Show submit button (default: false)
 * @param {boolean} props.config.showAudioAction - Show generate audio button (default: false)
 * @param {Function} props.getAuthorName - Function to get author name from article
 * @param {Function} props.getCategoryName - Function to get category name from article
 * @param {Function} props.getImageUrl - Function to get image URL from article
 * @param {Function} props.formatDate - Function to format date
 * @param {Function} props.isArticleAuthor - Function to check if user is article author
 */
function ArticleManagementTable({
  articles = [],
  loading = false,
  onView,
  onEdit,
  onDelete,
  onApprove,
  onReject,
  onSubmit,
  onGenerateAudio,
  config = {},
  getAuthorName,
  getCategoryName,
  getImageUrl,
  formatDate,
  isArticleAuthor
}) {
  const {
    showAuthor = true,
    showCategory = true,
    showStatus = true,
    showImage = true,
    showDate = true,
    showApproveActions = false,
    showEditAction = true,
    showDeleteAction = true,
    showSubmitAction = false,
    showAudioAction = false,
    dateField = 'createdAt',
    dateLabel = 'Created'
  } = config;

  if (loading && articles.length === 0) {
    return (
      <div className="flex items-center justify-center py-12 bg-white rounded-lg border border-gray-200">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-500"></div>
      </div>
    );
  }

  if (articles.length === 0) {
    return (
      <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
        <FileText className="h-12 w-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">No Articles Found</h3>
        <p className="text-gray-600">No articles to display</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50 border-b border-gray-200">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Title
              </th>
              {showAuthor && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Author
                </th>
              )}
              {showCategory && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Category
                </th>
              )}
              {showStatus && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
              )}
              {showDate && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {dateLabel}
                </th>
              )}
              {showImage && (
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Image
                </th>
              )}
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {articles.map((article) => (
              <tr key={article.id} className="hover:bg-gray-50 transition-colors">
                <td className="px-6 py-4">
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 line-clamp-2">
                      {article.title}
                    </p>
                    {article.description && (
                      <p className="text-xs text-gray-500 mt-1 line-clamp-1">
                        {article.description}
                      </p>
                    )}
                  </div>
                </td>
                
                {showAuthor && (
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-900">
                      <User className="h-4 w-4 mr-2 text-gray-400" />
                      {getAuthorName ? getAuthorName(article) : article.authorName || 'Anonymous'}
                    </div>
                  </td>
                )}
                
                {showCategory && (
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="text-sm text-gray-900">
                      {getCategoryName ? getCategoryName(article) : article.categoryName || 'Uncategorized'}
                    </span>
                  </td>
                )}
                
                {showStatus && (
                  <td className="px-6 py-4 whitespace-nowrap">
                    <StatusBadge status={article.status} />
                  </td>
                )}
                
                {showDate && (
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center text-sm text-gray-500">
                      <Clock className="h-4 w-4 mr-2 text-gray-400" />
                      {formatDate ? formatDate(article[dateField] || article.createdAt) : 
                       new Date(article[dateField] || article.createdAt).toLocaleDateString()}
                    </div>
                  </td>
                )}
                
                {showImage && (
                  <td className="px-6 py-4 whitespace-nowrap">
                    {(article.featuredImage || article.imageUrl) ? (
                      <img 
                        src={getImageUrl ? getImageUrl(article.featuredImage || article.imageUrl) : 
                             (article.featuredImage || article.imageUrl)} 
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
                )}
                
                <td className="px-6 py-4 whitespace-nowrap text-right">
                  <div className="flex items-center justify-end gap-2">
                    {onView && (
                      <button
                        onClick={() => onView(article)}
                        className="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg transition-colors"
                        title="View article"
                      >
                        <Eye className="h-4 w-4" />
                      </button>
                    )}
                    
                    {/* Edit button */}
                    {showEditAction && onEdit && (
                      <PermissionGuard 
                        requiredPermissions={[PERMISSIONS.ARTICLE_UPDATE]}
                        fallback={null}
                      >
                        {/* MODERATOR/ADMIN can edit articles in any status (like AdminArticlesManagement) */}
                        <button
                          onClick={() => onEdit(article)}
                          className="p-2 text-blue-600 hover:text-blue-900 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Edit article"
                        >
                          <Edit className="h-4 w-4" />
                        </button>
                      </PermissionGuard>
                    )}
                    
                    {/* Generate Audio button - only for approved articles and only if user is the author */}
                    {showAudioAction && onGenerateAudio && (
                      <PermissionGuard 
                        requiredPermissions={[PERMISSIONS.ARTICLE_TTS]}
                        fallback={null}
                      >
                        {article.status === 'APPROVED' && 
                         isArticleAuthor && 
                         isArticleAuthor(article) && (
                          <button
                            onClick={() => onGenerateAudio(article.id, article.title)}
                            className="p-2 text-orange-600 hover:text-orange-900 hover:bg-orange-50 rounded-lg transition-colors"
                            title="Generate audio (Author only)"
                          >
                            <Volume2 className="h-4 w-4" />
                          </button>
                        )}
                      </PermissionGuard>
                    )}
                    
                    {/* Approve/Reject buttons */}
                    {showApproveActions && (
                      <PermissionGuard 
                        requiredPermissions={[PERMISSIONS.ARTICLE_APPROVE]}
                        fallback={null}
                      >
                        {(article.status === 'PENDING_REVIEW' || article.status === 'SUBMITTED') && (
                          <>
                            {onApprove && (
                              <button
                                onClick={() => onApprove(article.id, article.title)}
                                className="p-2 text-green-600 hover:text-green-900 hover:bg-green-50 rounded-lg transition-colors"
                                title="Approve article"
                              >
                                <CheckCircle className="h-4 w-4" />
                              </button>
                            )}
                            
                            {onReject && (
                              <button
                                onClick={() => onReject(article.id, article.title)}
                                className="p-2 text-red-600 hover:text-red-900 hover:bg-red-50 rounded-lg transition-colors"
                                title="Reject article"
                              >
                                <XCircle className="h-4 w-4" />
                              </button>
                            )}
                          </>
                        )}
                      </PermissionGuard>
                    )}
                    
                    {/* Submit button - only for AUTHOR (who creates articles) */}
                    {showSubmitAction && onSubmit && (
                      <PermissionGuard 
                        requiredPermissions={[PERMISSIONS.ARTICLE_CREATE]}
                        fallback={null}
                      >
                        {article.status === 'DRAFT' && (
                          <button
                            onClick={() => onSubmit(article.id)}
                            className="p-2 text-orange-600 hover:text-orange-900 hover:bg-orange-50 rounded-lg transition-colors"
                            title="Submit for review"
                          >
                            <Send className="h-4 w-4" />
                          </button>
                        )}
                      </PermissionGuard>
                    )}
                    
                    {/* Delete button */}
                    {showDeleteAction && onDelete && (
                      <PermissionGuard 
                        requiredPermissions={[PERMISSIONS.ARTICLE_DELETE]}
                        fallback={null}
                      >
                        {/* MODERATOR/ADMIN can delete articles in any status (like AdminArticlesManagement) */}
                        <button
                          onClick={() => onDelete(article.id, article.title)}
                          className="p-2 text-red-600 hover:text-red-900 hover:bg-red-50 rounded-lg transition-colors"
                          title="Delete article"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </PermissionGuard>
                    )}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ArticleManagementTable;

