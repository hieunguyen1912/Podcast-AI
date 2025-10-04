-- Create audio_generations table (fixed version)
CREATE TABLE audio_generations (
    id BIGSERIAL PRIMARY KEY,
    content_source VARCHAR(50) NOT NULL,
    search_query VARCHAR(500),
    input_text TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    source_content TEXT,
    processed_content TEXT,
    -- Audio file info moved to episodes table
    language_code VARCHAR(10),
    voice_name VARCHAR(100),
    speaking_rate DOUBLE PRECISION,
    pitch DOUBLE PRECISION,
    volume_gain_db DOUBLE PRECISION,
    audio_encoding VARCHAR(50),
    is_summarized BOOLEAN,
    is_translated BOOLEAN,
    target_language VARCHAR(10),
    -- Metadata moved to episodes table
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    CONSTRAINT fk_audio_generations_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Add audio_generation_id column to episodes table
ALTER TABLE episodes 
ADD COLUMN audio_generation_id BIGINT;

-- Add category column to episodes table
ALTER TABLE episodes 
ADD COLUMN category VARCHAR(100);

-- Add foreign key constraint for episodes -> audio_generations
ALTER TABLE episodes 
ADD CONSTRAINT fk_episodes_audio_generation 
FOREIGN KEY (audio_generation_id) 
REFERENCES audio_generations(id) ON DELETE CASCADE;

-- Create indexes for performance
CREATE INDEX idx_audio_generations_user_id ON audio_generations(user_id);
CREATE INDEX idx_audio_generations_status ON audio_generations(status);
CREATE INDEX idx_audio_generations_content_source ON audio_generations(content_source);
CREATE INDEX idx_audio_generations_created_at ON audio_generations(created_at);
CREATE INDEX idx_episodes_audio_generation_id ON episodes(audio_generation_id);

-- Add check constraints
ALTER TABLE audio_generations 
ADD CONSTRAINT chk_audio_generations_content_source 
CHECK (content_source IN ('NEWS_API', 'TEXT_INPUT', 'URL_INPUT', 'FILE_UPLOAD'));

ALTER TABLE audio_generations 
ADD CONSTRAINT chk_audio_generations_status 
CHECK (status IN ('PENDING', 'FETCHING_NEWS', 'PROCESSING_CONTENT', 'GENERATING_AUDIO', 'COMPLETED', 'FAILED'));

ALTER TABLE audio_generations 
ADD CONSTRAINT chk_audio_generations_speaking_rate 
CHECK (speaking_rate >= 0.25 AND speaking_rate <= 4.0);

ALTER TABLE audio_generations 
ADD CONSTRAINT chk_audio_generations_pitch 
CHECK (pitch >= -20.0 AND pitch <= 20.0);

ALTER TABLE audio_generations 
ADD CONSTRAINT chk_audio_generations_volume_gain 
CHECK (volume_gain_db >= -96.0 AND volume_gain_db <= 16.0);

-- Add comments for documentation
COMMENT ON TABLE audio_generations IS 'Stores audio generation requests and processing information';
COMMENT ON COLUMN audio_generations.content_source IS 'Source of content: NEWS_API, TEXT_INPUT, URL_INPUT, FILE_UPLOAD';
COMMENT ON COLUMN audio_generations.search_query IS 'Search query for NEWS_API source';
COMMENT ON COLUMN audio_generations.input_text IS 'Direct text input for TEXT_INPUT source';
COMMENT ON COLUMN audio_generations.status IS 'Processing status: PENDING, FETCHING_NEWS, PROCESSING_CONTENT, GENERATING_AUDIO, COMPLETED, FAILED';
COMMENT ON COLUMN audio_generations.source_content IS 'Original content from source';
COMMENT ON COLUMN audio_generations.processed_content IS 'Content after processing (summarization, translation)';
COMMENT ON COLUMN audio_generations.speaking_rate IS 'Speech rate (0.25 to 4.0)';
COMMENT ON COLUMN audio_generations.pitch IS 'Voice pitch (-20.0 to 20.0 semitones)';
COMMENT ON COLUMN audio_generations.volume_gain_db IS 'Volume gain in dB (-96.0 to 16.0)';
COMMENT ON COLUMN audio_generations.is_summarized IS 'Whether content was summarized';
COMMENT ON COLUMN audio_generations.is_translated IS 'Whether content was translated';
COMMENT ON COLUMN audio_generations.target_language IS 'Target language for translation';
COMMENT ON COLUMN audio_generations.retry_count IS 'Number of retry attempts for failed processing';
