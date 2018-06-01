-- 工作日模块建表脚本
drop table if exists BC_WORKDAY;
create table BC_WORKDAY (
  ID        serial  not null primary key,
  DAYOFF    boolean not null,
  FROM_DATE date    not null unique,
  TO_DATE   date    not null check (TO_DATE >= FROM_DATE),
  DESC_     varchar(1000)
  -- 需要程序约束不同记录不能存在时间出现交叉的情况
);
comment on table BC_WORKDAY is '工作日';
comment on column BC_WORKDAY.DAYOFF is 'TRUE: 休息日, FALSE: 工作日';
comment on column BC_WORKDAY.FROM_DATE is '开始日期';
comment on column BC_WORKDAY.TO_DATE is '结束日期';
comment on column BC_WORKDAY.DESC_ is '备注';