DELETE
FROM MPA_RATING;

INSERT INTO mpa_rating (id, code)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

DELETE
FROM GENRES;

INSERT INTO genres (id, genre)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');