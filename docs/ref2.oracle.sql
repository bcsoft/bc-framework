--Start with...Connect By�Ӿ�ݹ��ѯһ������һ����ά�����νṹ��Ӧ�á�
--����ʾ����
create table TBL_TEST
(
  ID   number,
  NAME varchar2(100 BYTE),
  PID  number
);

--����������ݣ�
insert into TBL_TEST (ID, NAME, PID) values ('1', '10', '0');
insert into TBL_TEST (ID, NAME, PID) values ('2', '11', '1');
insert into TBL_TEST (ID, NAME, PID) values ('3', '20', '0');
insert into TBL_TEST (ID, NAME, PID) values ('4', '12', '1');
insert into TBL_TEST (ID, NAME, PID) values ('5', '121', '2');

--��Root����ĩ�ҵݹ�
select t.*, sys_connect_by_path(t.name, '/'), connect_by_isleaf, connect_by_root id
from TBL_TEST t start with id=1 connect by prior id = pid;

--��ĩ������ROOT�ݹ�
select t.*, sys_connect_by_path(pid, '/'), connect_by_isleaf, connect_by_root
from TBL_TEST t start with id=5 connect by prior pid = id;

--��Root����ĩ�ҵݹ�
select t.*, sys_connect_by_path(t.name, '/'), connect_by_isleaf, connect_by_root id
from TBL_TEST t start with id=2 connect by prior id = pid;
