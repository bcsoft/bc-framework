drop table if exists bc_jpa_book_detail;
drop table if exists bc_jpa_book;

create table bc_jpa_book (
  id   serial not null,
  code character varying(255),
  primary key (id)
);

create table bc_jpa_book_detail (
  id   int not null,
  info character varying(255),
  primary key (id),
  foreign key (id) references bc_jpa_book (id)
);

select *
from bc_jpa_book b left join bc_jpa_book_detail d on d.id = b.id;