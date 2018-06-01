drop table if exists BC_IPCAMERA;
create table BC_IPCAMERA (
  ID       int          not null,
  OWNER_ID int,
  NAME_    varchar(255) not null,
  URL      varchar(255) not null,
  constraint BCPK_IPCAMERA primary key (ID)
);
comment on table BC_IPCAMERA is 'IP摄像头';
comment on column BC_IPCAMERA.ID is 'ID';
comment on column BC_IPCAMERA.OWNER_ID is '拥有者ID，为空代表公共使用';
comment on column BC_IPCAMERA.NAME_ is '名称';
comment on column BC_IPCAMERA.URL is '地址';
alter table BC_IPCAMERA
  add constraint BCFK_IPCAMERA_OWNER foreign key (OWNER_ID)
references BC_IDENTITY_ACTOR (ID)
on update restrict on delete restrict;
alter table BC_IPCAMERA
  add constraint BCUK_IPCAMERA_NAME_OWNER unique (NAME_, OWNER_ID);

-- IP摄像头数据
insert into BC_IPCAMERA (id, name_, url, owner_id)
  select NEXTVAL('CORE_SEQUENCE'), '公共摄像头', 'http://172.27.35.2:8081/photo.jpg', null
  from BC_DUAL
  where not exists(select 0
                   from BC_IPCAMERA
                   where NAME_ = '公共摄像头' and OWNER_ID is null);
insert into BC_IPCAMERA (id, name_, url, owner_id)
  select NEXTVAL('CORE_SEQUENCE'), '华为X1摄像头', 'http://172.27.35.2:8081/photo.jpg', (select id
                                                                                    from bc_identity_actor
                                                                                    where code = 'hrj')
  from BC_DUAL
  where not exists(
    select 0
    from BC_IPCAMERA
    where NAME_ = '华为X1摄像头'
          and OWNER_ID = (select id
                          from bc_identity_actor
                          where code = 'hrj')
  );
insert into BC_IPCAMERA (id, name_, url, owner_id)
  select NEXTVAL('CORE_SEQUENCE'), '摄像头(Error测试)', 'http://1.1.1.1:8081/error.jpg', (select id
                                                                                     from bc_identity_actor
                                                                                     where code = 'hrj')
  from BC_DUAL
  where not exists(
    select 0
    from BC_IPCAMERA
    where NAME_ = '摄像头(Error测试)'
          and OWNER_ID = (select id
                          from bc_identity_actor
                          where code = 'hrj')
  );
-- select * from BC_IPCAMERA order by owner_id, name_;

/* 清除资源、角色、岗位配置数据
delete from BC_IDENTITY_ROLE_RESOURCE where sid in 
	(select id from BC_IDENTITY_RESOURCE where ORDER_ in ('074199'));
delete from BC_IDENTITY_RESOURCE where ORDER_ in ('074199');
*/
--	插入测试用资源
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '074199', '图片处理', '/bc/photo/main', 'i0001', '功能演示'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '074100'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '图片处理');
--	插入角色与资源之间的关系
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_COMMON'
        and m.NAME = '图片处理'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);
