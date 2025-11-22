# Frontend API Documentation - Podcast AI Backend

## Mục lục
1. [Authentication](#authentication)
2. [Hệ thống phân quyền (RBAC)](#hệ-thống-phân-quyền-rbac)
3. [Public Endpoints](#public-endpoints)
4. [Protected Endpoints](#protected-endpoints)
5. [Error Handling](#error-handling)
6. [Best Practices](#best-practices)

---

## Authentication

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication Method
- **JWT Bearer Token**
- Token được gửi trong header: `Authorization: Bearer <token>`

### Login Flow
```javascript
// 1. Login
POST /api/v1/auth/login
Body: { "email": "user@example.com", "password": "password" }
Response: { "data": { "accessToken": "...", "refreshToken": "..." } }

// 2. Lưu tokens
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('refreshToken', response.data.refreshToken);

// 3. Gửi token trong các request sau
headers: {
  'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
}
```

### Refresh Token
```javascript
// Khi accessToken hết hạn
POST /api/v1/auth/refresh
Body: { "refreshToken": "..." }
Response: { "data": { "accessToken": "...", "refreshToken": "..." } }
```

---

## Hệ thống phân quyền (RBAC)

### Roles

| Role | Mô tả | Permissions |
|------|-------|-------------|
| **USER** | Người đọc | Chỉ đọc tin tức |
| **AUTHOR** | Tác giả | Tạo, sửa, xóa bài viết của mình |
| **MODERATOR** | Biên tập viên | Duyệt bài, quản lý categories, xem stats |
| **ADMIN** | Quản trị viên | Toàn quyền |

### Permissions

#### Article Permissions
- `PERMISSION_ARTICLE_CREATE` - Tạo bài viết
- `PERMISSION_ARTICLE_READ` - Đọc bài viết
- `PERMISSION_ARTICLE_UPDATE` - Sửa bài viết
- `PERMISSION_ARTICLE_DELETE` - Xóa bài viết
- `PERMISSION_ARTICLE_APPROVE` - Duyệt bài viết
- `PERMISSION_ARTICLE_SUMMARY` - Generate summary
- `PERMISSION_ARTICLE_TTS` - Generate TTS audio

#### User Permissions
- `PERMISSION_USER_CREATE` - Tạo user
- `PERMISSION_USER_READ` - Đọc thông tin user
- `PERMISSION_USER_UPDATE` - Sửa user
- `PERMISSION_USER_DELETE` - Xóa user
- `PERMISSION_USER_ASSIGN_ROLE` - Gán role cho user

#### Role Permissions
- `PERMISSION_ROLE_CREATE` - Tạo role
- `PERMISSION_ROLE_READ` - Đọc roles
- `PERMISSION_ROLE_UPDATE` - Sửa role
- `PERMISSION_ROLE_DELETE` - Xóa role
- `PERMISSION_ROLE_ASSIGN_PERMISSION` - Gán permissions cho role

#### Category Permissions
- `PERMISSION_CATEGORY_CREATE` - Tạo category
- `PERMISSION_CATEGORY_UPDATE` - Sửa category
- `PERMISSION_CATEGORY_DELETE` - Xóa category

#### Other Permissions
- `PERMISSION_PERMISSION_READ` - Xem permissions
- `PERMISSION_STATS_READ` - Xem thống kê

### Mapping Roles → Permissions

#### USER (1 permission)
- `ARTICLE_READ` - Chỉ đọc tin tức

#### AUTHOR (6 permissions)
- `ARTICLE_CREATE` - Tạo bài viết
- `ARTICLE_READ` - Đọc bài viết
- `ARTICLE_UPDATE` - Sửa bài của mình
- `ARTICLE_DELETE` - Xóa bài của mình
- `ARTICLE_SUMMARY` - Generate summary
- `ARTICLE_TTS` - Generate TTS audio

#### MODERATOR (8 permissions)
- `ARTICLE_READ` - Xem tất cả bài viết
- `ARTICLE_UPDATE` - Sửa bài viết
- `ARTICLE_APPROVE` - Duyệt bài viết
- `ARTICLE_DELETE` - Xóa bài viết
- `CATEGORY_CREATE` - Tạo category
- `CATEGORY_UPDATE` - Sửa category
- `CATEGORY_DELETE` - Xóa category
- `STATS_READ` - Xem thống kê

#### ADMIN (22 permissions)
- Tất cả permissions

---

## Public Endpoints

Các endpoints này **KHÔNG CẦN** authentication, guest users có thể truy cập.

### Auth
```
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/revoke
```

### News (Public)
```
GET /api/v1/news/** (tất cả endpoints)
GET /api/v1/news/search
GET /api/v1/news/latest
GET /api/v1/news/trending
GET /api/v1/news/featured
GET /api/v1/news/{id}
GET /api/v1/news/category/{categoryId}
```

### Categories (GET only)
```
GET /api/v1/categories
GET /api/v1/categories/all
GET /api/v1/categories/{id}
GET /api/v1/categories/slug/{slug}
GET /api/v1/categories/tree
GET /api/v1/categories/root
GET /api/v1/categories/{id}/children
GET /api/v1/categories/{id}/breadcrumb
```

### Articles (GET only - Published articles)
```
GET /api/v1/articles/{id}
GET /api/v1/articles/{id}/category
```

### Comments (GET only)
```
GET /api/v1/comments/articles/{articleId}
GET /api/v1/comments/{commentId}/replies
```

### Audio (GET only)
```
GET /api/v1/articles/{articleId}/audio
GET /api/v1/articles/audio/{audioFileId}/stream
GET /api/v1/articles/audio/{audioFileId}/download
```

### Images
```
GET /api/v1/images/**
```

---

## Protected Endpoints

Các endpoints này **CẦN** authentication và permissions tương ứng.

### Article Management (AUTHOR)

#### Tạo bài viết
```javascript
POST /api/v1/articles
Content-Type: application/json
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_CREATE

// JSON
Body: {
  "title": "...",
  "content": "...",
  "categoryId": 1
}

// Multipart (với ảnh)
POST /api/v1/articles
Content-Type: multipart/form-data
Body: {
  data: { ... },
  featuredImage: File,
  contentImages: File[]
}
```

#### Xem bài viết của mình
```javascript
GET /api/v1/articles/my-drafts
GET /api/v1/articles/my-submitted
GET /api/v1/articles/my-approved
GET /api/v1/articles/my-rejected
GET /api/v1/articles/my-all
Required Permission: PERMISSION_ARTICLE_READ
```

#### Sửa bài viết
```javascript
PUT /api/v1/articles/{id}
Content-Type: application/json
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_UPDATE
```

#### Xóa bài viết
```javascript
DELETE /api/v1/articles/{id}
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_DELETE
```

#### Submit bài viết để duyệt
```javascript
POST /api/v1/articles/{id}/submit
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_UPDATE
```

#### Generate Summary
```javascript
POST /api/v1/articles/generate-summary
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_SUMMARY
Body: {
  "content": "...",
  "maxLength": 200
}
```

#### Generate TTS Audio
```javascript
POST /api/v1/articles/{id}/generate-audio
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_TTS
Body: {
  "ttsConfigId": 1  // optional
}
```

#### Xem audio files của mình
```javascript
GET /api/v1/articles/my-audio?page=0&size=10
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_TTS
```

### Article Management (MODERATOR/ADMIN)

#### Xem tất cả bài viết
```javascript
GET /api/v1/admin/articles/all
GET /api/v1/admin/articles/pending-review
GET /api/v1/admin/articles/approved
GET /api/v1/admin/articles/rejected
GET /api/v1/admin/articles/drafts
Required Permission: PERMISSION_ARTICLE_READ
```

#### Duyệt/Từ chối bài viết
```javascript
POST /api/v1/admin/articles/{id}/approve
POST /api/v1/admin/articles/{id}/reject
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_APPROVE
```

#### Sửa/Xóa bài viết (Admin)
```javascript
PUT /api/v1/admin/articles/{id}
DELETE /api/v1/admin/articles/{id}
Authorization: Bearer <token>
Required Permission: PERMISSION_ARTICLE_UPDATE / PERMISSION_ARTICLE_DELETE
```

### Category Management (MODERATOR/ADMIN)

```javascript
POST /api/v1/categories
PUT /api/v1/categories/{id}
DELETE /api/v1/categories/{id}
Authorization: Bearer <token>
Required Permissions: 
  - PERMISSION_CATEGORY_CREATE
  - PERMISSION_CATEGORY_UPDATE
  - PERMISSION_CATEGORY_DELETE
```

### Comment Management

#### Tạo comment
```javascript
POST /api/v1/comments/articles/{articleId}
Authorization: Bearer <token>
Body: {
  "content": "...",
  "parentId": null  // optional, for replies
}
```

#### Sửa/Xóa comment
```javascript
PUT /api/v1/comments/articles/{articleId}/comments/{commentId}
DELETE /api/v1/comments/articles/{articleId}/comments/{commentId}
Authorization: Bearer <token>
// Chỉ owner mới có thể sửa/xóa
```

### User Management (ADMIN)

```javascript
GET /api/v1/admin/users
GET /api/v1/admin/users/{id}
PUT /api/v1/admin/users/{id}
DELETE /api/v1/admin/users/{id}
Authorization: Bearer <token>
Required Permissions:
  - PERMISSION_USER_READ
  - PERMISSION_USER_UPDATE
  - PERMISSION_USER_DELETE
```

### Role & Permission Management (ADMIN)

```javascript
// Roles
GET /api/v1/roles
POST /api/v1/roles
PUT /api/v1/roles/{id}
DELETE /api/v1/roles/{id}
Required Permission: PERMISSION_ROLE_*

// Permissions
GET /api/v1/permissions
Required Permission: PERMISSION_PERMISSION_READ

// Assign permissions to role
POST /api/v1/roles/{roleId}/permissions
DELETE /api/v1/roles/{roleId}/permissions/{permissionId}
Required Permission: PERMISSION_ROLE_ASSIGN_PERMISSION

// Assign role to user
POST /api/v1/users/{userId}/roles
DELETE /api/v1/users/{userId}/roles/{roleId}
Required Permission: PERMISSION_USER_ASSIGN_ROLE
```

### Statistics (MODERATOR/ADMIN)

```javascript
GET /api/v1/admin/stats/dashboard
GET /api/v1/admin/stats/articles
GET /api/v1/admin/stats/users
GET /api/v1/admin/stats/articles/pending-review
GET /api/v1/admin/stats/articles/trends
GET /api/v1/admin/stats/top-authors
GET /api/v1/admin/stats/engagement
Authorization: Bearer <token>
Required Permission: PERMISSION_STATS_READ
```

### User Profile

```javascript
GET /api/v1/user/me
PUT /api/v1/user/me
PUT /api/v1/user/me/password
POST /api/v1/user/me/avatar
DELETE /api/v1/user/me
Authorization: Bearer <token>
// Cần authentication (hasRole('USER'))
```

### User Favorites

```javascript
GET /api/v1/user/me/favorites
POST /api/v1/user/me/favorites/{articleId}
DELETE /api/v1/user/me/favorites/{favoriteId}
Authorization: Bearer <token>
// Cần authentication (hasRole('USER'))
```

### Notifications

```javascript
GET /api/v1/user/me/notifications
GET /api/v1/user/me/notifications/unread-count
PUT /api/v1/user/me/notifications/{id}/read
PUT /api/v1/user/me/notifications/read-all
DELETE /api/v1/user/me/notifications/{id}
Authorization: Bearer <token>
// Cần authentication (hasRole('USER'))
```

---

## Error Handling

### HTTP Status Codes

| Code | Mô tả |
|------|-------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized (chưa đăng nhập hoặc token hết hạn) |
| 403 | Forbidden (không có permission) |
| 404 | Not Found |
| 500 | Internal Server Error |

### Error Response Format

```javascript
{
  "code": 4001,
  "status": 401,
  "message": "Unauthorized",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Common Error Codes

| Code | Mô tả |
|------|-------|
| 2000 | Success |
| 4001 | Unauthorized |
| 4003 | Forbidden (không có permission) |
| 4004 | Resource Not Found |
| 4000 | Bad Request |

### Xử lý lỗi trong Frontend

```javascript
// Axios interceptor example
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token hết hạn hoặc chưa đăng nhập
      // Redirect to login hoặc refresh token
      refreshToken();
    } else if (error.response?.status === 403) {
      // Không có permission
      // Show error message
      showError('Bạn không có quyền thực hiện thao tác này');
    }
    return Promise.reject(error);
  }
);
```

---

## Best Practices

### 1. Xử lý Authentication

```javascript
// Kiểm tra user đã đăng nhập
const isAuthenticated = () => {
  return !!localStorage.getItem('accessToken');
};

// Lấy current user info
const getCurrentUser = async () => {
  if (!isAuthenticated()) return null;
  
  try {
    const response = await axios.get('/api/v1/user/me', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
      }
    });
    return response.data.data;
  } catch (error) {
    // Token invalid, clear và redirect
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    return null;
  }
};
```

### 2. Xử lý Favorite Status (Optional Auth)

```javascript
// Khi vào trang chi tiết bài viết
const checkFavoriteStatus = async (articleId) => {
  // Nếu chưa đăng nhập, không cần gọi API
  if (!isAuthenticated()) {
    return false;
  }
  
  try {
    // Gọi API get favorites của user
    const response = await axios.get('/api/v1/user/me/favorites', {
      params: { articleId } // nếu API hỗ trợ filter
    });
    
    // Hoặc check trong danh sách favorites
    const favorites = response.data.data.content;
    return favorites.some(fav => fav.articleId === articleId);
  } catch (error) {
    // Nếu lỗi 401, user chưa đăng nhập
    if (error.response?.status === 401) {
      return false;
    }
    throw error;
  }
};
```

### 3. Conditional API Calls

```javascript
// Chỉ gọi API khi user đã đăng nhập
useEffect(() => {
  if (isAuthenticated()) {
    fetchUserFavorites();
    fetchNotifications();
  }
}, [isAuthenticated()]);
```

### 4. Permission-based UI

```javascript
// Ẩn/hiện UI dựa trên permissions
const hasPermission = (permission) => {
  const user = getCurrentUser();
  if (!user) return false;
  
  return user.permissions?.includes(permission) || 
         user.roles?.some(role => role.permissions?.includes(permission));
};

// Sử dụng
{hasPermission('PERMISSION_ARTICLE_CREATE') && (
  <Button onClick={createArticle}>Tạo bài viết</Button>
)}
```

### 5. Error Messages

```javascript
const getErrorMessage = (error) => {
  const errorMessages = {
    4001: 'Vui lòng đăng nhập để tiếp tục',
    4003: 'Bạn không có quyền thực hiện thao tác này',
    4004: 'Không tìm thấy tài nguyên',
  };
  
  return errorMessages[error.response?.data?.code] || 
         error.response?.data?.message || 
         'Đã xảy ra lỗi';
};
```

---

## API Response Format

### Success Response

```javascript
{
  "code": 2000,
  "status": 200,
  "message": "Success message",
  "data": { ... },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### Paginated Response

```javascript
{
  "code": 2000,
  "status": 200,
  "message": "Success",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "first": true,
    "last": false
  }
}
```

---

## Notes

1. **Public Endpoints**: Guest users có thể truy cập mà không cần authentication
2. **Protected Endpoints**: Cần authentication và permissions tương ứng
3. **Optional Auth**: Một số endpoints có thể hoạt động với hoặc không có authentication (như favorite status)
4. **Role-based Access**: UI nên ẩn/hiện features dựa trên roles và permissions của user
5. **Error Handling**: Luôn xử lý 401 (Unauthorized) và 403 (Forbidden) một cách graceful

---

## Support

Nếu có thắc mắc, vui lòng liên hệ backend team.

