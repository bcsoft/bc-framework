-- 系统安全相关模块
-- 模块
create table BC_SECURITY_MODULE (
    ID numeric not null identity,
    UID_ varchar(36),
    TYPE_ tinyint not null,
    STATUS_ tinyint not null,
    INNER_ tinyint not null,
    BELONG numeric,
    CODE varchar(100) not null,
    NAME varchar(255) not null,
    URL varchar(255),
    ICONCLASS varchar(255),
    OPTION_ text,
    primary key (ID)
);

-- 角色
create table BC_SECURITY_ROLE (
    ID numeric not null identity,
    UID_ varchar(36),
   	TYPE_ tinyint not null,
    STATUS_ tinyint not null,
    INNER_ tinyint not null,
    CODE varchar(100) not null,
    NAME varchar(255) not null,
    primary key (ID)
);

-- 角色与模块的关联
create table BC_SECURITY_ROLE_MODULE (
    RID numeric not null,
    MID numeric not null,
    primary key (RID,MID)
);
alter table BC_SECURITY_ROLE_MODULE add constraint FK_RM_ROLE foreign key (RID) 
	references BC_SECURITY_ROLE (ID);
alter table BC_SECURITY_ROLE_MODULE add constraint FK_RM_MODULE foreign key (MID) 
	references BC_SECURITY_MODULE (ID);
