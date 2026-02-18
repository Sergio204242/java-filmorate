package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper mpaRowMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper, GenreRowMapper genreRowMapper,
                         MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public FilmDto create(Film film) {
        validateFilm(film);

        if (film.getMpa() == null) {
            film.setMpa(new Mpa(1, "G"));
        }

        if (film.getMpa().getId() < 1 || film.getMpa().getId() > 5) {
            throw new ValidationException("Неправильный id рейтинга");
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() < 1 || genre.getId() > 6) {
                    throw new ValidationException("Неправильный id жанра");
                }
            }
        }

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                " VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Integer filmId = keyHolder.getKeyAs(Integer.class);
        film.setId(filmId);

        String linkSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(linkSql, filmId, genre.getId());
        }

        return getFilmById(filmId);
    }

    @Override
    public FilmDto update(Film film) {
        validateFilm(film);

        String updateSql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?," +
                " mpa_id = ? WHERE id = ?";

        jdbcTemplate.update(updateSql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", film.getId());
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            for (Long userId : film.getLikes()) {
                jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                        film.getId(), userId);
            }
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    film.getId(), genre.getId());
        }

        return getFilmById(film.getId());
    }

    @Override
    public Collection<FilmDto> getFilms() {
        List<Film> films = jdbcTemplate.query(
                "SELECT f.*, m.id AS mpa_id, m.code AS mpa_name FROM films f JOIN mpa_rating m ON f.mpa_id = m.id",
                filmRowMapper);

        List<FilmDto> dtos = new ArrayList<>();
        String genresSql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id ASC";
        String likesSql = "SELECT user_id FROM likes WHERE film_id = ? ORDER BY user_id ASC";

        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(jdbcTemplate.query(genresSql, genreRowMapper, film.getId())));
            film.setLikes(new LinkedHashSet<>(jdbcTemplate.queryForList(likesSql, Long.class, film.getId())));
            dtos.add(FilmMapper.mapToFilmDto(film));
        }

        return dtos;
    }

    @Override
    public FilmDto getFilmById(long id) {
        String filmSql = "SELECT f.*, m.id AS mpa_id, m.code AS mpa_name FROM films f " +
                "JOIN mpa_rating m ON f.mpa_id = m.id WHERE f.id = ?";

        Film film;
        try {
            film = jdbcTemplate.queryForObject(filmSql, filmRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Фильм с id = " + id + " не найден");
        }

        String genresSql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";

        film.setGenres(new LinkedHashSet<>(jdbcTemplate.query(genresSql, genreRowMapper, film.getId())));
        film.setLikes(new LinkedHashSet<>(jdbcTemplate.queryForList(likesSql, Long.class, film.getId())));

        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public List<FilmDto> getPopularFilms(int count) {
        List<FilmDto> filmDtos = new ArrayList<>();

        String popularFilmsSql =
                "SELECT f.*, m.id AS mpa_id, m.code AS mpa_name " +
                        "FROM films f " +
                        "JOIN mpa_rating m ON f.mpa_id = m.id " +
                        "LEFT JOIN likes l ON l.film_id = f.id " +
                        "GROUP BY f.id, m.id, m.code " +
                        "ORDER BY COUNT(l.user_id) DESC, f.id ASC " +
                        "LIMIT ?";

        List<Film> filmsFromDb = jdbcTemplate.query(popularFilmsSql, filmRowMapper, count);

        String genresSql = "SELECT g.* " +
                "FROM genres AS g " +
                "JOIN film_genres AS fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";

        for (Film film : filmsFromDb) {
            film.setGenres(new LinkedHashSet<>(jdbcTemplate.query(genresSql, genreRowMapper, film.getId())));
            film.setLikes(new HashSet<>(jdbcTemplate.queryForList(likesSql, Long.class, film.getId())));
            filmDtos.add(FilmMapper.mapToFilmDto(film));
        }

        return filmDtos;

    }

    @Override
    public FilmDto addLike(long filmId, long userId) {

        getFilmById(filmId);

        String existsSql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(existsSql, Integer.class, filmId, userId);

        if (count != null && count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO likes (film_id, user_id) VALUES (?, ?)",
                    filmId, userId);
        }

        return getFilmById(filmId);
    }

    @Override
    public FilmDto removeLike(long filmId, long userId) {

        getFilmById(filmId);

        String removeLikeSql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(removeLikeSql, filmId, userId);

        return getFilmById(filmId);
    }

    private void validateFilm(Film film) {
        final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new DateException("Дата релиза должна быть не раньше 28 декабря 1895");
        }
    }

    @Override
    public List<Genre> getGenres() {
        String genresSql = "SELECT * FROM genres ORDER BY id";

        return jdbcTemplate.query(genresSql, genreRowMapper);
    }

    @Override
    public Genre getGenre(long id) {

        if (id > 6 || id < 1) {
            throw new ValidationException("Жанра с таким id = " + id + " не существует");
        }

        String genresSql = "SELECT * FROM genres WHERE id = ?";

        return jdbcTemplate.queryForObject(genresSql, genreRowMapper, id);
    }

    @Override
    public List<Mpa> getMpas() {
        String mpasSql = "SELECT * FROM mpa_rating ORDER BY id";

        return jdbcTemplate.query(mpasSql, mpaRowMapper);
    }

    @Override
    public Mpa findMpaById(long id) {
        if (id > 6 || id < 1) {
            throw new ValidationException("Mpa с таким id = " + id + " не существует");
        }

        String mpaSql = "SELECT * FROM mpa_rating WHERE id = ?";

        return jdbcTemplate.queryForObject(mpaSql, mpaRowMapper, id);
    }

}