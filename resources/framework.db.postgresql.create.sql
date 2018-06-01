-- ##bc平台的 postgresql 建表脚本##

-- 创建Identity用的序列，开始于1000
create sequence CORE_SEQUENCE
  minvalue 1
  start with 1000
  increment by 1
  cache 20;

-- 创建序列，开始于1千万，方便历史数据的转换
create sequence HIBERNATE_SEQUENCE
  minvalue 1
  start with 10000000
  increment by 1
  cache 20;

-- 测试用的表
create table BC_EXAMPLE (
  ID   integer      not null,
  NAME varchar(255) not null,
  CODE varchar(255),
  primary key (ID)
);
comment on table BC_EXAMPLE is '测试用的表';
comment on column BC_EXAMPLE.NAME is '名称';

-- 系统标识相关模块
-- 系统资源
create table BC_IDENTITY_RESOURCE (
  ID        integer       not null,
  UID_      varchar(36),
  TYPE_     int default 0 not null,
  STATUS_   int default 0 not null,
  INNER_    boolean       not null default false,
  BELONG    integer,
  ORDER_    varchar(100),
  NAME      varchar(255)  not null,
  URL       varchar(255),
  ICONCLASS varchar(255),
  OPTION_   varchar(4000),
  PNAME     varchar(4000),
  constraint BCPK_RESOURCE primary key (ID)
);
comment on table BC_IDENTITY_RESOURCE is '系统资源';
comment on column BC_IDENTITY_RESOURCE.TYPE_ is '类型：1-文件夹,2-内部链接,3-外部链接,4-html';
comment on column BC_IDENTITY_RESOURCE.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_IDENTITY_RESOURCE.INNER_ is '是否为内置对象:0-否,1-是';
comment on column BC_IDENTITY_RESOURCE.BELONG is '所隶属的资源';
comment on column BC_IDENTITY_RESOURCE.ORDER_ is '排序号';
comment on column BC_IDENTITY_RESOURCE.NAME is '名称';
comment on column BC_IDENTITY_RESOURCE.URL is '地址';
comment on column BC_IDENTITY_RESOURCE.ICONCLASS is '图标样式';
comment on column BC_IDENTITY_RESOURCE.OPTION_ is '扩展参数';
comment on column BC_IDENTITY_RESOURCE.PNAME is '所隶属模块的全名:如系统维护/组织架构/单位配置';
create index BCIDX_RESOURCE_BELONG on BC_IDENTITY_RESOURCE (BELONG);
create index BCIDX_RESOURCE_BELONG_NULL on BC_IDENTITY_RESOURCE (COALESCE(BELONG, 0));

-- 角色
create table BC_IDENTITY_ROLE (
  ID      integer      not null,
  CODE    varchar(100) not null,
  ORDER_  varchar(100),
  NAME    varchar(255) not null,
  UID_    varchar(36),
  TYPE_   int          not null,
  STATUS_ int          not null,
  INNER_  boolean      not null default false,
  constraint BCPK_ROLE primary key (ID)
);
comment on table BC_IDENTITY_ROLE is '角色';
comment on column BC_IDENTITY_ROLE.CODE is '编码';
comment on column BC_IDENTITY_ROLE.ORDER_ is '排序号';
comment on column BC_IDENTITY_ROLE.NAME is '名称';
comment on column BC_IDENTITY_ROLE.TYPE_ is '类型';
comment on column BC_IDENTITY_ROLE.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_IDENTITY_ROLE.INNER_ is '是否为内置对象:0-否,1-是';

-- 角色与资源的关联
create table BC_IDENTITY_ROLE_RESOURCE (
  RID integer not null,
  SID integer not null,
  constraint BCPK_ROLE_RESOURCE primary key (RID, SID)
);
comment on table BC_IDENTITY_ROLE_RESOURCE is '角色与资源的关联：角色可以访问哪些资源';
comment on column BC_IDENTITY_ROLE_RESOURCE.RID is '角色ID';
comment on column BC_IDENTITY_ROLE_RESOURCE.SID is '资源ID';
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_ROLE foreign key (RID) references BC_IDENTITY_ROLE (ID);
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_RESOURCE foreign key (SID) references BC_IDENTITY_RESOURCE (ID);

-- 职务
create table BC_IDENTITY_DUTY (
  ID   integer      not null,
  CODE varchar(100) not null,
  NAME varchar(255) not null,
  constraint BCPK_DUTY primary key (ID)
);
comment on table BC_IDENTITY_DUTY is '职务';
comment on column BC_IDENTITY_DUTY.CODE is '编码';
comment on column BC_IDENTITY_DUTY.NAME is '名称';

-- 参与者的扩展属性
create table BC_IDENTITY_ACTOR_DETAIL (
  ID          integer not null,
  CREATE_DATE timestamp,
  WORK_DATE   timestamp,
  ISO         boolean not null default false,
  SEX         int              default 0,
  CARD        varchar(20),
  DUTY_ID     integer,
  COMMENT_    varchar(4000),
  constraint BCPK_ACTOR_DETAIL primary key (ID)
);
comment on table BC_IDENTITY_ACTOR_DETAIL is '参与者的扩展属性';
comment on column BC_IDENTITY_ACTOR_DETAIL.CREATE_DATE is '创建时间';
comment on column BC_IDENTITY_ACTOR_DETAIL.WORK_DATE is 'user-入职时间';
comment on column BC_IDENTITY_ACTOR_DETAIL.SEX is 'user-性别：0-未设置,1-男,2-女';
comment on column BC_IDENTITY_ACTOR_DETAIL.DUTY_ID is 'user-职务ID';
comment on column BC_IDENTITY_ACTOR_DETAIL.COMMENT_ is '备注';
alter table BC_IDENTITY_ACTOR_DETAIL
  add constraint BCFK_ACTORDETAIL_DUTY foreign key (DUTY_ID) references BC_IDENTITY_DUTY (ID);

-- 参与者
create table BC_IDENTITY_ACTOR (
  ID        integer       not null,
  UID_      varchar(36)   not null,
  TYPE_     int default 0 not null,
  STATUS_   int default 0 not null,
  INNER_    boolean       not null default false,
  CODE      varchar(100)  not null,
  NAME      varchar(255)  not null,
  PY        varchar(255),
  ORDER_    varchar(100),
  EMAIL     varchar(255),
  PHONE     varchar(255),
  DETAIL_ID integer,
  PCODE     varchar(4000),
  PNAME     varchar(4000),
  constraint BCPK_ACTOR primary key (ID)
);
comment on table BC_IDENTITY_ACTOR is '参与者(代表一个人或组织，组织也可以是单位、部门、岗位、团队等)';
comment on column BC_IDENTITY_ACTOR.UID_ is '全局标识';
comment on column BC_IDENTITY_ACTOR.TYPE_ is '类型：0-未定义,1-单位,2-部门,3-岗位,4-用户';
comment on column BC_IDENTITY_ACTOR.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_IDENTITY_ACTOR.INNER_ is '是否为内置对象:0-否,1-是';
comment on column BC_IDENTITY_ACTOR.CODE is '编码';
comment on column BC_IDENTITY_ACTOR.NAME is '名称';
comment on column BC_IDENTITY_ACTOR.PY is '名称的拼音';
comment on column BC_IDENTITY_ACTOR.ORDER_ is '同类参与者之间的排序号';
comment on column BC_IDENTITY_ACTOR.EMAIL is '邮箱';
comment on column BC_IDENTITY_ACTOR.PHONE is '联系电话';
comment on column BC_IDENTITY_ACTOR.DETAIL_ID is '扩展表的ID';
comment on column BC_IDENTITY_ACTOR.PCODE is '隶属机构的全编码';
comment on column BC_IDENTITY_ACTOR.PNAME is '隶属机构的全名';
alter table BC_IDENTITY_ACTOR
  add constraint BCFK_ACTOR_ACTORDETAIL foreign key (DETAIL_ID)
references BC_IDENTITY_ACTOR_DETAIL (ID) on delete cascade;
create index BCIDX_ACTOR_CODE on BC_IDENTITY_ACTOR (CODE asc);
create index BCIDX_ACTOR_NAME on BC_IDENTITY_ACTOR (NAME asc);
create index BCIDX_ACTOR_STATUSTYPE on BC_IDENTITY_ACTOR (STATUS_ asc, TYPE_ asc);
create index BCIDX_ACTOR_TYPE on BC_IDENTITY_ACTOR (TYPE_ asc);
create index BCIDX_ACTOR_DETAIL on BC_IDENTITY_ACTOR (DETAIL_ID asc);

