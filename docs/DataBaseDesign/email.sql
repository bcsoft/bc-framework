drop table if exists BC_EMAIL_HISTORY;
drop table if exists BC_EMAIL_TO;
drop table if exists BC_EMAIL_TRASH;
drop table if exists BC_EMAIL;

create table BC_EMAIL (
  ID        int           not null,
  UID_      varchar(32)   not null,
  TYPE_     int default 0 not null,
  PID       int,
  STATUS_   int default 0 not null,
  SUBJECT   varchar(500)  not null,
  CONTENT   text,
  SEND_DATE timestamp,
  SENDER_ID int           not null,
  FILE_DATE timestamp     not null,
  constraint BCPK_EMAIL primary key (ID)
);
comment on table BC_EMAIL is '邮件发件箱';
comment on column BC_EMAIL.ID is 'ID';
comment on column BC_EMAIL.UID_ is 'UID';
comment on column BC_EMAIL.TYPE_ is '类型 : 0-新邮件,1-回复,2-转发';
comment on column BC_EMAIL.PID is '源邮件ID : 转发回复时使用';
comment on column BC_EMAIL.STATUS_ is '状态 : 0-草稿,1-已发送';
comment on column BC_EMAIL.SUBJECT is '标题';
comment on column BC_EMAIL.CONTENT is '内容';
comment on column BC_EMAIL.SEND_DATE is '发送日期 : 对于提醒信息与创建时间相等';
comment on column BC_EMAIL.SENDER_ID is '发送人ID : 对应Actor的ID';
comment on column BC_EMAIL.FILE_DATE is '创建时间';
alter table BC_EMAIL
  add constraint BCFK_EMAIL_PID foreign key (PID)
references BC_EMAIL (ID) on update restrict on delete restrict;
alter table BC_EMAIL
  add constraint BCFK_EMAIL_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;

create table BC_EMAIL_TO (
  ID          int                     not null,
  PID         int default 0           not null,
  TYPE_       int                     not null,
  RECEIVER_ID int                     not null,
  UPPER_ID    int,
  ORDER_      int default 0           not null,
  READ_       boolean default 'false' not null,
  constraint BCPK_EMAIL_TO primary key (ID)
);
comment on table BC_EMAIL_TO is '邮件收件箱';
comment on column BC_EMAIL_TO.ID is 'ID';
comment on column BC_EMAIL_TO.PID is '邮件ID';
comment on column BC_EMAIL_TO.TYPE_ is '发送类型 : 0-主送,1-抄送,2-密送';
comment on column BC_EMAIL_TO.RECEIVER_ID is '收件人ID : 对应Actor的ID';
comment on column BC_EMAIL_TO.UPPER_ID is '收件人所属岗位或单位或部门的ID : 对应Actor的ID';
comment on column BC_EMAIL_TO.ORDER_ is '排序号 : 针对同一发送类型的不同收件人之间的排序';
comment on column BC_EMAIL_TO.READ_ is '已阅标记';
alter table BC_EMAIL_TO
  add constraint BCFK_EMAIL_TO_PID foreign key (PID)
