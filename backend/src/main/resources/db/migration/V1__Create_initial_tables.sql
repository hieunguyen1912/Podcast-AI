-- =====================================================
-- PodcastAI Database Migration - Initial Tables
-- Version: 1.0
-- Description: Create initial database schema for PodcastAI backend
-- =====================================================

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar_url VARCHAR(500),
    date_of_birth DATE,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN', 'MODERATOR')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'))
);

-- Create categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    slug VARCHAR(100) NOT NULL UNIQUE,
    icon_url VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    parent_category_id BIGINT REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0
);

-- Create tags table
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    color VARCHAR(7),
    usage_count INTEGER DEFAULT 0,
    is_trending BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0
);

-- Create podcasts table
CREATE TABLE podcasts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    cover_image_url VARCHAR(500),
    language VARCHAR(10),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    total_episodes INTEGER DEFAULT 0,
    total_duration_seconds BIGINT DEFAULT 0,
    total_plays BIGINT DEFAULT 0,
    total_likes BIGINT DEFAULT 0,
    total_shares BIGINT DEFAULT 0,
    public_url VARCHAR(500),
    rss_feed_url VARCHAR(500),
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_podcast_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'DELETED')),
    CONSTRAINT chk_podcast_visibility CHECK (visibility IN ('PUBLIC', 'PRIVATE', 'UNLISTED'))
);

-- Create episodes table
CREATE TABLE episodes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    episode_number INTEGER,
    season_number INTEGER,
    duration_seconds BIGINT,
    file_size_bytes BIGINT,
    audio_file_url VARCHAR(500) NOT NULL,
    transcript_url VARCHAR(500),
    summary VARCHAR(1000),
    keywords VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_explicit BOOLEAN NOT NULL DEFAULT FALSE,
    play_count BIGINT DEFAULT 0,
    like_count BIGINT DEFAULT 0,
    download_count BIGINT DEFAULT 0,
    published_at TIMESTAMP,
    transcription_status VARCHAR(50),
    summarization_status VARCHAR(50),
    translation_status VARCHAR(50),
    tts_status VARCHAR(50),
    podcast_id BIGINT NOT NULL REFERENCES podcasts(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_episode_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED', 'DELETED'))
);

-- Create playlists table
CREATE TABLE playlists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    cover_image_url VARCHAR(500),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    sort_order INTEGER DEFAULT 0,
    episode_count INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_playlist_visibility CHECK (visibility IN ('PUBLIC', 'PRIVATE', 'UNLISTED'))
);

