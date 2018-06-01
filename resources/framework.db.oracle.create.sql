-- ##bcƽ̨�� oracle ����ű�##

-- md5������select md5('888888') from dual --> 21218cca77804d2ba1922c33e0151105
create or replace function md5(passwd in varchar2)
return VARCHAR2 is retval varchar2(32);
begin
retval := lower(utl_raw.cast_to_raw(DBMS_OBFUSCATION_TOOLKIT.MD5(INPUT_STRING => passwd)));
return retval;
end;
/
-- ����Identity�õ����У���ʼ��1000
create sequence CORE_SEQUENCE
  minvalue 1
  start with 1000
  increment by 1
  cache 20;

-- �������У���ʼ��1ǧ�򣬷�����ʷ���ݵ�ת��
create sequence HIBERNATE_SEQUENCE
  minvalue 1
  start with 10000000
  increment by 1
  cache 20;

-- �����õı�
create table BC_EXAMPLE (
  ID   number(19)    not null,
  NAME varchar2(255) not null,
  CODE varchar2(255),
  primary key (ID)
);
comment on table BC_EXAMPLE is '�����õı�';
comment on column BC_EXAMPLE.NAME is '����';

-- ϵͳ��ʶ���ģ��
-- ϵͳ��Դ
create table BC_IDENTITY_RESOURCE (
  ID        number(19)          not null,
  UID_      varchar2(36),
  TYPE_     number(1) default 0 not null,
  STATUS_   number(1) default 0 not null,
  INNER_    number(1) default 0 not null,
  BELONG    number(19),
  ORDER_    varchar2(100),
  NAME      varchar2(255)       not null,
  URL       varchar2(255),
  ICONCLASS varchar2(255),
  OPTION_   varchar2(4000),
  PNAME     varchar2(4000),
  constraint BCPK_RESOURCE primary key (ID)
);
comment on table BC_IDENTITY_RESOURCE is 'ϵͳ��Դ';
comment on column BC_IDENTITY_RESOURCE.TYPE_ is '���ͣ�1-�ļ���,2-�ڲ�����,3-�ⲿ����,4-html';
comment on column BC_IDENTITY_RESOURCE.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_IDENTITY_RESOURCE.INNER_ is '�Ƿ�Ϊ���ö���:0-��,1-��';
comment on column BC_IDENTITY_RESOURCE.BELONG is '����������Դ';
comment on column BC_IDENTITY_RESOURCE.ORDER_ is '�����';
comment on column BC_IDENTITY_RESOURCE.NAME is '����';
comment on column BC_IDENTITY_RESOURCE.URL is '��ַ';
comment on column BC_IDENTITY_RESOURCE.ICONCLASS is 'ͼ����ʽ';
comment on column BC_IDENTITY_RESOURCE.OPTION_ is '��չ����';
comment on column BC_IDENTITY_RESOURCE.PNAME is '������ģ���ȫ��:��ϵͳά��/��֯�ܹ�/��λ����';
create index BCIDX_RESOURCE_BELONG on BC_IDENTITY_RESOURCE (BELONG);
create index BCIDX_RESOURCE_BELONG_NULL on BC_IDENTITY_RESOURCE (nvl(BELONG, 0));

-- ��ɫ
create table BC_IDENTITY_ROLE (
  ID      number(19)    not null,
  CODE    varchar2(100) not null,
  ORDER_  varchar2(100),
  NAME    varchar2(255) not null,
  UID_    varchar2(36),
  TYPE_   number(1)     not null,
  STATUS_ number(1)     not null,
  INNER_  number(1)     not null,
  constraint BCPK_ROLE primary key (ID)
);
comment on table BC_IDENTITY_ROLE is '��ɫ';
comment on column BC_IDENTITY_ROLE.CODE is '����';
comment on column BC_IDENTITY_ROLE.ORDER_ is '�����';
comment on column BC_IDENTITY_ROLE.NAME is '����';
comment on column BC_IDENTITY_ROLE.TYPE_ is '����';
comment on column BC_IDENTITY_ROLE.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_IDENTITY_ROLE.INNER_ is '�Ƿ�Ϊ���ö���:0-��,1-��';

-- ��ɫ����Դ�Ĺ���
create table BC_IDENTITY_ROLE_RESOURCE (
  RID number(19) not null,
  SID number(19) not null,
  constraint BCPK_ROLE_RESOURCE primary key (RID, SID)
);
comment on table BC_IDENTITY_ROLE_RESOURCE is '��ɫ����Դ�Ĺ�������ɫ���Է�����Щ��Դ';
comment on column BC_IDENTITY_ROLE_RESOURCE.RID is '��ɫID';
comment on column BC_IDENTITY_ROLE_RESOURCE.SID is '��ԴID';
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_ROLE foreign key (RID) references BC_IDENTITY_ROLE (ID);
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_RESOURCE foreign key (SID) references BC_IDENTITY_RESOURCE (ID);

-- ְ��
create table BC_IDENTITY_DUTY (
  ID   int           not null,
  CODE varchar2(100) not null,
  NAME varchar2(255) not null,
  constraint BCPK_DUTY primary key (ID)
);
comment on table BC_IDENTITY_DUTY is 'ְ��';
comment on column BC_IDENTITY_DUTY.CODE is '����';
comment on column BC_IDENTITY_DUTY.NAME is '����';

