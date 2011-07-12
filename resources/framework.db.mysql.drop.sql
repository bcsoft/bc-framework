-- ##bc平台的mysql删表脚本##

-- 测试用的表
drop table if exists ZTEST_EXAMPLE;

-- 用户反馈
drop table if exists BC_FEEDBACK;

-- 电子公告
drop table if exists BC_BULLETIN;

-- 文档附件
drop table if exists BC_DOCS_ATTACH_HISTORY;
drop table if exists BC_DOCS_ATTACH;

-- 系统日志
drop table if exists BC_LOG_SYSTEM;

-- 工作事务
drop table if exists BC_WORK_TODO;
drop table if exists BC_WORK_DONE;
drop table if exists BC_WORK;

-- 消息管理
drop table if exists BC_MESSAGE;

-- 个性化设置
drop table if exists BC_DESKTOP_SHORTCUT;
drop table if exists BC_DESKTOP_PERSONAL;

-- 系统标识
drop table if exists BC_IDENTITY_ROLE_ACTOR;
drop table if exists BC_IDENTITY_AUTH;
drop table if exists BC_IDENTITY_ACTOR_RELATION;
drop table if exists BC_IDENTITY_ACTOR;
drop table if exists BC_IDENTITY_ACTOR_DETAIL;
drop table if exists BC_IDENTITY_DUTY;
drop table if exists BC_IDENTITY_IDGENERATOR;
drop table if exists BC_IDENTITY_ROLE_RESOURCE;
drop table if exists BC_IDENTITY_ROLE;
drop table if exists BC_IDENTITY_RESOURCE;

-- 选项模块
drop table if exists BC_OPTION_ITEM;
drop table if exists BC_OPTION_GROUP;

