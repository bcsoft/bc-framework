-- #### identity 相关 sql 参考 ####

-- drop function identity_find_actor_belong_ids(int)
create or replace function identity_find_actor_belong_ids(actor_id int)
  returns table(id int) as
$BODY$
/** 获取 actor 直属的组织（单位、部门或所在岗位）
 *	@param actor_id 单位、部门、岗位或用户的id
 */
begin
  return query
  select r.master_id
  from bc_identity_actor_relation r
  where r.type_ = 0    -- 隶属关系
        and r.follower_id = $1
  order by r.master_id;
end;
$BODY$ language plpgsql;

-- drop function identity_find_actor_belong_ids(character varying)
create or replace function identity_find_actor_belong_ids(actor_code character varying)
  returns table(id int) as
$BODY$
/** 获取 actor 直属的组织（单位、部门或所在岗位）
 *	@param actor_code 单位、部门、岗位或用户的编码
 */
begin
  return query
  select r.master_id
  from bc_identity_actor_relation r
  inner join bc_identity_actor f on f.id = r.follower_id
  where r.type_ = 0      -- 隶属关系
        and f.code = $1
  order by r.master_id;
end;
$BODY$ language plpgsql;
-- select a.* from bc_identity_actor a inner join identity_find_actor_belong_ids(100052) b on b.id=a.id;
-- select a.* from bc_identity_actor a inner join identity_find_actor_belong_ids('hrj') b on b.id=a.id;


-- drop function identity_find_actor_ancestor_ids(int)
create or replace function identity_find_actor_ancestor_ids(actor_id int)
  returns table(id int) as
$BODY$
/** 获取 actor 的祖先组织（单位、部门或所在岗位）
 *	@param actor_id 单位、部门、岗位或用户的id
 */
begin
  return query
  with recursive t (id) as (
    -- 直属组织（单位、部门或所在的岗位）
    select identity_find_actor_belong_ids($1)

    union all

    -- 递归获取上级
    select r.master_id
    from bc_identity_actor_relation r
    inner join t on t.id = r.follower_id
    where r.type_ = 0      -- 隶属关系
  ) select distinct *
    from t
    order by id;
end;
$BODY$ language plpgsql;

-- drop function identity_find_actor_ancestor_ids(character varying)
create or replace function identity_find_actor_ancestor_ids(actor_code character varying)
  returns table(id int) as
$BODY$
/** 获取 actor 的祖先组织（单位、部门或所在岗位）
 *	@param actor_code 单位、部门、岗位或用户的编码
 */
begin
  return query
  with recursive t (id) as (
    -- 直属组织（单位、部门或所在岗位）
    select identity_find_actor_belong_ids($1)

    union all

    -- 递归获取上级
    select r.master_id
    from bc_identity_actor_relation r
    inner join t on t.id = r.follower_id
    where r.type_ = 0      -- 隶属关系
  ) select distinct *
    from t
    order by id;
end;
$BODY$ language plpgsql;
-- select a.* from bc_identity_actor a inner join identity_find_actor_ancestor_ids(100052) b on b.id=a.id;
-- select a.* from bc_identity_actor a inner join identity_find_actor_ancestor_ids('hrj') b on b.id=a.id;

-- drop function identity_find_actor_role_ids(int)
create or replace function identity_find_actor_role_ids(actor_id int)
  returns table(aid int, rid int) as
$BODY$
/** 获取 actor 拥有的角色（包括从上级继承的角色）
 *	@param actor_id 单位、部门、岗位或用户的id
 */
begin
  return query
  select ra.aid, ra.rid
  from bc_identity_role_actor ra
  inner join (
               select a.id
               from bc_identity_actor a
               where id = $1
               union all
               select identity_find_actor_ancestor_ids($1)
             ) a on a.id = ra.aid;
end;
$BODY$ language plpgsql;

-- drop function identity_find_actor_role_ids(character varying)
create or replace function identity_find_actor_role_ids(actor_code character varying)
  returns table(aid int, rid int) as
$BODY$
/** 获取 actor 拥有的角色（包括从上级继承的角色）
 *	@param actor_code 单位、部门、岗位或用户的编码
 */
begin
  return query
  select ra.aid, ra.rid
  from bc_identity_role_actor ra
  inner join (
               select a.id
               from bc_identity_actor a
               where code = $1
               union all
               select identity_find_actor_ancestor_ids($1)
             ) a on a.id = ra.aid;
end;
$BODY$ language plpgsql;
/*
select a.name,r.name,r.code from bc_identity_role r 
	inner join identity_find_actor_role_ids(100052) b on b.rid = r.id
	inner join bc_identity_actor a on a.id = b.aid;
select a.name,r.name,r.code from bc_identity_role r 
	inner join identity_find_actor_role_ids('hrj') b on b.rid = r.id
	inner join bc_identity_actor a on a.id = b.aid;
*/
