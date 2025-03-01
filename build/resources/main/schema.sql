CREATE TABLE IF NOT EXISTS account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL -- Plain text, not secure for production
);

CREATE TABLE IF NOT EXISTS character (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    class TEXT NOT NULL,
    health INT NOT NULL,
    attack INT NOT NULL,
    experience INT NOT NULL DEFAULT 0,
    defense INT,
    stamina INT,
    healing INT,
    mana INT,
    level INT NOT NULL DEFAULT 1,
    should_level_up BOOLEAN NOT NULL DEFAULT FALSE,
    is_owner BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS leaderboard (
    character_id BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0,
    draws INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS match (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challenger_id BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    opponent_id BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    match_outcome TEXT NOT NULL,
    challenger_xp INT NOT NULL DEFAULT 0,
    opponent_xp INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS round (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL REFERENCES match(id) ON DELETE CASCADE,
    round_number INT NOT NULL,
    character_id BIGINT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    health_delta INT NOT NULL,
    stamina_delta INT NOT NULL,
    mana_delta INT NOT NULL
);
