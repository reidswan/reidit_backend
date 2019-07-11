CREATE TABLE IF NOT EXISTS community (
  community_id SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE,
  description VARCHAR(300),
  CHECK (LOWER(name) = name)
);

CREATE TABLE IF NOT EXISTS account (
    account_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email_address VARCHAR(255) UNIQUE NULL DEFAULT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    votes INT NOT NULL DEFAULT 0,
    password_hash VARCHAR(128) NOT NULL,
    password_salt VARCHAR(16) NOT NULL
);

CREATE TYPE CONTENT_TYPE AS ENUM ('text', 'link');

CREATE TABLE IF NOT EXISTS post (
    post_id SERIAL PRIMARY KEY,
    community_id INT NOT NULL REFERENCES community(community_id),
    title VARCHAR(100) NOT NULL,
    post_type CONTENT_TYPE NOT NULL,
    content TEXT NOT NULL,
    votes INT NOT NULL DEFAULT 0,
    account_id INT NOT NULL REFERENCES account(account_id),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX post_vote_count ON post(votes) WHERE is_deleted = FALSE;


CREATE TABLE IF NOT EXISTS vote (
    account_id INT NOT NULL REFERENCES account(account_id),
    post_id INT NOT NULL REFERENCES post(post_id),
    vote_value SMALLINT NOT NULL,
    CHECK (vote_value = 1 OR vote_value = -1)
);

CREATE UNIQUE INDEX vote_account_post ON vote(account_id, post_id);

CREATE TABLE IF NOT EXISTS subscription (
    account_id INT NOT NULL REFERENCES account(account_id),
    community_id INT NOT NULL REFERENCES community(community_id)
);

CREATE UNIQUE INDEX subscription_account_community ON subscription(account_id, community_id);

CREATE TABLE IF NOT EXISTS comment (
    comment_id SERIAL PRIMARY KEY,
    post_id INT NOT NULL REFERENCES post(post_id),
    account_id INT NOT NULL REFERENCES account(account_id),
    content TEXT NOT NULL,
    votes INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    parent_comment INT NULL REFERENCES comment(comment_id)
);

CREATE INDEX comment_vote_count ON comment(votes, post_id) WHERE is_deleted = FALSE AND parent_comment IS NULL;
CREATE INDEX comment_tree ON comment(parent_comment, votes) WHERE parent_comment IS NOT NULL;
