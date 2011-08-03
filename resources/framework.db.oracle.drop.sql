-- ##bc平台的 oracle 删表脚本##

-- 设置将信息输出到控制台（如果是在SQL Plus命令行运行这个sql文件，须先行执行这个命令才能看到输出信息）
-- set serveroutput on;

-- 创建删除指定用户表的存储过程
CREATE OR REPLACE PROCEDURE DROP_USER_TABLE
(
   --参数IN表示输入参数，
   --OUT表示输入参数，类型可以使用任意Oracle中的合法类型。
   i_table_name IN varchar2
)
AS
--定义变量
num_ number;
str1 varchar2(1000);
BEGIN
  select count(1) into num_ from user_tables where table_name = upper(i_table_name) or table_name = lower(i_table_name); 
  if num_ > 0 then 
    str1 := 'DROP TABLE ' || i_table_name;
    execute immediate str1;
    dbms_output.put_line('表 ' || i_table_name || ' 已删除');
  end if; 
  if num_ <= 0 then 
    dbms_output.put_line('表 ' || i_table_name || ' 不存在，忽略');
  end if; 
END;
/

-- 创建删除指定序列的存储过程
CREATE OR REPLACE PROCEDURE DROP_USER_SEQUENCE
(
   i_sequence_name IN varchar2
)
AS
--定义变量
num_ number;
str1 varchar2(1000);
BEGIN
  select count(1) into num_ from user_sequences where sequence_name = upper(i_sequence_name) or sequence_name = lower(i_sequence_name); 
  if num_ > 0 then 
    str1 := 'DROP SEQUENCE ' || i_sequence_name;
    execute immediate str1;
    dbms_output.put_line('序列 ' || i_sequence_name || ' 已删除');
  end if; 
  if num_ <= 0 then 
    dbms_output.put_line('序列 ' || i_sequence_name || ' 不存在，忽略');
  end if; 
END;
/

-- 用于生成hibernate id的序列
CALL DROP_USER_SEQUENCE('hibernate_sequence');

-- 测试用的表
CALL DROP_USER_TABLE('BC_EXAMPLE');

-- 用户反馈
CALL DROP_USER_TABLE('BC_FEEDBACK');

-- 电子公告
CALL DROP_USER_TABLE('BC_BULLETIN');

-- 文档附件
CALL DROP_USER_TABLE('BC_DOCS_ATTACH_HISTORY');
CALL DROP_USER_TABLE('BC_DOCS_ATTACH');

-- 系统日志
CALL DROP_USER_TABLE('BC_LOG_SYSTEM');

-- 工作事务
CALL DROP_USER_TABLE('BC_WORK_TODO');
CALL DROP_USER_TABLE('BC_WORK_DONE');
CALL DROP_USER_TABLE('BC_WORK');

-- 消息管理
CALL DROP_USER_TABLE('BC_MESSAGE');

-- 个性化设置
CALL DROP_USER_TABLE('BC_DESKTOP_SHORTCUT');
CALL DROP_USER_TABLE('BC_DESKTOP_PERSONAL');

-- 系统标识
CALL DROP_USER_TABLE('BC_IDENTITY_ROLE_ACTOR');
CALL DROP_USER_TABLE('BC_IDENTITY_AUTH');
CALL DROP_USER_TABLE('BC_IDENTITY_ACTOR_RELATION');
CALL DROP_USER_TABLE('BC_IDENTITY_ACTOR_HISTORY');
CALL DROP_USER_TABLE('BC_IDENTITY_ACTOR');
CALL DROP_USER_TABLE('BC_IDENTITY_ACTOR_DETAIL');
CALL DROP_USER_TABLE('BC_IDENTITY_DUTY');
CALL DROP_USER_TABLE('BC_IDENTITY_IDGENERATOR');
CALL DROP_USER_TABLE('BC_IDENTITY_ROLE_RESOURCE');
CALL DROP_USER_TABLE('BC_IDENTITY_ROLE');
CALL DROP_USER_TABLE('BC_IDENTITY_RESOURCE');

-- 选项模块
CALL DROP_USER_TABLE('BC_OPTION_ITEM');
CALL DROP_USER_TABLE('BC_OPTION_GROUP');

-- 删除自建的存储过程
-- drop procedure DROP_USER_TABLE;
-- drop procedure DROP_USER_SEQUENCE;
