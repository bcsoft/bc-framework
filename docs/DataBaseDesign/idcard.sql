drop table if exists BC_IDCARD_CHECK;
drop table if exists BC_IDCARD;
drop table if exists BC_IDCARD_PIC;
drop sequence if exists IDCARD_SEQUENCE;

-- 创建刷卡记录用的序列，开始于1000
create sequence IDCARD_SEQUENCE
  minvalue 1
  start with 1000
  increment by 1
  cache 20;

create table BC_IDCARD (
  ID            int          not null,
  TYPE_         int          not null default 0,
  CODE          varchar(20)  not null,
  NAME          varchar(255) not null,
  SEX           varchar(10)  not null,
  FOLK          varchar(10)  not null,
  BIRTHDATE     date         not null,
  ISSUE         varchar(255) not null,
  START_DATE    date         not null,
  END_DATE      date         not null,
  ADDRESS       varchar(500) not null,
  NEW_ADDRESS   varchar(500),
  FILE_DATE     timestamp    not null,
  AUTHOR_ID     int          not null,
  MODIFIED_DATE timestamp,
  MODIFIER_ID   int,
  ISNEW         int          not null default 0,
  constraint BCPK_IDCARD primary key (ID),
  constraint BCUK_IDCARD_NUM unique (TYPE_, CODE)
);
comment on table BC_IDCARD is '身份证';
comment on column BC_IDCARD.ID is 'ID';
comment on column BC_IDCARD.TYPE_ is '用途:0-司机招聘,1-系统用户';
comment on column BC_IDCARD.CODE is '身份证号';
comment on column BC_IDCARD.NAME is '姓名';
comment on column BC_IDCARD.SEX is '性别';
comment on column BC_IDCARD.FOLK is '民族 : (不带路径的部分)';
comment on column BC_IDCARD.BIRTHDATE is '出生日期';
comment on column BC_IDCARD.ISSUE is '签发机关';
comment on column BC_IDCARD.START_DATE is '有效期-开始';
comment on column BC_IDCARD.END_DATE is '有效期-结束';
comment on column BC_IDCARD.ADDRESS is '住址';
comment on column BC_IDCARD.NEW_ADDRESS is '最新住址';
comment on column BC_IDCARD.ISNEW is '是否为最新记录:0-最新,1-历史';
comment on column BC_IDCARD.FILE_DATE is '创建时间';
comment on column BC_IDCARD.AUTHOR_ID is '创建人:对应Actor的ID';
comment on column BC_IDCARD.MODIFIED_DATE is '最后更新时间';
comment on column BC_IDCARD.MODIFIER_ID is '最后更新人:对应Actor的ID';
alter table BC_IDCARD
  add constraint BCFK_IDCARD_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;
alter table BC_IDCARD
  add constraint BCFK_IDCARD_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;
create index BCIDX_IDCARD_CODE on BC_IDCARD (TYPE_, CODE, ISNEW);

-- 身份证图像
create table BC_IDCARD_PIC (
  ID        int        not null,
  TYPE_     varchar(5) not null,
  PID       int        not null,
  FILE_DATE timestamp  not null,
  DATA_     bytea,
  constraint BCPK_IDCARD_PIC primary key (ID)
);
comment on table BC_IDCARD_PIC is '身份证图像';
comment on column BC_IDCARD_PIC.TYPE_ is '图片类型：如png、bmp';
comment on column BC_IDCARD_PIC.PID is '所属身份证的ID';
comment on column BC_IDCARD_PIC.FILE_DATE is '创建日期';
comment on column BC_IDCARD_PIC.DATA_ is '图像的二进制数据';
alter table BC_IDCARD_PIC
  add constraint BCFK_IDCARD_PIC foreign key (PID)
references BC_IDCARD (ID) on update restrict on delete restrict;

create table BC_IDCARD_CHECK (
  ID        int       not null,
  PID       int       not null,
  FILE_DATE timestamp not null,
  AUTHOR_ID int       not null,
  constraint BCPK_IDCARD_CHECK primary key (ID)
);
comment on table BC_IDCARD_CHECK is '刷卡记录';
comment on column BC_IDCARD_CHECK.ID is 'ID';
comment on column BC_IDCARD_CHECK.PID is '身份证';
comment on column BC_IDCARD_CHECK.FILE_DATE is '刷卡时间';
comment on column BC_IDCARD.AUTHOR_ID is '创建人:对应Actor的ID';
alter table BC_IDCARD_CHECK
  add constraint BC_IDCARD_CHECK_PID foreign key (PID)
