-- ##BC平台的MYSQL建表脚本##

-- 测试用的表
create table BC_EXAMPLE (
  ID   bigint       not null AUTO_INCREMENT,
  NAME varchar(255) not null comment '名称',
  CODE varchar(255),
  primary key (ID)
) comment = '测试用的表';

-- 系统标识相关模块
-- 系统资源
create table BC_IDENTITY_RESOURCE (
  ID        bigint       not null AUTO_INCREMENT,
  UID_      varchar(36) comment '全局标识',
  TYPE_     int (1) not null comment '类型：1-文件夹,2-内部链接,3-外部链接,4-HTML',
  STATUS_   int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  INNER_    int (1) not null comment '是否为内置对象:0-否,1-是',
  BELONG    bigint comment '所隶属的资源',
  ORDER_    varchar(100) comment '排序号',
  NAME      varchar(255) not null comment '名称',
  URL       varchar(255) comment '地址',
  ICONCLASS varchar(255) comment '图标样式',
  OPTION_   text comment '扩展参数',
  PNAME     text comment '所隶属模块的全名:如系统维护/组织架构/单位配置',
  primary key (ID)
) comment = '系统资源';
alter table BC_IDENTITY_RESOURCE
  add INDEX bcidx_resource_belong(BELONG);

-- 角色
create table BC_IDENTITY_ROLE (
  ID      bigint       not null AUTO_INCREMENT,
  CODE    varchar(100) not null comment '编码',
  ORDER_  varchar(100) comment '排序号',
  NAME    varchar(255) not null comment '名称',

  UID_    varchar(36) comment '全局标识',
  TYPE_   int (1) not null comment '类型',
  STATUS_ int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  INNER_  int (1) not null comment '是否为内置对象:0-否,1-是',
  primary key (ID)
) comment = '角色';

-- 角色与资源的关联
create table BC_IDENTITY_ROLE_RESOURCE (
  RID bigint not null comment '角色ID',
  SID bigint not null comment '资源ID',
  primary key (RID, SID)
) comment = '角色与资源的关联：角色可以访问哪些资源';
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_ROLE foreign key (RID)
references BC_IDENTITY_ROLE (ID);
alter table BC_IDENTITY_ROLE_RESOURCE
  add constraint BCFK_RS_RESOURCE foreign key (SID)
references BC_IDENTITY_RESOURCE (ID);

-- 职务
create table BC_IDENTITY_DUTY (
  ID   bigint       not null AUTO_INCREMENT,
  CODE varchar(100) not null comment '编码',
  NAME varchar(255) not null comment '名称',
  primary key (ID)
) comment = '职务';

-- 参与者的扩展属性
create table BC_IDENTITY_ACTOR_DETAIL (
  ID          bigint not null AUTO_INCREMENT,
  CREATE_DATE datetime comment '创建时间',
  WORK_DATE   date comment 'USER-入职时间',
  SEX         int (1) default 0 comment 'USER-性别：0-未设置,1-男,2-女',
  ISO         int (1) default 0 comment 'USER-iso',
  CARD        varchar(20) comment 'USER-身份证',
  DUTY_ID     bigint comment 'USER-职务ID',
  COMMENT_    varchar(1000) comment '备注',
  primary key (ID)
) comment = '参与者的扩展属性';
alter table BC_IDENTITY_ACTOR_DETAIL
  add constraint BCFK_ACTORDETAIL_DUTY foreign key (DUTY_ID)
references BC_IDENTITY_DUTY (ID);

-- 参与者
create table BC_IDENTITY_ACTOR (
  ID        bigint       not null AUTO_INCREMENT,
  UID_      varchar(36)  not null comment '全局标识',
  TYPE_     int (1) not null comment '类型：1-用户,2-单位,3-部门,4-岗位',
  STATUS_   int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  CODE      varchar(100) not null comment '编码',
  NAME      varchar(255) not null comment '名称',
  PY        varchar(255) comment '名称的拼音',
  ORDER_    varchar(100) comment '同类参与者之间的排序号',
  EMAIL     varchar(255) comment '邮箱',
  PHONE     varchar(255) comment '联系电话',
  DETAIL_ID bigint comment '扩展表的ID',
  INNER_    int (1) not null comment '是否为内置对象:0-否,1-是',
  PCODE     varchar(4000) comment '隶属机构的全编码',
  PNAME     varchar(4000) comment '隶属机构的全名',
  primary key (ID)
) comment = '参与者(代表一个人或组织，组织也可以是单位、部门、岗位、团队等)';
alter table BC_IDENTITY_ACTOR
  add constraint BCFK_ACTOR_ACTORDETAIL foreign key (DETAIL_ID)
