create table image (
  image_src varchar(255) not null,
  source varchar(255) not null,
  source_type varchar(255) not null,
  tag varchar(255) not null,
  primary KEY(image_src, tag)
)