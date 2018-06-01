/* Drop Tables */
drop table if exists BC_DEVICE_EVENT_NEW;
drop table if exists BC_DEVICE_EVENT;
drop table if exists BC_DEVICE;

/* 清除资源、角色、岗位配置数据
delete from BC_IDENTITY_ROLE_RESOURCE where sid in 
	(select id from BC_IDENTITY_RESOURCE where ORDER_ like '8005%');
delete from BC_IDENTITY_RESOURCE where ORDER_ like '8005%';

delete from BC_IDENTITY_ROLE_ACTOR where rid in 
	(select id from BC_IDENTITY_ROLE where code = 'BC_DEVICE_MANAGE');
delete from BC_IDENTITY_ROLE where code = 'BC_DEVICE_MANAGE';

delete from BC_IDENTITY_ACTOR_RELATION where FOLLOWER_ID in 
	(select id from BC_IDENTITY_ACTOR where code = 'DeviceManageGroup')
	or MASTER_ID in 
	(select id from BC_IDENTITY_ACTOR where code = 'DeviceManageGroup');
delete from BC_IDENTITY_ACTOR where code = 'DeviceManageGroup';
*/

-- 设备
create table BC_DEVICE (
  ID            int           not null,
  UID_          varchar(36)   not null,
  STATUS_       int default 0 not null,
  CODE          varchar(255)  not null unique,
  MODEL         varchar(255)  not null,
  NAME          varchar(255)  not null,
  PURPOSE       varchar(255)  not null,
  BUY_DATE      date          not null,
  SN            varchar(255)  not null unique,
  FILE_DATE     timestamp     not null,
  AUTHOR_ID     int           not null,
  MODIFIER_ID   int           not null,
  MODIFIED_DATE timestamp     not null,
  DESC_         varchar(4000),
  constraint BCPK_DEVICE primary key (ID)
);
comment on table BC_DEVICE is '设备';
comment on column BC_DEVICE.ID is 'ID';
comment on column BC_DEVICE.UID_ is 'UID';
comment on column BC_DEVICE.STATUS_ is '状态 : 0-使用中,1-已禁用';
comment on column BC_DEVICE.CODE is '编码';
comment on column BC_DEVICE.MODEL is '型号';
comment on column BC_DEVICE.NAME is '名称';
comment on column BC_DEVICE.PURPOSE is '用途';
comment on column BC_DEVICE.BUY_DATE is '购买日期';
comment on column BC_DEVICE.SN is '序列号';
comment on column BC_DEVICE.FILE_DATE is '创建时间';
comment on column BC_DEVICE.AUTHOR_ID is '创建人ID';
comment on column BC_DEVICE.MODIFIER_ID is '最后修改人';
comment on column BC_DEVICE.MODIFIED_DATE is '最后修改时间';
comment on column BC_DEVICE.DESC_ is '备注';
alter table BC_DEVICE
  add constraint BCFK_DEVICE_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;
alter table BC_DEVICE
  add constraint BCFK_DEVICE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;

-- 设备事件
create table BC_DEVICE_EVENT (
  ID           int          not null,
  DEVICE_ID    int          not null,
  TYPE_        varchar(255) not null,
  TRIGGER_TIME timestamp    not null,
  APPID        varchar(255),
  DATA_        text,
  constraint BCPK_DEVICE_EVENT primary key (ID)
);
comment on table BC_DEVICE_EVENT is '设备事件';
comment on column BC_DEVICE_EVENT.ID is 'ID';
comment on column BC_DEVICE_EVENT.DEVICE_ID is '设备ID';
comment on column BC_DEVICE_EVENT.TYPE_ is '事件类型';
comment on column BC_DEVICE_EVENT.TRIGGER_TIME is '触发时间';
comment on column BC_DEVICE_EVENT.APPID is '来源 : 如应用的ID';
comment on column BC_DEVICE_EVENT.DATA_ is '事件数据 : 使用json格式';
alter table BC_DEVICE_EVENT
  add constraint BCFK_DEVICE_EVENT_DID foreign key (DEVICE_ID)
references BC_DEVICE (ID)
on update restrict on delete restrict;

-- 新事件
create table BC_DEVICE_EVENT_NEW (
  ID int not null,
  constraint BCPK_DEVICE_EVENT_NEW primary key (ID)
);
alter table BC_DEVICE_EVENT_NEW
  add constraint BCFK_DEVICE_EVENT_NEW_ID foreign key (ID)
