/**
 * Error Handler Utility
 * Based on FRONTEND_API_DOCUMENTATION.md
 * Provides standardized error handling with error codes and user-friendly messages
 */

/**
 * Common error codes from API documentation
 */
export const ERROR_CODES = {
  SUCCESS: 2000,
  BAD_REQUEST: 4000,
  UNAUTHORIZED: 4001,
  FORBIDDEN: 4003,
  NOT_FOUND: 4004,
  // Custom error codes
  AUDIO_ONLY_AUTHOR_CAN_GENERATE: 5008
};

/**
 * Error code to Vietnamese message mapping
 * Based on FRONTEND_API_DOCUMENTATION.md section "Error Messages"
 */
const ERROR_MESSAGES = {
  [ERROR_CODES.UNAUTHORIZED]: 'Vui lòng đăng nhập để tiếp tục',
  [ERROR_CODES.FORBIDDEN]: 'Bạn không có quyền thực hiện thao tác này',
  [ERROR_CODES.NOT_FOUND]: 'Không tìm thấy tài nguyên',
  [ERROR_CODES.BAD_REQUEST]: 'Yêu cầu không hợp lệ',
  [ERROR_CODES.AUDIO_ONLY_AUTHOR_CAN_GENERATE]: 'Chỉ tác giả bài viết mới có thể tạo audio TTS'
};

/**
 * Get user-friendly error message from error object
 * @param {Error|Object} error - Error object from API or catch block
 * @param {string} defaultMessage - Default message if no specific message found
 * @returns {string} User-friendly error message
 */
export const getErrorMessage = (error, defaultMessage = 'Đã xảy ra lỗi') => {
  // Check for error code in response data
  const errorCode = error?.response?.data?.code || 
                    error?.response?.data?.error?.code || 
                    error?.errorCode || 
                    error?.code;
  
  // Check for error message in response data
  const errorMessage = error?.response?.data?.message || 
                       error?.response?.data?.error?.message || 
                       error?.message || 
                       error?.error;
  
  // Return mapped message if error code exists
  if (errorCode && ERROR_MESSAGES[errorCode]) {
    return ERROR_MESSAGES[errorCode];
  }
  
  // Return error message if available
  if (errorMessage) {
    return errorMessage;
  }
  
  // Return default message
  return defaultMessage;
};

/**
 * Get error code from error object
 * @param {Error|Object} error - Error object from API or catch block
 * @returns {number|string|null} Error code or null
 */
export const getErrorCode = (error) => {
  return error?.response?.data?.code || 
         error?.response?.data?.error?.code || 
         error?.errorCode || 
         error?.code || 
         null;
};

/**
 * Get HTTP status code from error object
 * @param {Error|Object} error - Error object from API or catch block
 * @returns {number|null} HTTP status code or null
 */
export const getErrorStatus = (error) => {
  return error?.response?.status || 
         error?.response?.data?.status || 
         error?.status || 
         null;
};

/**
 * Check if error is unauthorized (401)
 * @param {Error|Object} error - Error object
 * @returns {boolean} True if error is 401 Unauthorized
 */
export const isUnauthorizedError = (error) => {
  const status = getErrorStatus(error);
  const code = getErrorCode(error);
  return status === 401 || code === ERROR_CODES.UNAUTHORIZED;
};

/**
 * Check if error is forbidden (403)
 * @param {Error|Object} error - Error object
 * @returns {boolean} True if error is 403 Forbidden
 */
export const isForbiddenError = (error) => {
  const status = getErrorStatus(error);
  const code = getErrorCode(error);
  return status === 403 || code === ERROR_CODES.FORBIDDEN;
};

/**
 * Check if error is not found (404)
 * @param {Error|Object} error - Error object
 * @returns {boolean} True if error is 404 Not Found
 */
export const isNotFoundError = (error) => {
  const status = getErrorStatus(error);
  const code = getErrorCode(error);
  return status === 404 || code === ERROR_CODES.NOT_FOUND;
};

/**
 * Format error response for display
 * @param {Error|Object} error - Error object
 * @returns {Object} Formatted error object with message, code, and status
 */
export const formatError = (error) => {
  return {
    message: getErrorMessage(error),
    code: getErrorCode(error),
    status: getErrorStatus(error),
    isUnauthorized: isUnauthorizedError(error),
    isForbidden: isForbiddenError(error),
    isNotFound: isNotFoundError(error)
  };
};

