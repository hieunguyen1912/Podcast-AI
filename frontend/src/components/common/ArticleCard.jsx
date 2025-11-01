import React, { useState, useEffect, useRef } from 'react';
import { formatNewsTime } from '../../utils/formatTime';

/**
 * Reusable Article Card Component
 * @param {Object} props
 * @param {Object} props.article - Article data
 * @param {string} props.layout - 'horizontal' | 'vertical' | 'featured'
 * @param {number} props.index - Index for animation delay
 * @param {Function} props.onClick - Click handler
 * @param {string} props.className - Additional CSS classes
 */
function ArticleCard({ 
  article, 
  layout = 'vertical', 
  index = 0, 
  onClick, 
  className = '' 
}) {
  if (!article) return null;

  // State to track image load errors
  const [imageError, setImageError] = useState(false);
  const [imageLoading, setImageLoading] = useState(true);
  const imgRef = useRef(null);
  const timeoutRef = useRef(null);

  // Reset image state when article changes
  useEffect(() => {
    // Clear any existing timeout
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    
    setImageError(false);
    setImageLoading(true);
    
    // Fallback timeout: hide loading after 3 seconds even if events don't fire
    timeoutRef.current = setTimeout(() => {
      setImageLoading(false);
    }, 3000);
    
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, [article.id, article.imageUrl]);

  // Check if image is already loaded from cache after render
  useEffect(() => {
    // Use a small delay to ensure img element exists
    const checkImageLoaded = setTimeout(() => {
      if (imgRef.current) {
        // Image is already loaded from cache
        if (imgRef.current.complete && imgRef.current.naturalHeight !== 0) {
          if (timeoutRef.current) {
            clearTimeout(timeoutRef.current);
          }
          setImageLoading(false);
        }
      }
    }, 100);

    return () => clearTimeout(checkImageLoaded);
  }, [article.id, article.imageUrl]);

  // Default fallback image
  const getFallbackImage = () => {
    const defaultImages = {
      horizontal: "https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=200&h=150&fit=crop",
      featured: "https://images.unsplash.com/photo-1476242906366-d8eb5c1a4d1a?w=800&h=600&fit=crop",
      vertical: "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=300&h=200&fit=crop"
    };
    return defaultImages[layout] || defaultImages.vertical;
  };

  // Get image URL or fallback
  const getImageUrl = () => {
    if (imageError || !article.imageUrl || article.imageUrl.trim() === '') {
      return getFallbackImage();
    }
    return article.imageUrl;
  };

  // Handle image load error
  const handleImageError = () => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    setImageError(true);
    setImageLoading(false);
  };

  // Handle image load success
  const handleImageLoad = () => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    setImageLoading(false);
  };

  const handleClick = () => {
    if (onClick) {
      onClick(article.id);
    }
  };

  const getLayoutClasses = () => {
    switch (layout) {
      case 'horizontal':
        return 'flex';
      case 'featured':
        return 'lg:col-span-2';
      default:
        return '';
    }
  };

  const getImageClasses = () => {
    switch (layout) {
      case 'featured':
        return 'w-full h-80 object-cover';
      default:
        return 'w-full h-48 object-cover';
    }
  };

  const getContentClasses = () => {
    switch (layout) {
      case 'horizontal':
        return 'flex-1 p-4';
      case 'featured':
        return 'p-6';
      default:
        return 'p-4';
    }
  };

  const getTitleClasses = () => {
    switch (layout) {
      case 'featured':
        return 'text-3xl font-bold text-gray-900 mb-4';
      default:
        return 'font-semibold text-gray-900 mb-2 line-clamp-2';
    }
  };

  const renderAuthor = () => {
    if (layout === 'featured') {
      return (
        <div className="flex items-center mb-4">
          <div className="w-10 h-10 rounded-full mr-3 bg-gray-200 flex items-center justify-center">
            <span className="text-gray-600 font-semibold text-sm">
              {article.author?.charAt(0)?.toUpperCase() || 'A'}
            </span>
          </div>
          <div>
            <p className="font-semibold text-gray-900">{article.author || 'Anonymous'}</p>
            <p className="text-sm text-gray-500">Author</p>
          </div>
        </div>
      );
    }

    return (
      <div className="flex items-center text-sm text-gray-500 mb-2">
        <span>{article.author || 'Anonymous'}</span>
        <span className="mx-2">•</span>
        <span>{formatNewsTime(article.publishedAt)}</span>
      </div>
    );
  };

  const getCategoryName = () => {
    // Support both old format (categoryName) and new format (category.name)
    return article.category?.name || article.categoryName || 'News';
  };

  const renderMeta = () => {
    const categoryName = getCategoryName();
    
    if (layout === 'featured') {
      return (
        <div className="flex items-center text-sm text-gray-500 mb-4">
          <span className="text-orange-500 font-medium mr-4">{categoryName}</span>
          <span>{formatNewsTime(article.publishedAt)}</span>
          <span className="mx-2">•</span>
          <span>{article.viewCount || 0} views</span>
        </div>
      );
    }

    return (
      <div className="flex items-center text-sm text-gray-500">
        <span className="text-orange-500 font-medium mr-2">{categoryName}</span>
        <span>{article.viewCount || 0} views</span>
      </div>
    );
  };

  return (
    <article
      className={`article-card bg-white rounded-lg overflow-hidden shadow-md hover:shadow-lg transition-shadow cursor-pointer ${getLayoutClasses()} ${className}`}
      onClick={handleClick}
    >
      {layout === 'horizontal' ? (
        // Horizontal layout for sidebar trending news
        <>
          <div className={getContentClasses()}>
            {renderAuthor()}
            <h3 className={getTitleClasses()}>
              {article.title}
            </h3>
            {renderMeta()}
          </div>
          <div className="relative w-24 h-24 flex-shrink-0 overflow-hidden bg-gray-100">
            {imageLoading && !imageError && (
              <div className="absolute inset-0 flex items-center justify-center bg-gray-200 animate-pulse">
                <div className="w-8 h-8 border-2 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
              </div>
            )}
            <img 
              ref={imgRef}
              src={getImageUrl()}
              alt={article.title || 'Article image'}
              className={`w-full h-full object-cover rounded-r-lg ${imageLoading ? 'opacity-0' : 'opacity-100'} transition-opacity duration-300`}
              onError={handleImageError}
              onLoad={handleImageLoad}
              loading="lazy"
            />
          </div>
        </>
      ) : (
        // Vertical and featured layouts
        <>
          <div className="relative overflow-hidden bg-gray-100">
            {imageLoading && !imageError && (
              <div className={`absolute inset-0 flex items-center justify-center bg-gray-200 animate-pulse ${getImageClasses()}`}>
                <div className="w-12 h-12 border-2 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
              </div>
            )}
            <img 
              ref={imgRef}
              src={getImageUrl()}
              alt={article.title || 'Article image'}
              className={`${getImageClasses()} ${imageLoading ? 'opacity-0' : 'opacity-100'} transition-opacity duration-300`}
              onError={handleImageError}
              onLoad={handleImageLoad}
              loading="lazy"
            />
            {layout === 'featured' && (
              <div className="absolute top-4 left-4">
                <span className="bg-orange-500 text-white px-3 py-1 rounded-full text-sm font-medium">
                  {getCategoryName()}
                </span>
              </div>
            )}
          </div>
          
          <div className={getContentClasses()}>
            {renderAuthor()}
            <h3 className={getTitleClasses()}>
              {article.title}
            </h3>
            {renderMeta()}
          </div>
        </>
      )}
      </article>
  );
}

export default React.memo(ArticleCard);
