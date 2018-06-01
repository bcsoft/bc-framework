drop table if exists BC_WORK_SOURCE;
drop table if exists BC_WORK_DONE;
drop table if exists BC_WORK_TODO;
drop table if exists BC_WORK;

-- 事项来源
create table BC_WORK_SOURCE (
  ID    int           not null,
  TYPE_ varchar(255)  not null,
  URL_  varchar(1000) not null,
  constraint BCPK_WORK_SOURCE primary key (ID)
);
comment on table BC_WORK_SOURCE is '事项来源';
comment on column BC_WORK_SOURCE.ID is 'ID';
comment on column BC_WORK_SOURCE.TYPE_ is '来源类型';
comment on column BC_WORK_SOURCE.URL_ is '访问地址';

-- 工作事项
create table BC_WORK (
  ID          int           not null,
  CATEGORY    varchar(500)  null,
  CODE        varchar(255),
  SUBJECT     varchar(500)  not null,
  DETAIL      varchar(1000) not null,
  SOURCE_TYPE varchar(255)  not null,
  SOURCE_ID   int           not null,
  FILE_DATE   timestamp,
  AUTHOR_ID   int,
  constraint BCPK_WORK primary key (ID)
);
comment on table BC_WORK is '工作事项';
comment on column BC_WORK.ID is 'ID';
comment on column BC_WORK.CATEGORY is '分类 : 可以多级分类，使用"/"连接，如"通知/内部通告"';
comment on column BC_WORK.CODE is '单号';
comment on column BC_WORK.SUBJECT is '标题';
comment on column BC_WORK.DETAIL is '详细说明';
comment on column BC_WORK.SOURCE_TYPE is '来源类型';
comment on column BC_WORK.SOURCE_ID is '来源标识';
comment on column BC_WORK.FILE_DATE is '创建时间';
comment on column BC_WORK.AUTHOR_ID is '创建人ID';
alter table BC_WORK
  add constraint BCFK_WORK_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 经办事项
create table BC_WORK_DONE (
  ID           int          not null,
  TYPE_        int          not null,
  WID          int          not null,
  PRIORITY     int          not null,
  SEND_DATE    timestamp    not null,
  SENDER_ID    int          not null,
  WORKER_ID    int          not null,
  SUBJECT      varchar(500) not null,
  DETAIL       varchar(1000),
  DEADLINE     timestamp,
  CONFIRM_DATE timestamp,
  TEAM_ID      int,
  DONE_DATE    timestamp    not null,
  EXECUTOR_ID  int,
  constraint BCPK_WORK_DONE primary key (ID)
);
comment on table BC_WORK_DONE is '经办事项';
comment on column BC_WORK_DONE.ID is 'ID';
comment on column BC_WORK_DONE.TYPE_ is '经办类型 : 0-指派任务,1-个人经办,2-岗位经办,3-部门经办,4-单位经办';
comment on column BC_WORK_DONE.WID is '工作事项ID';
comment on column BC_WORK_DONE.PRIORITY is '优先级';
comment on column BC_WORK_DONE.SEND_DATE is '发送时间';
comment on column BC_WORK_DONE.SENDER_ID is '发送人ID';
comment on column BC_WORK_DONE.WORKER_ID is '经办人ID';
comment on column BC_WORK_DONE.SUBJECT is '标题';
comment on column BC_WORK_DONE.DETAIL is '详细说明';
comment on column BC_WORK_DONE.DEADLINE is '办理期限';
comment on column BC_WORK_DONE.CONFIRM_DATE is '确认时间';
comment on column BC_WORK_DONE.TEAM_ID is '源组织ID : 仅用于指派待办人的经办类型，记录源组织类待办的待办人ID';
comment on column BC_WORK_DONE.DONE_DATE is '完成时间';
comment on column BC_WORK_DONE.EXECUTOR_ID is '执行人ID : 仅用于指派待办人的经办类型，记录被指派到的待办人ID';
alter table BC_WORK_DONE
  add constraint BCFK_WORK_DONE_WID foreign key (WID)
references BC_WORK (ID);
alter table BC_WORK_DONE
  add constraint BCFK_WORK_DONE_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_WORK_DONE
  add constraint BCFK_WORK_DONE_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_WORK_DONE
  add constraint BCFK_WORK_DONE_TEAM foreign key (TEAM_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_WORK_DONE
  add constraint BCFK_WORK_DONE_EXECUTOR foreign key (EXECUTOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 待办事项
create table BC_WORK_TODO (
  ID           int          not null,
  TYPE_        int          not null,
  WID          int          not null,
  PRIORITY     int          not null,
  SEND_DATE    timestamp    not null,
  SENDER_ID    int          not null,
  WORKER_ID    int          not null,
  SUBJECT      varchar(500) not null,
  DETAIL       varchar(1000),
  DEADLINE     timestamp,
  CONFIRM_DATE timestamp,
  TEAM_ID      int,
  constraint BCPK_WORK_TODO primary key (ID)
);
comment on table BC_WORK_TODO is '待办事项';
comment on column BC_WORK_TODO.ID is 'ID';
comment on column BC_WORK_TODO.TYPE_ is '待办类型 : 1-个人待办,2-岗位待办,3-部门待办,4-单位待办';
comment on column BC_WORK_TODO.WID is '工作事项ID';
comment on column BC_WORK_TODO.PRIORITY is '优先级';
comment on column BC_WORK_TODO.SEND_DATE is '发送时间';
comment on column BC_WORK_TODO.SENDER_ID is '发送人ID';
comment on column BC_WORK_TODO.WORKER_ID is '待办人ID';
comment on column BC_WORK_TODO.SUBJECT is '标题';
comment on column BC_WORK_TODO.DETAIL is '详细说明';
comment on column BC_WORK_TODO.DEADLINE is '办理期限';
comment on column BC_WORK_TODO.CONFIRM_DATE is '确认时间';
comment on column BC_WORK_TODO.TEAM_ID is '源组织ID : 指用户所认领的组织类待办的待办人ID，或者被指派的组织类待办的ID';
alter table BC_WORK_TODO
  add constraint BCFK_WORK_TODO_WID foreign key (WID)
references BC_WORK (ID);
alter table BC_WORK_TODO
  add constraint BCFK_WORK_TODO_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_WORK_TODO
  add constraint BCFK_WORK_TODO_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_WORK_TODO
  add constraint BCFK_WORK_TODO_TEAM foreign key (TEAM_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
