package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();

        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setCountLikes(film.getLikesCount());

        if (film.getMpa() != null) {
            dto.setMpa(MpaMapper.mapToMpaDto(film.getMpa()));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<GenreDto> sortedGenres = film.getGenres()
                    .stream()
                    .sorted((g1, g2) -> Integer.compare(g1.getId(), g2.getId()))
                    .map(GenreMapper::mapToGenreDto)
                    .collect(Collectors.toList());

            dto.setGenres(sortedGenres);
        }

        return dto;
    }
}