-- �����ߵ���չ����
create table BC_IDENTITY_ACTOR_DETAIL (
  ID          number(19) not null,
  CREATE_DATE date,
  WORK_DATE   date,
  ISO         number(1) default 0,
  SEX         number(1) default 0,
  CARD        varchar2(20),
  DUTY_ID     number(19),
  COMMENT_    varchar2(4000),
  constraint BCPK_ACTOR_DETAIL primary key (ID)
);
comment on table BC_IDENTITY_ACTOR_DETAIL is '�����ߵ���չ����';
comment on column BC_IDENTITY_ACTOR_DETAIL.CREATE_DATE is '����ʱ��';
comment on column BC_IDENTITY_ACTOR_DETAIL.WORK_DATE is 'user-��ְʱ��';
comment on column BC_IDENTITY_ACTOR_DETAIL.SEX is 'user-�Ա�0-δ����,1-��,2-Ů';
comment on column BC_IDENTITY_ACTOR_DETAIL.DUTY_ID is 'user-ְ��ID';
comment on column BC_IDENTITY_ACTOR_DETAIL.COMMENT_ is '��ע';
alter table BC_IDENTITY_ACTOR_DETAIL
  add constraint BCFK_ACTORDETAIL_DUTY foreign key (DUTY_ID) references BC_IDENTITY_DUTY (ID);

-- ������
create table BC_IDENTITY_ACTOR (
  ID        number(19)          not null,
  UID_      varchar2(36)        not null,
  TYPE_     number(1) default 0 not null,
  STATUS_   number(1) default 0 not null,
  INNER_    number(1) default 0 not null,
  CODE      varchar2(100)       not null,
  NAME      varchar2(255)       not null,
  PY        varchar2(255),
  ORDER_    varchar2(100),
  EMAIL     varchar2(255),
  PHONE     varchar2(255),
  DETAIL_ID number(19),
  PCODE     varchar2(4000),
  PNAME     varchar2(4000),
  constraint BCPK_ACTOR primary key (ID)
);
comment on table BC_IDENTITY_ACTOR is '������(����һ���˻���֯����֯Ҳ�����ǵ�λ�����š���λ���Ŷӵ�)';
comment on column BC_IDENTITY_ACTOR.UID_ is 'ȫ�ֱ�ʶ';
comment on column BC_IDENTITY_ACTOR.TYPE_ is '���ͣ�0-δ����,1-��λ,2-����,3-��λ,4-�û�';
comment on column BC_IDENTITY_ACTOR.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_IDENTITY_ACTOR.INNER_ is '�Ƿ�Ϊ���ö���:0-��,1-��';
comment on column BC_IDENTITY_ACTOR.CODE is '����';
comment on column BC_IDENTITY_ACTOR.NAME is '����';
comment on column BC_IDENTITY_ACTOR.PY is '���Ƶ�ƴ��';
comment on column BC_IDENTITY_ACTOR.ORDER_ is 'ͬ�������֮��������';
comment on column BC_IDENTITY_ACTOR.EMAIL is '����';
comment on column BC_IDENTITY_ACTOR.PHONE is '��ϵ�绰';
comment on column BC_IDENTITY_ACTOR.DETAIL_ID is '��չ���ID';
comment on column BC_IDENTITY_ACTOR.PCODE is '����������ȫ����';
comment on column BC_IDENTITY_ACTOR.PNAME is '����������ȫ��';
alter table BC_IDENTITY_ACTOR
  add constraint BCFK_ACTOR_ACTORDETAIL foreign key (DETAIL_ID)
references BC_IDENTITY_ACTOR_DETAIL (ID) on delete cascade;
create index BCIDX_ACTOR_CODE on BC_IDENTITY_ACTOR (CODE asc);
create index BCIDX_ACTOR_NAME on BC_IDENTITY_ACTOR (NAME asc);
create index BCIDX_ACTOR_STATUSTYPE on BC_IDENTITY_ACTOR (STATUS_ asc, TYPE_ asc);
create index BCIDX_ACTOR_TYPE on BC_IDENTITY_ACTOR (TYPE_ asc);
create index BCIDX_ACTOR_DETAIL on BC_IDENTITY_ACTOR (DETAIL_ID asc);

