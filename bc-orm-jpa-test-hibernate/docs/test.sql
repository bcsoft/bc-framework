drop table if exists t_jpa_book_detail;
drop table if exists t_jpa_book;

create table t_jpa_book (
  id bigserial primary key,
  name varchar(255), -- 书名
  bool boolean,
  date date,
  time time,
  timestamp timestamp
);

create table t_jpa_book_detail (
  id bigint primary key,
  publisher varchar(255), -- 出版社
  constraint bcfk_jpa_book_detail_id foreign key (id)
    references t_jpa_book (id) on update no action on delete no action
);

select * from t_jpa_book b left join t_jpa_book_detail d on d.id = b.id;


drop table if exists t_user;
create table t_user (
  id bigserial primary key,
  code varchar(10),
  name varchar(10)
);
insert into t_user(code, name) values
  ('c2', 'n2'),
  ('c1', 'n1'),
  ('c3', 'n3');

select * from t_user order by code;