references BC_IDCARD (ID) on update restrict on delete restrict;
alter table BC_IDCARD_CHECK
  add constraint BCFK_IDCARD_CHECK_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR (ID) on update restrict on delete restrict;

-- 身份证读卡器使用岗
insert into BC_IDENTITY_ACTOR (ID, UID_, STATUS_, INNER_, TYPE_, CODE, NAME, ORDER_, PCODE, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 'IDCARD_USERS', 0, true, 3, 'IDCARD_USERS', '身份证读卡器使用岗', '0000', null, null
  from bc_dual
  where not exists(select 0
                   from BC_IDENTITY_ACTOR
                   where CODE = 'IDCARD_USERS');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.code = 'baochengzongbu' and af.code = 'IDCARD_USERS'
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION ar
                       where ar.TYPE_ = 0 and ar.MASTER_ID = am.id and ar.FOLLOWER_ID = af.id);
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.code = 'IDCARD_USERS' and af.code in ('admin', 'ghy', 'xu', 'hrj', 'lubaojin', 'zxr')
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION ar
                       where ar.TYPE_ = 0 and ar.MASTER_ID = am.id and ar.FOLLOWER_ID = af.id);

-- 插入同步司机照片的定时任务
insert into bc_sd_job (id, status_, name, groupn, cron, bean, method, order_, memo_, ignore_error)
  select NEXTVAL('hibernate_sequence'), 1, '同步身份证刷卡器的照片到司机招聘库', 'bc', '0 1 0 * * ? *', 'tempDriverService',
    'doSyncPortrait', '0002', '', false
  from bc_dual
  where not exists(select 0
                   from bc_sd_job
                   where bean = 'tempDriverService' and method = 'doSyncPortrait');

-- 创建触发器
create or replace function antoInsertTempDriver() returns trigger as $$
declare
  guy       record;
  driver_id int := 0;
begin
  for guy in select *
             from bc_idcard
             where id = NEW.pid loop
    -- 获取已经存在的司机的id
    select id into driver_id
    from bs_temp_driver
    where cert_identity = guy.code limit 1;

    if (driver_id > 0)
    then -- 司机已存在：更新身份证信息、最后修改信息
      update bs_temp_driver set name = guy.name
        , sex                        = to_number(guy.sex, 'FM999')
        , nation                     = guy.FOLK
        , birthdate                  = guy.BIRTHDATE
        , address                    = guy.ADDRESS
        , issue                      = guy.ISSUE
        , valid_start_date           = guy.START_DATE
        , valid_end_date             = guy.END_DATE
        , register_date              = guy.FILE_DATE
        , modifier_id                = (select id
                                        from bc_identity_actor_history
                                        where current = true and actor_id = guy.author_id)
        , modified_date              = NEW.FILE_DATE
        , origin                     = getplaceoriginbycertidentity(guy.code)
      where id = driver_id;
      return null;
    else -- 司机不存在：插入新的信息
      insert into bs_temp_driver (id, uid_, status_, name
        , sex, nation, birthdate, address
        , cert_identity, issue, valid_start_date, valid_end_date, register_date
        , file_date, author_id, modified_date, modifier_id, origin, region_)
      values (NEXTVAL('hibernate_sequence'), 'tempDriver.auid.' || NEXTVAL('hibernate_sequence'), 0, guy.name
        , to_number(guy.sex, 'FM999'), guy.FOLK, guy.BIRTHDATE, guy.ADDRESS
        , guy.code, guy.ISSUE, guy.START_DATE, guy.END_DATE, guy.FILE_DATE
        , guy.FILE_DATE, (select id
                          from bc_identity_actor_history
                          where current = true and actor_id = guy.author_id)
        , guy.FILE_DATE, (select id
                          from bc_identity_actor_history
                          where current = true and actor_id = guy.author_id)
        , getplaceoriginbycertidentity(guy.code)
        , case when guy.address like '%广州%'
          then 1
          when guy.address like '%广东%'
            then 2
          when guy.CODE like '44%'
            then 2
          else 3 end);
      return null;
    end if;
  end loop;
  return null;
