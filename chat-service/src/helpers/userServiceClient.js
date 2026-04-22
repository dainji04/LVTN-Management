// 3 lớp bảo vệ: Redis cache → Circuit Breaker → Graceful Degradation
const axios   = require('axios');
const redis   = require('../config/redis.config');  
const requestContext = require('../utils/requestContext.js'); 

const USER_SERVICE_URL = process.env.USER_SERVICE_URL;
const CACHE_TTL        = 300;   // 5 phút (giây)
const REQUEST_TIMEOUT  = 2000;  // 2 giây
const CB_THRESHOLD     = 5;     // 5 lần lỗi liên tiếp → mở circuit
const CB_RESET_MS      = 30000; // 30 giây thì thử lại

// ─── Circuit Breaker đơn giản ────────────────────────────────────────────────
const circuitBreaker = {
  failCount:   0,
  isOpen:      false,
  openedAt:    null,

  recordSuccess() {
    this.failCount = 0;
    this.isOpen    = false;
  },

  recordFailure() {
    this.failCount++;
    if (this.failCount >= CB_THRESHOLD) {
      this.isOpen   = true;
      this.openedAt = Date.now();
      console.warn('[UserServiceClient] Circuit OPEN — User Service không phản hồi');
    }
  },

  // Trả về true nếu có thể gọi, false nếu đang mở
  canCall() {
    if (!this.isOpen) return true;
    // Half-open: sau CB_RESET_MS cho thử 1 lần
    if (Date.now() - this.openedAt > CB_RESET_MS) {
      console.info('[UserServiceClient] Half-open — thử lại User Service');
      return true;
    }
    return false;
  },
};

// ─── Fallback data khi không lấy được user ───────────────────────────────────
function fallbackUser(userId) {
  return {
    maNguoiDung:    userId,
    ho:             '',
    ten:            'Người dùng',
    bietDanh:       null,
    anhDaiDien:     null,
    hoatDongLanCuoi: null,
    _isFallback:    true,   // flag để FE biết đây là dữ liệu tạm
  };
}

// ─── Hàm gọi HTTP có timeout ─────────────────────────────────────────────────
async function callUserService(path, method = 'get', data = null) {
  if (!circuitBreaker.canCall()) {
    throw new Error('CIRCUIT_OPEN');
  }

  try {
    const store = requestContext.get();
    console.info(`[UserServiceClient] RequestContext token: ${store?.token}`);
    const token = store?.token; // Lấy token từ AsyncLocalStorage nếu có
    console.info(`[UserServiceClient] Calling User Service: ${method.toUpperCase()} ${path} with token: ${token ? 'YES' : 'NO'}`);
    const config = {
      method,
      url: `${USER_SERVICE_URL}${path}`,
      timeout: REQUEST_TIMEOUT,
      headers: token ? { Authorization: `${token}` } : {},
    };
    if (data) config.data = data;


    const res = await axios(config);
    circuitBreaker.recordSuccess();
    return res.data;

  } catch (err) {
    circuitBreaker.recordFailure();
    throw err;
  }
}

