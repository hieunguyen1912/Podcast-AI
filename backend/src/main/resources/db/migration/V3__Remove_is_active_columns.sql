-- =====================================================
-- PodcastAI Database Migration - Remove is_active columns
-- Version: 3.0
-- Description: Remove is_active columns as entities use status enums for soft delete
-- =====================================================

-- Remove is_active column from users table (UserStatus handles soft delete)
ALTER TABLE users DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from categories table (no soft delete needed)
ALTER TABLE categories DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from tags table (no soft delete needed)
ALTER TABLE tags DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from podcasts table (PodcastStatus handles soft delete)
ALTER TABLE podcasts DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from episodes table (EpisodeStatus handles soft delete)
ALTER TABLE episodes DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from playlists table and add status column
ALTER TABLE playlists DROP COLUMN IF EXISTS is_active;
ALTER TABLE playlists ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE playlists ADD CONSTRAINT chk_playlist_status CHECK (status IN ('ACTIVE', 'ARCHIVED', 'DELETED'));

-- Remove is_active column from user_favorites table (no soft delete needed)
ALTER TABLE user_favorites DROP COLUMN IF EXISTS is_active;

-- Remove is_active column from playlist_episodes table (junction table, no soft delete needed)
ALTER TABLE playlist_episodes DROP COLUMN IF EXISTS is_active;

-- Add comments for documentation
COMMENT ON TABLE users IS 'User accounts with status-based soft delete using UserStatus enum';
COMMENT ON TABLE podcasts IS 'Podcast content with status-based soft delete using PodcastStatus enum';
COMMENT ON TABLE episodes IS 'Podcast episodes with status-based soft delete using EpisodeStatus enum';
COMMENT ON TABLE playlists IS 'User playlists with status-based soft delete';
COMMENT ON TABLE categories IS 'Content categories - reference data, no soft delete needed';
COMMENT ON TABLE tags IS 'Content tags - reference data, no soft delete needed';
COMMENT ON TABLE user_favorites IS 'User favorite items - no soft delete needed';
COMMENT ON TABLE playlist_episodes IS 'Playlist-episode junction table - no soft delete needed';
