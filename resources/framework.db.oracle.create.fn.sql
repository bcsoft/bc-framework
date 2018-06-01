-- ##bcƽ̨�� oracle �Զ��庯���ʹ洢����##

-- ���ý���Ϣ���������̨���������SQL Plus�������������sql�ļ���������ִ�����������ܿ��������Ϣ��
-- set serveroutput on; -- pl/sql��SQL���ڲ�֧�ָ��������������ڿ���
-- set serveroutput on SIZE number(1000);

-- ��������actor��pcode��pname�Ĵ洢���̣���ݹ鴦���¼���λ�Ͳ���
create or replace procedure update_actor_pcodepname(
--actor�������ϼ���id��Ϊ0�����㵥λ
pid in number
)
as
--�������
pfcode varchar2(4000);
pfname varchar2(4000);
cid number;
ct number;
pid1 number;
cursor curChilden is select a.id, a.type_
                     from bc_identity_actor a inner join bc_identity_actor_relation r on r.follower_id = a.id
                     where r.type_ = 0 and r.master_id = pid
                     order by a.order_;
cursor curTops is select a.id
                  from bc_identity_actor a
                  where a.type_ = 1 and not exists
                  (select r.follower_id
                   from bc_identity_actor_relation r
                   where r.type_ = 0 and a.id = r.follower_id)
                  order by a.order_;
begin
dbms_output.put_line('pid=' || pid);

if pid > 0 then
select (case when pcode is null
  then ''
        else pcode || '/' end) || '[' || type_ || ']' || code, (case when pname is null
  then ''
                                                                else pname || '/' end) || name
into pfcode,pfname from bc_identity_actor where id = pid;
dbms_output.put_line('pfcode='||pfcode||',pfname='||pfname);
open curChilden;
fetch curChilden into cid, ct;
while curChilden%found loop
dbms_output.put_line('cid='||cid);
update bc_identity_actor a set a.pcode = pfcode, a.pname = pfname
where a.id = cid;
if ct < 3 then
dbms_output.put_line('--');
-- ��λ����ִ�еݹ鴦��
update_actor_pcodepname(cid);
end if;
-- ���α�ָ��������¼, ����Ϊ��ѭ��
fetch curChilden into cid, ct;
end loop;
close curChilden;
else
open curTops;
fetch curTops into pid1;
while curTops%found loop
update_actor_pcodepname(pid1);
-- ���α�ָ��������¼, ����Ϊ��ѭ��
fetch curTops into pid1;
end loop;
close curTops;
end if;
end;
/

-- ��������resource��pname�Ĵ洢���̣���ݹ鴦���¼���Դ
create or replace procedure update_resource_pname(
--resource��������id��Ϊ0��������Դ
pid in number
)
as
--�������
pfname varchar2(4000);
cid number;
ct number;
pid1 number;
cursor curChilden is select r.id, r.type_
                     from bc_identity_resource r
                     where r.belong = pid
                     order by r.order_;
cursor curTops is select r.id
                  from bc_identity_resource r
                  where nvl(r.belong, 0) = 0
                  order by r.order_;
begin
dbms_output.put_line('pid=' || pid);

if pid > 0 then
select (case when pname is null
  then ''
        else pname || '/' end) || name
into pfname
from bc_identity_resource
where id = pid;
dbms_output.put_line('pfname='||pfname);
open curChilden;
fetch curChilden into cid, ct;
while curChilden%found loop
dbms_output.put_line('cid='||cid);
update bc_identity_resource r set r.pname = pfname
where r.id = cid;
if ct = 1 then
dbms_output.put_line('--');
-- ��������Դִ�еݹ鴦��
update_resource_pname(cid);
end if;
-- ���α�ָ��������¼, ����Ϊ��ѭ��
fetch curChilden into cid, ct;
end loop;
close curChilden;
else
open curTops;
fetch curTops into pid1;
while curTops%found loop
update_resource_pname(pid1);
-- ���α�ָ��������¼, ����Ϊ��ѭ��
fetch curTops into pid1;
end loop;
close curTops;
end if;
end;
/
