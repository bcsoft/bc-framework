-- 公告模块
create table BC_BULLETIN (
  ID           int          not null auto_increment,
  UID_         varchar(36)  not null comment '关联附件的标识号',
  SCOPE        int (1) not null comment '公告发布范围：0-本单位,1-全系统',
  STATUS_      int (1) not null comment '状态:0-草稿,1-已发布,2-已过期',

  FILE_DATE    datetime     not null comment '创建时间',
  OVERDUE_DATE datetime comment '过期日期：为空代表永不过期',
  ISSUE_DATE   datetime comment '发布时间',
  FILE_YEAR    int (4) comment '发布时间的年度yyyy',
  FILE_MONTH   int (2) comment '发布时间的月份(1-12)',
  FILE_DAY     int (2) comment '发布时间的日(1-31)',
  ISSUER_ID    int comment '发布人ID',
  ISSUER_NAME  varchar(100) comment '发布人姓名',

  SUBJECT      varchar(500) not null comment '标题',

  AUTHOR_ID    int          not null comment '创建人ID',
  AUTHOR_NAME  varchar(100) not null comment '创建人姓名',
  DEPART_ID    int comment '创建人所在部门ID，如果用户直接隶属于单位，则为null',
  DEPART_NAME  varchar(255) comment '创建人所在部门名称，如果用户直接隶属于单位，则为null',
  UNIT_ID      int          not null comment '创建人所在单位ID',
  UNIT_NAME    varchar(255) not null comment '创建人所在单位名称',

  CONTENT      text         not null comment '详细内容',

  INNER_       int (1) default 0 comment '未用',
  primary key (ID)
) comment = '公告模块';
alter table BC_BULLETIN
  add constraint FK_BULLETIN_ISSUER foreign key (ISSUER_ID)
references BC_IDENTITY_ACTOR (ID);
alter table BC_BULLETIN
  add INDEX idx_bulletin_search(SCOPE, UNIT_ID, STATUS_);
alter table BC_BULLETIN
  add INDEX idx_bulletin_archive(SCOPE, UNIT_ID, STATUS_, FILE_YEAR, FILE_MONTH, FILE_DAY);
