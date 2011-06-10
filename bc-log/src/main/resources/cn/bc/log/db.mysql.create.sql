-- 系统日志
create table BC_LOG_SYSTEM (
    ID int NOT NULL auto_increment,
    TYPE_ int(1) NOT NULL COMMENT '类别：0-登录,1-主动注销,2-超时注销',
    
    CREATE_DATE datetime NOT NULL COMMENT '创建时间',
    SUBJECT varchar(500) NOT NULL COMMENT '标题',
    CREATER_ID int NOT NULL COMMENT '创建人ID',
    CREATER_NAME varchar(255) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '用户所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '用户所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '用户所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '用户所在单位名称',

    C_IP varchar(100) COMMENT '用户机器IP',
    C_NAME varchar(100) COMMENT '用户机器名称',
    C_INFO varchar(1000) COMMENT '用户浏览器信息：User-Agent',
    S_IP varchar(100) COMMENT '服务器IP',
    S_NAME varchar(100) COMMENT '服务器名称',
    S_INFO varchar(1000) COMMENT '服务器信息',

    CONTENT text COMMENT '详细内容',
    
    UID_ varchar(36) COMMENT '未用',
    STATUS_ int(1) default 0 COMMENT '未用',
    INNER_ int(1) default 0 COMMENT '未用',
    primary key (ID)
) COMMENT='系统日志';
ALTER TABLE BC_LOG_SYSTEM ADD CONSTRAINT FK_SYSLOG_USER FOREIGN KEY (CREATER_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BC_LOG_SYSTEM ADD INDEX IDX_SYSLOG_ACTOR (UNIT_ID,DEPART_ID,CREATER_ID);
ALTER TABLE BC_LOG_SYSTEM ADD INDEX IDX_SYSLOG_CLIENT (C_IP);
ALTER TABLE BC_LOG_SYSTEM ADD INDEX IDX_SYSLOG_SERVER (S_IP);