end;
$$ language plpgsql;
drop trigger if exists trigger_antoInsertTempDriver on BC_IDCARD;
drop trigger if exists trigger_antoInsertTempDriver on BC_IDCARD_CHECK;
create trigger trigger_antoInsertTempDriver
  after insert on BC_IDCARD_CHECK
  for each row execute procedure antoInsertTempDriver();

-- test
insert into bc_idcard (id, code, name, sex, folk, birthdate, issue, start_date, end_date, address, file_date, author_id)
values (1, 'ID01', 'dragon', '1', '汉族', '1976-02-01', '广州市公安局', '2006-02-01', '2026-02-01', '广州市XXX001', now(), 9);
insert into bc_idcard_check (id, pid, file_date, author_id)
values (NEXTVAL('hibernate_sequence'), 1, now(), 9);

-- 刷卡器的登录sql
select *
from bc_identity_actor u
inner join bc_identity_auth a on a.id = u.id
inner join bc_identity_actor_relation ur on ur.follower_id = u.id and ur.type_ = 0
inner join bc_identity_actor g on g.id = ur.master_id and g.type_ = 3
where u.type_ = 4 and u.code = 'dragon' and a.password = '21218cca77804d2ba1922c33e0151105'
      and g.code = 'IDCARD_USERS';

-- 清空删除测试数据
delete from bc_idcard_check;
delete from bc_idcard_pic;
delete from bc_idcard;
delete from bs_temp_driver
where id in (10159034, 10159032);
delete from bs_temp_driver_workflow;
delete from bs_temp_driver
where id = 10150592;
-- update bs_temp_driver set valid_start_date = null;

-- 查询
select b.*, a.*
from bc_idcard a left join bc_idcard_check b on b.pid = a.id
order by b.file_date desc;
select b.*, a.*
from bc_idcard a left join bc_idcard_pic b on b.pid = a.id
order by b.file_date desc;
select *
from bc_idcard_pic b
order by b.file_date desc;
select octet_length(p.data_), p.*
from bc_idcard_pic p;
select *
from bs_temp_driver
order by modified_date desc;
select *
from bc_docs_attach
where ptype = 'portrait' and puid = 'tempDriver.auid.10156240';
update bc_idcard set code = '440121222211113333'
where id = 1;
update bc_idcard_check set file_date = '2012-12-13 00:00:01'
where id = 10156238;
update bc_docs_attach set modified_date = '2012-12-13 00:00:00'
where ptype = 'portrait' and puid = 'tempDriver.auid.10156240';
update bc_idcard_pic set file_date = '2012-12-13 00:00:03'
where id = 1860;
delete from bc_docs_attach
where ptype = 'portrait' and puid = 'tempDriver.auid.10156240';
update bc_idcard set modified_date = file_date, modifier_id = author_id;

select *
from bs_temp_driver
order by modified_date desc;
select *
from bc_idcard_pic b
order by b.file_date desc;
select *
from bc_docs_attach
where ptype = 'portrait' and puid = 'tempDriver.auid.10152673';

-- 查询需要同步照片的最新刷卡信息:有身份证图片但司机招聘中的图片较旧的就同步图片
select p.file_date                                                               p_file_date, p.type_ p_type,
       p.data_                                                                   p_data, c.code d_code,
       c.name as                                                                 d_name, d.id d_id, d.uid_ d_uid,
       a.id   as                                                                 attach_id, a.path attach_path,
       (select h.id
        from bc_identity_actor_history h
        where h.current = true and h.actor_id = c.MODIFIER_ID)                   hid
from bc_idcard_pic p
inner join bc_idcard c on c.id = p.pid
inner join bs_temp_driver d on d.cert_identity = c.code
left join bc_docs_attach a on a.puid = d.uid_
where (a.id is null or (a.ptype = 'portrait' and a.modified_date < p.file_date))
      and not exists(
  select 0
  from bc_idcard_pic p1 inner join bc_idcard c1 on c1.id = p1.pid
  where p1.pid = p.pid and p1.file_date > p.file_date
)
order by p.file_date asc;
