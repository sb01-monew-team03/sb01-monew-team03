-- 사용자 테이블 생성
CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL,
    email    VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    role VARCHAR(10) NOT NULL CHECK ( role IN ('USER', 'ADMIN'))
);


-- 관심사 테이블 생성
CREATE TABLE interests
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(60)      NOT NULL UNIQUE,
    subscriber_count INTEGER DEFAULT 0 NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ
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
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,

    PRIMARY KEY (user_id, interest_id)
);

-- 제약조건 추가
ALTER TABLE subscriptions
    ADD CONSTRAINT fk_subscriptions_interests FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE;
ALTER TABLE subscriptions
    ADD CONSTRAINT fk_subscriptions_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;


-- 댓글 테이블 생성
CREATE TABLE comments
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id UUID NOT NULL,
    article_id UUID NOT NULL,
    content VARCHAR(500) NOT NULL,
    like_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

-- 제약 조건 추가
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_articles FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;


-- 댓글 좋아요 테이블 생성
CREATE TABLE comment_likes
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    comment_id UUID NOT NULL,
    user_id UUID NOT NULL,
    article_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    UNIQUE (comment_id, user_id)
);

-- 제약 조건 추가
ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_comments FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE;

ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE comment_likes
    ADD CONSTRAINT fk_comment_likes_articles FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE;


-- articles 테이블 생성
CREATE TABLE articles
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source VARCHAR NOT NULL,
    original_link VARCHAR NOT NULL,
    title VARCHAR NOT NULL,
    published_at TIMESTAMPTZ NOT NULL,
    summary VARCHAR NOT NULL,
    views INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NULL,
    deleted_at TIMESTAMPTZ NULL
);

-- articles 테이블 제약 조건 추가
ALTER TABLE articles ADD CONSTRAINT uq_articles_original_link
    UNIQUE (original_link);

ALTER TABLE articles ADD CONSTRAINT chk_articles_source_not_empty
    CHECK (length(trim(source)) > 0);

ALTER TABLE articles ADD CONSTRAINT chk_articles_title_not_empty
    CHECK (length(trim(title)) > 0);

ALTER TABLE articles ADD CONSTRAINT chk_articles_views_non_negative
    CHECK (views >= 0);

ALTER TABLE articles ADD CONSTRAINT chk_articles_deleted
    CHECK (deleted_at IS NULL OR deleted_at > created_at);

ALTER TABLE articles ADD CONSTRAINT chk_articles_updated
    CHECK (updated_at >= created_at);


-- activity 테이블 생성
CREATE TABLE activity
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NULL
);

-- activity 제약 조건 추가
ALTER TABLE activity ADD CONSTRAINT fk_activity_users
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 필요시 업데이트 시간 제약조건 추가
ALTER TABLE activity ADD CONSTRAINT chk_activity_updated
    CHECK (updated_at IS NULL OR updated_at >= created_at);


-- article_view 테이블 생성
CREATE TABLE article_view
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    article_id UUID NOT NULL,
    user_id UUID,
    viewed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- article_view 제약 조건 추가
ALTER TABLE article_view ADD CONSTRAINT fk_article_view_articles
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE;

ALTER TABLE article_view ADD CONSTRAINT fk_article_view_users
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;


-- notification 테이블 생성
CREATE TABLE notification
(
    id UUID GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id UUID NOT NULL,
    content VARCHAR(255) NOT NULL,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    message_id UUID,
    notification_type VARCHAR(50) NOT NULL CHECK ( notification_type IN ('INTEREST', 'COMMENT'),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ
);

-- notification 제약 조건 추가
ALTER TABLE notification ADD CONSTRAINT fk_notification_users
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notification ADD CONSTRAINT chk_notification_content
    CHECK (length(trim(content)) > 0);
