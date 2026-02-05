/**
 * API 응답 기본 타입
 */
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

/**
 * API 에러 응답 타입
 */
export interface ApiError {
  success: boolean;
  code: string;
  message: string;
  errors?: Record<string, string>;
  timestamp: string;
}

/**
 * 사용자 타입
 */
export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
  createdAt: string;
}

/**
 * 사용자 역할
 */
export type UserRole = "USER" | "ADMIN";

/**
 * 인증 토큰 응답 타입
 */
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

/**
 * 페이지네이션 파라미터
 */
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

/**
 * 페이지네이션 응답
 */
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
