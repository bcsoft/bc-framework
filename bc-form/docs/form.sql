/* 清除资源、角色配置数据
delete from BC_IDENTITY_ROLE_RESOURCE where sid in 
	(select id from BC_IDENTITY_RESOURCE where ORDER_ in ('800440'));
delete from BC_IDENTITY_RESOURCE where ORDER_ in ('800440');
delete from BC_TEMPLATE_TYPE where CODE='MUSTACHE' AND NAME='MUSTACHE模板';
*/

--删除自定义表单相关表
drop table if exists BC_FORM_FIELD_LOG;
drop table if exists BC_FORM_FIELD;
drop table if exists BC_FORM;

--自定义表单相关表
--自定义表单表
create table BC_FORM (
  ID            int          not null,
  PID           int          not null,
  UID_          varchar(36)  not null,
  TYPE_         varchar(255) not null,
  CODE          varchar(255) not null,
  STATUS_       int          not null,
  SUBJECT       varchar(255) not null,
  TPL_          varchar(255) not null,
  VER_          varchar(20)  not null,
  DESC_         varchar(255) not null,
  EXT01         varchar(255) not null,
  EXT02         varchar(255) not null,
  EXT03         varchar(255) not null,
  AUTHOR_ID     int          not null,
  FILE_DATE     timestamp    not null,
  MODIFIER_ID   int          not null,
  MODIFIED_DATE timestamp    not null,
  constraint BCPK_FORM primary key (ID)
);
comment on table BC_FORM is '表单';
comment on column BC_FORM.ID is 'ID';
comment on column BC_FORM.PID is 'PID';
comment on column BC_FORM.UID_ is '关联附件的标识号';
comment on column BC_FORM.TYPE_ is '类别 : 用于区分模块，通常使用Domain类名';
comment on column BC_FORM.CODE is '编码';
comment on column BC_FORM.STATUS_ is '状态 : -1-草稿，0-已提交';
comment on column BC_FORM.SUBJECT is '标题';
comment on column BC_FORM.TPL_ is '模板编码';
comment on column BC_FORM.VER_ is '本版';
comment on column BC_FORM.DESC_ is '备注';
comment on column BC_FORM.EXT01 is '扩展域1';
comment on column BC_FORM.EXT02 is '扩展域2';
comment on column BC_FORM.EXT03 is '扩展域3';
comment on column BC_FORM.AUTHOR_ID is '创建人ID';
comment on column BC_FORM.FILE_DATE is '创建时间';
comment on column BC_FORM.MODIFIER_ID is '最后修改人ID';
comment on column BC_FORM.MODIFIED_DATE is '最后修改时间';
alter table BC_FORM
  add constraint BC_FORM_MODIFIER foreign key (MODIFIER_ID)
references PUBLIC.BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict
on delete restrict;
alter table BC_FORM
  add constraint BC_FORM_AUTHOR foreign key (AUTHOR_ID)
references PUBLIC.BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict
on delete restrict;
create unique index BCUK_FORM_PARENT on bc_form (type_, code, pid, ver_);

create table BC_FORM_FIELD (
  ID     int         not null,
  PID    int         not null,
  NAME_  varchar(20) not null,
  LABEL_ varchar(40) not null,
  TYPE_  varchar(20) not null,
  VALUE_ varchar(4000),
  constraint BCPK_FORM_FIELD primary key (ID)
);
comment on table BC_FORM_FIELD is '表单字段';
comment on column BC_FORM_FIELD.ID is 'ID';
comment on column BC_FORM_FIELD.PID is '所属表单';
comment on column BC_FORM_FIELD.NAME_ is '名称';
comment on column BC_FORM_FIELD.LABEL_ is '标签';
comment on column BC_FORM_FIELD.TYPE_ is '值类型 : int,float,string,date,datetime,boolean等';
comment on column BC_FORM_FIELD.VALUE_ is '值';
alter table BC_FORM_FIELD
  add constraint BCFK_FORM_FIELD_PID foreign key (PID)
references BC_FORM (ID)
on update restrict
on delete cascade;

create table BC_FORM_FIELD_LOG (
  ID          int         not null,
  PID         int         not null,
  VALUE_      varchar(4000),
  UPDATOR     int         not null,
  UPDATE_TIME timestamp   not null,
  BATCH_NO    varchar(50) not null,
  constraint BCPK_FORM_FIELD_LOG primary key (ID)
);
comment on table BC_FORM_FIELD_LOG is '表单字段审计日志';
comment on column BC_FORM_FIELD_LOG.ID is 'ID';
comment on column BC_FORM_FIELD_LOG.PID is '所属字段';
comment on column BC_FORM_FIELD_LOG.VALUE_ is '值';
comment on column BC_FORM_FIELD_LOG.UPDATOR is '更新人';
comment on column BC_FORM_FIELD_LOG.UPDATE_TIME is '更新时间';
comment on column BC_FORM_FIELD_LOG.BATCH_NO is '批号 : 多个字段同一次更新的标记号';
alter table BC_FORM_FIELD_LOG
  add constraint BC_FORM_FIELD_LOG_PID foreign key (PID)
references BC_FORM_FIELD (ID)
on update restrict
on delete cascade;
alter table BC_FORM_FIELD_LOG
  add constraint BC_FORM_FIELD_LOG_UPDATOR foreign key (UPDATOR)
references PUBLIC.BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict
on delete restrict;

--	插入资源
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('HIBERNATE_SEQUENCE'), 0, false, 2, m.id, '800440', '自定义表单管理', '/bc/formManages/paging', 'i0001',
    '系统维护'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '800000'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '自定义表单管理');
--	插入角色与资源之间的关系
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_ADMIN'
        and m.NAME = '自定义表单管理'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);

--插入模板类型
insert into bc_template_type (ID, STATUS_, ORDER_, CODE, NAME, IS_PATH, IS_PURE_TEXT, EXT, DESC_, FILE_DATE, AUTHOR_ID, MODIFIER_ID, MODIFIED_DATE)
  select nextval('hibernate_sequence'), 0, '0001', 'MUSTACHE', 'MUSTACHE模板', true, true, '', 'MUSTACHE模板', now(),
    (select id
     from bc_identity_actor_history
     where actor_code = 'admin' and current = true), (select id
                                                      from bc_identity_actor_history
                                                      where actor_code = 'admin' and current = true), now()
  from bc_dual
  where not exists(select 0
                   from bc_template_type
                   where code = 'MUSTACHE' and name = 'MUSTACHE模板');

--插入函数
--根据表单的pid、type、code和表单字段的key，查找表单字段对应的值
create or replace function get_formfieldvalue_byfromparem_fieldkey(formpid      integer, formtype character varying,
                                                                   formcode     character varying,
                                                                   formfieldkey character varying)
  returns character varying as
$BODY$
/** 根据表单的pid、type、code和表单字段的key，查找表单字段对应的值 */
declare
  --key对应的值
  value_ character varying;
begin
  select ff.value_
  into value_
  from bc_form_field ff
  inner join bc_form f on f.id = ff.pid
  where f.pid = formpid and f.type_ = formtype and f.code = formcode and ff.name_ = formfieldkey;
  return value_;
end;
$BODY$ language plpgsql;