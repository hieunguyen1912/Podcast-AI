# API Changes for Frontend - Audio TTS Feature

## 📋 Tổng quan thay đổi

### 1. **Quan hệ Entity**
- **Trước**: Một article có thể có nhiều audio files (OneToMany)
- **Sau**: Một article chỉ có **một** audio file duy nhất (OneToOne)

### 2. **Quyền truy cập**
- **Chỉ author của bài viết** mới được tạo TTS cho bài viết đó
- Nếu user không phải author, sẽ nhận lỗi: `AUDIO_ONLY_AUTHOR_CAN_GENERATE` (5008)

### 3. **Logic tự động**
- Khi tạo TTS mới cho article đã có audio, hệ thống sẽ **tự động xóa audio cũ** trước khi tạo mới
- Đảm bảo mỗi article luôn chỉ có một audio file

---

## 🔄 API Endpoints Changes

### ✅ **Endpoints đã thay đổi**

#### 1. **Lấy audio file của article** (CHANGED)
```http
GET /api/v1/articles/{articleId}/audio
```

**Thay đổi:**
- **Trước**: Trả về `List<AudioFileDto>` (có thể có nhiều audio)
- **Sau**: Trả về `AudioFileDto | null` (chỉ có một audio hoặc null)

**Response:**
```json
{
  "success": true,
  "message": "Audio file retrieved successfully",
  "data": {
    "id": 1,
    "fileName": "article-title-voice-name-timestamp.wav",
    "status": "COMPLETED",
    "gcsUri": "gs://bucket/path/to/audio.wav",
    "createdAt": "2024-01-01T00:00:00",
    // ... other fields
  }
}
```

**Hoặc nếu không có audio:**
```json
{
  "success": true,
  "message": "No audio file found for this article",
  "data": null
}
```

**Security:** ✅ **PUBLIC** - Không cần authentication

---

#### 2. **Lấy danh sách audio files của user** (MOVED)
```http
GET /api/v1/articles/my-audio
```

**Thay đổi:**
- **Trước**: `GET /api/v1/user/audio` (trong UserController)
- **Sau**: `GET /api/v1/articles/my-audio` (trong ArticleController)

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 10)
- `sortBy` (default: "createdAt")
- `sortDirection` (default: "desc")

