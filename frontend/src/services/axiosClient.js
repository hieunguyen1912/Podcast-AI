/**
 * Axios Client Configuration
 * Based on FRONTEND_API_DOCUMENTATION.md
 * 
 * Handles:
 * - JWT Bearer Token authentication
 * - Response format transformation (extracts data field)
 * - Error handling with error codes
 * - Token refresh on 401 errors
 * - Public endpoints (no auth required)
 */
import axios from 'axios';
import { authService } from '../features/auth/api';
import { navigationService } from './navigationService';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api/v1',
  timeout: 10000,
  withCredentials: true,
  headers: {
    'X-Requested-With': 'XMLHttpRequest'
  },
  maxContentLength: Infinity,
  maxBodyLength: Infinity
});

let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  
  failedQueue = [];
};

apiClient.interceptors.request.use(
  (config) => {
    const isRefreshEndpoint = config.url?.includes('/auth/refresh');

    if (!isRefreshEndpoint) {
      const token = authService.getToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type'];
    } else if (config.data && !config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json';
    }
    
    config.metadata = { startTime: Date.now() };

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// List of public endpoints that don't require authentication
// Based on FRONTEND_API_DOCUMENTATION.md
const PUBLIC_ENDPOINTS = [
  // Auth endpoints (public)
  '/auth/register',
  '/auth/login',
  '/auth/refresh',
  '/auth/revoke',
  
  // News endpoints (all public)
  '/news/search',
  '/news/featured',
  '/news/trending',
  '/news/latest',
  '/news/categories',
  '/news/category',
  
  // Categories (GET only - public)
  '/categories',
  '/categories/all',
  '/categories/tree',
  '/categories/root',
  '/categories/slug',
  
  // Articles (GET only - published articles)
  '/articles', // GET /articles/{id} for published articles
  
  // Comments (GET only)
  '/comments/articles',
  '/comments', // GET /comments/{commentId}/replies
  
  // Audio (GET only - public)
  '/articles', // GET /articles/{articleId}/audio
  '/articles/audio', // GET /articles/audio/{audioFileId}/stream and /download
  
  // Images
  '/images'
];

// Check if an endpoint is public (allows access without auth)
const isPublicEndpoint = (url) => {
  if (!url) return false;
  // Extract path without query parameters
  const path = url.split('?')[0];
  // Remove base URL prefix if present (e.g., /api/v1)
  // Note: axios config.url is relative to baseURL, so it should already be without /api/v1
  const cleanPath = path.replace(/^\/api\/v1/, '') || path;
  
  // Check exact matches
  if (PUBLIC_ENDPOINTS.some(endpoint => cleanPath === endpoint)) {
    return true;
  }
  
  // Check pattern matches
  return (
    // News endpoints
    /^\/news\/\d+$/.test(cleanPath) || // /news/{id}
    /^\/news\/\d+\/related/.test(cleanPath) || // /news/{id}/related
    /^\/news\/category\/\d+/.test(cleanPath) || // /news/category/{categoryId}
    
    // Categories endpoints
    /^\/categories\/\d+$/.test(cleanPath) || // /categories/{id}
    /^\/categories\/slug\/.+/.test(cleanPath) || // /categories/slug/{slug}
    /^\/categories\/\d+\/children/.test(cleanPath) || // /categories/{id}/children
    /^\/categories\/\d+\/breadcrumb/.test(cleanPath) || // /categories/{id}/breadcrumb
    
    // Articles (GET only - published)
    /^\/articles\/\d+$/.test(cleanPath) || // /articles/{id} - published articles
    /^\/articles\/\d+\/category/.test(cleanPath) || // /articles/{id}/category
    
    // Comments (GET only)
    /^\/comments\/articles\/\d+$/.test(cleanPath) || // /comments/articles/{articleId}
    /^\/comments\/\d+\/replies/.test(cleanPath) || // /comments/{commentId}/replies
    
    // Audio (GET only - public)
    /^\/articles\/\d+\/audio$/.test(cleanPath) || // /articles/{articleId}/audio
    /^\/articles\/audio\/\d+\/stream/.test(cleanPath) || // /articles/audio/{audioFileId}/stream
    /^\/articles\/audio\/\d+\/download/.test(cleanPath) || // /articles/audio/{audioFileId}/download
    
    // Images
    /^\/images\/.+/.test(cleanPath) // /images/**
  );
};

/**
 * Response interceptor
 * Handles API response format based on FRONTEND_API_DOCUMENTATION.md
 * 
 * Expected response format:
 * {
 *   "code": 2000,
 *   "status": 200,
 *   "message": "Success message",
 *   "data": { ... },
 *   "timestamp": "2024-01-01T00:00:00Z"
 * }
 * 
 * This interceptor extracts the `data` field and returns it directly,
 * making it easier to use in components.
 */
apiClient.interceptors.response.use(
  (response) => {
    // Check if response follows the documented format: { code, status, message, data, timestamp }
    const backendStatus = response.data?.status;
    
    if (backendStatus !== undefined) {
      // Success response (status 200-299)
      if (backendStatus >= 200 && backendStatus < 300) {
        // Extract and return only the data field for easier consumption
        response.data = response.data.data;
        return response;
      } else {
        // Error response - convert to error format
        const error = new Error(response.data.message || 'Request failed');
        error.response = response;
        error.response.status = backendStatus;
        error.config = response.config;
        // Preserve error code for error handling
        error.code = response.data?.code;
        return Promise.reject(error);
      }
    }

    // Response doesn't follow documented format, return as-is
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // Handle 403 Forbidden errors - format error message for better UX
    const isForbidden = error.response?.status === 403 || 
                        (error.response?.data?.status === 403);
    
    if (isForbidden) {
      // Format 403 error with user-friendly message
      const errorData = error.response?.data || {};
      const errorCode = errorData.code || errorData.error?.code;
      const errorMessage = errorData.message || errorData.error?.message || 'Bạn không có quyền thực hiện thao tác này';
      
      // Create a formatted error with better message
      const formattedError = new Error(errorMessage);
      formattedError.response = error.response;
      formattedError.response.status = 403;
      formattedError.code = errorCode || 4003; // 4003 is FORBIDDEN error code
      formattedError.config = error.config;
      
      // Log for debugging (optional)
      console.warn('403 Forbidden:', {
        url: originalRequest?.url,
        message: errorMessage,
        code: errorCode
      });
      
      return Promise.reject(formattedError);
    }
    
    const isUnauthorized = error.response?.status === 401 || 
                          (error.response?.data?.status === 401);
    
    if (isUnauthorized && originalRequest) {
      const isRefreshEndpoint = originalRequest.url?.includes('/auth/refresh');
      const isPublic = isPublicEndpoint(originalRequest.url);
      
      if (isRefreshEndpoint) {
        isRefreshing = false;
        failedQueue = [];
        
        authService.clearAuth();
        
        const currentPath = window.location.pathname;
        if (!currentPath.includes('/login')) {
          navigationService.replace('/login');
        }
        
        return Promise.reject(error);
      }

      // For public endpoints, if we get 401, try retrying without auth headers
      if (isPublic && !originalRequest._retryWithoutAuth) {
        originalRequest._retryWithoutAuth = true;
        // Remove Authorization header and retry
        const retryConfig = {
          ...originalRequest,
          headers: {
            ...originalRequest.headers,
            Authorization: undefined
          },
          _retry: true
        };
        delete retryConfig.headers.Authorization;
        return apiClient(retryConfig);
      }

      if (originalRequest._retry) {
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(token => {
            const retryConfig = {
              ...originalRequest,
              headers: {
                ...originalRequest.headers,
                Authorization: `Bearer ${token}`
              },
              _retry: true
            };
            return apiClient(retryConfig);
          })
          .catch(err => {
            // For public endpoints, if refresh fails, try without auth
            if (isPublic && !originalRequest._retryWithoutAuth) {
              originalRequest._retryWithoutAuth = true;
              const retryConfig = {
                ...originalRequest,
                headers: {
                  ...originalRequest.headers,
                  Authorization: undefined
                },
                _retry: true
              };
              delete retryConfig.headers.Authorization;
              return apiClient(retryConfig);
            }
            return Promise.reject(err);
          });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshResult = await authService.refreshToken();
        
        if (refreshResult.success && refreshResult.data?.accessToken) {
          const newToken = refreshResult.data.accessToken;
          
          processQueue(null, newToken);
          isRefreshing = false;
          
          const retryConfig = {
            ...originalRequest,
            headers: {
              ...originalRequest.headers,
              Authorization: `Bearer ${newToken}`
            },
            _retry: true
          };
          
          // Retry the original request with new token
          return apiClient(retryConfig);
        } else {
          // Refresh failed
          // For public endpoints, try without auth instead of redirecting
          if (isPublic && !originalRequest._retryWithoutAuth) {
            processQueue(null, null);
            isRefreshing = false;
            originalRequest._retryWithoutAuth = true;
            const retryConfig = {
              ...originalRequest,
              headers: {
                ...originalRequest.headers,
                Authorization: undefined
              },
              _retry: true
            };
            delete retryConfig.headers.Authorization;
            return apiClient(retryConfig);
          }
          // For protected endpoints, clear auth and redirect to login
          throw new Error('Token refresh failed');
        }
      } catch (refreshError) {
        // For public endpoints, try without auth instead of redirecting
        if (isPublic && !originalRequest._retryWithoutAuth) {
          processQueue(null, null);
          isRefreshing = false;
          originalRequest._retryWithoutAuth = true;
          const retryConfig = {
            ...originalRequest,
            headers: {
              ...originalRequest.headers,
              Authorization: undefined
            },
            _retry: true
          };
          delete retryConfig.headers.Authorization;
          return apiClient(retryConfig);
        }
        
        // For protected endpoints, clear auth and redirect to login
        processQueue(refreshError, null);
        isRefreshing = false;
        
        authService.clearAuth();
        
        const currentPath = window.location.pathname;
        if (!currentPath.includes('/login')) {
          navigationService.replace('/login');
        }
        
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
