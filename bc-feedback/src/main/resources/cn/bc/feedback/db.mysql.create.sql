-- 用户反馈
create table BC_FEEDBACK (
    ID int NOT NULL auto_increment,
    UID_ varchar(36) NOT NULL COMMENT '关联附件的标识号',
    STATUS_ int(1) NOT NULL COMMENT '状态:0-草稿,1-已提交,2-已受理',
   
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    FILE_YEAR int(4) COMMENT '发布时间的年度yyyy',
    FILE_MONTH int(2) COMMENT '发布时间的月份(1-12)',
    FILE_DAY int(2) COMMENT '发布时间的日(1-31)',
    
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
) COMMENT='用户反馈';
ALTER TABLE BC_FEEDBACK ADD CONSTRAINT FK_FEEDBACK_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BC_FEEDBACK ADD INDEX IDX_FEEDBACK_SEARCH (UNIT_ID,STATUS_);
ALTER TABLE BC_FEEDBACK ADD INDEX IDX_FEEDBACK_ARCHIVE (UNIT_ID,STATUS_,FILE_YEAR,FILE_MONTH,FILE_DAY);
