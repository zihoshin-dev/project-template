-- 사용자 테이블 생성
-- V1: 초기 스키마

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 이메일 인덱스 (로그인 성능 향상)
CREATE INDEX idx_users_email ON users(email);

-- 역할 인덱스 (권한 검색 성능 향상)
CREATE INDEX idx_users_role ON users(role);

-- 코멘트 추가
COMMENT ON TABLE users IS '사용자 정보 테이블';
COMMENT ON COLUMN users.id IS '사용자 고유 ID (UUID)';
COMMENT ON COLUMN users.email IS '이메일 (로그인 ID)';
COMMENT ON COLUMN users.password IS '암호화된 비밀번호';
COMMENT ON COLUMN users.name IS '사용자 이름';
COMMENT ON COLUMN users.role IS '사용자 역할 (USER, ADMIN)';
COMMENT ON COLUMN users.created_at IS '생성 시간';
COMMENT ON COLUMN users.updated_at IS '수정 시간';
