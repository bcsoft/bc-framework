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

-- 创建删除指定存储过程的存储过程
CREATE OR REPLACE PROCEDURE DROP_USER_PROCEDURE
(
   i_proedure_name IN varchar2
)
AS
--定义变量
num_ number;
str1 varchar2(1000);
BEGIN
  select count(1) into num_ from user_procedures where object_name = upper(i_proedure_name) or object_name = lower(i_proedure_name); 
  if num_ > 0 then 
    str1 := 'DROP PROCEDURE ' || i_proedure_name;
    execute immediate str1;
    dbms_output.put_line('存储过程 ' || i_proedure_name || ' 已删除');
  end if; 
  if num_ <= 0 then 
    dbms_output.put_line('存储过程 ' || i_proedure_name || ' 不存在，忽略');
  end if; 
END;
/

-- 创建删除指定函数的存储过程
CREATE OR REPLACE PROCEDURE DROP_USER_FUNCTION
(
   i_function_name IN varchar2
)
AS
--定义变量
num_ number;
str1 varchar2(1000);
BEGIN
  select count(1) into num_ from user_procedures where object_name = upper(i_function_name) or object_name = lower(i_function_name); 
  if num_ > 0 then 
    str1 := 'DROP FUNCTION ' || i_function_name;
    execute immediate str1;
    dbms_output.put_line('函数 ' || i_function_name || ' 已删除');
  end if; 
  if num_ <= 0 then 
    dbms_output.put_line('函数 ' || i_function_name || ' 不存在，忽略');
  end if; 
END;
/

