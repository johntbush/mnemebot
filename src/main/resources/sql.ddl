
CREATE USER 'admin'@'localhost' IDENTIFIED  BY 'admin';
GRANT ALL PRIVILEGES ON * . * TO 'admin'@'localhost';
flush privileges;
CREATE DATABASE bot;

create table bot.image (
  image_src varchar(255) not null,
  source varchar(255) not null,
  source_type varchar(255) not null,
  tags text,
  title text,
  primary KEY(image_src)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE FULLTEXT INDEX image_title ON image (title);
CREATE FULLTEXT INDEX image_tags ON image (tags);
CREATE FULLTEXT INDEX image_both ON image(title,tags);

create table bot.troll (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  tags text not null,
  message text,
  username varchar(255),
  created DATETIME default now(),
  primary KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE FULLTEXT INDEX troll_tags ON troll (tags);