-- Create playlist_episodes table (junction table with metadata)
CREATE TABLE playlist_episodes (
    id BIGSERIAL PRIMARY KEY,
    sort_order INTEGER DEFAULT 0,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    playlist_id BIGINT NOT NULL REFERENCES playlists(id) ON DELETE CASCADE,
    episode_id BIGINT NOT NULL REFERENCES episodes(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT uk_playlist_episode UNIQUE (playlist_id, episode_id)
);

-- Create user_favorites table
CREATE TABLE user_favorites (
    id BIGSERIAL PRIMARY KEY,
    favorite_type VARCHAR(20) NOT NULL,
    entity_id BIGINT NOT NULL,
    notes VARCHAR(500),
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(50) NOT NULL,
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT chk_favorite_type CHECK (favorite_type IN ('PODCAST', 'EPISODE', 'PLAYLIST', 'NEWS_SOURCE')),
    CONSTRAINT uk_user_favorite UNIQUE (user_id, favorite_type, entity_id)
);

-- Create podcast_categories junction table
CREATE TABLE podcast_categories (
    podcast_id BIGINT NOT NULL REFERENCES podcasts(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (podcast_id, category_id)
);

-- Create podcast_tags junction table
CREATE TABLE podcast_tags (
    podcast_id BIGINT NOT NULL REFERENCES podcasts(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (podcast_id, tag_id)
);

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Categories table indexes
CREATE INDEX idx_categories_name ON categories(name);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_parent_id ON categories(parent_category_id);
CREATE INDEX idx_categories_sort_order ON categories(sort_order);

-- Tags table indexes
CREATE INDEX idx_tags_name ON tags(name);
CREATE INDEX idx_tags_usage_count ON tags(usage_count);
CREATE INDEX idx_tags_is_trending ON tags(is_trending);

-- Podcasts table indexes
CREATE INDEX idx_podcasts_user_id ON podcasts(user_id);
CREATE INDEX idx_podcasts_status ON podcasts(status);
CREATE INDEX idx_podcasts_visibility ON podcasts(visibility);
CREATE INDEX idx_podcasts_is_featured ON podcasts(is_featured);
CREATE INDEX idx_podcasts_created_at ON podcasts(created_at);
CREATE INDEX idx_podcasts_total_plays ON podcasts(total_plays);
CREATE INDEX idx_podcasts_total_likes ON podcasts(total_likes);

-- Episodes table indexes
CREATE INDEX idx_episodes_podcast_id ON episodes(podcast_id);
CREATE INDEX idx_episodes_status ON episodes(status);
CREATE INDEX idx_episodes_published_at ON episodes(published_at);
CREATE INDEX idx_episodes_episode_number ON episodes(episode_number);
CREATE INDEX idx_episodes_play_count ON episodes(play_count);
CREATE INDEX idx_episodes_like_count ON episodes(like_count);

-- Playlists table indexes
CREATE INDEX idx_playlists_user_id ON playlists(user_id);
CREATE INDEX idx_playlists_visibility ON playlists(visibility);
CREATE INDEX idx_playlists_sort_order ON playlists(sort_order);

-- Playlist episodes table indexes
CREATE INDEX idx_playlist_episodes_playlist_id ON playlist_episodes(playlist_id);
CREATE INDEX idx_playlist_episodes_episode_id ON playlist_episodes(episode_id);
CREATE INDEX idx_playlist_episodes_sort_order ON playlist_episodes(sort_order);
CREATE INDEX idx_playlist_episodes_added_at ON playlist_episodes(added_at);

-- User favorites table indexes
CREATE INDEX idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX idx_user_favorites_type_entity ON user_favorites(favorite_type, entity_id);

-- Junction table indexes
CREATE INDEX idx_podcast_categories_podcast_id ON podcast_categories(podcast_id);
CREATE INDEX idx_podcast_categories_category_id ON podcast_categories(category_id);
CREATE INDEX idx_podcast_tags_podcast_id ON podcast_tags(podcast_id);
CREATE INDEX idx_podcast_tags_tag_id ON podcast_tags(tag_id);

-- =====================================================
-- TRIGGERS FOR AUTOMATIC UPDATES
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to all tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_tags_updated_at BEFORE UPDATE ON tags FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_podcasts_updated_at BEFORE UPDATE ON podcasts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_episodes_updated_at BEFORE UPDATE ON episodes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_playlists_updated_at BEFORE UPDATE ON playlists FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_playlist_episodes_updated_at BEFORE UPDATE ON playlist_episodes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_favorites_updated_at BEFORE UPDATE ON user_favorites FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- INITIAL DATA SEEDING
-- =====================================================

-- Insert default categories
-- INSERT INTO categories (name, description, slug, sort_order, created_by, created_at, updated_at, is_active, version) VALUES
-- ('Technology', 'Technology and innovation related content', 'technology', 1, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('News', 'Current events and news content', 'news', 2, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Business', 'Business and finance related content', 'business', 3, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Science', 'Science and research content', 'science', 4, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Entertainment', 'Entertainment and lifestyle content', 'entertainment', 5, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Education', 'Educational and learning content', 'education', 6, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Health', 'Health and wellness content', 'health', 7, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Sports', 'Sports and fitness content', 'sports', 8, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0);

-- Insert default tags
-- INSERT INTO tags (name, description, color, usage_count, is_trending, created_by, created_by, created_at, updated_at, is_active, version) VALUES
-- ('AI', 'Artificial Intelligence related content', '#FF6B6B', 0, true, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Machine Learning', 'Machine Learning and data science', '#4ECDC4', 0, true, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Programming', 'Programming and software development', '#45B7D1', 0, false, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Startup', 'Startup and entrepreneurship', '#96CEB4', 0, false, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Innovation', 'Innovation and technology trends', '#FFEAA7', 0, true, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Podcast', 'Podcast related content', '#DDA0DD', 0, false, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Interview', 'Interview and conversation content', '#98D8C8', 0, false, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0),
-- ('Tutorial', 'Tutorial and how-to content', '#F7DC6F', 0, false, 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true, 0);

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================

COMMENT ON TABLE users IS 'User accounts and profile information';
COMMENT ON TABLE categories IS 'Content categories for organizing podcasts';
COMMENT ON TABLE tags IS 'Content tags for labeling and searching';
COMMENT ON TABLE podcasts IS 'Podcast shows and their metadata';
COMMENT ON TABLE episodes IS 'Individual podcast episodes';
COMMENT ON TABLE playlists IS 'User-created playlists of episodes';
COMMENT ON TABLE playlist_episodes IS 'Junction table linking playlists to episodes with metadata';
COMMENT ON TABLE user_favorites IS 'User favorites for podcasts, episodes, and playlists';
COMMENT ON TABLE podcast_categories IS 'Many-to-many relationship between podcasts and categories';
COMMENT ON TABLE podcast_tags IS 'Many-to-many relationship between podcasts and tags';

COMMENT ON COLUMN users.role IS 'User role: USER, ADMIN, MODERATOR';
COMMENT ON COLUMN users.status IS 'User status: ACTIVE, INACTIVE, SUSPENDED, DELETED';
COMMENT ON COLUMN podcasts.status IS 'Podcast status: DRAFT, PUBLISHED, ARCHIVED, DELETED';
COMMENT ON COLUMN podcasts.visibility IS 'Podcast visibility: PUBLIC, PRIVATE, UNLISTED';
COMMENT ON COLUMN episodes.status IS 'Episode status: DRAFT, PUBLISHED, ARCHIVED, DELETED';
COMMENT ON COLUMN playlists.visibility IS 'Playlist visibility: PUBLIC, PRIVATE, UNLISTED';
COMMENT ON COLUMN user_favorites.favorite_type IS 'Type of favorited entity: PODCAST, EPISODE, PLAYLIST, NEWS_SOURCE';
COMMENT ON COLUMN playlist_episodes.sort_order IS 'Order of episodes within the playlist';
COMMENT ON COLUMN playlist_episodes.added_at IS 'Timestamp when episode was added to playlist';
