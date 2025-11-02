-- Insert gnewsapi news source
INSERT INTO news_sources (
    name, 
    display_name, 
    url, 
    type, 
    api_key, 
    priority, 
    update_interval_minutes, 
    max_articles_per_fetch, 
    language, 
    country, 
    is_active, 
    last_fetch_at, 
    last_success_at, 
    consecutive_failures, 
    max_failures,
    created_at,
    updated_at
) VALUES (
    'gnewsapi',
    'GNews API',
    'https://gnews.io/api/v4',
    'GNEWS_API',
    'your-gnewsapi-api-key-here', -- Replace with actual API key
    2,
    60,
    15,
    'en',
    'us',
    true,
    NULL,
    NULL,
    0,
    3,
    NOW(),
    NOW()
);