-- ������֮��Ĺ�����ϵ
create table BC_IDENTITY_ACTOR_RELATION (
  TYPE_       number(2)  not null,
  MASTER_ID   number(19) not null,
  FOLLOWER_ID number(19) not null,
  ORDER_      varchar2(100),
  constraint BCPK_ACTOR_RELATION primary key (MASTER_ID, FOLLOWER_ID, TYPE_)
);
comment on table BC_IDENTITY_ACTOR_RELATION is '������֮��Ĺ�����ϵ';
comment on column BC_IDENTITY_ACTOR_RELATION.TYPE_ is '��������';
comment on column BC_IDENTITY_ACTOR_RELATION.MASTER_ID is '���ط�ID';
comment on column BC_IDENTITY_ACTOR_RELATION.FOLLOWER_ID is '������ID';
comment on column BC_IDENTITY_ACTOR_RELATION.ORDER_ is '������֮��������';
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_MASTER foreign key (MASTER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_FOLLOWER foreign key (FOLLOWER_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_AR_TM on BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID);
create index BCIDX_AR_TF on BC_IDENTITY_ACTOR_RELATION (TYPE_, FOLLOWER_ID);

-- ACTOR������Ϣ�ı䶯��ʷ
create table BC_IDENTITY_ACTOR_HISTORY (
  ID          number(19)    not null,
  PID         number(19),
  CURRENT     number(1)     not null default 1,
  RANK        number(2)     not null default 0,
  CREATE_DATE date          not null,
  START_DATE  date,
  END_DATE    date,
  ACTOR_TYPE  number(1)     not null,
  ACTOR_ID    number(19)    not null,
  ACTOR_NAME  varchar2(100) not null,
  UPPER_ID    number(19),
  UPPER_NAME  varchar2(255),
  UNIT_ID     number(19),
  UNIT_NAME   varchar2(255),
  PCODE       varchar2(4000),
  PNAME       varchar2(4000),
  constraint BCPK_ACTOR_HISTORY primary key (ID)
);
comment on table BC_IDENTITY_ACTOR_HISTORY is 'ACTOR������Ϣ�ı䶯��ʷ';
comment on column BC_IDENTITY_ACTOR_HISTORY.PID is '��Ӧ�ɼ�¼��id';
comment on column BC_IDENTITY_ACTOR_HISTORY.CURRENT is '�Ƿ�Ϊ��ǰ����';
comment on column BC_IDENTITY_ACTOR_HISTORY.RANK is '�����ǰ���ü����ѡ������ֵԽС����Խ�ߣ�ֵ��0��ʼ������ֻ���������������֯�����';
comment on column BC_IDENTITY_ACTOR_HISTORY.CREATE_DATE is '����ʱ��';
comment on column BC_IDENTITY_ACTOR_HISTORY.START_DATE is '��ʼʱ��';
comment on column BC_IDENTITY_ACTOR_HISTORY.END_DATE is '����ʱ��';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_TYPE is 'ACTOR����';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_ID is 'ACTORID';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_NAME is 'ACTOR����';
comment on column BC_IDENTITY_ACTOR_HISTORY.UPPER_ID is 'ֱ���ϼ�ID';
comment on column BC_IDENTITY_ACTOR_HISTORY.UPPER_NAME is 'ֱ���ϼ�����';
comment on column BC_IDENTITY_ACTOR_HISTORY.UNIT_ID is '���ڵ�λID';
comment on column BC_IDENTITY_ACTOR_HISTORY.UNIT_NAME is '���ڵ�λ����';
comment on column BC_IDENTITY_ACTOR_HISTORY.PCODE is '����������ȫ����';
comment on column BC_IDENTITY_ACTOR_HISTORY.PNAME is '����������ȫ��';
alter table BC_IDENTITY_ACTOR_HISTORY
  add constraint BCFK_ACTORHISTORY_PID foreign key (PID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_IDENTITY_ACTOR_HISTORY
  add constraint BCFK_ACTORHISTORY_ACTOR foreign key (ACTOR_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_ACTORHISTORY_ACTORID on BC_IDENTITY_ACTOR_HISTORY (ACTOR_ID asc);
create index BCIDX_ACTORHISTORY_ACTORNAME on BC_IDENTITY_ACTOR_HISTORY (ACTOR_NAME asc);
create index BCIDX_ACTORHISTORY_UPPER on BC_IDENTITY_ACTOR_HISTORY (UPPER_ID asc);
create index BCIDX_ACTORHISTORY_UNIT on BC_IDENTITY_ACTOR_HISTORY (UNIT_ID asc);

-- ��֤��Ϣ
create table BC_IDENTITY_AUTH (
  ID       number(19)   not null,
  PASSWORD varchar2(32) not null,
  constraint BCPK_AUTH primary key (ID)
);
comment on table BC_IDENTITY_AUTH is '��֤��Ϣ';
comment on column BC_IDENTITY_AUTH.ID is '��Actor���id��Ӧ';
comment on column BC_IDENTITY_AUTH.PASSWORD is '��MD5���ܵ�����';
alter table BC_IDENTITY_AUTH
  add constraint BCFK_AUTH_ACTOR foreign key (ID)
references BC_IDENTITY_ACTOR (ID);

-- ��ʶ������
create table BC_IDENTITY_IDGENERATOR (
  TYPE_  varchar2(45) not null,
  VALUE_ number(19)   not null,
  FORMAT varchar2(45),
  constraint BCPK_IDGENERATOR primary key (TYPE_)
);
comment on table BC_IDENTITY_IDGENERATOR is '��ʶ������,��������������Ψһ������';
comment on column BC_IDENTITY_IDGENERATOR.TYPE_ is '����';
comment on column BC_IDENTITY_IDGENERATOR.VALUE_ is '��ǰֵ';
comment on column BC_IDENTITY_IDGENERATOR.FORMAT is '��ʽģ��,�硰case-${V}������${T}-${V}��,V����value��ֵ��T����type_��ֵ';

-- ���������ɫ�Ĺ���
create table BC_IDENTITY_ROLE_ACTOR (
  AID number(19) not null,
  RID number(19) not null,
  constraint BCPK_ROLE_ACTOR primary key (AID, RID)
);
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ROLE foreign key (RID)
references BC_IDENTITY_ROLE (ID);
comment on table BC_IDENTITY_ROLE_ACTOR is '���������ɫ�Ĺ�����������ӵ����Щ��ɫ';
comment on column BC_IDENTITY_ROLE_ACTOR.AID is '������ID';
comment on column BC_IDENTITY_ROLE_ACTOR.RID is '��ɫID';

-- ##ϵͳ�������ģ��##
-- �����ݷ�ʽ
create table BC_DESKTOP_SHORTCUT (
  ID         number(19)           not null,
  UID_       varchar(36),
  STATUS_    number(1)            not null,
  INNER_     number(1)            not null,
  ORDER_     varchar(100)         not null,
  STANDALONE number(1)            not null,
  NAME       varchar(255),
  URL        varchar(255),
  ICONCLASS  varchar(255),
  SID        number(19) default 0 not null,
  AID        number(19) default 0 not null,
  constraint BCPK_DESKTOP_SHORTCUT primary key (ID)
);
comment on table BC_DESKTOP_SHORTCUT is '�����ݷ�ʽ';
comment on column BC_DESKTOP_SHORTCUT.UID_ is 'ȫ�ֱ�ʶ';
comment on column BC_DESKTOP_SHORTCUT.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_DESKTOP_SHORTCUT.INNER_ is '�Ƿ�Ϊ���ö���:0-��,1-��';
comment on column BC_DESKTOP_SHORTCUT.STANDALONE is '�Ƿ��ڶ���������������д�';
comment on column BC_DESKTOP_SHORTCUT.NAME is '����,Ϊ����ʹ��ģ�������';
comment on column BC_DESKTOP_SHORTCUT.URL is '��ַ,Ϊ����ʹ��ģ��ĵ�ַ';
comment on column BC_DESKTOP_SHORTCUT.ICONCLASS is 'ͼ����ʽ';
comment on column BC_DESKTOP_SHORTCUT.SID is '��Ӧ����ԴID';
comment on column BC_DESKTOP_SHORTCUT.AID is '�����Ĳ�����(���Ϊ�ϼ�������,�絥λ����,�����µ����в����߶�ӵ�иÿ�ݷ�ʽ)';
create index BCIDX_SHORTCUT on BC_DESKTOP_SHORTCUT (AID, SID);

-- ��������
create table BC_DESKTOP_PERSONAL (
  ID      number(19)           not null,
  UID_    varchar2(36),
  STATUS_ number(1)            not null,
  FONT    varchar2(2)          not null,
  THEME   varchar2(255)        not null,
  AID     number(19) default 0 not null,
  INNER_  number(1)            not null,
  constraint BCPK_DESKTOP_PERSONAL primary key (ID)
);
comment on table BC_DESKTOP_PERSONAL is '��������';
comment on column BC_DESKTOP_PERSONAL.UID_ is 'ȫ�ֱ�ʶ';
comment on column BC_DESKTOP_PERSONAL.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_DESKTOP_PERSONAL.FONT is '�����С����12��14��16';
comment on column BC_DESKTOP_PERSONAL.THEME is '��������,��base';
comment on column BC_DESKTOP_PERSONAL.AID is '�����Ĳ�����';
comment on column BC_DESKTOP_PERSONAL.INNER_ is '�Ƿ�Ϊ���ö���:0-��,1-��';
alter table BC_DESKTOP_PERSONAL
  add constraint BCUK_PERSONAL_AID unique (AID);

-- ��Ϣģ��
create table BC_MESSAGE (
  ID          number(19)          not null,
  UID_        varchar2(36),
  STATUS_     number(1) default 0 not null,
  TYPE_       number(1) default 0 not null,
  SENDER_ID   number(19)          not null,
  SEND_DATE   date                not null,
  RECEIVER_ID number(19)          not null,
  READ_       number(1) default 0 not null,
  FROM_ID     number(19),
  FROM_TYPE   number(19),
  SUBJECT     varchar2(255)       not null,
  CONTENT     varchar2(4000),
  constraint BCPK_MESSAGE primary key (ID)
);
comment on table BC_MESSAGE is '��Ϣģ��';
comment on column BC_MESSAGE.STATUS_ is '״̬��0-������,1-�ѷ���,2-��ɾ��,3-����ʧ��';
comment on column BC_MESSAGE.TYPE_ is '��Ϣ����';
comment on column BC_MESSAGE.SENDER_ID is '������';
comment on column BC_MESSAGE.SEND_DATE is '����ʱ��';
comment on column BC_MESSAGE.RECEIVER_ID is '������';
comment on column BC_MESSAGE.FROM_ID is '��Դ��ʶ';
comment on column BC_MESSAGE.FROM_TYPE is '��Դ����';
comment on column BC_MESSAGE.READ_ is '���ı��';
comment on column BC_MESSAGE.SUBJECT is '����';
comment on column BC_MESSAGE.CONTENT is '����';
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_REVEIVER foreign key (RECEIVER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_MESSAGE_FROMID on BC_MESSAGE (FROM_TYPE, FROM_ID);
create index BCIDX_MESSAGE_TYPE on BC_MESSAGE (TYPE_);

-- ��������
create table BC_WORK (
  ID         number(19)    not null,
  CLASSIFIER varchar2(500) not null,
  SUBJECT    varchar2(255) not null,
  FROM_ID    number(19),
  FROM_TYPE  number(19),
  FROM_INFO  varchar2(255),
  OPEN_URL   varchar2(255),
  CONTENT    varchar2(4000),
  constraint BCPK_WORK primary key (ID)
);
comment on table BC_WORK is '��������';
comment on column BC_WORK.CLASSIFIER is '�����,�ɶ༶����,����ʹ��/����,�硰������/��ʽ���ġ�';
comment on column BC_WORK.SUBJECT is '����';
comment on column BC_WORK.FROM_ID is '��Դ��ʶ';
comment on column BC_WORK.FROM_TYPE is '��Դ����';
comment on column BC_WORK.FROM_INFO is '��Դ����';
comment on column BC_WORK.OPEN_URL is '�򿪵�Urlģ��';
comment on column BC_WORK.CONTENT is '����';
create index BCIDX_WORK_FROM on BC_WORK (FROM_TYPE, FROM_ID);

-- ��������
create table BC_WORK_TODO (
  ID        number(19) not null,
  WORK_ID   number(19) not null,
  SENDER_ID number(19) not null,
  SEND_DATE date       not null,
  WORKER_ID number(19) not null,
  INFO      varchar2(255),
  constraint BCPK_WORK_TODO primary key (ID)
);
comment on table BC_WORK_TODO is '��������';
comment on column BC_WORK_TODO.WORK_ID is '��������ID';
comment on column BC_WORK_TODO.SENDER_ID is '������';
comment on column BC_WORK_TODO.SEND_DATE is '����ʱ��';
comment on column BC_WORK_TODO.WORKER_ID is '������';
comment on column BC_WORK_TODO.INFO is '����˵��';
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_WORK foreign key (WORK_ID)
references BC_WORK (ID);
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR (ID);

-- �Ѱ�����
create table BC_WORK_DONE (
  ID           number(19) not null,
  FINISH_DATE  date       not null,
  FINISH_YEAR  number(4)  not null,
  FINISH_MONTH number(2)  not null,
  FINISH_DAY   number(2)  not null,
  WORK_ID      number(19) not null,
  SENDER_ID    number(19) not null,
  SEND_DATE    date       not null,
  WORKER_ID    number(19) not null,
  INFO         varchar2(255),
  constraint BCPK_WORK_DONE primary key (ID)
);
comment on table BC_WORK_DONE is '�Ѱ�����';
comment on column BC_WORK_DONE.FINISH_DATE is '���ʱ��';
comment on column BC_WORK_DONE.FINISH_YEAR is '���ʱ������';
comment on column BC_WORK_DONE.FINISH_MONTH is '���ʱ����·�(1-12)';
comment on column BC_WORK_DONE.FINISH_DAY is '���ʱ�����(1-31)';
comment on column BC_WORK_DONE.WORK_ID is '��������ID';
comment on column BC_WORK_DONE.SENDER_ID is '������';
comment on column BC_WORK_DONE.SEND_DATE is '����ʱ��';
comment on column BC_WORK_DONE.WORKER_ID is '������';
comment on column BC_WORK_DONE.INFO is '����˵��';
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORK foreign key (WORK_ID)
references BC_WORK (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_DONEWORK_FINISHDATE on BC_WORK_DONE (FINISH_YEAR, FINISH_MONTH, FINISH_DAY);

-- ϵͳ��־
create table BC_LOG_SYSTEM (
  ID        number(19)    not null,
  TYPE_     number(1)     not null,
  SUBJECT   varchar2(500) not null,
  FILE_DATE date          not null,
  AUTHOR_ID number(19)    not null,
  C_IP      varchar2(100),
  C_NAME    varchar2(100),
  C_INFO    varchar2(1000),
  S_IP      varchar2(100),
  S_NAME    varchar2(100),
  S_INFO    varchar2(1000),
  CONTENT   varchar2(4000),
  constraint BCPK_LOG_SYSTEM primary key (ID)
);
comment on table BC_LOG_SYSTEM is 'ϵͳ��־';
comment on column BC_LOG_SYSTEM.TYPE_ is '���0-��¼,1-ע��,2-��ʱ';
comment on column BC_LOG_SYSTEM.FILE_DATE is '����ʱ��';
comment on column BC_LOG_SYSTEM.SUBJECT is '����';
comment on column BC_LOG_SYSTEM.AUTHOR_ID is '������ID';
comment on column BC_LOG_SYSTEM.C_IP is '�û�����IP';
comment on column BC_LOG_SYSTEM.C_NAME is '�û���������';
comment on column BC_LOG_SYSTEM.C_INFO is '�û��������Ϣ��User-Agent';
comment on column BC_LOG_SYSTEM.S_IP is '������IP';
comment on column BC_LOG_SYSTEM.S_NAME is '����������';
comment on column BC_LOG_SYSTEM.S_INFO is '��������Ϣ';
comment on column BC_LOG_SYSTEM.CONTENT is '��ϸ����';
alter table BC_LOG_SYSTEM
  add constraint BCFK_SYSLOG_USER foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_SYSLOG_CLIENT on BC_LOG_SYSTEM (C_IP);
create index BCIDX_SYSLOG_SERVER on BC_LOG_SYSTEM (S_IP);

-- ����ģ��
create table BC_BULLETIN (
  ID            number(19)     not null,
  UID_          varchar2(36)   not null,
  UNIT_ID       number(19),
  SCOPE         number(1)      not null,
  STATUS_       number(1)      not null,
  OVERDUE_DATE  date,
  ISSUE_DATE    date,
  ISSUER_ID     number(19),
  SUBJECT       varchar2(500)  not null,
  FILE_DATE     date           not null,
  AUTHOR_ID     number(19)     not null,
  MODIFIER_ID   number(19),
  MODIFIED_DATE date,
  CONTENT       varchar2(4000) not null,
  constraint BCPK_BULLETIN primary key (ID)
);
comment on table BC_BULLETIN is '����ģ��';
comment on column BC_BULLETIN.UID_ is '���������ı�ʶ��';
comment on column BC_BULLETIN.UNIT_ID is '����������λID';
comment on column BC_BULLETIN.SCOPE is '���淢����Χ��0-����λ,1-ȫϵͳ';
comment on column BC_BULLETIN.STATUS_ is '״̬:0-�ݸ�,1-�ѷ���,2-�ѹ���';
comment on column BC_BULLETIN.OVERDUE_DATE is '�������ڣ�Ϊ�մ�����������';
comment on column BC_BULLETIN.ISSUE_DATE is '����ʱ��';
comment on column BC_BULLETIN.ISSUER_ID is '������ID';
comment on column BC_BULLETIN.SUBJECT is '����';
comment on column BC_BULLETIN.FILE_DATE is '����ʱ��';
comment on column BC_BULLETIN.AUTHOR_ID is '������ID';
comment on column BC_BULLETIN.MODIFIER_ID is '����޸���ID';
comment on column BC_BULLETIN.MODIFIED_DATE is '����޸�ʱ��';
comment on column BC_BULLETIN.CONTENT is '��ϸ����';
alter table BC_BULLETIN
  add constraint BCFK_BULLETIN_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_BULLETIN
  add constraint BCFK_BULLETIN_ISSUER foreign key (ISSUER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_BULLETIN
  add constraint BCFK_BULLETIN_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_BULLETIN
  add constraint BCFK_BULLETIN_UNIT foreign key (UNIT_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_BULLETIN_SEARCH on BC_BULLETIN (SCOPE, UNIT_ID, STATUS_);

-- �ĵ�����
create table BC_DOCS_ATTACH (
  ID            number(19)           not null,
  STATUS_       number(1)            not null,
  PTYPE         varchar2(36)         not null,
  PUID          varchar2(36)         not null,
  SIZE_         number(19)           not null,
  COUNT_        number(19) default 0 not null,
  EXT           varchar2(10),
  APPPATH       number(1)            not null,
  SUBJECT       varchar2(500)        not null,
  PATH          varchar2(500)        not null,
  FILE_DATE     date                 not null,
  AUTHOR_ID     number(19)           not null,
  MODIFIER_ID   number(19),
  MODIFIED_DATE date,
  constraint BCPK_ATTACH primary key (ID)
);
comment on table BC_DOCS_ATTACH is '�ĵ�����,��¼�ĵ�������ظ���֮��Ĺ�ϵ';
comment on column BC_DOCS_ATTACH.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_DOCS_ATTACH.PTYPE is '�������ĵ�������';
comment on column BC_DOCS_ATTACH.PUID is '�������ĵ���UID';
comment on column BC_DOCS_ATTACH.SIZE_ is '�ļ��Ĵ�С(��λΪ�ֽ�)';
comment on column BC_DOCS_ATTACH.COUNT_ is '�ļ������ش���';
comment on column BC_DOCS_ATTACH.EXT is '�ļ���չ������png��doc��mp3��';
comment on column BC_DOCS_ATTACH.APPPATH is 'ָ��path��ֵ�������Ӧ�ò���Ŀ¼��·�����������ȫ�����õ�app.dataĿ¼�µ�·��';
comment on column BC_DOCS_ATTACH.SUBJECT is '�ļ�����(����·���Ĳ���)';
comment on column BC_DOCS_ATTACH.PATH is '�����ļ���������·��(�����ȫ�����õĸ�����Ŀ¼�µ���·������"2011/bulletin/xxxx.doc")';

comment on column BC_DOCS_ATTACH.FILE_DATE is '����ʱ��';
comment on column BC_DOCS_ATTACH.AUTHOR_ID is '������ID';
comment on column BC_DOCS_ATTACH.MODIFIER_ID is '����޸���ID';
comment on column BC_DOCS_ATTACH.MODIFIED_DATE is '����޸�ʱ��';
alter table BC_DOCS_ATTACH
  add constraint BCFK_ATTACH_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH
  add constraint BCFK_ATTACH_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_ATTACH_PUID on BC_DOCS_ATTACH (PUID);
create index BCIDX_ATTACH_PTYPE on BC_DOCS_ATTACH (PTYPE);

-- ��������ۼ�
create table BC_DOCS_ATTACH_HISTORY (
  ID            number(19)    not null,
  AID           number(19)    not null,
  TYPE_         number(19)    not null,
  SUBJECT       varchar2(500) not null,
  FORMAT        varchar2(10),
  FILE_DATE     date          not null,
  AUTHOR_ID     number(19)    not null,
  MODIFIER_ID   number(19),
  MODIFIED_DATE date,
  C_IP          varchar2(100),
  C_INFO        varchar2(1000),
  MEMO          varchar2(2000),
  constraint BCPK_ATTACH_HISTORY primary key (ID)
);
comment on table BC_DOCS_ATTACH_HISTORY is '��������ۼ�';
comment on column BC_DOCS_ATTACH_HISTORY.AID is '����ID';
comment on column BC_DOCS_ATTACH_HISTORY.TYPE_ is '�������ͣ�0-����,1-���߲鿴,2-��ʽת��';
comment on column BC_DOCS_ATTACH_HISTORY.SUBJECT is '��˵��';
comment on column BC_DOCS_ATTACH_HISTORY.FORMAT is '���ص��ļ���ʽ��ת������ļ���ʽ����pdf��doc��mp3��';
comment on column BC_DOCS_ATTACH_HISTORY.C_IP is '�ͻ���IP';
comment on column BC_DOCS_ATTACH_HISTORY.C_INFO is '�������Ϣ��User-Agent';
comment on column BC_DOCS_ATTACH_HISTORY.MEMO is '��ע';
comment on column BC_DOCS_ATTACH_HISTORY.FILE_DATE is '����ʱ��';
comment on column BC_DOCS_ATTACH_HISTORY.AUTHOR_ID is '������ID';
comment on column BC_DOCS_ATTACH_HISTORY.MODIFIER_ID is '����޸���ID';
comment on column BC_DOCS_ATTACH_HISTORY.MODIFIED_DATE is '����޸�ʱ��';
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_ATTACH foreign key (AID)
references BC_DOCS_ATTACH (ID);

-- �û�����
create table BC_FEEDBACK (
  ID            number(19)     not null,
  UID_          varchar2(36)   not null,
  STATUS_       number(1)      not null,
  SUBJECT       varchar2(500)  not null,
  FILE_DATE     date           not null,
  AUTHOR_ID     number(19)     not null,
  MODIFIER_ID   number(19),
  MODIFIED_DATE date,
  CONTENT       varchar2(4000) not null,
  constraint BCPK_FEEDBACK primary key (ID)
);
comment on table BC_FEEDBACK is '�û�����';
comment on column BC_FEEDBACK.UID_ is '���������ı�ʶ��';
comment on column BC_FEEDBACK.STATUS_ is '״̬:0-�ݸ�,1-���ύ,2-������';
comment on column BC_FEEDBACK.FILE_DATE is '����ʱ��';
comment on column BC_FEEDBACK.SUBJECT is '����';
comment on column BC_FEEDBACK.CONTENT is '��ϸ����';

comment on column BC_FEEDBACK.AUTHOR_ID is '������ID';
comment on column BC_FEEDBACK.MODIFIER_ID is '����޸���ID';
comment on column BC_FEEDBACK.MODIFIED_DATE is '����޸�ʱ��';
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- ##ѡ��ģ��##
-- ѡ�����
create table BC_OPTION_GROUP (
  ID     number(19)    not null,
  KEY_   varchar2(255) not null,
  VALUE_ varchar2(255) not null,
  ORDER_ varchar2(100),
  ICON   varchar2(100),
  constraint BCPK_OPTION_GROUP primary key (ID)
);
comment on table BC_OPTION_GROUP is 'ѡ�����';
comment on column BC_OPTION_GROUP.KEY_ is '��';
comment on column BC_OPTION_GROUP.VALUE_ is 'ֵ';
comment on column BC_OPTION_GROUP.ORDER_ is '�����';
comment on column BC_OPTION_GROUP.ICON is 'ͼ����ʽ';
create index BCIDX_OPTIONGROUP_KEY on BC_OPTION_GROUP (KEY_);
create index BCIDX_OPTIONGROUP_VALUE on BC_OPTION_GROUP (VALUE_);

-- ѡ����Ŀ
create table BC_OPTION_ITEM (
  ID      number(19)    not null,
  PID     number(19)    not null,
  KEY_    varchar2(255) not null,
  VALUE_  varchar2(255) not null,
  ORDER_  varchar2(100),
  ICON    varchar2(100),
  STATUS_ number(1)     not null,
  DESC_   varchar2(1000),
  constraint BCPK_OPTION_ITEM primary key (ID)
);
comment on table BC_OPTION_ITEM is 'ѡ����Ŀ';
comment on column BC_OPTION_ITEM.PID is '���������ID';
comment on column BC_OPTION_ITEM.KEY_ is '��';
comment on column BC_OPTION_ITEM.VALUE_ is 'ֵ';
comment on column BC_OPTION_ITEM.ORDER_ is '�����';
comment on column BC_OPTION_ITEM.ICON is 'ͼ����ʽ';
comment on column BC_OPTION_ITEM.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��';
comment on column BC_OPTION_ITEM.DESC_ is '˵��';
alter table BC_OPTION_ITEM
  add constraint BCFK_OPTIONITEM_OPTIONGROUP foreign key (PID)
references BC_OPTION_GROUP (ID);
create index BCIDX_OPTIONITEM_KEY on BC_OPTION_ITEM (KEY_);
create index BCIDX_OPTIONITEM_VALUE on BC_OPTION_ITEM (VALUE_);
create index BCIDX_OPTIONITEM_PID on BC_OPTION_ITEM (PID);

-- ������������
create table BC_SD_JOB (
  ID           number(19)    not null,
  STATUS_      number(1)     not null,
  NAME         varchar2(255) not null,
  GROUPN       varchar2(255) not null,
  CRON         varchar2(255) not null,
  BEAN         varchar2(255) not null,
  METHOD       varchar2(255) not null,
  IGNORE_ERROR number(1)     not null,
  ORDER_       varchar2(100),
  NEXT_DATE    date,
  MEMO_        varchar2(1000),
  constraint BCPK_SD_JOB primary key (ID)
);
comment on table BC_SD_JOB is '������������';
comment on column BC_SD_JOB.NAME is '����';
comment on column BC_SD_JOB.BEAN is 'Ҫ���õ�SpringBean��';
comment on column BC_SD_JOB.METHOD is 'Ҫ���õ�SpringBean������';
comment on column BC_SD_JOB.IGNORE_ERROR is '�����쳣�Ƿ���Ժ��������:0-��,1-��';
comment on column BC_SD_JOB.MEMO_ is '��ע';
comment on column BC_SD_JOB.STATUS_ is '״̬��0-������,1-�ѽ���,2-��ɾ��,3-��������,4-��ͣ';
comment on column BC_SD_JOB.GROUPN is '������';
comment on column BC_SD_JOB.CRON is '���ʽ';
comment on column BC_SD_JOB.ORDER_ is '�����';
comment on column BC_SD_JOB.NEXT_DATE is '�������һ����ʱ��';

-- ���������־
create table BC_SD_LOG (
  ID         number(19)    not null,
  SUCCESS    number(1)     not null,
  START_DATE date          not null,
  END_DATE   date          not null,
  CFG_CRON   varchar2(255) not null,
  CFG_NAME   varchar2(255),
  CFG_GROUP  varchar2(255),
  CFG_BEAN   varchar2(255),
  CFG_METHOD varchar2(255),
  ERROR_TYPE varchar2(255),
  MSG        clob,
  constraint BCPK_SD_LOG primary key (ID)
);
comment on table BC_SD_LOG is '���������־';
comment on column BC_SD_LOG.SUCCESS is '�����Ƿ���ɹ�:0-ʧ��,1-�ɹ�';
comment on column BC_SD_LOG.START_DATE is '���������ʱ��';
comment on column BC_SD_LOG.END_DATE is '����Ľ���ʱ��';
comment on column BC_SD_LOG.ERROR_TYPE is '�쳣����';
comment on column BC_SD_LOG.MSG is '�쳣��Ϣ';
comment on column BC_SD_LOG.CFG_CRON is '��ӦScheduleJob��cron';
comment on column BC_SD_LOG.CFG_NAME is '��ӦScheduleJob��name';
comment on column BC_SD_LOG.CFG_GROUP is '��ӦScheduleJob��groupn';
comment on column BC_SD_LOG.CFG_BEAN is '��ӦScheduleJob��bean';
comment on column BC_SD_LOG.CFG_METHOD is '��ӦScheduleJob��method';

-- ͬ����Ϣ����
create table BC_SYNC_BASE (
  ID        number(19)          not null,
  STATUS_   number(1) default 0 not null,
  SYNC_TYPE varchar2(255)       not null,
  SYNC_CODE varchar2(255)       not null,
  SYNC_FROM varchar2(1000)      not null,
  SYNC_DATE date                not null,
  AUTHOR_ID number(19)          not null,
  constraint BCPK_SYNC_BASE primary key (ID)
);
comment on table BC_SYNC_BASE is 'ͬ����Ϣ����';
comment on column BC_SYNC_BASE.STATUS_ is '״̬:0-ͬ���������,1-�Ѵ���';
comment on column BC_SYNC_BASE.SYNC_TYPE is 'ͬ����Ϣ������';
comment on column BC_SYNC_BASE.SYNC_CODE is 'ͬ����Ϣ�ı�ʶ��';
comment on column BC_SYNC_BASE.SYNC_FROM is 'ͬ����Ϣ����Դ';
comment on column BC_SYNC_BASE.SYNC_DATE is 'ͬ��ʱ��';
comment on column BC_SYNC_BASE.AUTHOR_ID is 'ͬ����ID';
alter table BC_SYNC_BASE
  add constraint BCFK_SYNC_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_SYNC_BASE
  add constraint BCUK_SYNC_ID unique (SYNC_TYPE, SYNC_CODE);
