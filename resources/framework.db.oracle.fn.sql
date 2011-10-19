-- ##bc平台的 oracle 自定义函数和存储过程##

-- 设置将信息输出到控制台（如果是在SQL Plus命令行运行这个sql文件，须先行执行这个命令才能看到输出信息）
-- set serveroutput on;

-- 创建删除指定用户表的存储过程
CREATE OR REPLACE PROCEDURE update_actor_pcodepname(
   --actor所隶属上级的id，为0代表顶层单位
   pid IN number
)
AS
--定义变量
pfcode varchar2(4000);
pfname varchar2(4000);
cid number;
cursor curChilden is select a.id from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id where r.type_=0 and r.master_id=pid;
BEGIN
	dbms_output.put_line('pid=' || pid);
    
  	if pid > 0 then
		select pcode || '/' || code,pname || '/' || name into pfcode,pfname from bc_identity_actor where id=pid;
        dbms_output.put_line('pfcode='||pfcode);
        dbms_output.put_line('pfname='||pfname);
        open curChilden;
        fetch curChilden into cid;
        while curChilden%found loop
            dbms_output.put_line('cid='||cid);
            fetch curChilden into cid;-- 将游标指向下条记录, 否则为死循环.
        end loop;
        close curChilden;
	else
		dbms_output.put_line('pid=null');
	end if; 
END;
/


-- 任务调度
set serveroutput on SIZE number(1000);
CALL update_actor_pcodepname(1);

-- 删除自建的存储过程
-- drop procedure DROP_USER_TABLE;
-- drop procedure DROP_USER_SEQUENCE;
select * from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id where r.type_=0 and r.master_id=1;
