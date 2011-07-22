-- ##bc平台的 oracle 删表脚本##

-- 用于生成hibernate id的序列
drop sequence hibernate_sequence;

-- 测试用的表
drop table BC_EXAMPLE;

-- 用户反馈
drop table BC_FEEDBACK;

-- 电子公告
drop table BC_BULLETIN;

-- 文档附件
drop table BC_DOCS_ATTACH_HISTORY;
drop table BC_DOCS_ATTACH;

-- 系统日志
drop table BC_LOG_SYSTEM;

-- 工作事务
drop table BC_WORK_TODO;
drop table BC_WORK_DONE;
drop table BC_WORK;

-- 消息管理
drop table BC_MESSAGE;

-- 个性化设置
drop table BC_DESKTOP_SHORTCUT;
drop table BC_DESKTOP_PERSONAL;

-- 系统标识
drop table BC_IDENTITY_ROLE_ACTOR;
drop table BC_IDENTITY_AUTH;
drop table BC_IDENTITY_ACTOR_RELATION;
drop table BC_IDENTITY_ACTOR;
drop table BC_IDENTITY_ACTOR_DETAIL;
drop table BC_IDENTITY_DUTY;
drop table BC_IDENTITY_IDGENERATOR;
drop table BC_IDENTITY_ROLE_RESOURCE;
drop table BC_IDENTITY_ROLE;
drop table BC_IDENTITY_RESOURCE;

-- 选项模块
drop table BC_OPTION_ITEM;
drop table BC_OPTION_GROUP;

