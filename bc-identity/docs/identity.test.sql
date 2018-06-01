-- #### identity 相关 sql 参考 ####

-- 用户直属的单位或部门、所在的岗位
select u.id, u.name, o.id, o.type_, o.name
from bc_identity_actor o
inner join bc_identity_actor_relation r on r.master_id = o.id
inner join bc_identity_actor u on u.id = r.follower_id
where r.type_ = 0      -- 隶属关系
      and o.type_ in (1, 2, 3)  -- 单位、部门、岗位
      and u.type_ = 4    -- 用户
      and u.status_ = 0    -- 启用状态
      and o.status_ = 0    -- 启用状态
      and u.code in ('hrj')  -- *用户条件
order by u.order_, o.type_, o.order_;

-- 获取指定组织的所有祖先组织
with recursive ou (id, path, depth) as (
  select o.id, array [o.name :: text] as path, 1 as depth
  from bc_identity_actor o
  where o.id in (100002, 8633) -- 指定组织的id列表

  union all

  -- 递归获取上级
  select p.id, p.name :: text || ou.path as path, ou.depth + 1 as depth
  from bc_identity_actor p
  inner join bc_identity_actor_relation r on r.master_id = p.id
  inner join ou on ou.id = r.follower_id
  where r.type_ = 0      -- 隶属关系
        and p.type_ in (1, 2)  -- 单位、部门
        and p.status_ = 0    -- 启用状态
) select *
  from ou;

-- 查找直接配有指定角色的用户
select r.name 角色, u.code 帐号, u.name 姓名
from bc_identity_actor u
inner join bc_identity_role_actor ra on ra.aid = u.id
inner join bc_identity_role r on r.id = ra.rid
where r.code in ('BC_WORKFLOW_INSTANCE_DELETE', 'BC_WORKFLOW_DEPLOY_CASCADE') -- 角色的编码列表
      and u.type_ = 4

-- 查找直接配有指定岗位的用户
select g.name 岗位, u.code 帐号, u.name 姓名
from bc_identity_actor u
inner join bc_identity_actor_relation ar on ar.follower_id = u.id
inner join bc_identity_actor g on g.id = ar.master_id
where u.type_ = 4 and ar.type_ = 0
      and g.code in ('chaojiguanligang')

-- 查找ACL
select d.doc_id, d.doc_name
from bc_acl_doc d
inner join bc_acl_actor a on a.pid = d.id
where d.doc_type = 'pmc_part_type';
