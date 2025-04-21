-- 사용자 테이블 생성
CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    email    VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ ,
    deleted_at TIMESTAMPTZ ,
    role VARCHAR(10) NOT NULL CHECK ( role IN ('USER', 'ADMIN'))
);


-- 관심사 테이블 생성
CREATE TABLE interests
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(60)      NOT NULL UNIQUE,
    subscriber_count INTEGER DEFAULT 0 NOT NULL,
    created_at  TIMESTAMPTZ       NOT NULL
);


-- 관심사 키워드 테이블 생성
CREATE TABLE keywords
(
    id          UUID PRIMARY KEY,
    interest_id UUID       NOT NULL,
    name        VARCHAR(30) NOT NULL
);

-- 제약조건 추가
ALTER TABLE keywords
    ADD CONSTRAINT fk_keywords_interests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE;


-- 구독 테이블 생성
CREATE TABLE subscriptions
(
    user_id     UUID      NOT NULL,
    interest_id UUID      NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,

    PRIMARY KEY (user_id, interest_id)
);

-- 제약조건 추가
ALTER TABLE subscriptions
    ADD CONSTRAINT fk_subscriptions_interests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE;
ALTER TABLE subscriptions
    ADD CONSTRAINT fk_subscriptions_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;