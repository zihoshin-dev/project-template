import { storage } from "@/lib/utils";

/**
 * API 기본 URL (환경 변수에서 가져오거나 기본값 사용)
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

/**
 * 토큰 저장 키
 */
const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refresh_token";

/**
 * API 응답 타입
 */
interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

/**
 * 에러 응답 타입
 */
interface ApiError {
  success: boolean;
  code: string;
  message: string;
  errors?: Record<string, string>;
}

/**
 * 토큰 응답 타입
 */
interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * 사용자 응답 타입
 */
interface UserResponse {
  id: string;
  email: string;
  name: string;
  role: string;
  createdAt: string;
}

/**
 * API 클라이언트 클래스
 * - 인증 토큰 자동 첨부
 * - 토큰 갱신 처리
 * - 에러 핸들링
 */
class ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  /**
   * 액세스 토큰 가져오기
   */
  getAccessToken(): string | null {
    return storage.get<string>(ACCESS_TOKEN_KEY);
  }

  /**
   * 리프레시 토큰 가져오기
   */
  getRefreshToken(): string | null {
    return storage.get<string>(REFRESH_TOKEN_KEY);
  }

  /**
   * 토큰 저장
   */
  setTokens(accessToken: string, refreshToken: string): void {
    storage.set(ACCESS_TOKEN_KEY, accessToken);
    storage.set(REFRESH_TOKEN_KEY, refreshToken);
  }

  /**
   * 토큰 삭제 (로그아웃)
   */
  clearTokens(): void {
    storage.remove(ACCESS_TOKEN_KEY);
    storage.remove(REFRESH_TOKEN_KEY);
  }

  /**
   * HTTP 요청 실행
   */
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    const url = `${this.baseUrl}${endpoint}`;

    const headers: HeadersInit = {
      "Content-Type": "application/json",
      ...options.headers,
    };

    // 액세스 토큰이 있으면 Authorization 헤더 추가
    const accessToken = this.getAccessToken();
    if (accessToken) {
      (headers as Record<string, string>)["Authorization"] = `Bearer ${accessToken}`;
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    // 401 에러 시 토큰 갱신 시도
    if (response.status === 401 && this.getRefreshToken()) {
      const refreshed = await this.refreshToken();
      if (refreshed) {
        // 토큰 갱신 성공 시 원래 요청 재시도
        (headers as Record<string, string>)["Authorization"] = `Bearer ${this.getAccessToken()}`;
        const retryResponse = await fetch(url, { ...options, headers });
        return this.handleResponse<T>(retryResponse);
      }
    }

    return this.handleResponse<T>(response);
  }

  /**
   * 응답 처리
   */
  private async handleResponse<T>(response: Response): Promise<ApiResponse<T>> {
    const data = await response.json();

    if (!response.ok) {
      const error = data as ApiError;
      throw new Error(error.message || "요청 처리 중 오류가 발생했습니다.");
    }

    return data as ApiResponse<T>;
  }

  /**
   * 토큰 갱신
   */
  private async refreshToken(): Promise<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) return false;

    try {
      const response = await fetch(`${this.baseUrl}/api/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
      });

      if (!response.ok) {
        this.clearTokens();
        return false;
      }

      const data: ApiResponse<TokenResponse> = await response.json();
      this.setTokens(data.data.accessToken, data.data.refreshToken);
      return true;
    } catch {
      this.clearTokens();
      return false;
    }
  }

  /**
   * GET 요청
   */
  get<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "GET" });
  }

  /**
   * POST 요청
   */
  post<T>(endpoint: string, body?: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: "POST",
      body: body ? JSON.stringify(body) : undefined,
    });
  }

  /**
   * PUT 요청
   */
  put<T>(endpoint: string, body?: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: "PUT",
      body: body ? JSON.stringify(body) : undefined,
    });
  }

  /**
   * PATCH 요청
   */
  patch<T>(endpoint: string, body?: unknown): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, {
      method: "PATCH",
      body: body ? JSON.stringify(body) : undefined,
    });
  }

  /**
   * DELETE 요청
   */
  delete<T>(endpoint: string): Promise<ApiResponse<T>> {
    return this.request<T>(endpoint, { method: "DELETE" });
  }
}

// API 클라이언트 인스턴스
export const apiClient = new ApiClient(API_BASE_URL);

/**
 * 인증 API
 */
export const authApi = {
  /**
   * 로그인
   */
  login: (data: { email: string; password: string }) =>
    apiClient.post<TokenResponse>("/api/auth/login", data),

  /**
   * 회원가입
   */
  signup: (data: { email: string; password: string; name: string }) =>
    apiClient.post<UserResponse>("/api/auth/signup", data),

  /**
   * 토큰 갱신
   */
  refresh: (refreshToken: string) =>
    apiClient.post<TokenResponse>("/api/auth/refresh", { refreshToken }),
};

/**
 * 사용자 API
 */
export const userApi = {
  /**
   * 현재 사용자 정보 조회
   */
  getMe: () => apiClient.get<UserResponse>("/api/users/me"),

  /**
   * 사용자 정보 수정
   */
  updateMe: (data: { name?: string }) =>
    apiClient.patch<UserResponse>("/api/users/me", data),
};
