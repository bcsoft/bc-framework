-- ##bc平台的 oracle 自定义函数和存储过程##

-- 设置将信息输出到控制台（如果是在SQL Plus命令行运行这个sql文件，须先行执行这个命令才能看到输出信息）
-- set serveroutput on; -- pl/sql的SQL窗口不支持该命令，但在其命令窗口可以
-- set serveroutput on SIZE number(1000);

-- 创建更新actor的pcode和pname的存储过程：会递归处理下级单位和部门
CREATE OR REPLACE PROCEDURE update_actor_pcodepname(
   --actor所隶属上级的id，为0代表顶层单位
   pid IN number
)
AS
--定义变量
pfcode varchar2(4000);
pfname varchar2(4000);
cid number;
ct number;
pid1 number;
cursor curChilden is select a.id,a.type_ from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id 
	where r.type_=0 and r.master_id=pid order by a.order_;
cursor curTops is select a.id from bc_identity_actor a where a.type_=1 and not exists 
	(select r.follower_id from bc_identity_actor_relation r where r.type_=0 and a.id=r.follower_id)
    order by a.order_;
BEGIN
	dbms_output.put_line('pid=' || pid);
    
  	if pid > 0 then
		select (case when pcode is null then '' else pcode || '/' end) || '[' || type_ || ']' || code
        	,(case when pname is null then '' else pname || '/' end) || name
        	into pfcode,pfname from bc_identity_actor where id=pid;
        dbms_output.put_line('pfcode='||pfcode||',pfname='||pfname);
        open curChilden;
        fetch curChilden into cid,ct;
        while curChilden%found loop
            dbms_output.put_line('cid='||cid);
            update bc_identity_actor a set a.pcode=pfcode,a.pname=pfname where a.id=cid;
  			if ct < 3 then 
             	dbms_output.put_line('--');
           		-- 单位或部门执行递归处理
                update_actor_pcodepname(cid);
			end if;
            -- 将游标指向下条记录, 否则为死循环
            fetch curChilden into cid,ct;
        end loop;
        close curChilden;
	else
        open curTops;
        fetch curTops into pid1;
        while curTops%found loop
            update_actor_pcodepname(pid1);
            -- 将游标指向下条记录, 否则为死循环
            fetch curTops into pid1;
        end loop;
        close curTops;
  	end if; 
END;
/

-- 创建更新resource的pname的存储过程：会递归处理下级资源
CREATE OR REPLACE PROCEDURE update_resource_pname(
   --resource所隶属的id，为0代表顶层资源
   pid IN number
)
AS
--定义变量
pfname varchar2(4000);
cid number;
ct number;
pid1 number;
cursor curChilden is select r.id,r.type_ from bc_identity_resource r where r.belong = pid order by r.order_;
cursor curTops is select r.id from bc_identity_resource r where nvl(r.belong,0) = 0 order by r.order_;
BEGIN
	dbms_output.put_line('pid=' || pid);
    
  	if pid > 0 then
		select (case when pname is null then '' else pname || '/' end) || name
        	into pfname from bc_identity_resource where id=pid;
        dbms_output.put_line('pfname='||pfname);
        open curChilden;
        fetch curChilden into cid,ct;
        while curChilden%found loop
            dbms_output.put_line('cid='||cid);
            update bc_identity_resource r set r.pname=pfname where r.id=cid;
  			if ct = 1 then 
             	dbms_output.put_line('--');
           		-- 分类型资源执行递归处理
                update_resource_pname(cid);
			end if;
            -- 将游标指向下条记录, 否则为死循环
            fetch curChilden into cid,ct;
        end loop;
        close curChilden;
	else
        open curTops;
        fetch curTops into pid1;
        while curTops%found loop
            update_resource_pname(pid1);
            -- 将游标指向下条记录, 否则为死循环
            fetch curTops into pid1;
        end loop;
        close curTops;
  	end if; 
END;
/
