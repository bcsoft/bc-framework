-- 获取所有顶层单位信息
explain select m.* from BC_IDENTITY_ACTOR m where m.type_ =2 and m.id not in 
(select f.id from BC_IDENTITY_ACTOR f inner join BC_IDENTITY_ACTOR_RELATION ar on f.id=ar.follower_id where f.type_=2 and ar.type_=0);

-- 获取单位的下属单位或部门信息
explain select m1.name,f1.* from BC_IDENTITY_ACTOR f1
inner join BC_IDENTITY_ACTOR_RELATION ar1 on ar1.follower_id=f1.id
inner join BC_IDENTITY_ACTOR m1 on m1.id=ar1.master_id
where (f1.type_ =2) and ar1.type_=0 and m1.id in (
  select m.id from BC_IDENTITY_ACTOR m where m.type_=2 and m.id not in 
    (select f.id from BC_IDENTITY_ACTOR f inner join BC_IDENTITY_ACTOR_RELATION ar on f.id=ar.follower_id where f.type_=2 and ar.type_=0)
) order by f1.code;

-- 获取单位的下属部门信息
explain select m1.name,f1.* from BC_IDENTITY_ACTOR f1
inner join BC_IDENTITY_ACTOR_RELATION ar1 on ar1.follower_id=f1.id
inner join BC_IDENTITY_ACTOR m1 on m1.id=ar1.master_id
where f1.type_ =3 and ar1.type_=0 and m1.id in (
  select m.id from BC_IDENTITY_ACTOR m where m.type_=2 and m.id not in 
    (select f.id from BC_IDENTITY_ACTOR f inner join BC_IDENTITY_ACTOR_RELATION ar on f.id=ar.follower_id where f.type_=2 and ar.type_=0)
) order by f1.code;

-- 获取单位的上级单位
explain select f1.name,m1.* from BC_IDENTITY_ACTOR m1
inner join BC_IDENTITY_ACTOR_RELATION ar1 on ar1.master_id=m1.id
inner join BC_IDENTITY_ACTOR f1 on f1.id=ar1.follower_id
where m1.type_ =2 and ar1.type_=0 and f1.id = (select f.id from BC_IDENTITY_ACTOR f where f.name='修理厂') order by m1.code;

-- 获取部门的上级单位
explain select f1.name,m1.* from BC_IDENTITY_ACTOR m1
inner join BC_IDENTITY_ACTOR_RELATION ar1 on ar1.master_id=m1.id
inner join BC_IDENTITY_ACTOR f1 on f1.id=ar1.follower_id
where m1.type_ =2 and ar1.type_=0 and f1.id = (select f.id from BC_IDENTITY_ACTOR f where f.name='信息化项目小组') order by m1.code;

-- 使用in在mysql中会导致全表查询
explain select f.* from BC_IDENTITY_ACTOR f where f.type_ in(2,3);
explain (select f1.* from BC_IDENTITY_ACTOR f1 where f1.type_ =2) union all (select f1.* from BC_IDENTITY_ACTOR f1 where f1.type_ =3);

select * from BC_IDENTITY_ACTOR;

-- 创建存储过程：loop_time为循环的次数
DELIMITER $$ 
    DROP PROCEDURE IF EXISTS test $$ 
    CREATE PROCEDURE test (loop_time int) 
    BEGIN 
        DECLARE i int default 0; 
        WHILE i <  loop_time DO 
        insert into bc_identity_duty (STATUS_,INNER_,CODE,NAME) values(1,0,concat('c',i),concat('n',i));
        SET i = i + 1; 
        END WHILE; 
    END $$ 
DELIMITER ; 
-- 调用存储过程
-- CALL test(250); 


-- ====类oracle connect by 处理函数技术===
-- 创建测试数据
drop table if exists T1;
CREATE TABLE T1 (  
  id bigint NOT NULL auto_increment,  
  pid bigint,  
  code varchar(255),  
  PRIMARY KEY (id)  
);
insert into t1(id,pid,code) values(1,null,'1');
insert into t1(id,pid,code) values(2,null,'2');
insert into t1(id,pid,code) values(3,1,'1.1');
insert into t1(id,pid,code) values(4,1,'1.2');
insert into t1(id,pid,code) values(5,2,'2.1');
insert into t1(id,pid,code) values(6,3,'1.1.1');
select * from t1 order by code;

-- 定义递归处理函数:获取祖先的id和code，并用符号'/'按序连接，id和code间用';'连接
DELIMITER $$ 
     DROP FUNCTION IF EXISTS getAncestors $$ 
	CREATE FUNCTION getAncestors(id bigint) RETURNS VARCHAR(1000)
	BEGIN
            DECLARE done INT DEFAULT 0;
            DECLARE r VARCHAR(1000);
            DECLARE ri VARCHAR(1000);
            DECLARE rc VARCHAR(1000);
            DECLARE lev int;
            DECLARE cid bigint;
            DECLARE pid bigint;
            DECLARE pcode VARCHAR(255);
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1; 
            
            SET cid = id;
            SET lev = 0;
            SET ri = '';
            SET rc = '';
            REPEAT  
                SELECT p.id,p.code into pid,pcode FROM T1 c inner join  T1 p on p.id=c.pid where c.id=cid;
                IF NOT done THEN
                    SET cid = pid;
                    if length(ri) > 0 then
                        SET ri = concat(cast(pid as char),'/',ri);
                        SET rc = concat(cast(pid as char),'/',rc);
                    else 
                        SET ri= cast(pid as char);
                        SET rc= pcode;
                    end if;
                END IF;
            UNTIL done END REPEAT;
            if length(ri) > 0 then
                SET r = concat(ri,';',rc);
            else 
                SET r = null;
            end if;
           RETURN r;
    END $$ 
DELIMITER ; 
-- 返回：null;
select getAncestors(1);
-- 返回：'1;1';
select getAncestors(3);
-- 返回：'1/3;1/1.1';
select getAncestors(6);
-- =======================================