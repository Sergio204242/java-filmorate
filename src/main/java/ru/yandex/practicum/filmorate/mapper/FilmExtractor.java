package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {

        Map<Long, Film> films = new LinkedHashMap<>();

        while (rs.next()) {

            Long filmId = rs.getLong("id");

            if (!films.containsKey(filmId)) {

                Film film = new Film();

                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));

                Mpa mpa = new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
                film.setMpa(mpa);

                films.put(filmId, film);
            }

            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            films.get(filmId).getGenres().add(genre);

            Long likeId = rs.getLong("user_id");
            films.get(filmId).getLikes().add(likeId);
        }
        return new ArrayList<>(films.values());
    }
}
