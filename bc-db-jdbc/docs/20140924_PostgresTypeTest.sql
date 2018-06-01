-- postgres数据库特殊类型的测试用表
drop table if exists BC_EXAMPLE;
-- 测试用的表
create table BC_EXAMPLE (
  ID        integer      not null,
  NAME      varchar(255) not null,
  CODE      varchar(255),
  JSON_     json,
  ARRAY_INT integer [],
  primary key (ID)
);
comment on table BC_EXAMPLE is '测试用的表';
comment on column BC_EXAMPLE.NAME is '名称';

alter table bc_example
  add column JSON_ json;
alter table bc_example
  add column ARRAY_INT integer [];

--TEST
delete from bc_example;
insert into bc_example (id, name, code, json_, array_int)
values (1, 'n1', 'c1', '{
  "f1": {
    "f11": 11,
    "f12": 12
  },
  "f2": 2
}', array [1, 2])
  , (2, 'n2', 'c2', '{
  "f1": 2,
  "g2": {
    "g11": 11,
    "g12": 12
  }
}', array [21, 21])
  , (3, 'n3', 'c3', '{
  "f1": [
    1,
    "Robert \"M\"",
    true
  ]
}', array [21, 21]);

select *
from bc_example;
-- json 查询：第一个运算符是 “->”, 用来直接从 JSON 数据库获取字段值，使用文本值来标注字段的键：
select id, json_, json_ -> 'f1' as f1, json_ -> 'g1' as g1, json_ -> 'f1' -> 'f12' as f12
from bc_example;
-- json 查询：第二个运算符是 “->>”, 返回指定的文本，“->>” 返回纯文本。
select id, json_, json_ ->> 'f1' as f1
from bc_example;
-- 最后两个运算符是 “#>” 和 “#>>”. 用来直接获取数组中的元素
select id, json_, json_ #> '{f1,1}' f1, json_ #>> '{f1,1}' as f1_s
from bc_example;
