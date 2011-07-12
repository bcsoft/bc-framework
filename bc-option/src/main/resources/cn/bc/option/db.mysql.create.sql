-- 选项模块
-- 选项分组
create table BC_OPTION_GROUP (
    ID int NOT NULL auto_increment,
    KEY_ varchar(255) NOT NULL COMMENT '键',
    VALUE_ varchar(255) NOT NULL COMMENT '值',
    ORDER_ varchar(100) COMMENT '排序号',
    ICON varchar(100) COMMENT '图标样式',
    primary key (ID)
) COMMENT='选项分组';
ALTER TABLE BC_OPTION_GROUP ADD INDEX IDX_OPTIONGROUP_KEYVALUE (KEY_,VALUE_);

-- 选项条目
create table BC_OPTION_ITEM (
    ID int NOT NULL auto_increment,
    PID int NOT NULL COMMENT '所属分组的ID',
    KEY_ varchar(255) NOT NULL COMMENT '键',
    VALUE_ varchar(255) NOT NULL COMMENT '值',
    ORDER_ varchar(100) COMMENT '排序号',
    ICON varchar(100) COMMENT '图标样式',
    STATUS_ int(1) NOT NULL COMMENT '状态：0-已禁用,1-启用中,2-已删除',
    primary key (ID)
) COMMENT='选项条目';
ALTER TABLE BC_OPTION_ITEM ADD CONSTRAINT FK_OPTIONITEM_OPTIONGROUP FOREIGN KEY (PID) 
	REFERENCES BC_OPTION_GROUP (ID);
ALTER TABLE BC_OPTION_ITEM ADD INDEX IDX_OPTIONITEM_KEYVALUE (KEY_,VALUE_);