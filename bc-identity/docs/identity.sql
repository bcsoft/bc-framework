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

-- drop function identity_find_actor_upper_id(int)
-- select * from bc_identity_actor where id = identity_find_actor_upper_id(48607933);
create or replace function identity_find_actor_upper_id(actor_id int)
  returns int as
$$
  /** 获取 actor 直属上级的ID(单位或部门)
   *	@param actor_id 单位、部门、岗位或用户的id
   */
declare
  upper_id int := null;
begin
  if $1 is null then
    return null;
  end if;

  select m.id into upper_id
  from bc_identity_actor m
  inner join bc_identity_actor_relation r on r.master_id = m.id and r.type_ = 0
  inner join bc_identity_actor f on f.id = r.follower_id
  where f.id = $1
  and m.type_ in (1, 2); -- 直属上级(单位或部门)

  return upper_id;
end;
$$ language plpgsql;

-- drop function identity_find_actor_upper_unit_id(int)
-- select * from bc_identity_actor where id = identity_find_actor_upper_unit_id(100444);
-- select * from bc_identity_actor where id = identity_find_actor_upper_unit_id(48607933);
create or replace function identity_find_actor_upper_unit_id(actor_id int)
  returns int as
$$
  /** 获取 actor 直属单位的ID
   *	@param actor_id 单位、部门、岗位或用户的id
   */
declare
  upper_type int := null;
  upper_id int := null;
  fid int := $1;
begin
  if fid is null then
    return null;
  end if;

  loop
    select m.type_, m.id into upper_type, upper_id
    from bc_identity_actor m
    inner join bc_identity_actor_relation r on r.master_id = m.id and r.type_ = 0
    inner join bc_identity_actor f on f.id = r.follower_id
    where f.id = fid
    and m.type_ in (1, 2); -- 直属上级(单位或部门)

    if upper_type is null then -- 无 upper 返回 null
      return null;
    elsif upper_type = 1 then -- upper 是单位直接返回
      return upper_id;
    else -- 其它情况继续向上找
      fid := upper_id;
    end if;
  end loop;
end;
$$ language plpgsql;

-- drop function identity_find_actor_uppers(int)
-- select * from identity_find_actor_uppers(5862) order by deep;
create or replace function identity_find_actor_uppers(actor_id int)
  returns table(deep int, id int, type int, code text, name text, cid int) as
$$
  /** 获取 actor 隶属的祖先组织（单位或部门）
   *	@param actor_id 单位、部门、岗位或用户的id
   */
begin
  return query
  with recursive upper(deep, id, code, name, cid) as (
    -- 直属组织（单位或部门）
    select 1 as deep, m.id, m.type_, m.code::text, m.name::text, a.id as cid
    from bc_identity_actor a
    inner join bc_identity_actor_relation r on r.follower_id = a.id and r.type_ = 0 -- 隶属关系
    inner join bc_identity_actor m on m.id = r.master_id and m.type_ in (1, 2) -- 直属单位或部门
    where a.id = $1

    union all

    -- 递归获取上级的上级
    select upper.deep + 1, m.id, m.type_, m.code, m.name, r.follower_id
    from bc_identity_actor_relation r
    inner join upper on upper.id = r.follower_id and r.type_ = 0 -- 隶属关系
    inner join bc_identity_actor m on m.id = r.master_id and m.type_ in (1, 2) -- 直属单位或部门
  ) select * from upper order by deep;
end;
$$ language plpgsql;

-- drop function identity_gen_actor_pcode_pname(int)
-- select identity_gen_actor_pcode_pname(5862);
create or replace function identity_gen_actor_pcode_pname(actor_id int)
  returns text[] as
$$
  /** 生成 actor 的 pcode、pname 值
   *	@param actor_id actor 的 id 值
   *	@return [pcode, pname]
   */
declare
  r text[];