references BC_EMAIL (ID) on update restrict on delete restrict;
alter table BC_EMAIL_TO
  add constraint BCFK_EMAIL_TO_RECEIVER foreign key (RECEIVER_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;
alter table BC_EMAIL_TO
  add constraint BCFK_EMAIL_TO_UPPER foreign key (UPPER_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;

create table BC_EMAIL_HISTORY (
  ID        int       not null,
  PID       int       not null,
  READER_ID int       not null,
  FILE_DATE timestamp not null,
  constraint BCPK_EMAIL_HISTORY primary key (ID)
);
comment on table BC_EMAIL_HISTORY is '邮件查阅历史';
comment on column BC_EMAIL_HISTORY.ID is 'ID';
comment on column BC_EMAIL_HISTORY.PID is '邮件ID';
comment on column BC_EMAIL_HISTORY.READER_ID is '查阅人ID : 对应ActorHistory的ID';
comment on column BC_EMAIL_HISTORY.FILE_DATE is '查阅时间';
alter table BC_EMAIL_HISTORY
  add constraint BCFK_EMAIL_HISTORY_PID foreign key (PID)
references BC_EMAIL (ID) on update restrict on delete restrict;
alter table BC_EMAIL_HISTORY
  add constraint BCFK_EMAIL_HISTORY_READER foreign key (READER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID) on update restrict on delete restrict;

create table BC_EMAIL_TRASH (
  ID          int           not null,
  PID         int           not null,
  SRC         int           not null,
  OWNER_ID    int           not null,
  STATUS_     int default 0 not null,
  HANDLE_DATE timestamp     not null,
  constraint BCPK_EMAIL_TRASH primary key (ID)
);
comment on table BC_EMAIL_TRASH is '邮件垃圾箱';
comment on column BC_EMAIL_TRASH.ID is 'ID';
comment on column BC_EMAIL_TRASH.PID is '邮件ID';
comment on column BC_EMAIL_TRASH.SRC is '来源:1-发件箱 2-收件箱';
comment on column BC_EMAIL_TRASH.OWNER_ID is '所有者ID : 对应Actor的ID';
comment on column BC_EMAIL_TRASH.STATUS_ is '状态 : 0-可恢复,1-已删除';
comment on column BC_EMAIL_TRASH.HANDLE_DATE is '操作时间';
alter table BC_EMAIL_TRASH
  add constraint BCFK_EMAIL_TRASH_PID foreign key (PID)
references BC_EMAIL (ID) on update restrict on delete restrict;
alter table BC_EMAIL_TRASH
  add constraint BCFK_EMAIL_TRASH_OWNER foreign key (OWNER_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;

-- 资源配置：我的邮件
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '010400', '我的邮件', null, 'i0004'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '010000'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '010400');

-- 资源配置：收件箱
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '010410', '收件箱', '/bc/emailTos/paging', 'i0408'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '010400'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '010410');

-- 资源配置：发件箱
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '010420', '发件箱', '/bc/emailSends/paging', 'i0409'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '010400'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '010420');

-- 资源配置：垃圾箱
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '010430', '垃圾箱', '/bc/emailTrashs/paging', 'i0409'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '010400'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '010430');

-- 资源配置：邮箱管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800330', '邮箱管理', null, 'i0004'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '800000'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '800330');

-- 资源配置：发件管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800331', '发件管理', '/bc/emailSend2Manages/paging', 'i0409'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '800330'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '800331');

-- 资源配置：收件管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800332', '收件管理', '/bc/emailTo2Manages/paging', 'i0408'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '800330'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '800332');

-- 资源配置：查阅历史管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800333', '查阅历史管理', '/bc/emailHistory2Manages/paging', 'i0408'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '800330'
        and not exists(select 1
                       from bc_identity_resource
                       where order_ = '800333');

