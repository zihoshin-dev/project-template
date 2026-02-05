import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function HomePage() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-background to-muted p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-3xl font-bold">Template App</CardTitle>
          <CardDescription>
            Kotlin + Spring Boot + Next.js 15 풀스택 템플릿
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground text-center">
            이 템플릿은 JWT 기반 인증, PostgreSQL 데이터베이스,
            그리고 모던 React 19 프론트엔드를 포함합니다.
          </p>
          <div className="flex flex-col gap-3">
            <Link href="/login" className="w-full">
              <Button className="w-full" size="lg">
                로그인
              </Button>
            </Link>
            <Link href="/signup" className="w-full">
              <Button variant="outline" className="w-full" size="lg">
                회원가입
              </Button>
            </Link>
          </div>
          <p className="text-xs text-muted-foreground text-center pt-4">
            이미 계정이 있으신가요?{" "}
            <Link href="/login" className="text-primary hover:underline">
              로그인하기
            </Link>
          </p>
        </CardContent>
      </Card>
    </main>
  );
}
