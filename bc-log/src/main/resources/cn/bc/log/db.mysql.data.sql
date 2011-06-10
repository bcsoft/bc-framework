-- 插入1000条登录数据
-- 创建存储过程：loop_time为循环的次数
DELIMITER $$ 
    DROP PROCEDURE IF EXISTS test_create_syslog $$ 
    CREATE PROCEDURE test_create_syslog (loop_time int,userCode varchar(255)) 
    BEGIN 
        DECLARE i int default 0; 
        WHILE i <  loop_time DO 
        insert into BC_LOG_SYSTEM (TYPE_,CREATE_DATE,SUBJECT,CREATER_ID,CREATER_NAME,DEPART_ID,DEPART_NAME,UNIT_ID,UNIT_NAME,C_IP,S_IP,C_INFO) 
        	select 0,now(),concat(u.name,'登录系统',i),u.id,u.name,1,'D',1,'U','127.0.0.1','localhost','Chrome12'
        	from bc_identity_actor u where u.code=userCode;
        SET i = i + 1; 
        END WHILE; 
    END $$ 
DELIMITER ; 
-- 调用存储过程
CALL test_create_syslog(500,'admin'); 
CALL test_create_syslog(500,'huangrongji'); 