CREATE TABLE login (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,            -- BCryptハッシュを格納
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL REFERENCES login(username),
    title       VARCHAR(100) NOT NULL,
    content     TEXT,
    name        VARCHAR(50),
    start_date  DATE,
    end_date    DATE,
    status      VARCHAR(10)  NOT NULL DEFAULT '未完了',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_username ON tasks(username);

-- spring batch アーカイブ用テーブル
CREATE TABLE tasks_archive (
    id          BIGINT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    title       VARCHAR(100) NOT NULL,
    content     TEXT,
    name        VARCHAR(50),
    start_date  DATE,
    end_date    DATE,
    status      VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    archived_at TIMESTAMP    NOT NULL DEFAULT NOW()
);