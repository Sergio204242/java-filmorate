CREATE TABLE IF NOT EXISTS mpa_rating
(
    id
    INTEGER
    PRIMARY
    KEY,
    code
    VARCHAR
    NOT
    NULL
    UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
    NOT
    NULL,
    description
    VARCHAR,
    release_date
    DATE
    NOT
    NULL,
    duration
    INTEGER
    NOT
    NULL,
    mpa_id
    INTEGER
    REFERENCES
    mpa_rating
(
    id
)
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    email
    VARCHAR
    NOT
    NULL
    UNIQUE,
    login
    VARCHAR
    NOT
    NULL
    UNIQUE,
    name
    VARCHAR,
    birthday
    DATE
    NOT
    NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    id
    INTEGER
    PRIMARY
    KEY,
    genre
    VARCHAR
    NOT
    NULL
    UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id
    INTEGER
    NOT
    NULL
    REFERENCES
    films
(
    id
),
    genre_id INTEGER NOT NULL REFERENCES genres
(
    id
),
    PRIMARY KEY
(
    film_id,
    genre_id
)
    );

CREATE TABLE IF NOT EXISTS likes
(
    film_id
    INTEGER
    NOT
    NULL
    REFERENCES
    films
(
    id
),
    user_id INTEGER NOT NULL,
    PRIMARY KEY
(
    film_id,
    user_id
)
    );

CREATE TABLE IF NOT EXISTS friends
(
    user_id
    INTEGER
    NOT
    NULL
    REFERENCES
    users
(
    id
),
    friend_id INTEGER NOT NULL REFERENCES users
(
    id
),
    PRIMARY KEY
(
    user_id,
    friend_id
)
    );