-- ##bcƽ̨�� oracle ɾ��ű�##

-- ���ý���Ϣ���������̨���������SQL Plus�������������sql�ļ���������ִ�����������ܿ��������Ϣ��
-- set serveroutput on;

-- ����ɾ��ָ���û���Ĵ洢����
create or replace procedure DROP_USER_TABLE
(
--����IN��ʾ���������
--OUT��ʾ������������Ϳ���ʹ������Oracle�еĺϷ����͡�
i_table_name in varchar2
)
as
--�������
num_ number;
str1 varchar2(1000);
begin
select count(1) into num_
from user_tables
where table_name = upper(i_table_name) or table_name = lower(i_table_name);
if num_ > 0 then
str1 := 'DROP TABLE ' || i_table_name;
execute immediate str1;
dbms_output.put_line('�� ' || i_table_name || ' ��ɾ��');
end if;
if num_ <= 0 then
dbms_output.put_line('�� ' || i_table_name || ' �����ڣ�����');
end if;
end;
/

-- ����ɾ��ָ�����еĴ洢����
create or replace procedure DROP_USER_SEQUENCE
(
i_sequence_name in varchar2
)
as
--�������
num_ number;
str1 varchar2(1000);
begin
select count(1) into num_
from user_sequences
where sequence_name = upper(i_sequence_name) or sequence_name = lower(i_sequence_name);
if num_ > 0 then
str1 := 'DROP SEQUENCE ' || i_sequence_name;
execute immediate str1;
dbms_output.put_line('���� ' || i_sequence_name || ' ��ɾ��');
end if;
if num_ <= 0 then
dbms_output.put_line('���� ' || i_sequence_name || ' �����ڣ�����');
end if;
end;
/

-- ����ɾ��ָ���洢���̵Ĵ洢����
create or replace procedure DROP_USER_PROCEDURE
(
i_proedure_name in varchar2
)
as
--�������
num_ number;
str1 varchar2(1000);
begin
select count(1) into num_
from user_procedures
where object_name = upper(i_proedure_name) or object_name = lower(i_proedure_name);
if num_ > 0 then
str1 := 'DROP PROCEDURE ' || i_proedure_name;
execute immediate str1;
dbms_output.put_line('�洢���� ' || i_proedure_name || ' ��ɾ��');
end if;
if num_ <= 0 then
dbms_output.put_line('�洢���� ' || i_proedure_name || ' �����ڣ�����');
end if;
end;
/

-- ����ɾ��ָ�������Ĵ洢����
create or replace procedure DROP_USER_FUNCTION
(
i_function_name in varchar2
)
as
--�������
num_ number;
str1 varchar2(1000);
begin
select count(1) into num_
from user_procedures
where object_name = upper(i_function_name) or object_name = lower(i_function_name);
if num_ > 0 then
str1 := 'DROP FUNCTION ' || i_function_name;
execute immediate str1;
dbms_output.put_line('���� ' || i_function_name || ' ��ɾ��');
end if;
if num_ <= 0 then
dbms_output.put_line('���� ' || i_function_name || ' �����ڣ�����');
end if;
end;
/

