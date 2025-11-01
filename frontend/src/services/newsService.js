/**
 * News Service - API integration for news content
 * Handles all news-related API calls for HomePage
 */

import apiClient from './apiClient';

class NewsService {
  /**
   * Get featured article for hero section
   * @returns {Promise<Object>} Featured article data
   */
  async getFeaturedArticle() {
    try {
      const response = await apiClient.get('/news/featured');
      return response.data;
    } catch (error) {
      console.error('Error fetching featured article:', error);
      throw error;
    }
  }

  /**
   * Get trending articles for side panel
   * @param {number} limit - Number of articles to fetch
   * @returns {Promise<Array>} Array of trending articles
   */
  async getTrendingArticles(limit = 4) {
    try {
      const response = await apiClient.get(`/news/trending?limit=${limit}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching trending articles:', error);
      throw error;
    }
  }

  /**
   * Get latest articles
   * @param {number} limit - Number of articles to fetch
   * @returns {Promise<Array>} Array of latest articles
   */
  async getLatestArticles(limit = 4) {
    try {
      const response = await apiClient.get(`/news/latest?limit=${limit}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching latest articles:', error);
      throw error;
    }
  }

  /**
   * Get article by ID
   * @param {number} id - Article ID
   * @returns {Promise<Object>} Article data
   */
  async getArticleById(id) {
    try {
      const response = await apiClient.get(`/news/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching article ${id}:`, error);
      throw error;
    }
  }

  /**
   * Track article view
   * @param {number} articleId - Article ID
   * @returns {Promise<Object>} Updated view count
   */
  async trackArticleView(articleId) {
    try {
      const response = await apiClient.get(`/news/${articleId}`);
      return response.data;
    } catch (error) {
      console.error(`Error tracking view for article ${articleId}:`, error);
      throw error;
    }
  }

  /**
   * Like/Unlike article
   * @param {number} articleId - Article ID
   * @param {boolean} isLiked - Whether to like or unlike
   * @returns {Promise<Object>} Updated like count and status
   */
  async toggleArticleLike(articleId, isLiked) {
    try {
      const method = isLiked ? 'post' : 'delete';
      const response = await apiClient[method](`/news/${articleId}/like`);
      return response.data;
    } catch (error) {
      console.error(`Error toggling like for article ${articleId}:`, error);
      throw error;
    }
  }

  /**
   * Search articles with filters
   * @param {Object} filters - Search filters
   * @param {string} filters.keyword - Keyword to search
   * @param {number} filters.categoryId - Category ID filter
   * @param {string} filters.fromDate - Start date (ISO string or Instant format)
   * @param {string} filters.toDate - End date (ISO string or Instant format)
   * @param {number} page - Page number (0-indexed)
   * @param {number} size - Items per page
   * @returns {Promise<Object>} PaginatedResponse with content array and pagination metadata
   */
  async searchArticles(filters, page = 0, size = 10) {
    try {
      const params = new URLSearchParams();
      
      // Add optional filter parameters
      if (filters.keyword) params.append('keyword', filters.keyword);
      if (filters.categoryId) params.append('categoryId', filters.categoryId);
      
      // Convert dates to ISO string format for Instant
      if (filters.fromDate) {
        const fromDate = filters.fromDate instanceof Date 
          ? filters.fromDate.toISOString() 
          : new Date(filters.fromDate).toISOString();
        params.append('fromDate', fromDate);
      }
      if (filters.toDate) {
        const toDate = filters.toDate instanceof Date 
          ? filters.toDate.toISOString() 
          : new Date(filters.toDate).toISOString();
        params.append('toDate', toDate);
      }
      
      // Add pagination parameters (Pageable)
      params.append('page', page);
      params.append('size', size);

      const response = await apiClient.get(`/news/search?${params.toString()}`);
      // Response is PaginatedResponse<NewsArticleResponse>
      // Structure: { content, page, size, totalElements, totalPages, hasNext, hasPrevious, first, last }
      return response.data;
    } catch (error) {
      console.error('Error searching articles:', error);
      throw error;
    }
  }

  /**
   * Get all categories
   * @returns {Promise<Array>} Array of categories
   */
  async getCategories() {
    try {
      const response = await apiClient.get('/news/categories');
      return response.data;
    } catch (error) {
      console.error('Error fetching categories:', error);
      throw error;
    }
  }
}

export default new NewsService();
