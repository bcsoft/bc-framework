drop table if exists bc_jpa_book_detail;
drop table if exists bc_jpa_book;

create table bc_jpa_book (
  id        serial not null primary key,
  name      character varying(255), -- 书名
  bool      boolean,
  date      date,
  time      time,
  timestamp timestamp
);

create table bc_jpa_book_detail (
  id        integer not null primary key,
  publisher character varying(255), -- 出版社
  constraint bcfk_jpa_book_detail_id foreign key (id)
  references bc_jpa_book (id) on update no action on delete no action
);


select *
from bc_jpa_book b left join bc_jpa_book_detail d on d.id = b.id;