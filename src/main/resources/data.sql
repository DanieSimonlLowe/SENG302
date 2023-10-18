INSERT INTO location (city, country, postcode, address1, address2, suburb)
VALUES ('Christchurch', 'New Zealand', 8011, '19 Riccarton Rd', '', 'Upper Riccarton'),
       ('Christchurch', 'New Zealand', '', '', '', ''),
       ('Christchurch', 'New Zealand', 8041, '2 Ilam Rd', '', 'Ilam'),
       ('Auckland', 'New Zealand', '', '', '', ''),
       ('Wellington', 'New Zealand', '', '', '', ''),
       ('Florida', 'United States', '33056', '347 Don Shula Drive', '', 'Miami Gardens'),
       ('Chicago', 'United States', '60613', '1060 W Addison St', 'Wrigley Field', '');

INSERT INTO tab_users (date_of_birth_day, date_of_birth_month, date_of_birth_year, email, first_name, last_name,
                       location_string, password_hash, location_id)

VALUES (28, 8, 2003, 'tom.barthelmeh@hotmail.com', 'Tom', 'Barthelmeh', '2 Ilam Rd, Christchurch, 8041, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 3),
       (9, 2, 2004, 'morgan.english@hotmail.com', 'Morgan', 'English', 'Christchurch, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 2),
       (6, 9, 2002, 'notReal@uclive.ac.nz', 'Nathan', 'Harper', '19 Riccarton Rd, Christchurch, 8011, New Zealand',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 1),
       (9, 2, 2004, 'j.newport@hotmail.com', 'James', 'Newport', 'Auckland, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 4),
       (9, 2, 2004, 'm.williams@hotmail.com', 'Mary', 'Williams', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'j.smith@hotmail.com', 'John', 'Smith', 'Christchurch, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 2),
       (9, 2, 2004, 's.west@hotmail.com', 'Sam', 'West', 'Christchurch, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 2),
       (9, 2, 2004, 'n.weston@hotmail.com', 'Natalie', 'Weston', 'Christchurch, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 2),
       (9, 2, 2004, 't.smith@hotmail.com', 'Thomas', 'Smith', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'd.dixon@hotmail.com', 'Dan', 'Dixon', 'Auckland, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 4),
       (9, 2, 2004, 'j.klein@hotmail.com', 'Jerry', 'Klein', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'k.pugh@hotmail.com', 'Kiran', 'Pugh', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 't.tanner@hotmail.com', 'Tillie', 'Tanner', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'g.stevens@hotmail.com', 'Gary', 'Stevens', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'g.harding@hotmail.com', 'Georgina', 'Harding', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'i.deleon@hotmail.com', 'Isaac', 'Deleon', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'd.shannon@hotmail.com', 'Deacon', 'Shannon', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'c.huber@hotmail.com', 'Cerys', 'Huber', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'morgan.english7@hotmail.com', 'Morgan', 'English', 'Wellington, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 5),
       (9, 2, 2004, 'morgan.english8@hotmail.com', 'Morgan', 'English', 'Christchurch, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 2),
       (18, 3, 2003, 'notReal2@uclive.ac.nz', 'Celeste', 'Turnock', '2 Ilam Rd, Christchurch, 8041, NZ',
        '$2a$04$fvrk2c2qoLNtMbGFGsBLBO/BAUvCQxmmHeJyaesK5szZ6oqU0Q6Jq', 3);

UPDATE tab_users
SET privacy_type = 1
WHERE user_id = 1;
UPDATE tab_users
SET privacy_type = 1
WHERE user_id = 2;
UPDATE tab_users
SET privacy_type = 1
WHERE user_id = 3;
UPDATE tab_users
SET privacy_type = 1
WHERE user_id = 20;

INSERT INTO tab_authority (role, user_id)
VALUES ('ROLE_USER', 1),
       ('ROLE_USER', 2),
       ('ROLE_USER', 3),
       ('ROLE_USER', 4),
       ('ROLE_USER', 5),
       ('ROLE_USER', 6),
       ('ROLE_USER', 7),
       ('ROLE_USER', 8),
       ('ROLE_USER', 9),
       ('ROLE_USER', 10),
       ('ROLE_USER', 11),
       ('ROLE_USER', 12),
       ('ROLE_USER', 13),
       ('ROLE_USER', 14),
       ('ROLE_USER', 15),
       ('ROLE_USER', 16),
       ('ROLE_USER', 17),
       ('ROLE_USER', 18),
       ('ROLE_USER', 19),
       ('ROLE_USER', 20);



-- Password for both accounts is Password1!

INSERT INTO tab_sports (sport_name)
VALUES ('Golf'),
       ('Rugby'),
       ('Baseball'),
       ('Basketball'),
       ('Table tennis'),
       ('Volleyball'),
       ('Tennis'),
       ('Hockey'),
       ('Cricket'),
       ('Football');

INSERT INTO sport_fav (user_id, sport_id)
VALUES (1, 3),
       (1, 5),
       (1, 9),
       (3, 8),
       (3, 9),
       (4, 1),
       (4, 2),
       (4, 3),
       (5, 5),
       (6, 9),
       (6, 6),
       (6, 4),
       (7, 2),
       (7, 5),
       (7, 8),
       (8, 2),
       (8, 10),
       (8, 7),
       (8, 3),
       (9, 10),
       (9, 5),
       (9, 1),
       (10, 10),
       (10, 2),
       (11, 3),
       (11, 7),
       (11, 6),
       (12, 4),
       (12, 8),
       (12, 10),
       (12, 1),
       (13, 5),
       (13, 8),
       (14, 6),
       (14, 7),
       (14, 1),
       (15, 5),
       (15, 8),
       (15, 3),
       (16, 4),
       (16, 6),
       (16, 7),
       (17, 5),
       (17, 2),
       (17, 8),
       (17, 4),
       (18, 9),
       (18, 3),
       (19, 10),
       (19, 4),
       (20, 5),
       (20, 3),
       (20, 7),
       (19, 6),
       (19, 1),
       (19, 7);

INSERT INTO team (name, location_id, location_string, sport, invitation_token, profile_pic_name, date_created)
Values ('Test Team', 1, '19 Riccarton Rd, Upper Riccarton, Christchurch, 8011, New Zealand', 'Basketball',
        'DataBaseTestToken!123', 'images/defaultPic.png', CURRENT_DATE),
       ('Dolphins', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Dolphin!123', 'images/sample-teams/miami-dolphins.png', CURRENT_DATE),
       ('Heat', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'Basketball', 'Heat!123',
        'images/sample-teams/miami-heat.png', CURRENT_DATE),
       ('Marlins', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'Baseball', 'Marlins!123',
        'images/sample-teams/miami-marlins.png', CURRENT_DATE),
       ('Cubs', 7, '1060 W Addison St, Wrigley Field, Chicago, 60613, United States', 'Baseball', 'Cubs!123',
        'images/sample-teams/chicago-cubs.png', CURRENT_DATE),
       ('Bulls', 7, '1060 W Addison St, Wrigley Field, Chicago, 60613, United States', 'Basketball', 'Bulls!123',
        'images/sample-teams/chicago-bulls.png', CURRENT_DATE),
       ('All Blacks', 5, 'Wellington, New Zealand', 'Rugby', 'AllBlacks!123', 'images/sample-teams/all-blacks.png',
        CURRENT_DATE),
       ('Black Ferns', 5, 'Wellington, New Zealand', 'Rugby', 'BlackFerns!123', 'images/sample-teams/black-ferns.png',
        CURRENT_DATE),
       ('Christchurch Florida', 1, '19 Riccarton Rd, Upper Riccarton, Christchurch, 8011, New Zealand', 'Basketball',
        'ChristchurchFlorida!123', 'images/defaultPic.png', CURRENT_DATE),
       ('The Shriekapovas', 1, '19 Riccarton Rd, Upper Riccarton, Christchurch, 8011, New Zealand', 'Tennis',
        'TheShriekapovas!123', 'images/defaultPic.png', CURRENT_DATE),
       ('Red Blazers', 4, 'Auckland, New Zealand', 'Volleyball', 'RedBlazers!123', 'images/defaultPic.png',
        CURRENT_DATE),
       ('Buccaneers', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Buccaneers!123', 'images/sample-teams/buccaneers.png', CURRENT_DATE),
       ('last', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Buccaneers!123', 'images/sample-teams/buccaneers.png', CURRENT_DATE),
       ('last1', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Buccaneers!123', 'images/sample-teams/buccaneers.png', CURRENT_DATE),
       ('last2', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Buccaneers!123', 'images/sample-teams/buccaneers.png', CURRENT_DATE),
       ('last3', 6, '347 Don Shula Drive, Miami Gardens, Florida, 33056, United States', 'American Football',
        'Buccaneers!123', 'images/sample-teams/buccaneers.png', CURRENT_DATE);

INSERT INTO team_member(role, team_id, user_id)
VALUES (0, 1, 1),
       (1, 1, 2),
       (2, 1, 17),
       (2, 1, 18),
       (0, 2, 1),
       (0, 3, 1),
       (0, 4, 1),
       (0, 5, 1),
       (0, 6, 1),
       (0, 7, 1),
       (0, 8, 1),
       (0, 9, 1),
       (0, 10, 1),
       (0, 11, 1),
       (0, 12, 1),
       (0, 2, 3),
       (0, 3, 3),
       (0, 4, 3),
       (0, 5, 3),
       (0, 6, 3),
       (0, 7, 3),
       (0, 8, 3),
       (0, 9, 3),
       (0, 10, 3),
       (0, 11, 3),
       (0, 12, 3),
       (1, 2, 4),
       (1, 3, 5),
       (1, 4, 6),
       (1, 5, 7),
       (1, 6, 8),
       (1, 7, 9),
       (1, 8, 10),
       (1, 9, 11),
       (1, 10, 12),
       (1, 11, 13),
       (1, 12, 14),
       (2, 2, 15),
       (2, 3, 16),
       (2, 4, 17),
       (2, 5, 14),
       (2, 6, 13),
       (2, 7, 12),
       (2, 8, 11),
       (2, 9, 10),
       (2, 10, 9),
       (2, 11, 8),
       (2, 12, 7);

INSERT INTO formation (sport_pitch, team_id)
VALUES ('basketball_court', 2);
INSERT INTO formation_players_per_section (formation_id, players_per_section)
VALUES (1, 2),
       (1, 1);

INSERT INTO tab_activities (description, end_date, location_string, start_date, type, location_id, opposition_id,
                            team_id, user_id)
VALUES ('test', '2023-12-31 23:59:59', 'Christchurch, New Zealand', '2023-12-30 23:59:59', 0, 1, 5, 2, 1);

INSERT INTO tab_lineup (activity_id, formation_id)
VALUES (1, 1);

INSERT INTO tab_lineup_player (position, line_up_id, user_id)
VALUES (0, 1, 1),
       (1, 1, 2),
       (2, 1, 4),
       (3, 1, 15);
INSERT INTO substituted_stat (id, activity_id, sub_minute, new_player_user_id, old_player_user_id, team_id)
VALUES (0, 1, 23, 1, 2, 2);

INSERT INTO club (LOCATION_STRING, NAME, LOCATION_ID, USER_ID)
VALUES ('Christchurch, New Zealand', 'test', 1, 1),
       ('Christchurch, New Zealand', 'nz football', 1, 1),
       ('Christchurch, New Zealand', 'league of evil', 1, 1),
       ('Christchurch, New Zealand', 'the normal club', 1, 1),
       ('Chicago, Illinois', 'the abnormal club', 7, 2);

INSERT INTO club_teams (club_id, teams_id)
VALUES (1, 1),
       (1, 3),
       (2, 4),
       (3, 5),
       (4, 6),
       (4, 7),
       (4, 8),
       (4, 9),
       (4, 10);

INSERT INTO fact_stat (id, activity_id, description, time)
VALUES (1, 1, 'test', 5),
       (2, 1, 'test2', 3),
       (3, 1, 'test3', 6);

INSERT INTO user_following (user_id, following_id)
VALUES (1, 3),
       (1, 2),
       (3, 1);

UPDATE hibernate_sequences
SET next_val = 100
WHERE sequence_name = 'default'; -- this needs to be updated every time you change any fact table.

INSERT INTO team_following (user_id, team_id)
VALUES (3, 2);

INSERT INTO feed_post (author, date_time, message, owner_id, owner_name, owner_type, attachment_name, title, flagged)
VALUES (3, CURRENT_TIMESTAMP, 'This is a new test', 3, 'Nathan', 0, 'images/sample-teams/miami-dolphins.png',
        'This is a title', false),
       (1, CURRENT_TIMESTAMP, 'This is a team owned test', 2, 'Dolphins', 1, 'images/sample-teams/miami-dolphins.png',
        'should not appear', false);

INSERT INTO feed_alerts (read_alerts, user_id)
VALUES (0, 1),
       (0, 2),
       (0, 3);

INSERT INTO feed_post (author, date_time, message, owner_id, owner_name, owner_type, title, flagged)
VALUES (3, CURRENT_TIMESTAMP, 'This is a message', 3, 'Nathan', 0, 'This is a title', false),
       (1, CURRENT_TIMESTAMP, 'This is a team owned message', 2, 'Dolphins', 1, 'This is a team owned title', false);

INSERT INTO comment (user_id, date_time, message, parent_comment_id, post_id, flagged)
VALUES (1, CURRENT_TIMESTAMP, 'This is a comment', null, 2, false),
       (2, CURRENT_TIMESTAMP, 'I hate francis', null, 2, false),
       (1, CURRENT_TIMESTAMP, 'This is a reply', 1, 2, false),
       (2, CURRENT_TIMESTAMP, 'I hate reply', 2, 2, false);

INSERT INTO comment (user_id, date_time, message, parent_comment_id, post_id, flagged)
VALUES (1, CURRENT_TIMESTAMP, 'This is a comment', null, 1, false),
       (2, CURRENT_TIMESTAMP, 'I hate francis', null, 1, false);

INSERT INTO num_comments (count, user_id, club_id)
VALUES (5, 1, 1),
       (2, 3, 1),
       (1, 2, 1);

INSERT INTO num_posts (count, user_id, club_id)
VALUES (7, 9, 1),
       (4, 5, 1),
       (2, 2, 1);