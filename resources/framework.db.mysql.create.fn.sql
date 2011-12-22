-- ##bc平台的 mysql 自定义函数和存储过程##

-- 模拟oracle dual功能的在hibernate hql中使用的视图
DROP VIEW IF EXISTS bc_dual; 
CREATE VIEW bc_dual AS select null;

DELIMITER $$ 
DROP PROCEDURE IF EXISTS update_actor_pcodepname $$ 
-- 创建更新actor的pcode和pname的存储过程：会递归处理下级单位和部门
-- pid: actor所隶属上级的id，为0代表顶层单位
CREATE PROCEDURE update_actor_pcodepname(pid bigint)
BEGIN
	-- 定义变量
	DECLARE done INT DEFAULT 0;
	DECLARE pfcode varchar(4000);
	DECLARE pfname varchar(4000);
	DECLARE cid bigint;
	DECLARE ct bigint;
	DECLARE pid1 bigint;

	-- 定义游标
	DECLARE curChilden CURSOR FOR select a.id,a.type_ from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id 
		where r.type_=0 and r.master_id=pid order by a.order_;
	DECLARE curTops CURSOR FOR select a.id from bc_identity_actor a where a.type_=1 and not exists 
		(select r.follower_id from bc_identity_actor_relation r where r.type_=0 and a.id=r.follower_id)
		order by a.order_;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;

	-- 启用存储过程的递归调用
	SET @@max_sp_recursion_depth = 10; 

	if pid > 0 then
		select concat((case when pcode is null then '' else concat(pcode,'/') end),'[',type_,']',code)
			,concat((case when pname is null then '' else concat(pname,'/') end),name)
			into pfcode,pfname from bc_identity_actor where id=pid;
		open curChilden;
		REPEAT
			fetch curChilden into cid,ct;
			IF NOT done THEN
				update bc_identity_actor a set a.pcode=pfcode,a.pname=pfname where a.id=cid;
				if ct < 3 then 
					-- 单位或部门执行递归处理
					call update_actor_pcodepname(cid);
				end if;
			END IF;
		UNTIL done END REPEAT;
		close curChilden;
	else
		open curTops;
		REPEAT
			fetch curTops into pid1;
			IF NOT done THEN
				call update_actor_pcodepname(pid1);
			END IF;
		UNTIL done END REPEAT;
		close curTops;
	end if; 
END $$ 
DELIMITER ; 

DELIMITER $$ 
DROP PROCEDURE IF EXISTS update_resource_pname $$ 
-- 创建更新resource的pname的存储过程：会递归处理下级资源
-- pid: resource所隶属的id，为0代表顶层资源
CREATE PROCEDURE update_resource_pname(pid bigint)
BEGIN
	-- 定义变量
	DECLARE done INT DEFAULT 0;
	DECLARE pfname varchar(4000);
	DECLARE cid bigint;
	DECLARE ct bigint;
	DECLARE pid1 bigint;

	-- 定义游标
	DECLARE curChilden CURSOR FOR select r.id,r.type_ from bc_identity_resource r where r.belong = pid order by r.order_;
	DECLARE curTops CURSOR FOR select r.id from bc_identity_resource r where r.belong is null order by r.order_;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;

	-- 启用存储过程的递归调用
	SET @@max_sp_recursion_depth = 10; 

	if pid > 0 then
		select concat((case when pname is null then '' else concat(pname,'/') end),name)
        	into pfname from bc_identity_resource where id=pid;
		open curChilden;
		REPEAT
			fetch curChilden into cid,ct;
			IF NOT done THEN
				update bc_identity_resource r set r.pname=pfname where r.id=cid;
				if ct = 1 then 
					-- 分类型资源执行递归处理
					call update_resource_pname(cid);
				end if;
			END IF;
		UNTIL done END REPEAT;
		close curChilden;
	else
		open curTops;
		REPEAT
			fetch curTops into pid1;
			IF NOT done THEN
				call update_resource_pname(pid1);
			END IF;
		UNTIL done END REPEAT;
		close curTops;
	end if; 
END $$ 
DELIMITER ; 