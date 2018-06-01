-- 网络硬盘
drop table if exists BC_NETDISK_COMMENT;
drop table if exists BC_NETDISK_SHARE;
drop table if exists BC_NETDISK_VISIT;
drop table if exists BC_NETDISK_FILE;

-- 网络文件
create table BC_NETDISK_FILE (
  ID            int           not null,
  PID           int,
  STATUS_       int default 0 not null,
  TYPE_         int default 0 not null,
  FOLDER_TYPE   int default 0 not null,
  NAME          varchar(500)  not null,
  SIZE_         int default 0 not null,
  EXT           varchar(10),
  ORDER_        varchar(100),
  PATH          varchar(500),
  EDIT_ROLE     int default 0 not null,
  BATCH_NO      varchar(500),
  AUTHOR_ID     int           not null,
  FILE_DATE     timestamp     not null,
  MODIFIER_ID   int,
  MODIFIED_DATE timestamp,
  constraint BCPK_ND_FILE primary key (ID)
);
comment on table BC_NETDISK_FILE is '网络文件';
comment on column BC_NETDISK_FILE.ID is 'ID';
comment on column BC_NETDISK_FILE.PID is '所在文件夹ID';
comment on column BC_NETDISK_FILE.STATUS_ is '状态 : 0-正常,1-已删除';
comment on column BC_NETDISK_FILE.TYPE_ is '类型 : 0-文件夹,1-文件';
comment on column BC_NETDISK_FILE.FOLDER_TYPE is '文件夹类型 : 0-个人,1-公共';
comment on column BC_NETDISK_FILE.NAME is '名称 : (不带路径的部分)';
comment on column BC_NETDISK_FILE.SIZE_ is '大小 : 字节单位,文件大小或文件夹的总大小';
comment on column BC_NETDISK_FILE.EXT is '扩展名 : 仅适用于文件类型';
comment on column BC_NETDISK_FILE.ORDER_ is '排序号';
comment on column BC_NETDISK_FILE.PATH is '保存路径 : 相对于[NETDISK]目录下的子路径,开头不要带符号/,仅适用于文件类型';
comment on column BC_NETDISK_FILE.EDIT_ROLE is '编辑权限 : 0-编辑者可修改,1-只有拥有者可修改';
comment on column BC_NETDISK_FILE.BATCH_NO is '批号:标识是否是上传文件夹时到一批上传的文件';
comment on column BC_NETDISK_FILE.AUTHOR_ID is '创建人ID';
comment on column BC_NETDISK_FILE.FILE_DATE is '创建时间';
comment on column BC_NETDISK_FILE.MODIFIER_ID is '最后修改人ID';
comment on column BC_NETDISK_FILE.MODIFIED_DATE is '最后修改时间';
alter table BC_NETDISK_FILE
  add constraint BCFK_ND_FILE_PID foreign key (PID)
references BC_NETDISK_FILE (ID) on update no action;
alter table BC_NETDISK_FILE
  add constraint BCFK_ND_FILE_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_NETDISK_FILE
  add constraint BCFK_ND_FILE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 网络文件访问权限
create table BC_NETDISK_SHARE (
  ID        int           not null,
  PID       int default 0 not null,
  ROLE_     varchar(4)    not null,
  ORDER_    int,
  AID       int           not null,
  AUTHOR_ID int           not null,
  FILE_DATE timestamp     not null,
  constraint BCPK_ND_SHARE primary key (ID)
);
comment on table BC_NETDISK_SHARE is '网络文件访问权限';
comment on column BC_NETDISK_SHARE.ID is 'ID';
comment on column BC_NETDISK_SHARE.PID is '文件ID';
comment on column BC_NETDISK_SHARE.ROLE_ is '访问权限 : 用4为数字表示(wrfd:w-编辑,r-查看,f-评论,d-下载),每位数的值为0或1,1代表拥有此权限';
comment on column BC_NETDISK_SHARE.ORDER_ is '排序号';
comment on column BC_NETDISK_SHARE.AID is '访问人ID';
comment on column BC_NETDISK_SHARE.AUTHOR_ID is '添加人ID';
comment on column BC_NETDISK_SHARE.FILE_DATE is '添加时间';
alter table BC_NETDISK_SHARE
  add constraint BCFK_ND_FILE_SHARE foreign key (PID)
