DROP all objects;
CREATE TABLE FILMS (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(255) NOT NULL,
	DESCRIPTION varchar(255) NOT NULL,
	RELEASE_DATE timestamp NOT NULL,
	duration integer not null,
	CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID)
);

CREATE TABLE USERS (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME varchar(255) NOT NULL,
	EMAIL varchar(255) NOT NULL,
	LOGIN varchar(255) NOT NULL,
	BIRTHDAY timestamp NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE LIKES (
	FILM_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	CONSTRAINT LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

CREATE TABLE FRIENDS (
	USER_ID INTEGER NOT NULL,
	FRIEND_ID INTEGER NOT NULL,
	CONSTRAINT FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);

CREATE TABLE MPA (
	MPA_ID INTEGER NOT NULL AUTO_INCREMENT,
	MPA_NAME varchar NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE GENRES (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
	GENRE_NAME varchar(255) NOT NULL,
	CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

ALTER TABLE FILMS ADD MPA_ID INTEGER;
ALTER TABLE PUBLIC.FILMS ADD CONSTRAINT FILMS_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID) ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE FILM_GENRES (
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL
);

ALTER TABLE FILM_GENRES ADD CONSTRAINT FILM_GENRES_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID);
ALTER TABLE FILM_GENRES ADD CONSTRAINT FILM_GENRES_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES GENRES(GENRE_ID);