// ─── Public API ──────────────────────────────────────────────────────────────
const userServiceClient = {

  // Lấy 1 user — có cache
  async getUserById(id) {
    const cacheKey = `user:${id}`;

    // Lớp 1: Redis cache
    try {
      const cached = await redis.get(cacheKey);
      if (cached) return JSON.parse(cached);
    } catch (e) {
      console.warn('[UserServiceClient] Redis lỗi, bỏ qua cache:', e.message);
    }

    // Lớp 2: Gọi User Service
    try {
      const user = await callUserService(`/users/${id}`);
      console.log(`[UserServiceClient] Fetched user ${id} from User Service:`, user);
      // Lưu cache (không await — không chặn response)
      redis.setex(cacheKey, CACHE_TTL, JSON.stringify(user.data)).catch(() => {});
      return user;

    } catch (err) {
      // Lớp 3: Graceful degradation
      console.error(`[UserServiceClient] Không lấy được user ${id}:`, err.message);
      return fallbackUser(id);
    }
  },

  // Lấy nhiều user — batch, có cache
  async getUsersByIds(userIds) { //lấy danh sách user theo mảng id, trả về object { id: user }
    if (!userIds || userIds.length === 0) return {};
    const unique = [...new Set(userIds.filter(Boolean))];
    console.log(`[UserServiceClient] getUsersByIds: ${unique}`);
    const result = {};

    // Lớp 1: Kiểm tra cache từng ID
    const uncachedIds = [];
    for (const id of unique) {
      try {
        const cached = await redis.get(`user:${id}`);
        console.log(`[UserServiceClient] Cache check for user:${id} — ${cached ? 'HIT' : 'MISS'}`);
        if (cached) {
          result[id] = JSON.parse(cached);
        } else {
          uncachedIds.push(id);
        }
      } catch {
        uncachedIds.push(id);   // Redis lỗi → vẫn tiếp tục
      }
    }

    if (uncachedIds.length === 0) return result;

    // Lớp 2: Batch fetch những ID chưa có cache
    try {
      console.info(`[UserServiceClient] Batch fetching users by IDs: ${uncachedIds}`);
      const response = await callUserService('/ban-be/by-ids', 'post', uncachedIds );
      if (!response || !Array.isArray(response.data)) {
        throw new Error("Invalid response from User Service");
      }
      const users = response.data; 
        console.log(`[UserServiceClient] Batch fetch successful. Users received:`, users);
      for (const user of users) {
        result[user.maNguoiDung] = user;
        redis.setex(`user:${user.maNguoiDung}`, CACHE_TTL, JSON.stringify(user)).catch(() => {});
      }

    } catch (err) {
      // Lớp 3: Fallback cho những ID chưa có
      console.error('[UserServiceClient] Batch fetch thất bại:', err.message);
      for (const id of uncachedIds) {
        result[id] = fallbackUser(id);
      }
    }

    return result;
  },

  // Kiểm tra block — không cần cache vì trạng thái thay đổi thường xuyên
  // async isBlocked(userId, targetUserId) {
  //   try {
  //     const data = await callUserService(
  //       `/internal/users/${userId}/blocked/${targetUserId}`
  //     );
  //     return data.isBlocked;
  //   } catch {
  //     // Không biết → mặc định cho phép (lạc quan)
  //     // Hoặc đổi thành `return true` nếu muốn an toàn hơn (thận trọng)
  //     return false;
  //   }
  // },

  // Kiểm tra bạn bè
  async areFriends(userId1, userId2) {
  // Tạo key sao cho cả (5, 11) và (11, 5) đều dùng cache được
  const id1 = parseInt(userId1, 10);
  const id2 = parseInt(userId2, 10);
  const key = [id1, id2].sort().join(':');
  const cacheKey = `friends:${key}`;
    console.log(`[UserServiceClient] Checking friendship between ${id1} and ${id2} (cache key: ${cacheKey})`);
  // Lớp 1: Redis cache
  try {
    const cached = await redis.get(cacheKey);
    console.log(`[UserServiceClient] Cache check for ${cacheKey} — ${cached !== null ? 'HIT' : 'MISS'}`);
    if (cached !== null) {
      console.log(`[UserServiceClient] areFriends(${id1}, ${id2}) → ${cached} (CACHED)`);
      return cached === 'true';
    }
  } catch (e) {
    console.warn('[UserServiceClient] Redis cache lỗi:', e.message);
  }

  // Lớp 2: Gọi API
  try {
    console.info(`[UserServiceClient] Calling User Service API to check friendship between ${id1} and ${id2}`);
    const data = await callUserService(`/ban-be/check?id1=${id1}&id2=${id2}`, 'get');
    console.log(`[UserServiceClient] API response for areFriends(${id1}, ${id2}):`, data);
    const result = data.data;
    
    // Lưu cache (60 giây cho bạn bè, 30 giây cho non-friend)
    const ttl = result ? 60 : 30;
    redis.setex(cacheKey, ttl, String(result)).catch(() => {});
    
    console.log(`[UserServiceClient] areFriends(${id1}, ${id2}) → ${result}`);
    return result;
  } catch (err) {
    console.error(`[UserServiceClient] Lỗi kiểm tra bạn bè:`, err.message);
    return false; // mặc định không phải bạn bè nếu có lỗi
  }
},

  // Xóa cache thủ công (dùng khi user cập nhật avatar/tên)
  async invalidateUserCache(userId) {
    await redis.del(`user:${userId}`).catch(() => {});
  },

  // Tìm kiếm bạn bè — proxy sang User Service
// Thêm vào object userServiceClient
// async searchFriends(userId, keyword) {
//   try {
//     const data = await callUserService(
//       `/internal/friends/search?userId=${userId}&q=${encodeURIComponent(keyword)}`
//     );
//     return data;
//   } catch {
//     return []; // Fallback: trả về mảng rỗng, không crash
//   }
// },
};

module.exports = userServiceClient;