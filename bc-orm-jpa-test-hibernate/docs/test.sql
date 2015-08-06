DROP TABLE if exists bc_jpa_book_detail;
DROP TABLE if exists bc_jpa_book;

CREATE TABLE bc_jpa_book (
  id serial NOT NULL PRIMARY key,
  name character varying(255), -- 书名
  bool boolean,
  date Date,
  time time,
  timestamp timestamp
);

CREATE TABLE bc_jpa_book_detail (
  id integer NOT NULL PRIMARY key,
  publisher character varying(255), -- 出版社
  CONSTRAINT bcfk_jpa_book_detail_id FOREIGN KEY (id)
      REFERENCES bc_jpa_book (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);


select * from bc_jpa_book b left join bc_jpa_book_detail d on d.id = b.id;