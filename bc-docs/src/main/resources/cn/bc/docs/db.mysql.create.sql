-- 文档附件
create table BC_DOCS_ATTACH (
    ID int NOT NULL auto_increment,
    PTYPE varchar(36) NOT NULL COMMENT '所关联文档的类型',
    PUID varchar(36) NOT NULL COMMENT '所关联文档的UID',
   
    SIZE int NOT NULL COMMENT '文件的大小(单位为字节)',
    EXTEND varchar(10) COMMENT '文件扩展名：如png、doc、mp3等',
    APPPATH int(1) NOT NULL COMMENT '指定path的值是相对于应用部署目录下路径还是相对于全局配置的app.data目录下的路径',
    SUBJECT varchar(500) NOT NULL COMMENT '文件名称(不带路径的部分)',
    PATH varchar(500) NOT NULL COMMENT '物理文件保存的相对路径(相对于全局配置的附件根目录下的子路径，如"2011/bulletin/xxxx.doc")',
    
    FILE_DATE datetime NOT NULL COMMENT '创建时间',
    FILE_YEAR int(4) COMMENT '创建时间的年度yyyy',
    FILE_MONTH int(2) COMMENT '创建时间的月份(1-12)',
    FILE_DAY int(2) COMMENT '创建时间的日(1-31)',
    
    AUTHOR_ID int NOT NULL COMMENT '创建人ID',
    AUTHOR_NAME varchar(100) NOT NULL COMMENT '创建人姓名',
    DEPART_ID int COMMENT '创建人所在部门ID，如果用户直接隶属于单位，则为null',
    DEPART_NAME varchar(255) COMMENT '创建人所在部门名称，如果用户直接隶属于单位，则为null',
    UNIT_ID int NOT NULL COMMENT '创建人所在单位ID',
    UNIT_NAME varchar(255) NOT NULL COMMENT '创建人所在单位名称',
    
    STATUS_ int(1) COMMENT '未用',
    UID_ varchar(36) COMMENT '未用',
    INNER_ int(1) COMMENT '未用',
    primary key (ID)
) COMMENT='文档附件,记录文档与其相关附件之间的关系';
ALTER TABLE BC_DOCS_ATTACH ADD CONSTRAINT FK_ATTACH_AUTHOR FOREIGN KEY (AUTHOR_ID) 
	REFERENCES BC_IDENTITY_ACTOR (ID);
ALTER TABLE BC_DOCS_ATTACH ADD INDEX IDX_ATTACH_PUID (PUID);
ALTER TABLE BC_DOCS_ATTACH ADD INDEX IDX_ATTACH_PTYPE (PTYPE);
ALTER TABLE BC_DOCS_ATTACH ADD INDEX IDX_ATTACH_FILEDATE (UNIT_ID,FILE_YEAR,FILE_MONTH,FILE_DAY);
