drop table if exists BC_SUBSCRIBE_ACTOR;
drop table if exists BC_SUBSCRIBE;

-- 订阅部署表
create table BC_SUBSCRIBE (
  ID            int           not null,
  STATUS_       int default 0 not null,
  TYPE_         int default 0 not null,
  ORDER_        varchar(255),
  SUBJECT       varchar(500)  not null,
  EVENT_CODE    varchar(255)  not null unique,
  AUTHOR_ID     int           not null,
  FILE_DATE     timestamp     not null,
  MODIFIER_ID   int,
  MODIFIED_DATE timestamp,
  constraint BCPK_SUBSCRIBE primary key (ID)
);
comment on table BC_SUBSCRIBE is '订阅';
comment on column BC_SUBSCRIBE.ID is 'ID';
comment on column BC_SUBSCRIBE.STATUS_ is '状态 : -1-草稿,0-已发布,1-禁用';
comment on column BC_SUBSCRIBE.TYPE_ is '类型 : 0-邮件,1-短信';
comment on column BC_SUBSCRIBE.ORDER_ is '排序号';
comment on column BC_SUBSCRIBE.SUBJECT is '订阅标题';
comment on column BC_SUBSCRIBE.EVENT_CODE is '事件编码';
comment on column BC_SUBSCRIBE.AUTHOR_ID is '创建人ID';
comment on column BC_SUBSCRIBE.FILE_DATE is '创建时间';
comment on column BC_SUBSCRIBE.MODIFIER_ID is '最后修改人ID';
comment on column BC_SUBSCRIBE.MODIFIED_DATE is '最后修改时间';
alter table BC_SUBSCRIBE
  add constraint BCFK_SUBSCRIBE_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID) on update restrict on delete restrict;
alter table BC_SUBSCRIBE
  add constraint BCFK_SUBSCRIBE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID) on update restrict on delete restrict;

-- 订阅者表
create table BC_SUBSCRIBE_ACTOR (
  PID       int           not null,
  AID       int           not null,
  TYPE_     int default 0 not null,
  FILE_DATE timestamp     not null,
  constraint BCPK_SUBSCRIBE_ACTOR primary key (PID, AID)
);
comment on table BC_SUBSCRIBE_ACTOR is '订阅者';
comment on column BC_SUBSCRIBE_ACTOR.PID is '订阅ID';
comment on column BC_SUBSCRIBE_ACTOR.AID is '订阅者ID : 对应Actor的ID';
comment on column BC_SUBSCRIBE_ACTOR.TYPE_ is '类型: 0-用户订阅，1-系统推送';
comment on column BC_SUBSCRIBE_ACTOR.FILE_DATE is '订阅日期';
alter table BC_SUBSCRIBE_ACTOR
  add constraint BCFK_SUBSCRIBE_ACTOR_PID foreign key (PID)
references BC_SUBSCRIBE (ID) on update restrict on delete cascade;
alter table BC_SUBSCRIBE_ACTOR
  add constraint BCFK_SUBSCRIBE_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;