-- 参与者之间的关联关系
create table BC_IDENTITY_ACTOR_RELATION (
  TYPE_       integer not null,
  MASTER_ID   integer not null,
  FOLLOWER_ID integer not null,
  ORDER_      varchar(100),
  constraint BCPK_ACTOR_RELATION primary key (MASTER_ID, FOLLOWER_ID, TYPE_)
);
comment on table BC_IDENTITY_ACTOR_RELATION is '参与者之间的关联关系';
comment on column BC_IDENTITY_ACTOR_RELATION.TYPE_ is '关联类型';
comment on column BC_IDENTITY_ACTOR_RELATION.MASTER_ID is '主控方ID';
comment on column BC_IDENTITY_ACTOR_RELATION.FOLLOWER_ID is '从属方ID';
comment on column BC_IDENTITY_ACTOR_RELATION.ORDER_ is '从属方之间的排序号';
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_MASTER foreign key (MASTER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_FOLLOWER foreign key (FOLLOWER_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_AR_TM on BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID);
create index BCIDX_AR_TF on BC_IDENTITY_ACTOR_RELATION (TYPE_, FOLLOWER_ID);

-- ACTOR隶属信息的变动历史
create table BC_IDENTITY_ACTOR_HISTORY (
  ID          integer      not null,
  PID         integer,
  CURRENT     boolean      not null default true,
  RANK        integer      not null default 0,
  CREATE_DATE timestamp    not null,
  START_DATE  timestamp,
  END_DATE    timestamp,
  ACTOR_TYPE  int          not null,
  ACTOR_ID    integer      not null,
  ACTOR_NAME  varchar(100) not null,
  ACTOR_CODE  varchar(255) not null,
  UPPER_ID    integer,
  UPPER_NAME  varchar(255),
  UNIT_ID     integer,
  UNIT_NAME   varchar(255),
  PCODE       varchar(4000),
  PNAME       varchar(4000),
  constraint BCPK_ACTOR_HISTORY primary key (ID)
);
comment on table BC_IDENTITY_ACTOR_HISTORY is 'ACTOR隶属信息的变动历史';
comment on column BC_IDENTITY_ACTOR_HISTORY.PID is '对应旧记录的id';
comment on column BC_IDENTITY_ACTOR_HISTORY.CURRENT is '是否为当前配置';
comment on column BC_IDENTITY_ACTOR_HISTORY.RANK is '多个当前配置间的首选次序，数值越小级别越高，值从0开始递增，只适用于隶属多个组织的情况';
comment on column BC_IDENTITY_ACTOR_HISTORY.CREATE_DATE is '创建时间';
comment on column BC_IDENTITY_ACTOR_HISTORY.START_DATE is '起始时段';
comment on column BC_IDENTITY_ACTOR_HISTORY.END_DATE is '结束时段';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_TYPE is 'ACTOR类型';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_ID is 'ACTORID';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_NAME is 'ACTOR名称';
comment on column BC_IDENTITY_ACTOR_HISTORY.ACTOR_CODE is 'ACTOR编码';
comment on column BC_IDENTITY_ACTOR_HISTORY.UPPER_ID is '直属上级ID';
comment on column BC_IDENTITY_ACTOR_HISTORY.UPPER_NAME is '直属上级名称';
comment on column BC_IDENTITY_ACTOR_HISTORY.UNIT_ID is '所在单位ID';
comment on column BC_IDENTITY_ACTOR_HISTORY.UNIT_NAME is '所在单位名称';
comment on column BC_IDENTITY_ACTOR_HISTORY.PCODE is '隶属机构的全编码';
comment on column BC_IDENTITY_ACTOR_HISTORY.PNAME is '隶属机构的全名';
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

-- 认证信息
create table BC_IDENTITY_AUTH (
  ID       integer     not null,
  PASSWORD varchar(32) not null,
  constraint BCPK_AUTH primary key (ID)
);
comment on table BC_IDENTITY_AUTH is '认证信息';
comment on column BC_IDENTITY_AUTH.ID is '与Actor表的id对应';
comment on column BC_IDENTITY_AUTH.PASSWORD is '经MD5加密的密码';
alter table BC_IDENTITY_AUTH
  add constraint BCFK_AUTH_ACTOR foreign key (ID)
references BC_IDENTITY_ACTOR (ID);

-- 标识生成器
create table BC_IDENTITY_IDGENERATOR (
  TYPE_  varchar(45) not null,
  VALUE_ integer     not null,
  FORMAT varchar(45),
  constraint BCPK_IDGENERATOR primary key (TYPE_)
);
comment on table BC_IDENTITY_IDGENERATOR is '标识生成器,用于生成主键或唯一编码用';
comment on column BC_IDENTITY_IDGENERATOR.TYPE_ is '分类';
comment on column BC_IDENTITY_IDGENERATOR.VALUE_ is '当前值';
comment on column BC_IDENTITY_IDGENERATOR.FORMAT is '格式模板,如“case-${V}”、“${T}-${V}”,V代表value的值，T代表type_的值';

-- 参与者与角色的关联
create table BC_IDENTITY_ROLE_ACTOR (
  AID integer not null,
  RID integer not null,
  constraint BCPK_ROLE_ACTOR primary key (AID, RID)
);
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ROLE foreign key (RID)
references BC_IDENTITY_ROLE (ID);
comment on table BC_IDENTITY_ROLE_ACTOR is '参与者与角色的关联：参与者拥有哪些角色';
comment on column BC_IDENTITY_ROLE_ACTOR.AID is '参与者ID';
comment on column BC_IDENTITY_ROLE_ACTOR.RID is '角色ID';

-- ##系统桌面相关模块##
-- 桌面快捷方式
create table BC_DESKTOP_SHORTCUT (
  ID         integer           not null,
  UID_       varchar(36),
  STATUS_    int               not null,
  INNER_     boolean           not null default false,
  ORDER_     varchar(100)      not null,
  STANDALONE boolean           not null default false,
  NAME       varchar(255),
  URL        varchar(255),
  ICONCLASS  varchar(255),
  SID        integer default 0 not null,
  AID        integer default 0 not null,
  constraint BCPK_DESKTOP_SHORTCUT primary key (ID)
);
comment on table BC_DESKTOP_SHORTCUT is '桌面快捷方式';
comment on column BC_DESKTOP_SHORTCUT.UID_ is '全局标识';
comment on column BC_DESKTOP_SHORTCUT.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_DESKTOP_SHORTCUT.INNER_ is '是否为内置对象:0-否,1-是';
comment on column BC_DESKTOP_SHORTCUT.STANDALONE is '是否在独立的浏览器窗口中打开';
comment on column BC_DESKTOP_SHORTCUT.NAME is '名称,为空则使用模块的名称';
comment on column BC_DESKTOP_SHORTCUT.URL is '地址,为空则使用模块的地址';
comment on column BC_DESKTOP_SHORTCUT.ICONCLASS is '图标样式';
comment on column BC_DESKTOP_SHORTCUT.SID is '对应的资源ID';
comment on column BC_DESKTOP_SHORTCUT.AID is '所属的参与者(如果为上级参与者,如单位部门,则其下的所有参与者都拥有该快捷方式)';
create index BCIDX_SHORTCUT on BC_DESKTOP_SHORTCUT (AID, SID);

-- 个人设置
create table BC_DESKTOP_PERSONAL (
  ID      integer           not null,
  UID_    varchar(36),
  STATUS_ int               not null,
  FONT    varchar(2)        not null,
  THEME   varchar(255)      not null,
  AID     integer default 0 not null,
  INNER_  boolean           not null default false,
  constraint BCPK_DESKTOP_PERSONAL primary key (ID)
);
comment on table BC_DESKTOP_PERSONAL is '个人设置';
comment on column BC_DESKTOP_PERSONAL.UID_ is '全局标识';
comment on column BC_DESKTOP_PERSONAL.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_DESKTOP_PERSONAL.FONT is '字体大小，如12、14、16';
comment on column BC_DESKTOP_PERSONAL.THEME is '主题名称,如base';
comment on column BC_DESKTOP_PERSONAL.AID is '所属的参与者';
comment on column BC_DESKTOP_PERSONAL.INNER_ is '是否为内置对象:0-否,1-是';
alter table BC_DESKTOP_PERSONAL
  add constraint BCUK_PERSONAL_AID unique (AID);

-- 消息模块
create table BC_MESSAGE (
  ID          integer       not null,
  UID_        varchar(36),
  STATUS_     int default 0 not null,
  TYPE_       int default 0 not null,
  SENDER_ID   integer       not null,
  SEND_DATE   timestamp     not null,
  RECEIVER_ID integer       not null,
  READ_       int default 0 not null,
  FROM_ID     integer,
  FROM_TYPE   integer,
  SUBJECT     varchar(255)  not null,
  CONTENT     varchar(4000),
  constraint BCPK_MESSAGE primary key (ID)
);
comment on table BC_MESSAGE is '消息模块';
comment on column BC_MESSAGE.STATUS_ is '状态：0-发送中,1-已发送,2-已删除,3-发送失败';
comment on column BC_MESSAGE.TYPE_ is '消息类型';
comment on column BC_MESSAGE.SENDER_ID is '发送者';
comment on column BC_MESSAGE.SEND_DATE is '发送时间';
comment on column BC_MESSAGE.RECEIVER_ID is '接收者';
comment on column BC_MESSAGE.FROM_ID is '来源标识';
comment on column BC_MESSAGE.FROM_TYPE is '来源类型';
comment on column BC_MESSAGE.READ_ is '已阅标记';
comment on column BC_MESSAGE.SUBJECT is '标题';
comment on column BC_MESSAGE.CONTENT is '内容';
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_REVEIVER foreign key (RECEIVER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_MESSAGE_FROMID on BC_MESSAGE (FROM_TYPE, FROM_ID);
create index BCIDX_MESSAGE_TYPE on BC_MESSAGE (TYPE_);

-- 工作事项
create table BC_WORK (
  ID         integer      not null,
  CLASSIFIER varchar(500) not null,
  SUBJECT    varchar(255) not null,
  FROM_ID    integer,
  FROM_TYPE  integer,
  FROM_INFO  varchar(255),
  OPEN_URL   varchar(255),
  CONTENT    varchar(4000),
  constraint BCPK_WORK primary key (ID)
);
comment on table BC_WORK is '工作事项';
comment on column BC_WORK.CLASSIFIER is '分类词,可多级分类,级间使用/连接,如“发文类/正式发文”';
comment on column BC_WORK.SUBJECT is '标题';
comment on column BC_WORK.FROM_ID is '来源标识';
comment on column BC_WORK.FROM_TYPE is '来源类型';
comment on column BC_WORK.FROM_INFO is '来源描述';
comment on column BC_WORK.OPEN_URL is '打开的Url模板';
comment on column BC_WORK.CONTENT is '内容';
create index BCIDX_WORK_FROM on BC_WORK (FROM_TYPE, FROM_ID);

-- 待办事项
create table BC_WORK_TODO (
  ID        integer   not null,
  WORK_ID   integer   not null,
  SENDER_ID integer   not null,
  SEND_DATE timestamp not null,
  WORKER_ID integer   not null,
  INFO      varchar(255),
  constraint BCPK_WORK_TODO primary key (ID)
);
comment on table BC_WORK_TODO is '待办事项';
comment on column BC_WORK_TODO.WORK_ID is '工作事项ID';
comment on column BC_WORK_TODO.SENDER_ID is '发送者';
comment on column BC_WORK_TODO.SEND_DATE is '发送时间';
comment on column BC_WORK_TODO.WORKER_ID is '发送者';
comment on column BC_WORK_TODO.INFO is '附加说明';
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_WORK foreign key (WORK_ID)
references BC_WORK (ID);
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_TODO
  add constraint BCFK_TODOWORK_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR (ID);

-- 已办事项
create table BC_WORK_DONE (
  ID           integer   not null,
  FINISH_DATE  timestamp not null,
  FINISH_YEAR  integer   not null,
  FINISH_MONTH integer   not null,
  FINISH_DAY   integer   not null,
  WORK_ID      integer   not null,
  SENDER_ID    integer   not null,
  SEND_DATE    timestamp not null,
  WORKER_ID    integer   not null,
  INFO         varchar(255),
  constraint BCPK_WORK_DONE primary key (ID)
);
comment on table BC_WORK_DONE is '已办事项';
comment on column BC_WORK_DONE.FINISH_DATE is '完成时间';
comment on column BC_WORK_DONE.FINISH_YEAR is '完成时间的年度';
comment on column BC_WORK_DONE.FINISH_MONTH is '完成时间的月份(1-12)';
comment on column BC_WORK_DONE.FINISH_DAY is '完成时间的日(1-31)';
comment on column BC_WORK_DONE.WORK_ID is '工作事项ID';
comment on column BC_WORK_DONE.SENDER_ID is '发送者';
comment on column BC_WORK_DONE.SEND_DATE is '发送时间';
comment on column BC_WORK_DONE.WORKER_ID is '发送者';
comment on column BC_WORK_DONE.INFO is '附加说明';
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORK foreign key (WORK_ID)
references BC_WORK (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR (ID);
create index BCIDX_DONEWORK_FINISHtimestamp on BC_WORK_DONE (FINISH_YEAR, FINISH_MONTH, FINISH_DAY);

-- 系统日志
create table BC_LOG_SYSTEM (
  ID        integer      not null,
  SID       varchar(500),
  TYPE_     int          not null,
  SUBJECT   varchar(500) not null,
  FILE_DATE timestamp    not null,
  AUTHOR_ID integer      not null,
  C_IP      varchar(100),
  C_NAME    varchar(100),
  C_MAC     varchar(1000),
  C_INFO    varchar(1000),
  S_IP      varchar(100),
  S_NAME    varchar(100),
  S_INFO    varchar(1000),
  CONTENT   varchar(4000),
  constraint BCPK_LOG_SYSTEM primary key (ID)
);
comment on table BC_LOG_SYSTEM is '系统日志';
comment on column BC_LOG_SYSTEM.SID is '登录用户的session id';
comment on column BC_LOG_SYSTEM.TYPE_ is '类别：0-登录,1-注销,2-超时';
comment on column BC_LOG_SYSTEM.FILE_DATE is '创建时间';
comment on column BC_LOG_SYSTEM.SUBJECT is '标题';
comment on column BC_LOG_SYSTEM.AUTHOR_ID is '创建人ID';
comment on column BC_LOG_SYSTEM.C_IP is '用户机器IP';
comment on column BC_LOG_SYSTEM.C_NAME is '用户机器名称';
comment on column BC_LOG_SYSTEM.C_MAC is '用户mac地址';
comment on column BC_LOG_SYSTEM.C_INFO is '用户浏览器信息：User-Agent';
comment on column BC_LOG_SYSTEM.S_IP is '服务器IP';
comment on column BC_LOG_SYSTEM.S_NAME is '服务器名称';
comment on column BC_LOG_SYSTEM.S_INFO is '服务器信息';
comment on column BC_LOG_SYSTEM.CONTENT is '详细内容';
alter table BC_LOG_SYSTEM
  add constraint BCFK_SYSLOG_USER foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_SYSLOG_CLIENT on BC_LOG_SYSTEM (C_IP);
create index BCIDX_SYSLOG_SERVER on BC_LOG_SYSTEM (S_IP);

-- 操作日志
create table BC_LOG_OPERATE (
  ID        integer      not null,
  TYPE_     int          not null,
  WAY       int          not null,
  FILE_DATE timestamp    not null,
  AUTHOR_ID integer      not null,
  UID_      varchar(255),
  PTYPE     varchar(255),
  PID       varchar(255),
  OPERATE   varchar(255),
  SUBJECT   varchar(500) not null,
  CONTENT   varchar(4000),
  constraint BCPK_LOG_OPERATE primary key (ID)
);
comment on table BC_LOG_OPERATE is '操作日志:包括工作日志和审计日志';
comment on column BC_LOG_OPERATE.TYPE_ is '类别：0-工作日志,1-审计日志';
comment on column BC_LOG_OPERATE.WAY is '创建方式：0-用户创建,1-自动生成';
comment on column BC_LOG_OPERATE.FILE_DATE is '创建时间';
comment on column BC_LOG_OPERATE.AUTHOR_ID is '创建人ID';
comment on column BC_LOG_OPERATE.UID_ is 'uid';
comment on column BC_LOG_OPERATE.PID is '文档标识，通常使用文档的id、uid或批号';
comment on column BC_LOG_OPERATE.PTYPE is '所属模块:如User、Role，一般为类名';
comment on column BC_LOG_OPERATE.OPERATE is '操作分类:如create、update、delete等';
comment on column BC_LOG_OPERATE.SUBJECT is '标题';
comment on column BC_LOG_OPERATE.CONTENT is '详细内容';
alter table BC_LOG_OPERATE
  add constraint BCFK_OPERATELOG_USER foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_OPERATELOG_PARENT on BC_LOG_OPERATE (PTYPE, PID);
create index BCIDX_OPERATELOG_SUBJECT on BC_LOG_OPERATE (SUBJECT);

-- 审计日志对应的审计条目
create table BC_LOG_AUDIT_ITEM (
  ID        integer       not null,
  PID       integer       not null,
  KEY_      varchar(255),
  LABLE     varchar(255),
  OLD_VALUE varchar(4000),
  NEW_VALUE varchar(4000) not null,
  ORDER_    varchar(10),
  constraint BCPK_LOG_AUDIT_ITEM primary key (ID)
);
comment on table BC_LOG_AUDIT_ITEM is '审计日志对应的审计条目';
comment on column BC_LOG_AUDIT_ITEM.PID is '所隶属日志的id';
comment on column BC_LOG_AUDIT_ITEM.KEY_ is '审计条目:如字段名、属性名';
comment on column BC_LOG_AUDIT_ITEM.LABLE is '审计条目的描述:如字段名、属性名的中文描述';
comment on column BC_LOG_AUDIT_ITEM.OLD_VALUE is '原值';
comment on column BC_LOG_AUDIT_ITEM.NEW_VALUE is '新值';
comment on column BC_LOG_AUDIT_ITEM.ORDER_ is '同一PID内的排序号';
alter table BC_LOG_AUDIT_ITEM
  add constraint BCFK_AUDITITEM_PID foreign key (PID)
references BC_LOG_OPERATE (ID);
create index BCIDX_AUDITITEM_PID on BC_LOG_OPERATE (PID);

-- 公告模块
create table BC_BULLETIN (
  ID            integer       not null,
  UID_          varchar(36)   not null,
  UNIT_ID       integer,
  SCOPE         int           not null,
  STATUS_       int           not null,
  OVERDUE_DATE  timestamp,
  ISSUE_DATE    timestamp,
  ISSUER_ID     integer,
  SUBJECT       varchar(500)  not null,
  FILE_DATE     timestamp     not null,
  AUTHOR_ID     integer       not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  CONTENT       varchar(4000) not null,
  constraint BCPK_BULLETIN primary key (ID)
);
comment on table BC_BULLETIN is '公告模块';
comment on column BC_BULLETIN.UID_ is '关联附件的标识号';
comment on column BC_BULLETIN.UNIT_ID is '公告所属单位ID';
comment on column BC_BULLETIN.SCOPE is '公告发布范围：0-本单位,1-全系统';
comment on column BC_BULLETIN.STATUS_ is '状态:0-草稿,1-已发布,2-已过期';
comment on column BC_BULLETIN.OVERDUE_DATE is '过期日期：为空代表永不过期';
comment on column BC_BULLETIN.ISSUE_DATE is '发布时间';
comment on column BC_BULLETIN.ISSUER_ID is '发布人ID';
comment on column BC_BULLETIN.SUBJECT is '标题';
comment on column BC_BULLETIN.FILE_DATE is '创建时间';
comment on column BC_BULLETIN.AUTHOR_ID is '创建人ID';
comment on column BC_BULLETIN.MODIFIER_ID is '最后修改人ID';
comment on column BC_BULLETIN.MODIFIED_DATE is '最后修改时间';
comment on column BC_BULLETIN.CONTENT is '详细内容';
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

-- 文档附件
create table BC_DOCS_ATTACH (
  ID            integer           not null,
  STATUS_       int               not null,
  PTYPE         varchar(36)       not null,
  PUID          varchar(36)       not null,
  SIZE_         integer           not null,
  COUNT_        integer default 0 not null,
  format        varchar(10),
  APPPATH       boolean           not null default false,
  SUBJECT       varchar(500)      not null,
  ICON          varchar(255),
  PATH          varchar(500)      not null,
  FILE_DATE     timestamp         not null,
  AUTHOR_ID     integer           not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_ATTACH primary key (ID)
);
comment on table BC_DOCS_ATTACH is '文档附件,记录文档与其相关附件之间的关系';
comment on column BC_DOCS_ATTACH.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_DOCS_ATTACH.PTYPE is '所关联文档的类型';
comment on column BC_DOCS_ATTACH.PUID is '所关联文档的UID';
comment on column BC_DOCS_ATTACH.SIZE_ is '文件的大小(单位为字节)';
comment on column BC_DOCS_ATTACH.COUNT_ is '文件的下载次数';
comment on column BC_DOCS_ATTACH.format is '附件类型:如pdf、doc、mp3等';
comment on column BC_DOCS_ATTACH.APPPATH is '指定path的值是相对于应用部署目录下路径还是相对于全局配置的app.data目录下的路径';
comment on column BC_DOCS_ATTACH.ICON is '扩展字段';
comment on column BC_DOCS_ATTACH.SUBJECT is '文件名称(不带路径的部分)';
comment on column BC_DOCS_ATTACH.PATH is '物理文件保存的相对路径(相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc")';

comment on column BC_DOCS_ATTACH.FILE_DATE is '创建时间';
comment on column BC_DOCS_ATTACH.AUTHOR_ID is '创建人ID';
comment on column BC_DOCS_ATTACH.MODIFIER_ID is '最后修改人ID';
comment on column BC_DOCS_ATTACH.MODIFIED_DATE is '最后修改时间';
alter table BC_DOCS_ATTACH
  add constraint BCFK_ATTACH_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH
  add constraint BCFK_ATTACH_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
create index BCIDX_ATTACH_PUID on BC_DOCS_ATTACH (PUID);
create index BCIDX_ATTACH_PTYPE on BC_DOCS_ATTACH (PTYPE);

-- 附件处理痕迹
create table BC_DOCS_ATTACH_HISTORY (
  ID        integer               not null,
  TYPE_     integer               not null,
  SUBJECT   varchar(500)          not null,
  FORMAT    varchar(10),
  FILE_DATE timestamp             not null,
  AUTHOR_ID integer               not null,
  ptype     varchar(36)           not null,
  puid      varchar(36)           not null,
  path      varchar(500)          not null,
  apppath   boolean default false not null,
  C_IP      varchar(100),
  C_INFO    varchar(1000),
  MEMO      varchar(2000),
  constraint BCPK_ATTACH_HISTORY primary key (ID)
);
comment on table BC_DOCS_ATTACH_HISTORY is '附件处理痕迹';
comment on column BC_DOCS_ATTACH_HISTORY.TYPE_ is '处理类型：0-下载,1-在线查看,2-格式转换';
comment on column BC_DOCS_ATTACH_HISTORY.SUBJECT is '简单说明';
comment on column BC_DOCS_ATTACH_HISTORY.FORMAT is '下载的文件格式或转换后的文件格式：如pdf、doc、mp3等';
comment on column BC_DOCS_ATTACH_HISTORY.C_IP is '客户端IP';
comment on column BC_DOCS_ATTACH_HISTORY.C_INFO is '浏览器信息：User-Agent';
comment on column BC_DOCS_ATTACH_HISTORY.MEMO is '备注';
comment on column BC_DOCS_ATTACH_HISTORY.FILE_DATE is '处理时间';
comment on column BC_DOCS_ATTACH_HISTORY.AUTHOR_ID is '创建人ID';
comment on column bc_docs_attach_history.path is '物理文件保存的相对路径(相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc")';
comment on column bc_docs_attach_history.apppath is '指定path的值是相对于应用部署目录下路径还是相对于全局配置的app.data目录下的路径';
comment on column bc_docs_attach_history.format is '附件类型:如pdf、doc、mp3等';
comment on column bc_docs_attach_history.ptype is '文档类型';
comment on column bc_docs_attach_history.puid is '文档标识';
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH_HISTORY
  add constraint BCFK_ATTACHHISTORY_ATTACH foreign key (AID)
references BC_DOCS_ATTACH (ID);

-- 用户反馈
create table BC_FEEDBACK (
  ID              integer           not null,
  UID_            varchar(36)       not null,
  STATUS_         int               not null,
  SUBJECT         varchar(500)      not null,
  FILE_DATE       timestamp         not null,
  AUTHOR_ID       integer           not null,
  MODIFIER_ID     integer,
  MODIFIED_DATE   timestamp,
  LAST_REPLIER_ID integer,
  LAST_REPLY_DATE timestamp,
  REPLY_COUNT     integer default 0 not null,
  CONTENT         text              not null,
  constraint BCPK_FEEDBACK primary key (ID)
);
comment on table BC_FEEDBACK is '用户反馈';
comment on column BC_FEEDBACK.UID_ is '关联附件的标识号';
comment on column BC_FEEDBACK.STATUS_ is '状态:0-草稿,1-已提交,2-已受理';
comment on column BC_FEEDBACK.FILE_DATE is '创建时间';
comment on column BC_FEEDBACK.SUBJECT is '标题';
comment on column BC_FEEDBACK.CONTENT is '详细内容';
comment on column BC_FEEDBACK.AUTHOR_ID is '创建人ID';
comment on column BC_FEEDBACK.MODIFIER_ID is '最后修改人ID';
comment on column BC_FEEDBACK.MODIFIED_DATE is '最后修改时间';
comment on column BC_FEEDBACK.LAST_REPLIER_ID is '最后回复人ID';
comment on column BC_FEEDBACK.LAST_REPLY_DATE is '最后回复时间';
comment on column BC_FEEDBACK.REPLY_COUNT is '回复的总数量';
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK
  add constraint BSFK_FEEDBACK_REPLIER foreign key (LAST_REPLIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 用户反馈的回复
create table BC_FEEDBACK_REPLY (
  ID            integer     not null,
  PID           integer     not null,
  UID_          varchar(36) not null,
  STATUS_       int         not null,
  SUBJECT       varchar(500),
  FILE_DATE     timestamp   not null,
  AUTHOR_ID     integer     not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  CONTENT       text,
  constraint BCPK_FEEDBACK_REPLY primary key (ID)
);
comment on table BC_FEEDBACK_REPLY is '用户反馈的回复';
comment on column BC_FEEDBACK_REPLY.PID is '所属反馈的id';
comment on column BC_FEEDBACK_REPLY.UID_ is '关联附件的标识号';
comment on column BC_FEEDBACK_REPLY.STATUS_ is '状态:0-正常,1-禁用,2-删除';
comment on column BC_FEEDBACK_REPLY.FILE_DATE is '创建时间';
comment on column BC_FEEDBACK_REPLY.SUBJECT is '标题';
comment on column BC_FEEDBACK_REPLY.CONTENT is '详细内容';
comment on column BC_FEEDBACK_REPLY.AUTHOR_ID is '创建人ID';
comment on column BC_FEEDBACK_REPLY.MODIFIER_ID is '最后修改人ID';
comment on column BC_FEEDBACK_REPLY.MODIFIED_DATE is '最后修改时间';
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_PID foreign key (PID)
references BC_FEEDBACK (ID);
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- ##选项模块##
-- 选项分组
create table BC_OPTION_GROUP (
  ID     integer      not null,
  KEY_   varchar(255) not null,
  VALUE_ varchar(255) not null,
  ORDER_ varchar(100),
  ICON   varchar(100),
  constraint BCPK_OPTION_GROUP primary key (ID)
);
comment on table BC_OPTION_GROUP is '选项分组';
comment on column BC_OPTION_GROUP.KEY_ is '键';
comment on column BC_OPTION_GROUP.VALUE_ is '值';
comment on column BC_OPTION_GROUP.ORDER_ is '排序号';
comment on column BC_OPTION_GROUP.ICON is '图标样式';
create index BCIDX_OPTIONGROUP_KEY on BC_OPTION_GROUP (KEY_);
create index BCIDX_OPTIONGROUP_VALUE on BC_OPTION_GROUP (VALUE_);

-- 选项条目
create table BC_OPTION_ITEM (
  ID      integer      not null,
  PID     integer      not null,
  KEY_    varchar(255) not null,
  VALUE_  varchar(255) not null,
  ORDER_  varchar(100),
  ICON    varchar(100),
  STATUS_ int          not null,
  DESC_   varchar(1000),
  constraint BCPK_OPTION_ITEM primary key (ID)
);
comment on table BC_OPTION_ITEM is '选项条目';
comment on column BC_OPTION_ITEM.PID is '所属分组的ID';
comment on column BC_OPTION_ITEM.KEY_ is '键';
comment on column BC_OPTION_ITEM.VALUE_ is '值';
comment on column BC_OPTION_ITEM.ORDER_ is '排序号';
comment on column BC_OPTION_ITEM.ICON is '图标样式';
comment on column BC_OPTION_ITEM.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除';
comment on column BC_OPTION_ITEM.DESC_ is '说明';
alter table BC_OPTION_ITEM
  add constraint BCFK_OPTIONITEM_OPTIONGROUP foreign key (PID)
references BC_OPTION_GROUP (ID);
create index BCIDX_OPTIONITEM_KEY on BC_OPTION_ITEM (KEY_);
create index BCIDX_OPTIONITEM_VALUE on BC_OPTION_ITEM (VALUE_);
create index BCIDX_OPTIONITEM_PID on BC_OPTION_ITEM (PID);

-- 调度任务配置
create table BC_SD_JOB (
  ID           integer      not null,
  STATUS_      int          not null,
  NAME         varchar(255) not null,
  GROUPN       varchar(255) not null,
  CRON         varchar(255) not null,
  BEAN         varchar(255) not null,
  METHOD       varchar(255) not null,
  IGNORE_ERROR boolean      not null default false,
  ORDER_       varchar(100),
  NEXT_DATE    timestamp,
  MEMO_        varchar(1000),
  constraint BCPK_SD_JOB primary key (ID)
);
comment on table BC_SD_JOB is '调度任务配置';
comment on column BC_SD_JOB.NAME is '名称';
comment on column BC_SD_JOB.BEAN is '要调用的SpringBean名';
comment on column BC_SD_JOB.METHOD is '要调用的SpringBean方法名';
comment on column BC_SD_JOB.IGNORE_ERROR is '发现异常是否忽略后继续调度:0-否,1-是';
comment on column BC_SD_JOB.MEMO_ is '备注';
comment on column BC_SD_JOB.STATUS_ is '状态：0-启用中,1-已禁用,2-已删除,3-正在运行,4-暂停';
comment on column BC_SD_JOB.GROUPN is '分组名';
comment on column BC_SD_JOB.CRON is '表达式';
comment on column BC_SD_JOB.ORDER_ is '排序号';
comment on column BC_SD_JOB.NEXT_DATE is '任务的下一运行时间';

-- 任务调度日志
create table BC_SD_LOG (
  ID         integer      not null,
  SUCCESS    boolean      not null default false,
  START_DATE timestamp    not null,
  END_DATE   timestamp    not null,
  CFG_CRON   varchar(255) not null,
  CFG_NAME   varchar(255),
  CFG_GROUP  varchar(255),
  CFG_BEAN   varchar(255),
  CFG_METHOD varchar(255),
  ERROR_TYPE varchar(255),
  MSG        text,
  constraint BCPK_SD_LOG primary key (ID)
);
comment on table BC_SD_LOG is '任务调度日志';
comment on column BC_SD_LOG.SUCCESS is '任务是否处理成功';
comment on column BC_SD_LOG.START_DATE is '任务的启动时间';
comment on column BC_SD_LOG.END_DATE is '任务的结束时间';
comment on column BC_SD_LOG.ERROR_TYPE is '异常分类';
comment on column BC_SD_LOG.MSG is '异常信息';
comment on column BC_SD_LOG.CFG_CRON is '对应ScheduleJob的cron';
comment on column BC_SD_LOG.CFG_NAME is '对应ScheduleJob的name';
comment on column BC_SD_LOG.CFG_GROUP is '对应ScheduleJob的groupn';
comment on column BC_SD_LOG.CFG_BEAN is '对应ScheduleJob的bean';
comment on column BC_SD_LOG.CFG_METHOD is '对应ScheduleJob的method';

-- 同步信息基表
create table BC_SYNC_BASE (
  ID        integer           not null,
  STATUS_   integer default 0 not null,
  SYNC_TYPE varchar(255)      not null,
  SYNC_CODE varchar(255)      not null,
  SYNC_FROM varchar(1000)     not null,
  SYNC_DATE timestamp         not null,
  AUTHOR_ID integer           not null,
  constraint BCPK_SYNC_BASE primary key (ID)
);
comment on table BC_SYNC_BASE is '同步信息基表';
comment on column BC_SYNC_BASE.STATUS_ is '状态:0-同步后待处理,1-已处理';
comment on column BC_SYNC_BASE.SYNC_TYPE is '同步信息的类型';
comment on column BC_SYNC_BASE.SYNC_CODE is '同步信息的标识符';
comment on column BC_SYNC_BASE.SYNC_FROM is '同步信息的来源';
comment on column BC_SYNC_BASE.SYNC_DATE is '同步时间';
comment on column BC_SYNC_BASE.AUTHOR_ID is '同步人ID';
alter table BC_SYNC_BASE
  add constraint BCFK_SYNC_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_SYNC_BASE
  add constraint BCUK_SYNC_ID unique (SYNC_TYPE, SYNC_CODE);

-- 籍贯
create table BC_PLACEORIGIN (
  ID            integer       not null,
  PID           integer,
  TYPE_         int default 0 not null,
  STATUS_       int default 0 not null,
  CODE          varchar(255),
  FULL_CODE     varchar(255),
  NAME          varchar(255)  not null,
  FULL_NAME     varchar(255)  not null,
  DESC_         varchar(4000),
  FILE_DATE     timestamp     not null,
  AUTHOR_ID     integer       not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_PLACEORIGIN primary key (ID)
);
comment on table BC_PLACEORIGIN is '籍贯模块';
comment on column BC_PLACEORIGIN.PID is '所隶属的上级地方ID';
comment on column BC_PLACEORIGIN.TYPE_ is '类型(0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级)';
comment on column BC_PLACEORIGIN.STATUS_ is '状态：0-启用中,1-已禁用';
comment on column BC_PLACEORIGIN.CODE is '统计用区划代码和城乡划分代码';
comment on column BC_PLACEORIGIN.FULL_CODE is '全编码 统计用区划代码和城乡划分代码';
comment on column BC_PLACEORIGIN.NAME is '名称 例如：荔湾区';
comment on column BC_PLACEORIGIN.FULL_NAME is '全名 例如：广东省广州市荔湾区';
comment on column BC_PLACEORIGIN.DESC_ is '描述';
comment on column BC_PLACEORIGIN.FILE_DATE is '创建时间';
comment on column BC_PLACEORIGIN.AUTHOR_ID is '创建人ID';
comment on column BC_PLACEORIGIN.MODIFIER_ID is '最后修改人ID';
comment on column BC_PLACEORIGIN.MODIFIED_DATE is '最后修改时间';
alter table BC_PLACEORIGIN
  add constraint BCFK_PLACEORIGIN_PID foreign key (PID)
references BC_PLACEORIGIN (ID);
alter table BC_PLACEORIGIN
  add constraint BCFK_PLACEORIGIN_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_PLACEORIGIN
  add constraint BCFK_PLACEORIGIN_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 模板类型
create table BC_TEMPLATE_TYPE (
  ID            integer      not null,
  STATUS_       int          not null default 0,
  ORDER_        varchar(255),
  CODE          varchar(255) not null,
  NAME          varchar(255),
  IS_PATH       boolean      not null default false,
  IS_PURE_TEXT  boolean      not null default false,
  EXT           varchar(255),
  DESC_         varchar(4000),
  FILE_DATE     timestamp    not null,
  AUTHOR_ID     integer      not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_TEMPLATE_TYPE primary key (ID)
);
comment on table BC_TEMPLATE_TYPE is '模板类型';
comment on column BC_TEMPLATE_TYPE.STATUS_ is '状态：0-正常，1-禁用';
comment on column BC_TEMPLATE_TYPE.ORDER_ is '排序号';
comment on column BC_TEMPLATE_TYPE.CODE is '编码：全局唯一';
comment on column BC_TEMPLATE_TYPE.NAME is '模板类型名称';
comment on column BC_TEMPLATE_TYPE.IS_PATH is '关联附件';
comment on column BC_TEMPLATE_TYPE.IS_PURE_TEXT is '纯文本';
comment on column BC_TEMPLATE_TYPE.EXT is '附件扩展名';
comment on column BC_TEMPLATE_TYPE.DESC_ is '备注';
comment on column BC_TEMPLATE_TYPE.FILE_DATE is '创建时间';
comment on column BC_TEMPLATE_TYPE.AUTHOR_ID is '创建人ID';
comment on column BC_TEMPLATE_TYPE.MODIFIER_ID is '最后修改人ID';
comment on column BC_TEMPLATE_TYPE.MODIFIED_DATE is '最后修改时间';
alter table BC_TEMPLATE_TYPE
  add constraint BCFK_TEMPLATE_TYPE_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_TEMPLATE_TYPE
  add constraint BCFK_TEMPLATE_TYPE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_TEMPLATE_TYPE
  add constraint BCUK_TEMPLATE_TYPE_CODE unique (CODE);

-- 模板管理
create table BC_TEMPLATE (
  ID            integer      not null,
  uid_          varchar(36)  not null,
  ORDER_        varchar(255),
  STATUS_       integer      not null default 0,
  TYPE_ID       integer      not null,
  CATEGORY      varchar(255) not null,
  CODE          varchar(255) not null,
  VERSION_      varchar(255) not null,
  SUBJECT       varchar(255),
  PATH          varchar(255),
  CONTENT       varchar(4000),
  INNER_        boolean      not null default false,
  SIZE_         integer      not null default 0,
  FORMATTED     boolean      not null default false,
  DESC_         varchar(4000),
  FILE_DATE     timestamp    not null,
  AUTHOR_ID     integer      not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_TEMPLATE primary key (ID)
);
comment on table BC_TEMPLATE is '模板管理';
comment on column BC_TEMPLATE.ORDER_ is '排序号';
comment on column BC_TEMPLATE.uid_ is 'UID';
comment on column BC_TEMPLATE.STATUS_ is '状态：0-正常,1-禁用';
comment on column BC_TEMPLATE.TYPE_ID is '模板类型ID';
comment on column BC_TEMPLATE.CATEGORY is '所属分类';
comment on column BC_TEMPLATE.CODE is '编码：全局唯一';
comment on column BC_TEMPLATE.VERSION_ is '版本号';
comment on column BC_TEMPLATE.SUBJECT is '标题';
comment on column BC_TEMPLATE.PATH is '物理文件保存的相对路径';
comment on column BC_TEMPLATE.CONTENT is '模板内容：文本和Html类型显示模板内容';
comment on column BC_TEMPLATE.INNER_ is '内置：true-是、false-否，默认否';
comment on column BC_TEMPLATE.SIZE_ is '文件的大小(单位为字节) 默认0';
comment on column BC_TEMPLATE.FORMATTED is '是够允许格式化：默认否';
comment on column BC_TEMPLATE.DESC_ is '描述';
comment on column BC_TEMPLATE.FILE_DATE is '创建时间';
comment on column BC_TEMPLATE.AUTHOR_ID is '创建人ID';
comment on column BC_TEMPLATE.MODIFIER_ID is '最后修改人ID';
comment on column BC_TEMPLATE.MODIFIED_DATE is '最后修改时间';
alter table BC_TEMPLATE
  add constraint BCFK_TEMPLATE_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_TEMPLATE
  add constraint BCFK_TEMPLATE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_TEMPLATE
  add constraint BCUK_TEMPLATE_CODE_VERSION unique (CODE, VERSION_);
alter table BC_TEMPLATE
  add constraint BCFK_TEMPLATE_TYPE_ID foreign key (TYPE_ID)
references BC_TEMPLATE_TYPE (ID);

-- 模板参数
create table BC_TEMPLATE_PARAM (
  ID            integer   not null,
  STATUS_       int       not null default 0,
  ORDER_        varchar(255),
  NAME          varchar(255),
  CONFIG        varchar(4000),
  DESC_         varchar(4000),
  FILE_DATE     timestamp not null,
  AUTHOR_ID     integer   not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_TEMPLATE_PARAM primary key (ID)
);
comment on table BC_TEMPLATE_PARAM is '模板参数';
comment on column BC_TEMPLATE_PARAM.STATUS_ is '状态：0-正常，1-禁用';
comment on column BC_TEMPLATE_PARAM.ORDER_ is '排序号';
comment on column BC_TEMPLATE_PARAM.NAME is '模板参数名称';
comment on column BC_TEMPLATE_PARAM.CONFIG is '模板参数配置信息';
comment on column BC_TEMPLATE_PARAM.DESC_ is '备注';
comment on column BC_TEMPLATE_PARAM.FILE_DATE is '创建时间';
comment on column BC_TEMPLATE_PARAM.AUTHOR_ID is '创建人ID';
comment on column BC_TEMPLATE_PARAM.MODIFIER_ID is '最后修改人ID';
comment on column BC_TEMPLATE_PARAM.MODIFIED_DATE is '最后修改时间';
alter table BC_TEMPLATE_PARAM
  add constraint BCFK_TEMPLATE_PARAM_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_TEMPLATE_PARAM
  add constraint BCFK_TEMPLATE_PARAM_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 模板管理使用参数表
create table BC_TEMPLATE_TEMPLATE_PARAM (
  TID integer not null,
  PID integer not null,
  constraint BCPK_TEMPLATE_TEMPLATE_PARAM primary key (TID, PID)
);
comment on table BC_TEMPLATE_TEMPLATE_PARAM is '模板管理使用参数表';
comment on column BC_TEMPLATE_TEMPLATE_PARAM.TID is '模板id';
comment on column BC_TEMPLATE_TEMPLATE_PARAM.PID is '模板参数id';
alter table BC_TEMPLATE_TEMPLATE_PARAM
  add constraint BCFK_TEMPLATE_TEMPLATE_PARAM foreign key (TID)
references BC_TEMPLATE (ID);
alter table BC_TEMPLATE_TEMPLATE_PARAM
  add constraint BCFK_TEMPLATE_PARAM_TEMPLATE foreign key (PID)
references BC_TEMPLATE_PARAM (ID);

-- 报表模板
create table BC_REPORT_TEMPLATE (
  ID            integer      not null,
  STATUS_       int          not null default 0,
  ORDER_        varchar(255),
  CATEGORY      varchar(255),
  CODE          varchar(255) not null,
  NAME          varchar(255),
  DESC_         varchar(4000),
  CONFIG        varchar(4000),
  FILE_DATE     timestamp    not null,
  AUTHOR_ID     integer      not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_REPORT_TEMPLATE primary key (ID)
);
comment on table BC_REPORT_TEMPLATE is '报表模板';
comment on column BC_REPORT_TEMPLATE.status_ is '状态：0-启用,1-禁用';
comment on column BC_REPORT_TEMPLATE.ORDER_ is '排序号';
comment on column BC_REPORT_TEMPLATE.CATEGORY is '所属分类，如"营运系统/发票统计"';
comment on column BC_REPORT_TEMPLATE.CODE is '编码：全局唯一';
comment on column BC_REPORT_TEMPLATE.NAME is '名称';
comment on column BC_REPORT_TEMPLATE.DESC_ is '备注';
comment on column BC_REPORT_TEMPLATE.CONFIG is '详细配置';
comment on column BC_REPORT_TEMPLATE.FILE_DATE is '创建时间';
comment on column BC_REPORT_TEMPLATE.AUTHOR_ID is '创建人ID';
comment on column BC_REPORT_TEMPLATE.MODIFIER_ID is '最后修改人ID';
comment on column BC_REPORT_TEMPLATE.MODIFIED_DATE is '最后修改时间';
alter table BC_REPORT_TEMPLATE
  add constraint BCFK_REPORT_TEMPLATE_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_REPORT_TEMPLATE
  add constraint BCFK_REPORT_TEMPLATE_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_REPORT_TEMPLATE
  add constraint BCUK_REPORT_TEMPLATE_CODE unique (CODE);

-- 报表模板使用人
create table BC_REPORT_TEMPLATE_ACTOR (
  TID integer not null,
  AID integer not null,
  constraint BCPK_REPORT_TEMPLATE_ACTOR primary key (TID, AID)
);
comment on table BC_REPORT_TEMPLATE_ACTOR is '报表模板使用人';
comment on column BC_REPORT_TEMPLATE_ACTOR.TID is '报表模板id';
comment on column BC_REPORT_TEMPLATE_ACTOR.AID is '使用人id';
alter table BC_REPORT_TEMPLATE_ACTOR
  add constraint BCFK_REPORT_TEMPLATE_ACTOR_REPORT foreign key (TID)
references BC_REPORT_TEMPLATE (ID);
alter table BC_REPORT_TEMPLATE_ACTOR
  add constraint BCFK_REPORT_TEMPLATE_ACTOR_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID);

-- 报表任务
create table BC_REPORT_TASK (
  ID            integer   not null,
  STATUS_       int       not null default 0,
  ORDER_        varchar(255),
  NAME          varchar(255),
  CRON          varchar(255),
  DESC_         varchar(4000),
  CONFIG        varchar(4000),
  START_DATE    timestamp,
  IGNORE_ERROR  boolean   not null default false,
  PID           integer   not null,
  FILE_DATE     timestamp not null,
  AUTHOR_ID     integer   not null,
  MODIFIER_ID   integer,
  MODIFIED_DATE timestamp,
  constraint BCPK_REPORT_TASK primary key (ID)
);
comment on table BC_REPORT_TASK is '报表任务';
comment on column BC_REPORT_TASK.status_ is '状态：0-启用,1-禁用';
comment on column BC_REPORT_TASK.ORDER_ is '排序号';
comment on column BC_REPORT_TASK.NAME is '名称';
comment on column BC_REPORT_TASK.CRON is '定时表达式，按标准的cron表达式';
comment on column BC_REPORT_TASK.DESC_ is '备注';
comment on column BC_REPORT_TASK.CONFIG is '详细配置';
comment on column BC_REPORT_TASK.START_DATE is '开始时间';
comment on column BC_REPORT_TASK.PID is '所用模板ID';
comment on column BC_REPORT_TASK.IGNORE_ERROR is '发现异常是否忽略后继续调度';
comment on column BC_REPORT_TASK.FILE_DATE is '创建时间';
comment on column BC_REPORT_TASK.AUTHOR_ID is '创建人ID';
comment on column BC_REPORT_TASK.MODIFIER_ID is '最后修改人ID';
comment on column BC_REPORT_TASK.MODIFIED_DATE is '最后修改时间';
alter table BC_REPORT_TASK
  add constraint BCFK_REPORT_TASK_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_REPORT_TASK
  add constraint BCFK_REPORT_TASK_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_REPORT_TASK
  add constraint BCFK_REPORT_TASK_TEMPLATE foreign key (PID)
references BC_REPORT_TEMPLATE (ID);

-- 历史报表
create table BC_REPORT_HISTORY (
  ID          integer   not null,
  CATEGORY    varchar(255),
  SUBJECT     varchar(255),
  MSG         varchar(4000),
  SUCCESS     boolean   not null default true,
  PATH        varchar(4000),
  SOURCE_TYPE varchar(255),
  SOURCE_ID   integer,
  START_DATE  timestamp not null,
  END_DATE    timestamp not null,
  AUTHOR_ID   integer   not null,
  constraint BCPK_REPORT_HISTORY primary key (ID)
);
comment on table BC_REPORT_HISTORY is '历史报表';
comment on column BC_REPORT_HISTORY.CATEGORY is '所属分类，如"营运系统/发票统计"';
comment on column BC_REPORT_HISTORY.SUBJECT is '标题';
comment on column BC_REPORT_HISTORY.MSG is '运行结果的描述信息，如成功、异常信息';
comment on column BC_REPORT_HISTORY.SUCCESS is '运行是否成功';
comment on column BC_REPORT_HISTORY.PATH is '报表运行结果所在的相对路径';
comment on column BC_REPORT_HISTORY.SOURCE_ID is '来源ID';
comment on column BC_REPORT_HISTORY.SOURCE_TYPE is '来源类型:用户生成、报表任务';
comment on column BC_REPORT_HISTORY.START_DATE is '执行报表的开始时间';
comment on column BC_REPORT_HISTORY.END_DATE is '执行报表的结束时间';
comment on column BC_REPORT_HISTORY.AUTHOR_ID is '创建人ID';
alter table BC_REPORT_HISTORY
  add constraint BCFK_REPORT_HISTORY_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 问卷
create table BC_IVG_QUESTIONARY (
  ID              integer   not null,
  TYPE_           integer   not null default 0,
  STATUS_         integer   not null default 0,
  SUBJECT         varchar(255),
  START_DATE      timestamp not null,
  END_DATE        timestamp,
  PERMITTED       boolean   not null default false,
  ISSUE_DATE      timestamp,
  ISSUER_ID       integer,
  PIGEONHOLE_DATE timestamp,
  PIGEONHOLER_ID  integer,
  FILE_DATE       timestamp not null,
  AUTHOR_ID       integer   not null,
  MODIFIER_ID     integer,
  MODIFIED_DATE   timestamp,
  constraint BCPK_IVG_QUESTIONARY primary key (ID)
);
comment on table BC_IVG_QUESTIONARY is '问卷';
comment on column BC_IVG_QUESTIONARY.TYPE_ is '类型 : 0-网上调查,1-网上考试';
comment on column BC_IVG_QUESTIONARY.STATUS_ is '状态：-1-草稿,0-已发布,1-已归档';
comment on column BC_IVG_QUESTIONARY.SUBJECT is '标题';
comment on column BC_IVG_QUESTIONARY.START_DATE is '开始日期';
comment on column BC_IVG_QUESTIONARY.END_DATE is '结束日期';
comment on column BC_IVG_QUESTIONARY.PERMITTED is '提交前允许查看统计';
comment on column BC_IVG_QUESTIONARY.ISSUE_DATE is '发布时间';
comment on column BC_IVG_QUESTIONARY.ISSUER_ID is '发布人ID';
comment on column BC_IVG_QUESTIONARY.PIGEONHOLE_DATE is '归档时间';
comment on column BC_IVG_QUESTIONARY.PIGEONHOLER_ID is '归档人ID';
comment on column BC_IVG_QUESTIONARY.FILE_DATE is '创建时间';
comment on column BC_IVG_QUESTIONARY.AUTHOR_ID is '创建人ID';
comment on column BC_IVG_QUESTIONARY.MODIFIER_ID is '最后修改人ID';
comment on column BC_IVG_QUESTIONARY.MODIFIED_DATE is '最后修改时间';
alter table BC_IVG_QUESTIONARY
  add constraint BCFK_IVG_QUESTIONARY_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_IVG_QUESTIONARY
  add constraint BCFK_IVG_QUESTIONARY_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_IVG_QUESTIONARY
  add constraint BCFK_IVG_QUESTIONARY_ISSUER foreign key (ISSUER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_IVG_QUESTIONARY
  add constraint BCFK_IVG_QUESTIONARY_PIGEONHOLER foreign key (PIGEONHOLER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 问卷所限制的参与人
create table BC_IVG_QUESTIONARY_ACTOR (
  QID integer not null,
  AID integer not null,
  constraint BCPK_IVG_QUESTIONARY_ACTOR primary key (QID, AID)
);
comment on table BC_IVG_QUESTIONARY_ACTOR is '问卷所限制的参与人';
comment on column BC_IVG_QUESTIONARY_ACTOR.QID is '问卷ID';
comment on column BC_IVG_QUESTIONARY_ACTOR.AID is '参与人ID';
alter table BC_IVG_QUESTIONARY_ACTOR
  add constraint BCFK_IVG_QA_QUESTIONARY foreign key (QID) references BC_IVG_QUESTIONARY (ID);
alter table BC_IVG_QUESTIONARY_ACTOR
  add constraint BCFK_IVG_QA_ACTOR foreign key (AID) references BC_IDENTITY_ACTOR (ID);

-- 问题
create table BC_IVG_QUESTION (
  ID             integer       not null,
  PID            integer       not null,
  TYPE_          integer       not null default 0,
  REQUIRED       boolean       not null default true,
  SUBJECT        varchar(255),
  ORDER_         integer       not null default 0,
  SCORE          int default 0 not null,
  SEPERATE_SCORE boolean                default true,
  CONFIG         varchar(1000),
  GRADE          boolean       not null default false,
  constraint BCPK_IVG_QUESTION primary key (ID)
);
comment on table BC_IVG_QUESTION is '问题';
comment on column BC_IVG_QUESTION.PID is '所属问卷ID';
comment on column BC_IVG_QUESTION.TYPE_ is '类型 : 0-单选题,1-多选题,2-填空题,3-问答题';
comment on column BC_IVG_QUESTION.REQUIRED is '必选题，默认为是';
comment on column BC_IVG_QUESTION.SUBJECT is '标题';
comment on column BC_IVG_QUESTION.ORDER_ is '排序号';
comment on column BC_IVG_QUESTION.CONFIG is '配置:使用json格式，如控制选项水平、垂直布局，控制问答题输入框的默认大小等，格式为：{layout_orientation:"horizontal|vertical",row:5}';
comment on column BC_IVG_QUESTION.SCORE is '分数';
comment on column BC_IVG_QUESTION.SEPERATE_SCORE is '各个选项独立给分:(仅适用于网上考试的多选题)注意答错任一项将为0分';
comment on column BC_IVG_QUESTION.GRADE is '是否需要评分';
alter table BC_IVG_QUESTION
  add constraint BCFK_IVG_QUESTION_PID foreign key (PID)
references BC_IVG_QUESTIONARY (ID);

-- 问题项
create table BC_IVG_QUESTION_ITEM (
  ID       integer               not null,
  PID      integer               not null,
  SUBJECT  varchar(255),
  ORDER_   integer               not null default 0,
  STANDARD boolean default false not null,
  SCORE    int default 0         not null,
  CONFIG   varchar(1000),
  constraint BCPK_IVG_QUESTION_ITEM primary key (ID)
);
comment on table BC_IVG_QUESTION_ITEM is '问题项';
comment on column BC_IVG_QUESTION_ITEM.PID is '所属问题ID';
comment on column BC_IVG_QUESTION_ITEM.SUBJECT is '单选多选题显示的选项文字,如果为问答题则为默认填写的内容';
comment on column BC_IVG_QUESTION_ITEM.ORDER_ is '排序号';
comment on column BC_IVG_QUESTION_ITEM.STANDARD is '标准答案';
comment on column BC_IVG_QUESTION_ITEM.SCORE is '分数 : 选择此答案的得分';
comment on column BC_IVG_QUESTION_ITEM.CONFIG is '特殊配置 : 用于填空题的标准答案配置，使用json数组格式[{},...]，每个json元素格式为{key:"占位符",value:"标准答案",score:分数}';
alter table BC_IVG_QUESTION_ITEM
  add constraint BCFK_IVG_QUESTION_ITEM_PID foreign key (PID)
references BC_IVG_QUESTION (ID);

-- 作答记录
create table BC_IVG_RESPOND (
  ID        integer       not null,
  PID       integer       not null,
  FILE_DATE timestamp     not null,
  AUTHOR_ID integer       not null,
  SCORE     int default 0 not null,
  GRADE     boolean       not null default false,
  constraint BCPK_IVG_RESPOND primary key (ID)
);
comment on table BC_IVG_RESPOND is '作答记录';
comment on column BC_IVG_RESPOND.PID is '所属问卷ID';
comment on column BC_IVG_RESPOND.FILE_DATE is '作答时间';
comment on column BC_IVG_RESPOND.AUTHOR_ID is '作答人';
comment on column BC_IVG_RESPOND.SCORE is '总得分';
comment on column BC_IVG_RESPOND.GRADE is '是否需要评分';
alter table BC_IVG_RESPOND
  add constraint BCFK_IVG_RESPOND_PID foreign key (PID)
references BC_IVG_QUESTIONARY (ID);
alter table BC_IVG_RESPOND
  add constraint BCFK_IVG_RESPOND_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 问题项的答案
create table BC_IVG_ANSWER (
  ID      integer       not null,
  QID     integer       not null,
  RID     integer       not null,
  CONTENT varchar(255),
  SCORE   int default 0 not null,
  GRADE   boolean       not null default false,
  constraint BCPK_IVG_ANSWER primary key (ID)
);
comment on table BC_IVG_ANSWER is '答案';
comment on column BC_IVG_ANSWER.QID is '问题项ID';
comment on column BC_IVG_ANSWER.RID is '作答记录ID';
comment on column BC_IVG_ANSWER.SCORE is '给分';
comment on column BC_IVG_ANSWER.CONTENT is '问答题填写的内容';
comment on column BC_IVG_ANSWER.GRADE is '是否需要评分';
alter table BC_IVG_ANSWER
  add constraint BCFK_IVG_ANSWER_QID foreign key (QID)
references BC_IVG_QUESTION_ITEM (ID);
alter table BC_IVG_ANSWER
  add constraint BCFK_IVG_ANSWER_RID foreign key (RID)
references BC_IVG_RESPOND (ID);

-- 对答案的评分记录
create table BC_IVG_GRADE (
  ID        int           not null,
  ANSWER_ID int           not null,
  SCORE     int default 0 not null,
  FILE_DATE timestamp     not null,
  AUTHOR_ID int           not null,
  DESC_     varchar(1000),
  primary key (ID)
);
comment on table BC_IVG_GRADE is '评分记录';
comment on column BC_IVG_GRADE.ID is 'ID';
comment on column BC_IVG_GRADE.ANSWER_ID is '答案ID';
comment on column BC_IVG_GRADE.SCORE is '给分';
comment on column BC_IVG_GRADE.FILE_DATE is '评分时间';
comment on column BC_IVG_GRADE.AUTHOR_ID is '评分人ID';
comment on column BC_IVG_GRADE.DESC_ is '备注';
alter table BC_IVG_GRADE
  add constraint BCFK_IVG_GRADE_AID foreign key (ANSWER_ID)
references BC_IVG_ANSWER (ID);
alter table BC_IVG_GRADE
  add constraint BCFK_IVG_GRADE_AUTHORID foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);