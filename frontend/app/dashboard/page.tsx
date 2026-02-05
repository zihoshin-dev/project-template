"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Header } from "@/components/shared/header";
import { useUser } from "@/lib/hooks/use-user";
import { useUserStore } from "@/lib/stores/user-store";

export default function DashboardPage() {
  const router = useRouter();
  const { user, isLoading, error } = useUser();
  const { isAuthenticated, logout } = useUserStore();

  // 인증되지 않은 경우 로그인 페이지로 리다이렉트
  useEffect(() => {
    if (!isAuthenticated) {
      router.push("/login");
    }
  }, [isAuthenticated, router]);

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  if (!isAuthenticated) {
    return null; // 리다이렉트 중
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />

      <main className="container mx-auto py-8 px-4">
        <div className="mb-8">
          <h1 className="text-3xl font-bold">대시보드</h1>
          <p className="text-muted-foreground mt-2">
            환영합니다! 서비스를 이용해주셔서 감사합니다.
          </p>
        </div>

        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {/* 사용자 정보 카드 */}
          <Card>
            <CardHeader>
              <CardTitle>내 정보</CardTitle>
              <CardDescription>현재 로그인한 계정 정보</CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="animate-pulse space-y-2">
                  <div className="h-4 bg-muted rounded w-3/4"></div>
                  <div className="h-4 bg-muted rounded w-1/2"></div>
                </div>
              ) : error ? (
                <p className="text-destructive text-sm">
                  정보를 불러오는데 실패했습니다.
                </p>
              ) : user ? (
                <div className="space-y-2">
                  <p className="text-sm">
                    <span className="text-muted-foreground">이름:</span>{" "}
                    <span className="font-medium">{user.name}</span>
                  </p>
                  <p className="text-sm">
                    <span className="text-muted-foreground">이메일:</span>{" "}
                    <span className="font-medium">{user.email}</span>
                  </p>
                  <p className="text-sm">
                    <span className="text-muted-foreground">역할:</span>{" "}
                    <span className="font-medium">{user.role}</span>
                  </p>
                </div>
              ) : null}
            </CardContent>
          </Card>

          {/* 통계 카드 예시 */}
          <Card>
            <CardHeader>
              <CardTitle>활동 통계</CardTitle>
              <CardDescription>이번 달 활동 현황</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-sm text-muted-foreground">총 로그인</span>
                  <span className="text-2xl font-bold">24</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-muted-foreground">작업 완료</span>
                  <span className="text-2xl font-bold">12</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 빠른 작업 카드 */}
          <Card>
            <CardHeader>
              <CardTitle>빠른 작업</CardTitle>
              <CardDescription>자주 사용하는 기능</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <Button variant="outline" className="w-full justify-start">
                새 프로젝트 만들기
              </Button>
              <Button variant="outline" className="w-full justify-start">
                설정
              </Button>
              <Button
                variant="destructive"
                className="w-full justify-start"
                onClick={handleLogout}
              >
                로그아웃
              </Button>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
