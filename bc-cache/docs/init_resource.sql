-- 资源配置：缓存管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800314', '缓存管理', '/bc/cache/list', 'i0509', '系统维护'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '800000'
        and not exists(select 1
                       from BC_IDENTITY_RESOURCE
                       where order_ = '800314');

-- 角色配置：缓存管理 BC_CACHE_MANAGE
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0028', 'BC_CACHE_MANAGE', '缓存管理'
  from bc_dual
  where not exists(select 1
                   from bc_identity_role
                   where code = 'BC_CACHE_MANAGE');

-- 角色-资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_CACHE_MANAGE'
        and m.NAME = '缓存管理'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);

--	岗位配置：缓存管理岗
insert into BC_IDENTITY_ACTOR (ID, UID_, STATUS_, INNER_, TYPE_, CODE, NAME, ORDER_, PCODE, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 'group.cache.manage' || NEXTVAL('CORE_SEQUENCE'), 0, false, 3,
    'BC_CACHE_MANAGE_GROUP', '缓存管理岗', '9919', '[1]baochengzongbu', '宝城'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ACTOR
                   where CODE = 'BC_CACHE_MANAGE_GROUP');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'baochengzongbu'
        and af.CODE = 'BC_CACHE_MANAGE_GROUP'
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION r
                       where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id);

-- 岗位-角色：缓存管理岗，缓存管理角色
insert into BC_IDENTITY_ROLE_ACTOR (AID, RID)
  select a.id, r.id
  from BC_IDENTITY_ACTOR a, BC_IDENTITY_ROLE r
  where a.CODE in ('BC_CACHE_MANAGE_GROUP') and r.CODE in ('BC_CACHE_MANAGE')
        and not exists(select 0
                       from BC_IDENTITY_ROLE_ACTOR ra
                       where ra.AID = a.id and ra.RID = r.id);

-- 岗位-用户：缓存管理岗，admin
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'BC_CACHE_MANAGE_GROUP'
        and af.CODE in ('admin') -- 用户帐号
        and not exists(select 0
                       from BC_IDENTITY_ACTOR_RELATION r
                       where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id);