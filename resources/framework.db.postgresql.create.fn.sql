-- ##bc平台的 postgresql 自定义函数和存储过程##

-- 模拟oracle dual功能的在hibernate hql中使用的视图
CREATE OR REPLACE VIEW bc_dual AS SELECT NULL::unknown;

-- 创建更新actor的pcode和pname的存储过程：会递归处理下级单位和部门
--	pid: actor所隶属上级的id，为0代表顶层单位
CREATE OR REPLACE FUNCTION update_actor_pcodepname(pid IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	pfcode varchar(4000);
	pfname varchar(4000);
	t integer := 0;
	child RECORD;
BEGIN
  	if pid > 0 then
		select (case when pcode is null then '' else pcode || '/' end) || '[' || type_ || ']' || code
        	,(case when pname is null then '' else pname || '/' end) || name
        	into pfcode,pfname from bc_identity_actor where id=pid;

		FOR child IN select a.id id,a.type_ type_ from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id 
		where r.type_=0 and r.master_id=pid order by a.order_ LOOP
			update bc_identity_actor a set pcode=pfcode,pname=pfname where a.id=child.id;
			if child.type_ < 3 then 
				-- 单位或部门执行递归处理
				t := t + update_actor_pcodepname(child.id);
			end if;
		END LOOP;
	else
		FOR child IN select a.id id from bc_identity_actor a where a.type_=1 and not exists 
		(select r.follower_id from bc_identity_actor_relation r where r.type_=0 and a.id=r.follower_id)
	    order by a.order_ LOOP
			t := t + update_actor_pcodepname(child.id);
		END LOOP;
  	end if; 
	return t;
END;
$$ LANGUAGE plpgsql;


-- 创建更新resource的pname的存储过程：会递归处理下级资源
--	pid:resource所隶属的id，为0代表顶层资源
CREATE OR REPLACE FUNCTION update_resource_pname(pid IN integer) RETURNS integer AS $$
DECLARE
	--定义变量
	pfname varchar(4000);
	t integer := 0;
	child RECORD;
BEGIN
  	if pid > 0 then
		select (case when pname is null then '' else pname || '/' end) || name
        	into pfname from bc_identity_resource where id=pid;

		FOR child IN select r.id id,r.type_ type_ from bc_identity_resource r where r.belong = pid order by r.order_ LOOP
			update bc_identity_resource set pname=pfname where id=child.id;
			if child.type_ = 1 then 
				-- 分类型资源执行递归处理
				t := t + update_resource_pname(child.id);
			end if;
		END LOOP;
	else
		FOR child IN select r.id id from bc_identity_resource r where COALESCE(r.belong,0) = 0 order by r.order_ LOOP
			t := t + update_resource_pname(child.id);
		END LOOP;
  	end if; 
	return t;
END;
$$ LANGUAGE plpgsql;