-- =========================================
-- ENUM TYPES
-- =========================================

CREATE TYPE user_role_enum AS ENUM (
    'Citizen',
    'Dispatcher',
    'Institution',
    'GlobalAdmin'
);

CREATE TYPE global_admin_clearance_enum AS ENUM (
    'Root',
    'Auditor',
    'Dev'
);

CREATE TYPE persoana_fizica_user_type_enum AS ENUM (
    'Identificat',
    'Anonim',
    'Guest'
);

CREATE TYPE ticket_status_enum AS ENUM (
    'Sesizat',
    'Validat',
    'Trimis',
    'Preluat',
    'In_Lucru',
    'Rezolvat'
);

CREATE TYPE institution_service_type_enum AS ENUM (
    'Drumuri',
    'Iluminat',
    'Salubritate',
    'Apa_Canal',
    'Transport',
    'Spatii_Verzi',
    'Ordine_Publica',
    'Altele'
);

CREATE TYPE dispatcher_role_enum AS ENUM (
    'Dispecer',
    'Team_Lead',
    'Manager',
    'Admin_Dispecerat'
);

CREATE TYPE photo_type_enum AS ENUM (
    'Before',
    'After'
);

-- =========================================
-- USERS
-- =========================================

CREATE TABLE users (
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    user_role           user_role_enum NOT NULL,
    last_login          TIMESTAMP NULL
);

-- =========================================
-- GLOBAL_ADMIN
-- =========================================

