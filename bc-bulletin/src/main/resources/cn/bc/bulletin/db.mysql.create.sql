-- 公告模块
create table BC_BULLETIN (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL COMMENT '关联附件的标识号',
    SCOPE int(1) NOT NULL COMMENT '公告发布范围：0-本单位,1-全系统',
    STATUS_ int(1) NOT NULL COMMENT '状态:0-草稿,1-已发布,2-已过期',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    OVERDUE_DATE datetime COMMENT '过期日期：为空代表永不过期',
   	ISSUE_DATE datetime COMMENT '发布时间',
    FILE_YEAR int(4) COMMENT '发布时间的年度yyyy',
    FILE_MONTH int(2) COMMENT '发布时间的月份(1-12)',
    FILE_DAY int(2) COMMENT '发布时间的日(1-31)',
    ISSUER_ID int COMMENT '发布人ID',
    ISSUER_NAME varchar(100) COMMENT '发布人姓名',
    
    SUBJECT varchar(500) NOT NULL COMMENT '标题',
    
    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',

    CONTENT text NOT NULL COMMENT '详细内容',
    
    INNER_ int(1) default 0 COMMENT '未用',
    primary key (ID)
) COMMENT='公告模块';
ALTER TABLE BC_BULLETIN ADD CONSTRAINT FK_BULLETIN_ISSUER FOREIGN KEY (ISSUER_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BC_BULLETIN ADD INDEX IDX_BULLETIN_SEARCH (SCOPE,UNIT_ID,STATUS_);
ALTER TABLE BC_BULLETIN ADD INDEX IDX_BULLETIN_ARCHIVE (SCOPE,UNIT_ID,STATUS_,FILE_YEAR,FILE_MONTH,FILE_DAY);
