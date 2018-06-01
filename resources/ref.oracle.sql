-- ��������
create sequence HIBERNATE_SEQUENCE
  minvalue 1
  start with 1
  increment by 1
  cache 20;

-- ������
create table TBALE1 (
  ID   number(19)    not null,
  NAME varchar2(255) not null,
  CODE varchar2(255),
  primary key (ID)
);

-- ������ע��
comment on table TBALE1 is '�����ߵ���չ����';

-- ������ע��
comment on column TBALE1.NAME is '����ʱ��';

-- �������
alter table TBALE1
  add constraint FK1 foreign key (DETAIL_ID) references TBALE2 (ID);
alter table TBALE1
  add constraint FK1 foreign key (DETAIL_ID) references TBALE2 (ID) on delete cascade;

-- ��������
create index IDX1 on TBALE1 (NAME asc);

-- ���ý���Ϣ���������̨���������SQL Plus�������������sql�ļ���������ִ�����������ܿ��������Ϣ��
-- set serveroutput on;

-- ����ɾ��ָ���û���Ĵ洢����
create or replace procedure dropUserTable
(
--����IN��ʾ���������
--OUT��ʾ������������Ϳ���ʹ������Oracle�еĺϷ����͡�
i_table_name in varchar2
)
as
--�������
num number;
begin
select count(1) into num
from user_tables
where table_name = upper(i_table_name) or table_name = lower(i_table_name);
if num > 0 then
execute immediate 'drop table ' || i_table_name;
dbms_output.put_line('�� ' || i_table_name || ' ��ɾ��');
end if;
if num <= 0 then
dbms_output.put_line('�� ' || i_table_name || ' �����ڣ�����');
end if;
end;
/

-- ��ȡ�û���������������֯����λ�����ţ�
select f.name, m.name
from BC_IDENTITY_ACTOR m, BC_IDENTITY_ACTOR f
where f.type_ = 4 --f.code='qiong'
      and f.pcode like (case when m.pcode is null
  then ''
                        else m.pcode || '/' end) || '[' || m.type_ || ']' || m.code || '%'
order by m.order_;
    
