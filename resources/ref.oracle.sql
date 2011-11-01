-- 创建序列
create sequence HIBERNATE_SEQUENCE
    minvalue 1
    start with 1
    increment by 1
    cache 20;

-- 创建表
create table TBALE1 (
    ID NUMBER(19) not null,
    NAME varchar2(255) not null,
    CODE VARCHAR2(255),
    primary key (ID)
);

-- 创建表注释
COMMENT ON TABLE TBALE1 IS '参与者的扩展属性';

-- 创建列注释
COMMENT ON COLUMN TBALE1.NAME IS '创建时间';

-- 创建外键
ALTER TABLE TBALE1 ADD CONSTRAINT FK1 FOREIGN KEY (DETAIL_ID) REFERENCES TBALE2 (ID);
ALTER TABLE TBALE1 ADD CONSTRAINT FK1 FOREIGN KEY (DETAIL_ID) REFERENCES TBALE2 (ID) ON DELETE CASCADE;

-- 创建索引
CREATE INDEX IDX1 ON TBALE1 (NAME ASC);


-- 设置将信息输出到控制台（如果是在SQL Plus命令行运行这个sql文件，须先行执行这个命令才能看到输出信息）
-- set serveroutput on;

-- 创建删除指定用户表的存储过程
CREATE OR REPLACE PROCEDURE dropUserTable
(
   --参数IN表示输入参数，
   --OUT表示输入参数，类型可以使用任意Oracle中的合法类型。
   i_table_name IN varchar2
)
AS
--定义变量
num number;
BEGIN
	select count(1) into num from user_tables where table_name = upper(i_table_name) or table_name = lower(i_table_name); 
	if num > 0 then 
		execute immediate 'drop table ' || i_table_name;
		dbms_output.put_line('表 ' || i_table_name || ' 已删除');
	end if; 
	if num <= 0 then 
		dbms_output.put_line('表 ' || i_table_name || ' 不存在，忽略');
	end if; 
END;
/

-- 获取用户所隶属的祖先组织（单位、部门）
select f.name,m.name from BC_IDENTITY_ACTOR m,BC_IDENTITY_ACTOR f
	where f.type_=4 --f.code='qiong'
    and f.pcode like (case when m.pcode is null then '' else m.pcode || '/' end) || '[' || m.type_ || ']' || m.code || '%'
    order by m.order_;
    