begin
  select array[
    string_agg('[' || p.type || ']' || p.code, '/' order by p.deep desc),
    string_agg(p.name, '/' order by p.deep desc)
  ] into r from identity_find_actor_uppers($1) p;
  return r;
end;
$$ language plpgsql;

/**
-- 查找 pcode、pname 有异常的 status_ = 0 的 actor
select * from (
  select a.id, a.type_, a.code, a.name, a.pcode, a.pname, identity_gen_actor_pcode_pname(a.id) as pcn
  from bc_identity_actor a
  where a.status_ = 0
  order by a.order_
) t where pcode != pcn[1] or pname != pcn[2];

-- 查找异常的 actor_history 进行修正
---- 查找 upper、unit、pcode、pname 有异常的 actor.status_ = 0 and current 的 actor_history
with error_actor_history(
  id, create_date, end_date, actor_id, actor_type, actor_code, actor_name,
  unit_id, unit_name, upper_id, upper_name, pcode, pname,
  r_id, r_type, r_code, r_name, r_unit_id, r_unit_name, r_upper_id, r_upper_name, r_pcode, r_pname
) as (
  select id, create_date, end_date, actor_id, actor_type, actor_code, actor_name,
    unit_id, unit_name, upper_id, upper_name, pcode, pname,
    r_id, r_type, r_code, r_name, r_unit_id, r_unit_name, r_upper_id, r_upper_name, r_pcode, r_pname
  from (
    select t.*, r_pcode_pname[1] as r_pcode, r_pcode_pname[2] as r_pname,
      a.id as r_id, a.type_ as r_type, a.code as r_code, a.name as r_name, u.name as r_unit_name, d.name as r_upper_name
    from (
      select h.*,
        identity_gen_actor_pcode_pname(h.actor_id) as r_pcode_pname,
        identity_find_actor_upper_unit_id(h.actor_id) as r_unit_id,
        identity_find_actor_upper_id(h.actor_id) as r_upper_id
      from bc_identity_actor_history h
      inner join bc_identity_actor a on a.id = h.actor_id
      where h.current and a.status_ = 0
      order by h.create_date desc
    ) t
    left join bc_identity_actor u on u.id = r_unit_id
    left join bc_identity_actor d on d.id = r_upper_id
    left join bc_identity_actor a on a.id = actor_id
  ) t
  --where t.actor_type != 3
  where true
  and (
    pcode != r_pcode or pname != r_pname
    or unit_name != r_unit_name
    or upper_name != r_upper_name
    or actor_name != r_name
  )
)
-- select array_agg(h.id order by h.id) from error_actor_history h;
-- select * from error_actor_history h order by h.create_date desc;
---- 插入新的纠正后的 actor_history: start_date='2024-05-01 00:00:00'
, new_actor_history(id) as (
  insert into bc_identity_actor_history (
    id, pid, current, rank, create_date, start_date, end_date,
    actor_id, actor_type, actor_code, actor_name,
    unit_id, unit_name, upper_id, upper_name, pcode, pname
  )
  select nextval('core_sequence') as id, h.id as pid, true, 0, now(), '2024-05-01 00:00:00'::timestamp, null,
    h.r_id, h.r_type, h.r_code, h.r_name,
    h.r_unit_id, h.r_unit_name, h.r_upper_id, h.r_upper_name, h.r_pcode, h.r_pname
  from error_actor_history h
  returning id
)
---- 更新错误的 actor_history 的 current=false, end_date='2024-05-01 00:00:00'
, fixed_actor_history(id) as (
  update bc_identity_actor_history h1 set
    current = false, end_date = '2024-05-01 00:00:00'::timestamp
  from error_actor_history h2
  where h2.id = h1.id
  returning h1.id
) select
  (select array_agg(id) from error_actor_history) as error_actor_history_ids,
  (select array_agg(id) from new_actor_history) as new_actor_history_ids,
  (select array_agg(id) from fixed_actor_history) as fixed_actor_history_ids;
*/