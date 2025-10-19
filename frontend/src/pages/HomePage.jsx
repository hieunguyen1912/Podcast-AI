/**
 * Home page component - News Website with TTS
 * Modern news platform with AI-powered text-to-speech features
 */

import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import '../styles/pages/HomePage.css';

function HomePage() {
  const { isAuthenticated, user } = useAuth();
  const [isLoaded, setIsLoaded] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);

  useEffect(() => {
    setIsLoaded(true);
  }, []);

  const handleTTS = () => {
    setIsPlaying(!isPlaying);
    // TODO: Implement TTS functionality
  };

  return (
    <div className="home-page">
      {/* Header Section */}
      <section className="news-header">
        <div className="container">
          <div className="header-content">
            <div className="logo-section">
              <h1 className="text-display-2 logo">NewsAI</h1>
              <p className="text-caption tagline">Tin tức thông minh với AI</p>
            </div>
            
            <div className="header-actions">
              {isAuthenticated ? (
                <div className="user-info">
                  <span className="text-caption">Xin chào, {user?.name || 'User'}!</span>
                  <a href="/dashboard" className="btn btn-sm btn-primary">
                    Dashboard
                  </a>
                </div>
              ) : (
                <div className="auth-buttons">
                  <a href="/login" className="btn btn-sm btn-outline">
                    Đăng nhập
                  </a>
                  <a href="/register" className="btn btn-sm btn-primary">
                    Đăng ký
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Main News Feed Layout */}
      <div className="news-feed-container">
        <div className="container">
          <div className="news-feed-layout">
            {/* Sidebar */}
            <aside className="news-sidebar">
              <div className="sidebar-section">
                <h3 className="text-heading-3 sidebar-title">Chủ đề</h3>
                <div className="category-list">
                  {[
                    { name: 'Thế giới', icon: '🌍', count: 24 },
                    { name: 'Kinh tế', icon: '💼', count: 18 },
                    { name: 'Công nghệ', icon: '💻', count: 32 },
                    { name: 'Thể thao', icon: '⚽', count: 15 },
                    { name: 'Giải trí', icon: '🎭', count: 21 },
                    { name: 'Sức khỏe', icon: '🏥', count: 12 },
                    { name: 'Giáo dục', icon: '📚', count: 8 },
                    { name: 'Chính trị', icon: '🏛️', count: 19 }
                  ].map((category) => (
                    <a
                      key={category.name}
                      href={`/category/${category.name.toLowerCase()}`}
                      className="category-item"
                    >
                      <span className="category-icon">{category.icon}</span>
                      <span className="category-name">{category.name}</span>
                      <span className="category-count">{category.count}</span>
                    </a>
                  ))}
                </div>
              </div>

              <div className="sidebar-section">
                <h3 className="text-heading-3 sidebar-title">Tin nổi bật</h3>
                <div className="trending-list">
                  {[
                    { title: 'AI phát triển mạnh mẽ', time: '2h' },
                    { title: 'Kinh tế Việt Nam tăng trưởng', time: '4h' },
                    { title: 'Bóng đá World Cup 2026', time: '6h' }
                  ].map((trend, index) => (
                    <div key={index} className="trending-item">
                      <span className="trending-number">{index + 1}</span>
                      <div className="trending-content">
                        <p className="trending-title">{trend.title}</p>
                        <span className="trending-time">{trend.time} trước</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </aside>

            {/* Main News Feed */}
            <main className="news-main">
              {/* Filter Bar */}
              <div className="news-filter-bar">
                <div className="filter-tabs">
                  <button className="filter-tab active">Mới nhất</button>
                  <button className="filter-tab">Nổi bật</button>
                  <button className="filter-tab">Theo dõi</button>
                </div>
                <div className="filter-actions">
                  <button className="btn btn-sm btn-outline">
                    🔍 Tìm kiếm
                  </button>
                </div>
              </div>

              {/* News Feed */}
              <div className="news-feed">
                {[
                  {
                    id: 1,
                    title: 'Công nghệ AI phát triển mạnh mẽ trong năm 2024',
                    summary: 'Các công ty công nghệ lớn đang đầu tư mạnh vào AI, mở ra nhiều cơ hội mới cho ngành công nghiệp. OpenAI, Google, Microsoft đều công bố những sản phẩm AI tiên tiến...',
                    category: 'Công nghệ',
                    time: '2 giờ trước',
                    author: 'Nguyễn Văn A',
                    image: 'https://via.placeholder.com/400x250?text=AI+News',
                    isBreaking: true,
                    views: 1250,
                    likes: 89
                  },
                  {
                    id: 2,
                    title: 'Kinh tế Việt Nam tăng trưởng tích cực quý 4',
                    summary: 'GDP tăng trưởng 6.8% so với cùng kỳ năm trước, vượt kỳ vọng của các chuyên gia. Ngành xuất khẩu và tiêu dùng nội địa đều có dấu hiệu phục hồi mạnh mẽ...',
                    category: 'Kinh tế',
                    time: '4 giờ trước',
                    author: 'Trần Thị B',
                    image: 'https://via.placeholder.com/400x250?text=Economy',
                    isBreaking: false,
                    views: 890,
                    likes: 45
                  },
                  {
                    id: 3,
                    title: 'Bóng đá: Việt Nam chuẩn bị cho vòng loại World Cup',
                    summary: 'Đội tuyển Việt Nam đang tích cực tập luyện cho trận đấu quan trọng với Thái Lan. HLV Troussier đã công bố danh sách 23 cầu thủ tham dự...',
                    category: 'Thể thao',
                    time: '6 giờ trước',
                    author: 'Lê Văn C',
                    image: 'https://via.placeholder.com/400x250?text=Football',
                    isBreaking: false,
                    views: 2100,
                    likes: 156
                  },
                  {
                    id: 4,
                    title: 'Giáo dục: Chương trình học mới áp dụng từ năm 2025',
                    summary: 'Bộ Giáo dục và Đào tạo vừa công bố chương trình giáo dục phổ thông mới sẽ được áp dụng từ năm học 2025-2026. Chương trình tập trung vào phát triển kỹ năng...',
                    category: 'Giáo dục',
                    time: '8 giờ trước',
                    author: 'Phạm Thị D',
                    image: 'https://via.placeholder.com/400x250?text=Education',
                    isBreaking: false,
                    views: 567,
                    likes: 23
                  },
                  {
                    id: 5,
                    title: 'Sức khỏe: Phát hiện mới về điều trị ung thư',
                    summary: 'Các nhà khoa học Việt Nam đã có bước đột phá trong nghiên cứu điều trị ung thư bằng liệu pháp miễn dịch. Thử nghiệm lâm sàng cho thấy tỷ lệ thành công cao...',
                    category: 'Sức khỏe',
                    time: '10 giờ trước',
                    author: 'BS. Hoàng Văn E',
                    image: 'https://via.placeholder.com/400x250?text=Health',
                    isBreaking: true,
                    views: 3200,
                    likes: 234
                  }
                ].map((article) => (
                  <article key={article.id} className="news-article">
                    <div className="article-image">
                      <img src={article.image} alt={article.title} />
                      {article.isBreaking && <div className="breaking-badge">Tin nóng</div>}
                      <div className="article-category">{article.category}</div>
                    </div>
                    
                    <div className="article-content">
                      <div className="article-header">
                        <h2 className="article-title">{article.title}</h2>
                        <div className="article-actions">
                          <button className="action-btn" title="Lưu">
                            <span>💾</span>
                          </button>
                          <button className="action-btn" title="Chia sẻ">
                            <span>📤</span>
                          </button>
                        </div>
                      </div>
                      
                      <p className="article-summary">{article.summary}</p>
                      
                      <div className="article-meta">
                        <div className="meta-left">
                          <span className="author">By {article.author}</span>
                          <span className="time">{article.time}</span>
                        </div>
                        <div className="meta-right">
                          <span className="views">👁️ {article.views}</span>
                          <span className="likes">❤️ {article.likes}</span>
                        </div>
                      </div>
                      
                      <div className="article-actions-bottom">
                        <button className="btn btn-sm btn-outline tts-btn" onClick={handleTTS}>
                          🔊 Nghe tin tức
                        </button>
                        <button className="btn btn-sm btn-primary">
                          Đọc tiếp
                        </button>
                      </div>
                    </div>
                  </article>
                ))}
              </div>

              {/* Load More */}
              <div className="load-more-section">
                <button className="btn btn-outline load-more-btn">
                  Tải thêm tin tức
                </button>
              </div>
            </main>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