references BC_IDENTITY_ACTOR_DETAIL (ID) on delete cascade on update cascade;
alter table BC_IDENTITY_ACTOR
  add INDEX bcidx_actor_type(TYPE_);
alter table BC_IDENTITY_ACTOR
  add INDEX bcidx_actor_code(CODE);
alter table BC_IDENTITY_ACTOR
  add INDEX bcidx_actor_detail(DETAIL_ID);
alter table BC_IDENTITY_ACTOR
  add INDEX bcidx_actor_statustype(STATUS_, TYPE_);

-- 参与者之间的关联关系
create table BC_IDENTITY_ACTOR_RELATION (
  TYPE_       int (2) not null comment '关联类型',
  MASTER_ID   bigint not null comment '主控方ID',
  FOLLOWER_ID bigint not null comment '从属方ID',
  ORDER_      varchar(100) comment '从属方之间的排序号',
  primary key (MASTER_ID, FOLLOWER_ID, TYPE_)
) comment = '参与者之间的关联关系';
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_MASTER foreign key (MASTER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_RELATION
  add constraint BCFK_AR_FOLLOWER foreign key (FOLLOWER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_RELATION
  add INDEX bcidx_ar_tm(TYPE_, MASTER_ID),
  add INDEX bcidx_ar_tf(TYPE_, FOLLOWER_ID);

-- ACTOR隶属信息的变动历史
create table BC_IDENTITY_ACTOR_HISTORY (
  ID          bigint       not null AUTO_INCREMENT,
  PID         bigint comment '对应旧记录的id',
  CURRENT     int (1) not null default 1 comment '是否为当前配置',
  RANK        int (2) not null default 0 comment '多个当前配置间的首选次序，数值越小级别越高，值从0开始递增，只适用于隶属多个组织的情况',
  CREATE_DATE datetime     not null comment '创建时间',
  START_DATE  datetime comment '起始时段',
  END_DATE    datetime comment '结束时段',
  ACTOR_TYPE  int (1) not null comment 'ACTOR的类型',
  ACTOR_ID    bigint       not null comment 'ACTOR的ID',
  ACTOR_NAME  varchar(100) not null comment 'ACTOR的名称',
  UPPER_ID    bigint comment '直属上级ID',
  UPPER_NAME  varchar(255) comment '直属上级名称',
  UNIT_ID     bigint comment '所在单位ID',
  UNIT_NAME   varchar(255) comment '所在单位名称',
  PCODE       varchar(4000) comment '直属机构的全编码',
  PNAME       varchar(4000) comment '直属机构的全名',
  primary key (ID)
) comment 'ACTOR隶属信息的变动历史';
alter table BC_IDENTITY_ACTOR_HISTORY
  add constraint BCFK_ACTORHISTORY_PID foreign key (PID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_IDENTITY_ACTOR_HISTORY
  add constraint BCFK_ACTORHISTORY_ACTOR foreign key (ACTOR_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_HISTORY
  add INDEX bcidx_actorhistory_upper(UPPER_ID)
,
  add INDEX bcidx_actorhistory_unit(UNIT_ID)
,
  add INDEX bcidx_actorhistory_actor(ACTOR_ID);

-- 认证信息
create table BC_IDENTITY_AUTH (
  ID       bigint      not null AUTO_INCREMENT comment '与ACTOR表的ID对应',
  PASSWORD varchar(32) not null comment '密码',
  primary key (ID)
) comment = '认证信息';
alter table BC_IDENTITY_AUTH
  add constraint BCFK_AUTH_ACTOR foreign key (ID)
references BC_IDENTITY_ACTOR (ID);

-- 标识生成器
create table BC_IDENTITY_IDGENERATOR (
  TYPE_  varchar(45) not null comment '分类',
  VALUE_ bigint      not null comment '当前值',
  FORMAT varchar(45) comment '格式模板,如“CASE-${V}”、“${T}-${V}”,V代表VALUE的值，T代表TYPE_的值',
  primary key (TYPE_)
) comment = '标识生成器,用于生成主键或唯一编码用';

-- 参与者与角色的关联
create table BC_IDENTITY_ROLE_ACTOR (
  AID bigint not null comment '参与者ID',
  RID bigint not null comment '角色ID',
  primary key (AID, RID)
) comment = '参与者与角色的关联：参与者拥有哪些角色';
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ACTOR foreign key (AID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ROLE_ACTOR
  add constraint BCFK_RA_ROLE foreign key (RID)
references BC_IDENTITY_ROLE (ID);

-- ##系统桌面相关模块##
-- 桌面快捷方式
create table BC_DESKTOP_SHORTCUT (
  ID         bigint       not null AUTO_INCREMENT,
  UID_       varchar(36) comment '全局标识',
  STATUS_    int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  ORDER_     varchar(100) not null comment '排序号',
  STANDALONE int (1) not null comment '是否在独立的浏览器窗口中打开',
  NAME       varchar(255) comment '名称,为空则使用资源的名称',
  URL        varchar(255) comment '地址,为空则使用资源的地址',
  ICONCLASS  varchar(255) comment '图标样式',
  SID        bigint       not null default 0 comment '对应的资源ID',
  AID        bigint       not null default 0 comment '所属的参与者(如果为上级参与者,如单位部门,则其下的所有参与者都拥有该快捷方式)',
  INNER_     int (1) not null comment '是否为内置对象:0-否,1-是',
  primary key (ID)
) comment = '桌面快捷方式';
alter table BC_DESKTOP_SHORTCUT
  add INDEX bcidx_shortcut(AID, SID);

-- 个人设置
create table BC_DESKTOP_PERSONAL (
  ID      bigint       not null AUTO_INCREMENT,
  UID_    varchar(36) comment '全局标识',
  STATUS_ int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  FONT    varchar(2)   not null comment '字体大小，如12、14、16',
  THEME   varchar(255) not null comment '主题名称,如BASE',
  AID     bigint       not null default 0 comment '所属的参与者',
  INNER_  int (1) not null comment '是否为内置对象:0-否,1-是',
  primary key (ID)
) comment = '个人设置';
alter table BC_DESKTOP_PERSONAL
  add unique index BCUK_PERSONAL_AID (AID);

-- 消息模块
create table BC_MESSAGE (
  ID          bigint       not null AUTO_INCREMENT,
  UID_        varchar(36) comment '全局标识',
  STATUS_     int (1) not null default 0 comment '状态：0-发送中,1-已发送,2-已删除,3-发送失败',
  TYPE_       int (1) not null default 0 comment '消息类型',
  SENDER_ID   bigint       not null comment '发送者',
  SEND_DATE   datetime     not null comment '发送时间',
  RECEIVER_ID bigint       not null comment '接收者',
  READ_       int (1) not null default 0 comment '已阅标记',
  FROM_ID     bigint comment '来源标识',
  FROM_TYPE   bigint comment '来源类型',
  SUBJECT     varchar(255) not null comment '标题',
  CONTENT     text comment '内容',
  primary key (ID)
) comment = '消息';
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_MESSAGE
  add constraint BCFK_MESSAGE_REVEIVER foreign key (RECEIVER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_MESSAGE
  add INDEX bcidx_message_fromid(FROM_TYPE, FROM_ID);
alter table BC_MESSAGE
  add INDEX bcidx_message_type(TYPE_);

-- 工作事项
create table BC_WORK (
  ID         bigint       not null AUTO_INCREMENT,
  CLASSIFIER varchar(500) not null comment '分类词,可多级分类,级间使用/连接,如“发文类/正式发文”',
  SUBJECT    varchar(255) not null comment '标题',
  FROM_ID    bigint comment '来源标识',
  FROM_TYPE  bigint comment '来源类型',
  FROM_INFO  varchar(255) comment '来源描述',
  OPEN_URL   varchar(255) comment '打开的URL模板',
  CONTENT    text comment '内容',
  primary key (ID)
) comment = '工作事项';
alter table BC_WORK
  add INDEX bcidx_work_fromid(FROM_TYPE, FROM_ID);

-- 待办事项
create table BC_WORK_TODO (
  ID        bigint   not null AUTO_INCREMENT,
  WORK_ID   bigint   not null comment '工作事项ID',
  SENDER_ID bigint   not null comment '发送者',
  SEND_DATE datetime not null comment '发送时间',
  WORKER_ID bigint   not null comment '发送者',
  INFO      varchar(255) comment '附加说明',
  primary key (ID)
) comment = '待办事项';
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
  ID           bigint   not null AUTO_INCREMENT,
  FINISH_DATE  datetime not null comment '完成时间',
  FINISH_YEAR  int (4) not null comment '完成时间的年度',
  FINISH_MONTH int (2) not null comment '完成时间的月份(1-12)',
  FINISH_DAY   int (2) not null comment '完成时间的日(1-31)',

  WORK_ID      bigint   not null comment '工作事项ID',
  SENDER_ID    bigint   not null comment '发送者',
  SEND_DATE    datetime not null comment '发送时间',
  WORKER_ID    bigint   not null comment '发送者',
  INFO         varchar(255) comment '附加说明',
  primary key (ID)
) comment = '已办事项';
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORK foreign key (WORK_ID)
references BC_WORK (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_SENDER foreign key (SENDER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_DONE
  add constraint BCFK_DONEWORK_WORKER foreign key (WORKER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_WORK_DONE
  add INDEX bcidx_donework_finishdate(FINISH_YEAR, FINISH_MONTH, FINISH_DAY);

-- 系统日志
create table BC_LOG_SYSTEM (
  ID        bigint       not null AUTO_INCREMENT,
  TYPE_     int (1) not null comment '类别：0-登录,1-主动注销,2-超时注销',

  FILE_DATE datetime     not null comment '创建时间',
  SUBJECT   varchar(500) not null comment '标题',
  AUTHOR_ID bigint       not null comment '创建人ID',
  C_IP      varchar(100) comment '用户机器IP',
  C_NAME    varchar(100) comment '用户机器名称',
  C_INFO    varchar(1000) comment '用户浏览器信息：USER-AGENT',
  S_IP      varchar(100) comment '服务器IP',
  S_NAME    varchar(100) comment '服务器名称',
  S_INFO    varchar(1000) comment '服务器信息',

  CONTENT   text comment '详细内容',
  primary key (ID)
) comment = '系统日志';
alter table BC_LOG_SYSTEM
  add constraint BCFK_SYSLOG_USER foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_LOG_SYSTEM
  add INDEX bcidx_syslog_client(C_IP);
alter table BC_LOG_SYSTEM
  add INDEX bcidx_syslog_server(S_IP);

-- 工作日志
create table BC_LOG_WORK (
  ID        bigint       not null AUTO_INCREMENT,
  UID_      varchar(36)  not null comment '关联附件的标识号',
  TYPE_     int (1) not null comment '类型：0-系统创建,1-用户创建',
  PTYPE     varchar(36)  not null comment '所关联文档的类型',
  PID       varchar(36)  not null comment '所关联文档的标识，通常使用文档的uid或批号',
  AUTHOR_ID bigint       not null comment '创建人ID',
  FILE_DATE datetime     not null comment '创建时间',
  SUBJECT   varchar(500) not null comment '标题',
  CONTENT   text comment '详细内容',
  primary key (ID)
) comment = '工作日志';
alter table BC_LOG_WORK
  add constraint BCFK_WORKLOG_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_LOG_WORK
  add INDEX bcidx_worklog_parent(PTYPE, PID);

-- 公告模块
create table BC_BULLETIN (
  ID            bigint       not null AUTO_INCREMENT,
  UID_          varchar(36)  not null comment '关联附件的标识号',
  UNIT_ID       bigint comment '公告所属单位ID',
  SCOPE         int (1) not null comment '公告发布范围：0-本单位,1-全系统',
  STATUS_       int (1) not null comment '状态:0-草稿,1-已发布,2-已过期',

  OVERDUE_DATE  datetime comment '过期日期：为空代表永不过期',
  ISSUE_DATE    datetime comment '发布时间',
  ISSUER_ID     bigint comment '发布人ID',
  ISSUER_NAME   varchar(100) comment '发布人姓名',

  SUBJECT       varchar(500) not null comment '标题',

  FILE_DATE     datetime     not null comment '创建时间',
  AUTHOR_ID     bigint       not null comment '创建人ID',
  MODIFIER_ID   bigint comment '最后修改人ID',
  MODIFIED_DATE datetime comment '最后修改时间',

  CONTENT       text         not null comment '详细内容',
  primary key (ID)
) comment = '公告模块';
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
alter table BC_BULLETIN
  add INDEX bcidx_bulletin_search(SCOPE, UNIT_ID, STATUS_);

-- 文档附件
create table BC_DOCS_ATTACH (
  ID            bigint       not null AUTO_INCREMENT,
  STATUS_       int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  PTYPE         varchar(36)  not null comment '所关联文档的类型',
  PUID          varchar(36)  not null comment '所关联文档的UID',

  SIZE_         bigint       not null comment '文件的大小(单位为字节)',
  COUNT_        bigint       not null default 0 comment '文件的下载次数',
  EXT           varchar(10) comment '文件扩展名：如PNG、DOC、MP3等',
  APPPATH       int (1) not null comment '指定PATH的值是相对于应用部署目录下路径还是相对于全局配置的APP.DATA目录下的路径',
  SUBJECT       varchar(500) not null comment '文件名称(不带路径的部分)',
  PATH          varchar(500) not null comment '物理文件保存的相对路径(相对于全局配置的附件根目录下的子路径，如"2011/BULLETIN/XXXX.DOC")',

  FILE_DATE     datetime     not null comment '创建时间',
  AUTHOR_ID     bigint       not null comment '创建人ID',
  MODIFIER_ID   bigint comment '最后修改人ID',
  MODIFIED_DATE datetime comment '最后修改时间',
  primary key (ID)
) comment = '文档附件,记录文档与其相关附件之间的关系';
alter table BC_DOCS_ATTACH
  add constraint BCFK_ATTACH_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_DOCS_ATTACH
  add INDEX bcidx_attach_puid(PUID);
alter table BC_DOCS_ATTACH
  add INDEX bcidx_attach_ptype(PTYPE);

-- 附件处理痕迹
create table BC_DOCS_ATTACH_HISTORY (
  ID            bigint       not null AUTO_INCREMENT,
  AID           bigint       not null comment '附件ID',
  TYPE_         bigint       not null comment '处理类型：0-下载,1-在线查看,2-格式转换',
  SUBJECT       varchar(500) not null comment '简单说明',
  FORMAT        varchar(10) comment '下载的文件格式或转换后的文件格式：如PDF、DOC、MP3等',

  FILE_DATE     datetime     not null comment '处理时间',
  AUTHOR_ID     bigint       not null comment '处理人ID',
  MODIFIER_ID   bigint comment '最后修改人ID',
  MODIFIED_DATE datetime comment '最后修改时间',

  C_IP          varchar(100) comment '客户端IP',
  C_INFO        varchar(1000) comment '浏览器信息：USER-AGENT',

  MEMO          varchar(2000) comment '备注',
  primary key (ID)
) comment = '附件处理痕迹';
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
  ID            bigint       not null AUTO_INCREMENT,
  UID_          varchar(36)  not null comment '关联附件的标识号',
  STATUS_       int (1) not null comment '状态:0-草稿,1-已提交,2-已受理',
  SUBJECT       varchar(500) not null comment '标题',
  FILE_DATE     datetime     not null comment '创建时间',
  AUTHOR_ID     bigint       not null comment '创建人ID',
  MODIFIER_ID   bigint comment '最后修改人ID',
  MODIFIED_DATE datetime comment '最后修改时间',

  CONTENT       text         not null comment '详细内容',
  primary key (ID)
) comment = '用户反馈';
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK
  add constraint BCFK_FEEDBACK_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 用户反馈的回复
create table BC_FEEDBACK_REPLY (
  ID            bigint       not null AUTO_INCREMENT,
  PID           bigint       not null comment '所属反馈的id',
  UID_          varchar(36)  not null comment '关联附件的标识号',
  STATUS_       int (1) not null comment '状态:0-正常,1-禁用,2-删除',
  SUBJECT       varchar(500) not null comment '标题',
  FILE_DATE     datetime     not null comment '创建时间',
  AUTHOR_ID     bigint       not null comment '创建人ID',
  MODIFIER_ID   bigint comment '最后修改人ID',
  MODIFIED_DATE datetime comment '最后修改时间',
  CONTENT       text comment '详细内容',
  primary key (ID)
) comment = '用户反馈的回复';
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_PID foreign key (PID)
references BC_FEEDBACK (ID);
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_FEEDBACK_REPLY
  add constraint BCFK_FEEDBACK_REPLY_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);

-- 选项模块
-- 选项分组
create table BC_OPTION_GROUP (
  ID     bigint       not null AUTO_INCREMENT,
  KEY_   varchar(255) not null comment '键',
  VALUE_ varchar(255) not null comment '值',
  ORDER_ varchar(100) comment '排序号',
  ICON   varchar(100) comment '图标样式',
  primary key (ID)
) comment = '选项分组';
alter table BC_OPTION_GROUP
  add INDEX bcidx_optiongroup_key(KEY_);
alter table BC_OPTION_GROUP
  add INDEX bcidx_optiongroup_value(VALUE_);

-- 选项条目
create table BC_OPTION_ITEM (
  ID      bigint       not null AUTO_INCREMENT,
  PID     bigint       not null comment '所属分组的ID',
  KEY_    varchar(255) not null comment '键',
  VALUE_  varchar(255) not null comment '值',
  ORDER_  varchar(100) comment '排序号',
  ICON    varchar(100) comment '图标样式',
  STATUS_ int (1) not null comment '状态：0-已禁用,1-启用中,2-已删除',
  DESC_   varchar(1000) comment '说明',
  primary key (ID)
) comment = '选项条目';
alter table BC_OPTION_ITEM
  add constraint BCFK_OPTIONITEM_OPTIONGROUP foreign key (PID)
references BC_OPTION_GROUP (ID);
alter table BC_OPTION_ITEM
  add INDEX bcidx_optionitem_key(KEY_);
alter table BC_OPTION_ITEM
  add INDEX bcidx_optionitem_value(VALUE_);
alter table BC_OPTION_ITEM
  add INDEX bcidx_optionitem_pid(PID);

-- 调度任务配置
create table BC_SD_JOB (
  ID           bigint       not null AUTO_INCREMENT,
  STATUS_      int (1) not null comment '状态：0-启用中,1-已禁用,2-已删除,3-正在运行,4-暂停',
  NAME         varchar(255) not null comment '名称',
  GROUPN       varchar(255) not null comment '分组名',
  CRON         varchar(255) not null comment '表达式',
  BEAN         varchar(255) not null comment '要调用的SpringBean名',
  METHOD       varchar(255) not null comment '要调用的SpringBean方法名',
  IGNORE_ERROR int (1) not null comment '发现异常是否忽略后继续调度:0-否,1-是',
  ORDER_       varchar(100) comment '排序号',
  NEXT_DATE    datetime comment '任务的下一运行时间',
  MEMO_        varchar(1000) comment '备注',
  primary key (ID)
) comment = '调度任务配置';

-- 任务调度日志
create table BC_SD_LOG (
  ID         bigint       not null AUTO_INCREMENT,
  SUCCESS    int (1) not null comment '任务是否处理成功:0-失败,1-成功',
  START_DATE datetime     not null comment '任务的启动时间',
  END_DATE   datetime     not null comment '任务的结束时间',
  CFG_CRON   varchar(255) not null comment '对应ScheduleJob的cron',
  CFG_NAME   varchar(255) comment '对应ScheduleJob的name',
  CFG_GROUP  varchar(255) comment '对应ScheduleJob的groupn',
  CFG_BEAN   varchar(255) comment '对应ScheduleJob的bean',
  CFG_METHOD varchar(255) comment '对应ScheduleJob的method',
  ERROR_TYPE varchar(255) comment '异常分类',
  MSG        text comment '异常信息',
  primary key (ID)
) comment = '任务调度日志';

-- 同步信息基表
create table BC_SYNC_BASE (
  ID        bigint        not null AUTO_INCREMENT,
  STATUS_   int (1) default 0 not null comment '状态:0-同步后待处理,1-已处理',
  SYNC_TYPE varchar(255)  not null comment '同步信息的类型',
  SYNC_CODE varchar(255)  not null comment '同步信息的标识符',
  SYNC_FROM varchar(1000) not null comment '同步信息的来源',
  SYNC_DATE datetime      not null comment '同步时间',
  AUTHOR_ID bigint        not null comment '同步人ID',
  primary key (ID)
) comment = '用户反馈';
alter table BC_SYNC_BASE
  add constraint BCFK_SYNC_AUTHOR foreign key (AUTHOR_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID);
alter table BC_SYNC_BASE
  add INDEX bcuq_sync_id(SYNC_CODE);
-- ALTER TABLE BC_SYNC_BASE ADD UNIQUE INDEX BCUQ_SYNC_ID (SYNC_TYPE,SYNC_CODE);

