
CREATE USER 'admin'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';
GRANT ALL PRIVILEGES ON bot.* TO 'admin'@'localhost';
flush privileges;
CREATE DATABASE bot;

create table meme_image (
  tag varchar(255) NOT NULL,
  url text NOT NULL,
  username varchar(255),
  created DATETIME default now(),
  primary KEY(tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table image (
  image_src varchar(255) not null,
  source varchar(255) not null,
  source_type varchar(255) not null,
  tags text,
  title text,
  primary KEY(image_src)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table sld_match (
  sld varchar (255) not null,
  friend bit default 0,
  username varchar(255),
  created DATETIME default now(),
  primary KEY(sld)
)

CREATE FULLTEXT INDEX image_title ON image (title);
CREATE FULLTEXT INDEX image_tags ON image (tags);
CREATE FULLTEXT INDEX image_both ON image(title,tags);

create table troll (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  tags text not null,
  message text,
  mood int default 0,
  username varchar(255),
  created DATETIME default now(),
  primary KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE FULLTEXT INDEX troll_tags ON troll (tags);

create table mood_trigger (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  mood_level int default 0,
  trigger_text text,
  username varchar(255),
  created DATETIME default now(),
  primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table trigger_log (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  username varchar(255),
  created DATETIME default now(),
  mood_level INT default 0,
  is_trigger BIT default 0,
  primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;