references BC_DEVICE_EVENT (ID)
on update restrict on delete restrict;
comment on table BC_DEVICE_EVENT_NEW is '新事件';
comment on column BC_DEVICE_EVENT_NEW.ID is 'ID';
comment on table BC_IDENTITY_ACTOR_HISTORY is '参与者历史';
comment on column BC_IDENTITY_ACTOR_HISTORY.ID is 'ID';

--插入设备事件序列
-- Sequence: devcie_event_sequence
-- DROP SEQUENCE devcie_event_sequence;
create sequence devcie_event_sequence
  increment 1
  minvalue 1
  maxvalue 9223372036854775807
  start 1
  cache 20;
alter table devcie_event_sequence
  owner to bcsystem;

/* Create Indexes */
-- 设备事件索引
create index DEVICEEVENTIDX_DEVICEEVENT_DEVICEID_TYPE_APPID on BC_DEVICE_EVENT using BTREE (DEVICE_ID, TYPE_, APPID);

-- 插入资源
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800500', '硬件设备', '', 'i0509', '系统维护'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '800000'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '硬件设备');
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800510', '设备配置', '/bc/device/paging', 'i0509', '系统维护/硬件设备'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '800500'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '设备配置');
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800511', '设备事件', '/bc/deviceEvent/paging', 'i0303', '系统维护/硬件设备'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '800500'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '设备事件');
-- 插入角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0301', 'BC_DEVICE_MANAGE', '硬件设备管理'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_DEVICE_MANAGE');
-- 插入角色与资源之间的关系
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_DEVICE_MANAGE'
        and m.NAME in ('设备配置', '设备事件')
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);
-- 插入岗位
insert into BC_IDENTITY_ACTOR (ID, UID_, STATUS_, INNER_, TYPE_, CODE, NAME, ORDER_, PCODE, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 'group.init.' || NEXTVAL('CORE_SEQUENCE'), 0, false, 3, 'DeviceManageGroup',
    '硬件设备管理岗', '8808', '[1]baochengzongbu', '宝城'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ACTOR
                   where CODE = 'DeviceManageGroup');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'baochengzongbu'
        and af.CODE = 'DeviceManageGroup'
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION r
                       where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id);
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'DeviceManageGroup'
        and af.CODE in ('ghy', 'luliang', 'hwx', 'hrj', 'lubaojin')
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION r
                       where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id);
--	插入岗位与角色之间的关系
insert into BC_IDENTITY_ROLE_ACTOR (AID, RID)
  select a.id, r.id
  from BC_IDENTITY_ACTOR a, BC_IDENTITY_ROLE r
  where a.CODE in ('chaojiguanligang', 'DeviceManageGroup')
        and r.CODE in ('BC_DEVICE_MANAGE')
        and not exists(select 0
                       from BC_IDENTITY_ROLE_ACTOR ra
                       where ra.AID = a.id and ra.RID = r.id);

-- 插入考勤设备数据
insert into bc_device (
  id, uid_, status_, code, model, name, purpose, buy_date, sn, desc_
  , file_date, author_id, modified_date, modifier_id)
  select NEXTVAL('hibernate_sequence'), 'D' || NEXTVAL('hibernate_sequence'), 0, 'A15.01', 'A15', '1号考勤机', '上下班考勤',
      date '2013-11-15', '8123513040002281', '汉王A15人脸识别机', now(), (select id
                                                                   from bc_identity_actor_history
                                                                   where actor_code = 'admin' and current = true),
    now(), (select id
            from bc_identity_actor_history
            where actor_code = 'admin' and current = true)
  from bc_dual
  where not exists(select 0
                   from bc_device
                   where code = 'A15.01');

--插入广播设备事件定时任务
insert into bc_sd_job (id, status_, name, groupn, cron, bean, method, order_, memo_, ignore_error)
  select NEXTVAL('hibernate_sequence'), 0, '广播设备事件', 'bc', '0/5 * * ? * *', 'deviceEventNewPublishService',
    'publishEvent', '0100', '每5秒执行一次', true
  from bc_dual
  where not exists(select 0
                   from bc_sd_job
                   where name = '广播设备事件');