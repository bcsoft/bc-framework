-- 系统桌面相关模块
-- 桌面快捷方式
create table BC_DESKTOP_SHORTCUT (
    ID numeric not null identity,
    UID_ varchar(36),
    STATUS_ tinyint not null,
    INNER_ tinyint not null,
    ORDER_ varchar(100) not null,
    STANDALONE tinyint not null,
    NAME varchar(255),
    URL varchar(255),
    ICONCLASS varchar(255),
    MID numeric,
    AID numeric,
    primary key (ID)
);
alter table BC_DESKTOP_SHORTCUT add constraint FK_SHORTCUT_MODULE foreign key (MID) 
	references BC_SECURITY_MODULE (ID);
alter table BC_DESKTOP_SHORTCUT add constraint FK_SHORTCUT_ACTOR foreign key (AID) 
	references BC_IDENTITY_ACTOR (ID);
