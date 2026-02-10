# java-filmorate
Template repository for Filmorate project.

Ссылка на базу данных
![Data base](https://github.com/Sergio204242/java-filmorate/blob/main/Data%20base.png)

Код базы данных 
Table films {
  id integer [primary key]
  name varchar [not null]
  description varchar
  release_date date [not null]
  duration integer [not null]
  rating_id integer [not null]
}

Table likes {
  film_id integer [primary key]
  user_id integer [primary key]
}

Table genres {
  id integer [primary key]
  genre varchar [not null, unique]
}

Table mpa_rating {
  id integer [primary key]
  code varchar [not null, unique]
}

Table film_genres {
  film_id integer [primary key]
  genre_id integer [primary key]
}

Table users {
  id integer [primary key]
  email varchar [not null, unique]
  login varchar [not null, unique]
  name varchar
  birthday date [not null]

}

Table friends {
  user_id integer [primary key]
  friend_id integer [primary key]
  status varchar [not null]
}

Ref: films.rating_id > mpa_rating.id

Ref: film_genres.film_id > films.id

Ref: film_genres.genre_id > genres.id

Ref: likes.film_id > films.id

Ref: likes.user_id > users.id

Ref: friends.user_id > users.id

Ref: friends.friend_id > users.id