-- 角色配置：电子邮箱 BC_EMAIL 管理自己的邮件
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
values (NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0155', 'BC_EMAIL', '电子邮箱');

-- 角色配置：电子邮箱管理 BC_EMAIL_MANAGE 管理所有的邮件
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
values (NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0156', 'BC_EMAIL_MANAGE', '电子邮箱管理');

-- 权限访问配置：通用角色
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_COMMON'
        and m.type_ > 1 and m.order_ in ('010410', '010420', '010430')
        and not exists(select 1
                       from BC_IDENTITY_ROLE_RESOURCE
                       where rid = (select r2.id
                                    from BC_IDENTITY_ROLE r2
                                    where r2.code = 'BC_COMMON')
                             and sid in (select m2.id
                                         from BC_IDENTITY_RESOURCE m2
                                         where m2.type_ > 1 and m2.order_ in ('010410', '010420', '010430')))
  order by m.order_;

-- 权限访问配置：超级管理员
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_ADMIN'
        and m.type_ > 1 and m.order_ in ('010410', '010420', '010430', '800331', '800332', '800333')
        and not exists(select 1
                       from BC_IDENTITY_ROLE_RESOURCE
                       where rid = (select r2.id
                                    from BC_IDENTITY_ROLE r2
                                    where r2.code = 'BC_ADMIN')
                             and sid in (select m2.id
                                         from BC_IDENTITY_RESOURCE m2
                                         where m2.type_ > 1 and m2.order_ in
                                                                ('010410', '010420', '010430', '800331', '800332', '800333')))
  order by m.order_;

-- 权限访问配置：电子邮箱
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_EMAIL'
        and m.type_ > 1 and m.order_ in ('010410', '010420', '010430')
        and not exists(select 1
                       from BC_IDENTITY_ROLE_RESOURCE
                       where rid = (select r2.id
                                    from BC_IDENTITY_ROLE r2
                                    where r2.code = 'BC_EMAIL')
                             and sid in (select m2.id
                                         from BC_IDENTITY_RESOURCE m2
                                         where m2.type_ > 1 and m2.order_ in ('010410', '010420', '010430')))
  order by m.order_;

-- 权限访问配置：电子邮箱管理
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_EMAIL_MANAGE'
        and m.type_ > 1 and m.order_ in ('800331', '800332', '800333')
        and not exists(select 1
                       from BC_IDENTITY_ROLE_RESOURCE
                       where rid = (select r2.id
                                    from BC_IDENTITY_ROLE r2
                                    where r2.code = 'BC_EMAIL_MANAGE')
                             and sid in (select m2.id
                                         from BC_IDENTITY_RESOURCE m2
                                         where m2.type_ > 1 and m2.order_ in ('800331', '800332', '800333')))
  order by m.order_;

-- 获取邮件的收件人
create or replace function getemailreceiver2json(emailid integer)
  returns varchar as
$BODY$
declare
  -- 定义变量
  -- 收件人
  receiver          character varying;
  receiver_upper_id integer;
  -- 抄送
  cc                character varying;
  cc_upper_id       integer;
  -- 密送
  bcc               character varying;
  bcc_upper_id      integer;
  -- 变量一行结果的记录
  rowinfo           record;
begin
  receiver := '';
  cc := '';
  bcc := '';

  -- 循环集合
  for rowinfo in select t.upper_id, a.name aname, u.name uname, t.type_
                 from bc_email_to t
                 inner join bc_identity_actor a on a.id = t.receiver_id
                 left join bc_identity_actor u on u.id = t.upper_id
                 where t.pid = emailid
                 order by t.type_, t.order_
  loop
    -- 类型为收件人时
    if rowinfo.type_ = 0
    then
      -- 先判断是否带上级组织
      if rowinfo.upper_id is null
      then
        -- 判断是否添加逗号
        if char_length(receiver) > 0
        then receiver := receiver || ','; end if;
        -- 拼接收件人名称字符串
        receiver := receiver || rowinfo.aname;
      -- 相同上级组织的收件人只显示上级组织名称
      elseif rowinfo.upper_id != receiver_upper_id or receiver_upper_id is null
        then
          if char_length(receiver) > 0
          then receiver := receiver || ','; end if;
          receiver := receiver || rowinfo.uname;
          receiver_upper_id := rowinfo.upper_id;
      end if;
    elseif rowinfo.type_ = 1
      then
        if rowinfo.upper_id is null
        then
          if char_length(cc) > 0
          then cc := cc || ','; end if;
          cc := cc || rowinfo.aname;
        elseif rowinfo.upper_id != cc_upper_id or cc_upper_id is null
          then
            if char_length(cc) > 0
            then cc := cc || ','; end if;
            cc := cc || rowinfo.uname;
            cc_upper_id := rowinfo.upper_id;
        end if;
    elseif rowinfo.type_ = 2
      then
        if rowinfo.upper_id is null
        then
          if char_length(bcc) > 0
          then bcc := bcc || ','; end if;
          bcc := bcc || rowinfo.aname;
        elseif rowinfo.upper_id != bcc_upper_id or bcc_upper_id is null
          then
            if char_length(bcc) > 0
            then bcc := bcc || ','; end if;
            bcc := bcc || rowinfo.uname;
            bcc_upper_id := rowinfo.upper_id;
        end if;
    end if;
  end loop;

  return '{"receiver":"' || receiver || '","cc":"' || cc || '","bcc":"' || bcc || '"}';
end;
$BODY$
language plpgsql;

-- 获取邮件的接收人查阅邮件的次数
create or replace function getemailreceiverreadcount(emailid integer, receivercode varchar)
  returns int as
$BODY$
declare
  i int;
begin
  select count(h.id) into i
  from bc_email_history h
  inner join bc_identity_actor_history ah on ah.id = h.reader_id
  where h.pid = emailid and ah.actor_code = receivercode;
  return i;
end;
$BODY$
language plpgsql;