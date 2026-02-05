import useSWR from "swr";
import { userApi } from "@/lib/api/client";
import { useUserStore } from "@/lib/stores/user-store";
import { useEffect } from "react";

/**
 * 사용자 정보 타입
 */
interface User {
  id: string;
  email: string;
  name: string;
  role: string;
  createdAt: string;
}

/**
 * SWR fetcher 함수
 */
const fetcher = async (): Promise<User> => {
  const response = await userApi.getMe();
  return response.data;
};

/**
 * 현재 사용자 정보를 가져오는 SWR 훅
 *
 * 사용 예시:
 * ```tsx
 * const { user, isLoading, error, mutate } = useUser();
 *
 * if (isLoading) return <div>Loading...</div>;
 * if (error) return <div>Error loading user</div>;
 * if (user) return <div>Hello, {user.name}!</div>;
 * ```
 */
export function useUser() {
  const { isAuthenticated, setUser } = useUserStore();

  const { data, error, isLoading, mutate } = useSWR<User>(
    // 인증된 경우에만 요청
    isAuthenticated ? "/api/users/me" : null,
    fetcher,
    {
      // 5분간 캐시 유지
      dedupingInterval: 5 * 60 * 1000,
      // 포커스 시 재검증
      revalidateOnFocus: true,
      // 에러 발생 시 재시도 안함
      shouldRetryOnError: false,
    }
  );

  // 사용자 데이터가 로드되면 스토어에도 저장
  useEffect(() => {
    if (data) {
      setUser({
        id: data.id,
        email: data.email,
        name: data.name,
        role: data.role,
      });
    }
  }, [data, setUser]);

  return {
    user: data,
    isLoading,
    error,
    mutate, // 수동으로 재검증할 때 사용
  };
}

/**
 * 사용자 정보 업데이트 훅
 *
 * 사용 예시:
 * ```tsx
 * const { updateUser, isUpdating } = useUpdateUser();
 *
 * const handleSubmit = async (name: string) => {
 *   await updateUser({ name });
 * };
 * ```
 */
export function useUpdateUser() {
  const { mutate } = useUser();

  const updateUser = async (data: { name?: string }): Promise<User> => {
    const response = await userApi.updateMe(data);
    // SWR 캐시 업데이트
    await mutate();
    return response.data;
  };

  return { updateUser };
}
