import { create } from "zustand";
import { persist } from "zustand/middleware";
import { apiClient } from "@/lib/api/client";

/**
 * 사용자 정보 타입
 */
interface User {
  id: string;
  email: string;
  name: string;
  role: string;
}

/**
 * 사용자 스토어 상태 타입
 */
interface UserState {
  // 상태
  user: User | null;
  isAuthenticated: boolean;

  // 액션
  setUser: (user: User | null) => void;
  setAuth: (accessToken: string, refreshToken: string) => void;
  logout: () => void;
}

/**
 * Zustand 사용자 스토어
 * - 로그인 상태 관리
 * - 사용자 정보 캐싱
 * - 로컬 스토리지 영속화
 */
export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      // 초기 상태
      user: null,
      isAuthenticated: false,

      // 사용자 정보 설정
      setUser: (user) =>
        set({
          user,
          isAuthenticated: !!user,
        }),

      // 인증 설정 (토큰 저장 + 인증 상태 업데이트)
      setAuth: (accessToken, refreshToken) => {
        apiClient.setTokens(accessToken, refreshToken);
        set({ isAuthenticated: true });
      },

      // 로그아웃
      logout: () => {
        apiClient.clearTokens();
        set({
          user: null,
          isAuthenticated: false,
        });
      },
    }),
    {
      name: "user-storage", // 로컬 스토리지 키
      partialize: (state) => ({
        // 영속화할 상태만 선택
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
