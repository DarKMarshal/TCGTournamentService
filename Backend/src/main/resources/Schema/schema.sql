
-- =========================================
-- Players
-- =========================================

CREATE TABLE IF NOT EXISTS players (
    id                  INTEGER PRIMARY KEY,
    name                TEXT NOT NULL,
    championship_points INTEGER DEFAULT 0
);

-- =========================================
-- Events
-- =========================================

CREATE TABLE IF NOT EXISTS events (
    id          TEXT PRIMARY KEY,
    name        TEXT NOT NULL,
    uploader_id INTEGER
);

-- =========================================
-- Tournaments (Divisions)
-- =========================================

CREATE TABLE IF NOT EXISTS tournaments (
    event_id        TEXT NOT NULL,
    age_division    TEXT NOT NULL,
    tournament_type TEXT NOT NULL,
    PRIMARY KEY (event_id, age_division),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- =========================================
-- Player Results
-- =========================================

CREATE TABLE IF NOT EXISTS results (
    event_id                            TEXT NOT NULL,
    age_division                        TEXT NOT NULL,
    player_id                           INTEGER NOT NULL,
    placement                           INTEGER NOT NULL,
    points                              INTEGER DEFAULT 0,
    match_points                        INTEGER DEFAULT 0,
    opponent_win_percentage             REAL DEFAULT 0.0,
    opponent_opponent_win_percentage    REAL DEFAULT 0.0,
    PRIMARY KEY (event_id, age_division, player_id),
    FOREIGN KEY (event_id, age_division) REFERENCES tournaments(event_id, age_division) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);