**Response:**
```json
{
  "success": true,
  "message": "Audio files retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "fileName": "article-title-voice-name-timestamp.wav",
        "status": "COMPLETED",
        "newsArticle": {
          "id": 123,
          "title": "Article Title"
        },
        // ... other fields
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

**Security:** 🔒 **REQUIRES AUTH** - Cần permission `PERMISSION_ARTICLE_TTS`

---

### ✅ **Endpoints không thay đổi (nhưng có cập nhật logic)**

#### 3. **Tạo TTS từ full article**
```http
POST /api/v1/articles/{articleId}/generate-audio
```

**Thay đổi logic:**
- ✅ Kiểm tra user hiện tại có phải là author không
- ✅ Nếu article đã có audio, tự động xóa audio cũ trước khi tạo mới
- ❌ Nếu user không phải author → Error 403: `AUDIO_ONLY_AUTHOR_CAN_GENERATE`

**Request Body (optional):**
```json
{
  "customVoiceSettings": {
    "voiceName": "en-US-Neural2-F",
    "languageCode": "en-US",
    "speakingRate": 1.0,
    "pitch": 0.0,
    "volumeGainDb": 0.0
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Audio generation started. Use check-status endpoint to track progress.",
  "data": {
    "id": 1,
    "status": "GENERATING_AUDIO",
    "operationName": "operations/xxx",
    // ... other fields
  }
}
```

**Security:** 🔒 **REQUIRES AUTH** - Cần permission `PERMISSION_ARTICLE_TTS`

---

#### 4. **Tạo TTS từ summary**
```http
POST /api/v1/articles/{id}/generate-audio-from-summary
```

**Thay đổi logic:**
- ✅ Kiểm tra user hiện tại có phải là author không
- ✅ Nếu article đã có audio, tự động xóa audio cũ trước khi tạo mới
- ❌ Nếu user không phải author → Error 403: `AUDIO_ONLY_AUTHOR_CAN_GENERATE`

**Request Body:** Tương tự như generate-audio

**Security:** 🔒 **REQUIRES AUTH** - Cần permission `PERMISSION_ARTICLE_TTS`

---

### ✅ **Endpoints không thay đổi**

#### 5. **Kiểm tra trạng thái generation**
```http
GET /api/v1/articles/audio/{audioFileId}/check-status
```

**Security:** 🔒 **REQUIRES AUTH** - Chỉ owner của audio file mới được check

---

#### 6. **Stream audio**
```http
GET /api/v1/articles/audio/{audioFileId}/stream
```

**Security:** ✅ **PUBLIC** - Không cần authentication

**Response:** Audio stream (audio/wav)

---

#### 7. **Download audio**
```http
GET /api/v1/articles/audio/{audioFileId}/download
```

**Security:** ✅ **PUBLIC** - Không cần authentication

**Response:** Audio file download (audio/wav)

---

#### 8. **Xóa audio file**
```http
DELETE /api/v1/articles/audio/{audioFileId}
```

**Security:** 🔒 **REQUIRES AUTH** - Chỉ owner của audio file mới được xóa

---

## 🚨 Error Codes mới

### `AUDIO_ONLY_AUTHOR_CAN_GENERATE` (5008)
```json
{
  "success": false,
  "error": {
    "code": 5008,
    "message": "Only the article author can generate TTS audio",
    "status": "FORBIDDEN"
  }
}
```

**Khi nào xảy ra:**
- User không phải author cố gắng tạo TTS cho article

**Cách xử lý:**
- Hiển thị thông báo lỗi cho user
- Ẩn nút "Generate Audio" nếu user không phải author

---

## 📝 Frontend Implementation Guide

### 1. **Kiểm tra user có phải author không**

```javascript
// Khi hiển thị nút "Generate Audio"
const isAuthor = article.author.id === currentUser.id;
const hasPermission = userPermissions.includes('PERMISSION_ARTICLE_TTS');

if (isAuthor && hasPermission) {
  // Hiển thị nút Generate Audio
}
```

### 2. **Lấy audio file của article (PUBLIC)**

```javascript
// Không cần authentication
const getArticleAudio = async (articleId) => {
  const response = await fetch(`/api/v1/articles/${articleId}/audio`);
  const data = await response.json();
  
  if (data.data) {
    // Có audio file
    return data.data;
  } else {
    // Không có audio file
    return null;
  }
};
```

### 3. **Tạo TTS (chỉ author)**

```javascript
const generateAudio = async (articleId, customVoiceSettings = null) => {
  try {
    const response = await fetch(`/api/v1/articles/${articleId}/generate-audio`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        customVoiceSettings: customVoiceSettings
      })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Audio generation started
      return data.data; // AudioFileDto với status = GENERATING_AUDIO
    } else if (data.error?.code === 5008) {
      // User không phải author
      throw new Error('Only the article author can generate TTS audio');
    } else {
      throw new Error(data.error?.message || 'Failed to generate audio');
    }
  } catch (error) {
    console.error('Error generating audio:', error);
    throw error;
  }
};
```

### 4. **Lấy danh sách audio files của user (MOVED)**

```javascript
// Endpoint đã chuyển từ /api/v1/user/audio sang /api/v1/articles/my-audio
const getMyAudioFiles = async (page = 0, size = 10) => {
  const response = await fetch(
    `/api/v1/articles/my-audio?page=${page}&size=${size}&sortBy=createdAt&sortDirection=desc`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );
  
  const data = await response.json();
  return data.data; // PaginatedResponse<AudioFileDto>
};
```

### 5. **Stream audio (PUBLIC)**

```javascript
// Không cần authentication
const audioUrl = `/api/v1/articles/audio/${audioFileId}/stream`;

// Sử dụng trong HTML audio tag
<audio controls src={audioUrl} />
```

### 6. **Download audio (PUBLIC)**

```javascript
// Không cần authentication
const downloadAudio = (audioFileId) => {
  window.open(`/api/v1/articles/audio/${audioFileId}/download`, '_blank');
};
```

---

## 🔄 Migration Guide

### Cần cập nhật trong Frontend:

1. **Thay đổi endpoint lấy audio của article:**
   ```javascript
   // OLD: Trả về array
   const audioFiles = await getArticleAudio(articleId);
   const audioFile = audioFiles[0]; // Lấy phần tử đầu tiên
   
   // NEW: Trả về object hoặc null
   const audioFile = await getArticleAudio(articleId);
   if (audioFile) {
     // Có audio
   }
   ```

2. **Thay đổi endpoint lấy danh sách audio của user:**
   ```javascript
   // OLD
   GET /api/v1/user/audio
   
   // NEW
   GET /api/v1/articles/my-audio
   ```

3. **Thêm validation kiểm tra author:**
   ```javascript
   // Trước khi cho phép generate audio
   if (article.author.id !== currentUser.id) {
     // Không cho phép generate
     return;
   }
   ```

4. **Xử lý error mới:**
   ```javascript
   try {
     await generateAudio(articleId);
   } catch (error) {
     if (error.code === 5008) {
       // Hiển thị thông báo: "Chỉ tác giả mới có thể tạo TTS"
     }
   }
   ```

---

## ✅ Summary

### Thay đổi chính:
1. ✅ Mỗi article chỉ có **một** audio file
2. ✅ Chỉ **author** mới được tạo TTS
3. ✅ Endpoint lấy audio trả về `object | null` thay vì `array`
4. ✅ Endpoint `/api/v1/user/audio` → `/api/v1/articles/my-audio`
5. ✅ Audio endpoints (stream/download) là **PUBLIC**

### Không thay đổi:
- Logic tạo TTS (vẫn async, cần check status)
- Format response của các endpoint khác
- Error handling (trừ error code mới 5008)

