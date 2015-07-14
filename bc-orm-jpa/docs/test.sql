drop table if exists bc_jpa_book_detail;
drop table if exists bc_jpa_book;

CREATE TABLE bc_jpa_book(
  id serial NOT NULL,
  code character varying(255),
  PRIMARY KEY (id)
);

CREATE TABLE bc_jpa_book_detail(
  id int NOT NULL,
  info character varying(255),
  PRIMARY KEY (id),
  foreign key (id) REFERENCES bc_jpa_book (id)
);

select * from bc_jpa_book b left join bc_jpa_book_detail d on d.id = b.id;