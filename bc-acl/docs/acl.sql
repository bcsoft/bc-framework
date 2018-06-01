drop table if exists BC_ACL_HISTORY;
drop table if exists BC_ACL_ACTOR;
drop table if exists BC_ACL_DOC;

-- 访问对象
create table BC_ACL_DOC (
  ID            int          not null,
  DOC_ID        varchar(255) not null,
  DOC_TYPE      varchar(255) not null,
  DOC_NAME      varchar(255) not null,
  AUTHOR_ID     int          not null,
  FILE_DATE     timestamp    not null,
  MODIFIER_ID   int,
  MODIFIED_DATE timestamp,
  constraint BCPK_ACL_DOC primary key (ID),
  constraint BCUK_ACL_DOC unique (DOC_ID, DOC_TYPE)
);
comment on table BC_ACL_DOC is '访问对象';
comment on column BC_ACL_DOC.ID is 'ID';
comment on column BC_ACL_DOC.DOC_ID is '文档标识';
comment on column BC_ACL_DOC.DOC_TYPE is '文档类型';
comment on column BC_ACL_DOC.DOC_NAME is '文档名称';
comment on column BC_ACL_DOC.AUTHOR_ID is '创建人ID';
comment on column BC_ACL_DOC.FILE_DATE is '创建时间';
comment on column BC_ACL_DOC.MODIFIER_ID is '最后修改人ID';
comment on column BC_ACL_DOC.MODIFIED_DATE is '最后修改时间';
alter table BC_ACL_DOC
  add constraint BCFK_ACL_DOC_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;
alter table BC_ACL_DOC
  add constraint BCFK_ACL_DOC_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;

-- 访问者
create table BC_ACL_ACTOR (
  PID           int default 0 not null,
  AID           int           not null,
  ROLE          varchar(255)  not null,
  ORDER_        int default 0 not null,
  MODIFIER_ID   int           not null,
  MODIFIED_DATE timestamp     not null,
  constraint BCPK_ACL_ACTOR primary key (PID, AID)
);
comment on table BC_ACL_ACTOR is '访问者';
comment on column BC_ACL_ACTOR.PID is '访问对象ID';
comment on column BC_ACL_ACTOR.AID is '访问者ID : 对应Actor的ID';
comment on column BC_ACL_ACTOR.ROLE is '访问权限 : 右边数起第1位控制查阅,第2位控制编辑;0代表无此权限,1代表有此权限';
comment on column BC_ACL_ACTOR.MODIFIER_ID is '最后修改人ID';
comment on column BC_ACL_ACTOR.MODIFIED_DATE is '最后修改时间';
comment on column BC_ACL_ACTOR.ORDER_ is '排序号';
alter table BC_ACL_ACTOR
  add constraint BCFK_ACL_ACTOR_PID foreign key (PID)
references BC_ACL_DOC (ID)
on update restrict on delete cascade;
alter table BC_ACL_ACTOR
  add constraint BCFK_ACL_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID)
on update restrict on delete restrict;
alter table BC_ACL_ACTOR
  add constraint BCFK_ACL_ACTOR_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;

-- 访问历史
create table BC_ACL_HISTORY (
  ID          int           not null,
  DOC_ID      varchar(255)  not null,
  DOC_TYPE    varchar(255)  not null,
  DOC_NAME    varchar(255)  not null,
  AHID        int           not null,
  ACCESS_DATE timestamp     not null,
  SRC         varchar(255)  not null,
  URL         varchar(1000) not null,
  PID         int,
  ROLE        varchar(255),
  constraint BCPK_ACL_HISTORY primary key (ID)
);
comment on table BC_ACL_HISTORY is '访问历史';
comment on column BC_ACL_HISTORY.ID is 'ID';
comment on column BC_ACL_HISTORY.DOC_ID is '文档标识 : 指实际所访问信息的ID';
comment on column BC_ACL_HISTORY.DOC_TYPE is '文档类型 : 指实际所访问信息的类型';
comment on column BC_ACL_HISTORY.DOC_NAME is '文档名称 : 指实际所访问信息的描述';
comment on column BC_ACL_HISTORY.AHID is '访问者HID';
comment on column BC_ACL_HISTORY.ACCESS_DATE is '访问时间';
comment on column BC_ACL_HISTORY.SRC is '来源 : 如流程实例监控、流程部署监控、部门监控等';
comment on column BC_ACL_HISTORY.URL is '链接';
comment on column BC_ACL_HISTORY.PID is '访问对象ID : 当从ACL控制中得到访问权限时才记录';
comment on column BC_ACL_HISTORY.ROLE is '访问权限';
alter table BC_ACL_HISTORY
  add constraint BCFK_ACL_HISTORY_ACTORHISTORY foreign key (AHID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;