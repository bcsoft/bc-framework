-- 系统标识相关模块

-- 参与者的扩展属性
create table BC_IDENTITY_ACTOR_DETAIL (
    ID numeric not null identity,
    CREATEDATE datetime,
    FIRST_NAME varchar(45),
    LAST_NAME varchar(45),
    SEX tinyint,
    primary key (ID)
);

-- 参与者
create table BC_IDENTITY_ACTOR (
    ID numeric not null identity,
    UID_ varchar(36) not null,
    TYPE_ tinyint not null,
    STATUS_ tinyint not null,
    INNER_ tinyint not null,
    CODE varchar(100) not null,
    NAME varchar(255) not null,
    ORDER_ varchar(100),
    EMAIL varchar(255),
    PHONE varchar(255),
    DETAIL_ID numeric,
    primary key (ID)
);
alter table BC_IDENTITY_ACTOR add constraint FK_ACTOR_ACTORDETAIL foreign key (DETAIL_ID) 
	references BC_IDENTITY_ACTOR_DETAIL (ID) on delete cascade on update cascade;
create index IDX_ACTOR_TYPE on BC_IDENTITY_ACTOR (TYPE_);

-- 参与者之间的关联关系
create table BC_IDENTITY_ACTOR_RELATION (
    TYPE_ smallint not null,
    MASTER_ID numeric not null,
   	FOLLOWER_ID numeric not null,
    ORDER_ varchar(100),
    primary key (MASTER_ID,FOLLOWER_ID,TYPE_)
);
alter table BC_IDENTITY_ACTOR_RELATION add constraint FK_AR_MASTER foreign key (MASTER_ID) 
	references BC_IDENTITY_ACTOR (ID);
alter table BC_IDENTITY_ACTOR_RELATION add constraint FK_AR_FOLLOWER foreign key (FOLLOWER_ID) 
	references BC_IDENTITY_ACTOR (ID);
create index IDX_AR_TM on BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID);
create index IDX_AR_TF on BC_IDENTITY_ACTOR_RELATION (TYPE_, FOLLOWER_ID);

-- 职务
create table BC_IDENTITY_DUTY (
    ID numeric not null identity,
    UID_ varchar(36),
    STATUS_ tinyint not null,
    INNER_ tinyint not null,
    CODE varchar(100) not null,
    NAME varchar(255) not null,
    primary key (ID)
);

-- 标识生成器
create table BC_IDENTITY_IDGENERATOR (
  TYPE_ varchar(45) not null,
  VALUE numeric not null,
  FORMAT varchar(45),
  PRIMARY KEY (TYPE_)
);

-- 参与者与角色的关联
create table BC_SECURITY_ROLE_ACTOR (
    AID numeric not null,
    RID numeric not null,
    primary key (AID,RID)
);
alter table BC_SECURITY_ROLE_ACTOR add constraint FK_RA_ACTOR foreign key (AID) 
	references BC_IDENTITY_ACTOR (ID);
alter table BC_SECURITY_ROLE_ACTOR add constraint FK_RA_ROLE foreign key (RID) 
	references BC_SECURITY_ROLE (ID);