drop table if exists T1;
create table T1 (
  ` id` bigint not null auto_increment,
  `pid` bigint,
  `code` varchar (255
),
primary key (`id`
)
);
insert into t1 (id, pid, code) values (1, null, '1');
insert into t1 (id, pid, code) values (2, null, '2');
insert into t1 (id, pid, code) values (3, 1, '1.1');
insert into t1 (id, pid, code) values (4, 1, '1.2');
insert into t1 (id, pid, code) values (5, 2, '2.1');
insert into t1 (id, pid, code) values (6, 3, '1.1.1');
select *
from t1
order by code;
