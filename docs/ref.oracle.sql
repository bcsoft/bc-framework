-- ����ָ��actor�����ȣ����š���λ����λ
select *
from BC_IDENTITY_ACTOR a
where a.id in (
  select ar.master_id
  from BC_IDENTITY_ACTOR_RELATION ar
  where ar.type_ = 0
start with ar.follower_id = 9
connect by prior ar.master_id = ar.follower_id
) order by a.order_;

-- ����ָ��actor�ĺ�������š���λ����λ
select *
from BC_IDENTITY_ACTOR a
where a.id in (
  select ar.follower_id, sys_connect_by_path(master_id, '/'), connect_by_isleaf
  from BC_IDENTITY_ACTOR_RELATION ar
  where ar.type_ = 0
start with ar.master_id = 8
connect by prior ar.follower_id = ar.master_id
) order by a.order_;

-- �û�ֱ��ӵ�еĽ�ɫ
select *
from BC_IDENTITY_ROLE ro
inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid = ro.id
where ro_at.aid = 9;

-- �û����ӵ�еĽ�ɫ
select *
from BC_IDENTITY_ROLE ro
inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid = ro.id
where ro_at.aid in (
  select ar.master_id
  from BC_IDENTITY_ACTOR_RELATION ar
  where ar.type_ = 0
start with ar.follower_id = 9
connect by prior ar.master_id = ar.follower_id
);

-- �û�ֱ�ӿɷ��ʵ���Դ
select distinct res.*
from Bc_Identity_Resource res
join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid = res.id
where ro_res.rid in (
  select ro.id
  from BC_IDENTITY_ROLE ro
  inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid = ro.id
  where ro_at.aid = 100171
);

-- �û���ӿɷ��ʵ���Դ
select distinct res.*
from Bc_Identity_Resource res
join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid = res.id
where ro_res.rid in (
  select ro.id
  from BC_IDENTITY_ROLE ro
  inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid = ro.id
  where ro_at.aid in (
    select ar.master_id
    from BC_IDENTITY_ACTOR_RELATION ar
    where ar.type_ = 0
start with ar.follower_id = 100171
connect by prior ar.master_id = ar.follower_id
)
);

-- �û��Խ��Ŀ�ݷ�ʽ
select *
from (
  select *
  from BC_DESKTOP_SHORTCUT sc
  where sc.aid = 100171
  union all
  -- ͨ�õĿ�ݷ�ʽ
  --select * from BC_DESKTOP_SHORTCUT sc
  --    where sc.aid is null and sc.sid is null
  --union all
  -- ��Ȩ�޷��ʵ���Դ��Ӧ�Ŀ�ݷ�ʽ���������ϼ��̳еģ�
  select *
  from BC_DESKTOP_SHORTCUT sc
  where sc.aid is null and sc.sid in (
    select distinct res.id
    from Bc_Identity_Resource res
    join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid = res.id
    where ro_res.rid in (
      select ro.id
      from BC_IDENTITY_ROLE ro
      inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid = ro.id
      where ro_at.aid = 100171 or ro_at.aid in (
        select ar.master_id
        from BC_IDENTITY_ACTOR_RELATION ar
        where ar.type_ = 0
  start with ar.follower_id = 100171
        connect by prior ar.master_id = ar.follower_id
)
)
)
) t order by t.order_;

--
select *
from BC_IDENTITY_RESOURCE r;
-- ��Դ������������
select *
from BC_IDENTITY_RESOURCE r start with r.name='ϵͳά��' connect by prior r.id = r.belong;
-- ��Դ������������
select *
from BC_IDENTITY_RESOURCE r start with r.name='��λ����' connect by prior r.belong = r.id;

-- ������ϵ������������
select a.*, rowid
from BC_IDENTITY_ACTOR a
where a.code = 'daxin'
order by a.id;
select *
from BC_IDENTITY_AUTH a
order by a.id;
update BC_IDENTITY_AUTH set password = '21218cca77804d2ba1922c33e0151105';
