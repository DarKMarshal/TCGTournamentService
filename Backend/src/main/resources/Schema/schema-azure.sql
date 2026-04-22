
-- =========================================
-- Accounts
-- =========================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'accounts')
CREATE TABLE accounts (
    id              INT PRIMARY KEY IDENTITY(1,1),
    username        NVARCHAR(255) NOT NULL UNIQUE,
    player_id       INT NOT NULL,
    date_of_birth   NVARCHAR(50) NOT NULL,
    password_hash   NVARCHAR(255) NOT NULL,
    role            NVARCHAR(50) NOT NULL DEFAULT 'PLAYER'
);

-- =========================================
-- Players
-- =========================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'players')
CREATE TABLE players (
    id                  INT PRIMARY KEY,
    name                NVARCHAR(255) NOT NULL,
    ageDivision         NVARCHAR(50),
    championship_points INT DEFAULT 0
);

-- =========================================
-- Events
-- =========================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'events')
CREATE TABLE events (
    id          NVARCHAR(255) PRIMARY KEY,
    name        NVARCHAR(255) NOT NULL,
    uploader_id INT
);

-- =========================================
-- Tournaments (Divisions)
-- =========================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'tournaments')
CREATE TABLE tournaments
(
    event_id        NVARCHAR(255) NOT NULL,
    age_division    NVARCHAR(50)  NOT NULL,
    tournament_type NVARCHAR(50)  NOT NULL,
    PRIMARY KEY (event_id, age_division),
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

-- =========================================
-- Player Results
-- =========================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'results')
CREATE TABLE results
(
    event_id                         NVARCHAR(255) NOT NULL,
    age_division                     NVARCHAR(50)  NOT NULL,
    player_id                        INT           NOT NULL,
    placement                        INT           NOT NULL,
    points                           INT   DEFAULT 0,
    match_points                     INT   DEFAULT 0,
    opponent_win_percentage          FLOAT DEFAULT 0.0,
    opponent_opponent_win_percentage FLOAT DEFAULT 0.0,
    PRIMARY KEY (event_id, age_division, player_id),
    FOREIGN KEY (event_id, age_division) REFERENCES tournaments (event_id, age_division) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players (id) ON DELETE CASCADE
);


