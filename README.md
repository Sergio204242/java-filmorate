# java-filmorate
Template repository for Filmorate project.

Ссылка на базу данных
![Data base](https://github.com/Sergio204242/java-filmorate/blob/main/Data%20base.png)

Примеры запросов:
1) Получение всех фильмов
   SELECT *
   FROM films
2) Получекние всех пользователей
   SELECT *
   FROM users
3) Получение 10 самых популярных фильмов
   SELECT 
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id, f.name, f.description, f.release_date, f.duration
ORDER BY likes_count DESC
LIMIT 10;
4) Получение фильма по id
   SELECT *
   FROM films
   WHERE id = 5
5) Получение пользователя по id
   SELECT *
   FROM users
   WHERE id = 3
