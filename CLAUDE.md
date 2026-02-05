# Project Template

## Project Overview
Kotlin + Spring Boot + Next.js 풀스택 프로젝트 템플릿입니다. 새 프로젝트 시작 시 이 템플릿을 복사하여 사용합니다.

## Tech Stack
- **Backend**: Kotlin 2.3.0, Spring Boot 4.0.2, Gradle 9.3.1
- **Frontend**: Next.js 16.1.6, React 19.2.4, TypeScript 5.9.3
- **Database**: PostgreSQL
- **Auth**: JWT
- **Migration**: Flyway
- **Java**: OpenJDK 21

## Project Structure
```
project-template/
├── backend/
│   ├── src/main/kotlin/com/template/
│   │   ├── auth/         # JWT Authentication
│   │   ├── common/       # Common utilities
│   │   ├── config/       # Configuration
│   │   └── domain/       # Domain modules
│   │       └── user/     # User domain
│   └── build.gradle.kts
├── frontend/
│   ├── app/              # Next.js App Router
│   │   ├── (auth)/       # Auth pages
│   │   └── dashboard/    # Dashboard pages
│   ├── components/       # React Components
│   │   ├── ui/           # UI primitives
│   │   └── shared/       # Shared components
│   └── lib/              # Utilities
├── docker-compose.yml    # Local development
└── CLAUDE.md
```

## Development Commands
```bash
# Backend
cd backend
./gradlew build              # Build
./gradlew bootRun            # Run dev server

# Frontend
cd frontend
npm install                  # Install dependencies
npm run dev                  # Start dev server
npm run build                # Production build

# Docker
docker-compose up -d         # Start PostgreSQL
```

## Environment Variables
### Backend (application.yml)
- `JWT_SECRET`: JWT signing key
- `DATABASE_URL`: PostgreSQL connection

### Frontend (.env.local)
- `NEXT_PUBLIC_API_URL`: Backend API URL

## Features Included
- JWT authentication (login/register)
- User management
- Role-based access control
- CORS configuration
- API error handling
- Database migrations

## Tech Stack Rules (MANDATORY)

### Kotlin 2.3.0 패턴
```kotlin
// ✅ CORRECT: compilerOptions DSL 사용
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

// ❌ WRONG: kotlinOptions 사용 금지 (deprecated)
// kotlinOptions { jvmTarget = "21" }
```

### Spring Security 6.x 패턴
```kotlin
// ✅ CORRECT: DaoAuthenticationProvider
@Bean
fun authenticationProvider(userDetailsService: UserDetailsService) =
    DaoAuthenticationProvider().apply {
        setUserDetailsService(userDetailsService)
        setPasswordEncoder(passwordEncoder())
    }
```

### TypeScript/React 19 패턴
```typescript
// ✅ CORRECT: API 함수는 구체적 타입 반환
export async function fetchUsers(): Promise<User[]> { ... }

// ❌ WRONG: Promise<unknown> 반환 금지
// export async function fetchUsers(): Promise<unknown> { ... }
```

### 빌드 검증
```bash
# 변경 후 반드시 빌드 테스트
cd backend && ./gradlew build --no-daemon
cd frontend && npm run build
```
