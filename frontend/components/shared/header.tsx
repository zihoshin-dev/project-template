"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { useUserStore } from "@/lib/stores/user-store";

/**
 * 공용 헤더 컴포넌트
 * - 로고, 네비게이션, 사용자 메뉴 포함
 */
export function Header() {
  const router = useRouter();
  const { isAuthenticated, logout } = useUserStore();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-14 items-center">
        {/* 로고 */}
        <div className="mr-4 flex">
          <Link href="/" className="flex items-center space-x-2">
            <span className="font-bold text-xl">Template</span>
          </Link>
        </div>

        {/* 네비게이션 */}
        <nav className="flex flex-1 items-center space-x-6 text-sm font-medium">
          {isAuthenticated && (
            <>
              <Link
                href="/dashboard"
                className="transition-colors hover:text-foreground/80 text-foreground/60"
              >
                대시보드
              </Link>
              {/* 추가 네비게이션 링크 */}
            </>
          )}
        </nav>

        {/* 사용자 메뉴 */}
        <div className="flex items-center space-x-2">
          {isAuthenticated ? (
            <Button variant="ghost" size="sm" onClick={handleLogout}>
              로그아웃
            </Button>
          ) : (
            <>
              <Link href="/login">
                <Button variant="ghost" size="sm">
                  로그인
                </Button>
              </Link>
              <Link href="/signup">
                <Button size="sm">회원가입</Button>
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
