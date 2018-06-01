-- ##bc平台的 postgresql 自定义函数和存储过程##

-- 模拟oracle dual功能的在hibernate hql中使用的视图
create or replace view bc_dual as
  select null :: unknown;

-- 创建更新actor的pcode和pname的存储过程：会递归处理下级单位和部门
--	pid: actor所隶属上级的id，为0代表顶层单位
create or replace function update_actor_pcodepname(pid in integer) returns integer as $$
declare
  --定义变量
  pfcode varchar(4000);
  pfname varchar(4000);
  t      integer := 0;
  child  record;
begin
  if pid > 0
  then
    select (case when pcode is null
      then ''
            else pcode || '/' end) || '[' || type_ || ']' || code, (case when pname is null
      then ''
                                                                    else pname || '/' end) || name
    into pfcode, pfname
    from bc_identity_actor
    where id = pid;

    for child in select a.id id, a.type_ type_
                 from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id
                 where r.type_ = 0 and r.master_id = pid
                 order by a.order_ loop
      update bc_identity_actor a set pcode = pfcode, pname = pfname
      where a.id = child.id;
      if child.type_ < 3
      then
        -- 单位或部门执行递归处理
        t := t + update_actor_pcodepname(child.id);
      end if;
    end loop;
  else
    for child in select a.id id
                 from bc_identity_actor a
                 where a.type_ = 1 and not exists
                 (select r.follower_id
                  from bc_identity_actor_relation r
                  where r.type_ = 0 and a.id = r.follower_id)
                 order by a.order_ loop
      t := t + update_actor_pcodepname(child.id);
    end loop;
  end if;
  return t;
end;
$$ language plpgsql;


-- 创建更新resource的pname的存储过程：会递归处理下级资源
--	pid:resource所隶属的id，为0代表顶层资源
create or replace function update_resource_pname(pid in integer) returns integer as $$
declare
  --定义变量
  pfname varchar(4000);
  t      integer := 0;
  child  record;
begin
  if pid > 0
  then
    select (case when pname is null
      then ''
            else pname || '/' end) || name
    into pfname
    from bc_identity_resource
    where id = pid;

    for child in select r.id id, r.type_ type_
                 from bc_identity_resource r
                 where r.belong = pid
                 order by r.order_ loop
      update bc_identity_resource set pname = pfname
      where id = child.id;
      if child.type_ = 1
      then
        -- 分类型资源执行递归处理
        t := t + update_resource_pname(child.id);
      end if;
    end loop;
  else
    for child in select r.id id
                 from bc_identity_resource r
                 where COALESCE(r.belong, 0) = 0
                 order by r.order_ loop
      t := t + update_resource_pname(child.id);
    end loop;
  end if;
  return t;
end;
$$ language plpgsql;

-- 报表模板使用人列函数
create or replace function getreporttemplateuser(rtid integer)
  returns character varying as
$BODY$
declare
  -- 使用者字符串
  users   character varying;
  -- 记录使用者字符串长度
  _length integer;
  -- 一行结果的记录
  rowinfo record;
begin
  -- 初始化变量
  users := '';
  _length := 0;
  for rowinfo in select b.name
                 from bc_report_template a
                 inner join bc_report_template_actor r on r.tid = a.id
                 inner join bc_identity_actor b on r.aid = b.id
                 where a.id = rtid
    -- 循环开始
  loop
    users := users || rowinfo.name || ',';
  end loop;
  _length := length(users);
  if _length > 0
  then
    users := substring(users from 1 for _length - 1);
  end if;
  return users;
end;
$BODY$
language plpgsql;
