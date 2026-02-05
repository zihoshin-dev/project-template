# Project Template

Kotlin + Spring Boot 3.2 + Next.js 15 풀스택 보일러플레이트 템플릿

## 기술 스택

### Backend
- **Kotlin 2.0** + **Spring Boot 3.2**
- Spring Security + JWT 인증
- Spring Data JPA + PostgreSQL
- Flyway 마이그레이션
- Kotest + MockK 테스트

### Frontend
- **Next.js 15** + **React 19**
- TypeScript (Strict Mode)
- TailwindCSS + shadcn/ui
- Zustand (클라이언트 상태)
- SWR (서버 상태)
- React Hook Form + Zod (폼 검증)

## 빠른 시작

### 1. 데이터베이스 실행

```bash
# PostgreSQL 컨테이너 시작
docker compose up -d postgres

# (선택) pgAdmin도 함께 실행
docker compose --profile tools up -d
```

### 2. Backend 실행

```bash
cd backend

# Gradle Wrapper 권한 부여 (최초 1회)
chmod +x gradlew

# 애플리케이션 실행 (개발 모드)
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/template-backend-0.0.1-SNAPSHOT.jar
```

백엔드 서버: http://localhost:8080

### 3. Frontend 실행

```bash
cd frontend

# 의존성 설치
npm install

# 환경 변수 설정
cp .env.local.example .env.local

# 개발 서버 실행
npm run dev
```

프론트엔드 서버: http://localhost:3000

## 프로젝트 구조

```
project-template/
├── frontend/                 # Next.js 15 프론트엔드
│   ├── app/                  # App Router 페이지
│   ├── components/           # React 컴포넌트
│   │   ├── ui/              # shadcn/ui 컴포넌트
│   │   └── shared/          # 공용 컴포넌트
│   ├── lib/                  # 유틸리티
│   │   ├── api/             # API 클라이언트
│   │   ├── stores/          # Zustand 스토어
│   │   ├── hooks/           # 커스텀 훅
│   │   └── validations/     # Zod 스키마
│   └── types/               # TypeScript 타입
│
├── backend/                  # Spring Boot 백엔드
│   └── src/main/kotlin/
│       └── com/template/
│           ├── domain/       # 도메인 (엔티티, 서비스, 컨트롤러)
│           ├── auth/         # 인증 (JWT)
│           ├── config/       # 설정
│           └── common/       # 공통 유틸리티
│
└── docker-compose.yml        # Docker 설정
```

## API 엔드포인트

### 인증 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| POST | `/api/auth/refresh` | 토큰 갱신 |

### 사용자 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/users/me` | 내 정보 조회 |
| PATCH | `/api/users/me` | 내 정보 수정 |
| GET | `/api/users` | 전체 사용자 조회 (관리자) |
| GET | `/api/users/{id}` | 사용자 조회 (관리자) |
| DELETE | `/api/users/{id}` | 사용자 삭제 (관리자) |

## 환경 변수

### Backend (`application.yml`)

```yaml
# JWT 설정
jwt:
  secret: ${JWT_SECRET}  # 32자 이상 비밀 키
  access-token-expiration: 900000      # 15분
  refresh-token-expiration: 604800000  # 7일

# 데이터베이스
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/template_db
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}

# CORS
cors:
  allowed-origins: http://localhost:3000
```

### Frontend (`.env.local`)

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## 개발 가이드

### Backend 테스트 실행

```bash
cd backend
./gradlew test
```

### Frontend 타입 체크

```bash
cd frontend
npm run type-check
```

### 새로운 API 엔드포인트 추가

1. **Entity 생성**: `domain/{feature}/{Feature}.kt`
2. **Repository 생성**: `domain/{feature}/{Feature}Repository.kt`
3. **Service 생성**: `domain/{feature}/{Feature}Service.kt`
4. **Controller 생성**: `domain/{feature}/{Feature}Controller.kt`
5. **Migration 추가**: `resources/db/migration/V{n}__description.sql`

### 새로운 페이지 추가

1. **페이지 생성**: `app/{route}/page.tsx`
2. **컴포넌트 생성**: `components/shared/{Component}.tsx`
3. **API 훅 생성**: `lib/hooks/use-{feature}.ts`
4. **Zod 스키마 생성**: `lib/validations/{feature}.ts`

## 배포

### Backend (Docker)

```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jre-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Frontend (Vercel)

```bash
vercel
```

## 라이선스

MIT License