CREATE TABLE global_admin (
    user_id                 INTEGER PRIMARY KEY,
    admin_handle            VARCHAR(100) NOT NULL,
    security_clearance      global_admin_clearance_enum NOT NULL,
    master_key_hash         VARCHAR(255) NOT NULL,
    can_manage_tags         BOOLEAN NOT NULL DEFAULT FALSE,
    can_manage_users        BOOLEAN NOT NULL DEFAULT FALSE,
    last_security_audit     TIMESTAMP NULL,

    CONSTRAINT fk_global_admin_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- PERSOANA_FIZICA
-- =========================================

CREATE TABLE persoana_fizica (
    user_id              INTEGER PRIMARY KEY,
    first_name           VARCHAR(100) NOT NULL,
    last_name            VARCHAR(100) NOT NULL,
    phone_number         VARCHAR(30),
    karma_points         INTEGER NOT NULL DEFAULT 0,
    user_type            persoana_fizica_user_type_enum NOT NULL,

    CONSTRAINT fk_persoana_fizica_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_karma_points_nonnegative
        CHECK (karma_points >= 0)
);

-- =========================================
-- DISPATCHER
-- =========================================

CREATE TABLE dispatcher (
    user_id              INTEGER PRIMARY KEY,
    full_name            VARCHAR(100) NOT NULL,
    role                 dispatcher_role_enum NOT NULL,
    access_level         INTEGER NOT NULL,

    CONSTRAINT fk_dispatcher_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_dispatcher_access_level
        CHECK (access_level >= 0)
);

-- =========================================
-- INSTITUTION
-- =========================================

CREATE TABLE institution (
    user_id              INTEGER PRIMARY KEY,
    official_name        VARCHAR(255) NOT NULL,
    fiscal_code          VARCHAR(50) NOT NULL UNIQUE,
    legal_address        VARCHAR(255) NOT NULL,
    service_type         institution_service_type_enum NOT NULL,

    CONSTRAINT fk_institution_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- =========================================
-- POST
-- =========================================

CREATE TABLE post (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    creator_user_id      INTEGER,
    title                VARCHAR(255) NOT NULL,
    description          TEXT NOT NULL,
    problem_tag          VARCHAR(100) NOT NULL,
    city                 VARCHAR(100) NOT NULL,
    street               VARCHAR(255) NOT NULL,
    district             VARCHAR(100),
    landmark             VARCHAR(255),
    gps_latitude         DOUBLE PRECISION,
    gps_longitude        DOUBLE PRECISION,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_anonymous         BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_post_creator
        FOREIGN KEY (creator_user_id)
        REFERENCES persoana_fizica(user_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_post_latitude
        CHECK (gps_latitude BETWEEN -90 AND 90),

    CONSTRAINT chk_post_longitude
        CHECK (gps_longitude BETWEEN -180 AND 180)
);

-- =========================================
-- TICKET
-- =========================================

CREATE TABLE ticket (
    id                             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_number                  VARCHAR(100) NOT NULL UNIQUE,
    post_id                        INTEGER NOT NULL UNIQUE,
    problem_tag                    VARCHAR(100) NOT NULL,
    assigned_institution_user_id   INTEGER,
    dispatcher_user_id             INTEGER,
    status                         ticket_status_enum NOT NULL DEFAULT 'Sesizat',
    ranking_score                  INTEGER NOT NULL DEFAULT 0,
    last_update                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_red_flag                    BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_ticket_post
        FOREIGN KEY (post_id)
        REFERENCES post(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ticket_institution_user
        FOREIGN KEY (assigned_institution_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_ticket_dispatcher_user
        FOREIGN KEY (dispatcher_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- =========================================
-- PHOTO
-- =========================================

CREATE TABLE photo (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    post_id              INTEGER NOT NULL,
    photo_url            TEXT NOT NULL,
    photo_type           photo_type_enum NOT NULL,

    CONSTRAINT fk_photo_post
        FOREIGN KEY (post_id)
        REFERENCES post(id)
        ON DELETE CASCADE
);

-- =========================================
-- VOTE
-- =========================================

CREATE TABLE vote (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id              INTEGER NOT NULL,
    post_id              INTEGER NOT NULL,
    value                INTEGER NOT NULL,

    CONSTRAINT fk_vote_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_vote_post
        FOREIGN KEY (post_id)
        REFERENCES post(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_vote_value
        CHECK (value IN (-1, 1)),

    CONSTRAINT uq_vote_user_post
        UNIQUE (user_id, post_id)
);

-- =========================================
-- STATUS_HISTORY
-- =========================================

CREATE TABLE status_history (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id            INTEGER NOT NULL,
    changed_status       ticket_status_enum NOT NULL,
    changed_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_status_history_ticket
        FOREIGN KEY (ticket_id)
        REFERENCES ticket(id)
        ON DELETE CASCADE
);

-- =========================================
-- SYSTEM_LOGS
-- =========================================

CREATE TABLE system_logs (
    id                   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    actor_user_id        INTEGER,
    action_type          VARCHAR(100) NOT NULL,
    old_value            TEXT,
    new_value            TEXT,
    "timestamp"          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_system_logs_actor
        FOREIGN KEY (actor_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- =========================================
-- INDEXES
-- =========================================
--
--CREATE INDEX idx_users_role
--    ON users(user_role);
--
--CREATE INDEX idx_post_creator_user
--    ON post(creator_user_id);
--
--CREATE INDEX idx_post_problem_tag
--    ON post(problem_tag);
--
--CREATE INDEX idx_post_city
--    ON post(city);
--
--CREATE INDEX idx_ticket_status
--    ON ticket(status);
--
--CREATE INDEX idx_ticket_problem_tag
--    ON ticket(problem_tag);
--
--CREATE INDEX idx_ticket_assigned_institution
--    ON ticket(assigned_institution_user_id);
--
--CREATE INDEX idx_ticket_dispatcher
--    ON ticket(dispatcher_user_id);
--
--CREATE INDEX idx_photo_post
--    ON photo(post_id);
--
--CREATE INDEX idx_vote_post
--    ON vote(post_id);
--
--CREATE INDEX idx_status_history_ticket
--    ON status_history(ticket_id);
--
--CREATE INDEX idx_system_logs_actor
--    ON system_logs(actor_user_id);
--
--CREATE INDEX idx_system_logs_timestamp
--    ON system_logs("timestamp");