
-- =========================================
-- Players
-- =========================================

CREATE TABLE IF NOT EXISTS players (
    id                  INTEGER PRIMARY KEY,
    name                TEXT NOT NULL,
    championship_points INTEGER DEFAULT 0
);

-- =========================================
-- Tournaments
-- =========================================

CREATE TABLE IF NOT EXISTS tournaments (
    id      INTEGER PRIMARY KEY,
    name    TEXT NOT NULL
);

-- =========================================
-- Age Divisions
-- =========================================

CREATE TABLE IF NOT EXISTS divisions (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    tournament_id   INTEGER NOT NULL,
    age_division    TEXT NOT NULL,
    tournament_type TEXT NOT NULL,

    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE
);

-- =========================================
-- Player Results
-- =========================================

CREATE TABLE IF NOT EXISTS results (
    id                                  INTEGER PRIMARY KEY AUTOINCREMENT,
    division_id                         INTEGER NOT NULL,
    player_id                           INTEGER NOT NULL,
    placement                           INTEGER NOT NULL,
    points                              INTEGER DEFAULT 0,
    match_points                        INTEGER DEFAULT 0,
    opponent_win_percentage             REAL DEFAULT 0.0,
    opponent_opponent_win_percentage    REAL DEFAULT 0.0,

    FOREIGN KEY (division_id) REFERENCES divisions(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);
