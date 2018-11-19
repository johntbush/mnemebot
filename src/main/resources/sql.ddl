
CREATE USER 'admin'@'%' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON * . * TO 'admin'@'%';
flush privileges;
CREATE DATABASE bot;

create table bot.image (
  image_src varchar(255) not null,
  source varchar(255) not null,
  source_type varchar(255) not null,
  tag varchar(255) not null,
  primary KEY(image_src, tag)
);