references BC_NETDISK_FILE (ID) on update no action on delete cascade;
alter table BC_NETDISK_SHARE
  add constraint BCFK_ND_SHARE_AID foreign key (AID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_NETDISK_SHARE
  add constraint BCFK_ND_SHARE_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 网络文件评论
create table BC_NETDISK_COMMENT (
  ID        int           not null,
  PID       int default 0 not null,
  AID       int           not null,
  FILE_DATE timestamp     not null,
  DESC_     varchar(4000) not null,
  constraint BCPK_ND_COMMENT primary key (ID)
);
comment on table BC_NETDISK_COMMENT is '网络文件评论';
comment on column BC_NETDISK_COMMENT.ID is 'ID';
comment on column BC_NETDISK_COMMENT.PID is '文件ID';
comment on column BC_NETDISK_COMMENT.AID is '评论人ID';
comment on column BC_NETDISK_COMMENT.FILE_DATE is '评论时间';
comment on column BC_NETDISK_COMMENT.DESC_ is '评论内容';
alter table BC_NETDISK_COMMENT
  add constraint BCFK_ND_FILE_COMMENT foreign key (PID)
references BC_NETDISK_FILE (ID) on update no action on delete cascade;
alter table BC_NETDISK_COMMENT
  add constraint BCFK_ND_COMMENT_AID foreign key (AID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 网络文件访问记录
create table BC_NETDISK_VISIT (
  ID    int           not null,
  PID   int default 0 not null,
  AID   int           not null,
  TYPE_ int default 0 not null,
  DESC_ varchar(4000) not null,
  constraint BCPK_ND_VISIT primary key (ID)
);
comment on table BC_NETDISK_VISIT is '网络文件访问记录';
comment on column BC_NETDISK_VISIT.ID is 'ID';
comment on column BC_NETDISK_VISIT.PID is '文件ID';
comment on column BC_NETDISK_VISIT.AID is '访问人ID';
comment on column BC_NETDISK_VISIT.TYPE_ is '访问类型 : 0-查看,1-整理,2-更改权限';
comment on column BC_NETDISK_VISIT.DESC_ is '说明';
alter table BC_NETDISK_VISIT
  add constraint BCFK_ND_FILE_VISIT foreign key (PID)
references BC_NETDISK_FILE (ID) on update no action on delete cascade;
alter table BC_NETDISK_VISIT
  add constraint BCFK_ND_VISIT_AID foreign key (AID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

--##网络硬盘模块的 postgresql 自定义函数和存储过程##


--通过当前文件夹id查找指定文件夹以及指定文件夹以下的所有文件的id
--参数fid文件夹id
create or replace function getMyselfAndChildFileId(fid in integer) returns varchar as $$
declare
  --定义变量
  fileId varchar(4000);
begin
  with recursive n as (select *
                       from bc_netdisk_file
                       where id = fid
                       union select f.*
                             from bc_netdisk_file f, n
                             where f.pid = n.id)
  select string_agg(id || '', ',') into fileId
  from n;

  return fileId;
end;
$$ language plpgsql;

--通过当前文件id查找指定文件以及指定文件以上的所有文件夹的id
--参数fid文件id
create or replace function getMyselfAndParentsFileId(fid in integer) returns varchar as $$
declare
  --定义变量
  fileId varchar(4000);
begin
  with recursive n as (select *
                       from bc_netdisk_file
                       where id = fid
                       union select f.*
                             from bc_netdisk_file f, n
                             where f.id = n.pid)
  select string_agg(id || '', ',') into fileId
  from n;

  return fileId;
end;
$$ language plpgsql;


--通过用户id查找其可以访问指定文件夹以及指定文件以下的所有文件的id
--参数uid用户id
create or replace function getUserSharFileId(uid in integer) returns varchar as $$
declare
  --定义变量
  fileId varchar(4000);
begin
  with recursive n as (select *
                       from bc_netdisk_file
                       where id in (select pid
                                    from bc_netdisk_share
                                    where aid = uid)
                       union select f.*
                             from bc_netdisk_file f, n
                             where f.pid = n.id)
  select string_agg(id || '', ',') into fileId
  from n;

  return fileId;
end;
$$ language plpgsql;

--通过用户id查找其可以访问指定文件夹以及指定文件以下的所有文件的id
--参数uid用户id
create or replace function getUserSharFileId2All(uid in integer) returns varchar as $$
declare
  --定义变量
  fileId varchar(4000);
begin
  with recursive n as (
    select *
    from bc_netdisk_file
    where id in (select pid
                 from bc_netdisk_share
                 where aid = uid)
          or id in (select id
                    from bc_netdisk_file
                    where author_id in (select id
                                        from bc_identity_actor_history
                                        where actor_id = uid))
    union select f.*
          from bc_netdisk_file f, n
          where f.pid = n.id
  )
  select string_agg(id || '', ',') into fileId
  from n;

  return fileId;
end;
$$ language plpgsql;

--查找所有公共文件
create or replace function getPublicFileId() returns varchar as $$
declare
  --定义变量
  fileId varchar(4000);
begin
  with recursive n as (select *
                       from bc_netdisk_file
                       where folder_type = 1
                       union select f.*
                             from bc_netdisk_file f, n
                             where f.pid = n.id)
  select string_agg(id || '', ',') into fileId
  from n;

  return fileId;
end;
$$ language plpgsql;

--在我的事务中插入网络硬盘入口数据
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '040700', '网络硬盘', '/bc/netdiskFiles/paging', 'i0408'
  from BC_IDENTITY_RESOURCE m
  where m.order_ = '040000';

insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_COMMON'
        and m.type_ > 1 and m.name = '网络硬盘'
  order by m.order_;

-- 插入公共硬盘管理角色数据
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
values (NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0147', 'BC_NETDISK_PUBLIC', '公共硬盘管理');