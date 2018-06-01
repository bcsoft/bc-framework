-- ##BC平台的 postgresql 删表脚本##

-- 用于生成hibernate id的序列
drop sequence if exists CORE_SEQUENCE;
drop sequence if exists hibernate_sequence;

-- 网络抓取配置
drop table if exists BC_SPIDER_CFG;

-- 网络硬盘
drop table if exists BC_NETDISK_COMMENT;
drop table if exists BC_NETDISK_SHARE;
drop table if exists BC_NETDISK_VISIT;
drop table if exists BC_NETDISK_FILE;

-- 刷卡
drop table if exists BC_IDCARD_PIC;
drop table if exists BC_IDCARD_CHECK;
drop table if exists BC_IDCARD;
drop sequence if exists IDCARD_SEQUENCE;

-- 测试用的表
drop table if exists BC_EXAMPLE;

-- 调查问卷
drop table if exists BC_IVG_GRADE;
drop table if exists BC_IVG_ANSWER;
drop table if exists BC_IVG_RESPOND;
drop table if exists BC_IVG_QUESTION_ITEM;
drop table if exists BC_IVG_QUESTION;
drop table if exists BC_IVG_QUESTIONARY_ACTOR;
drop table if exists BC_IVG_QUESTIONARY;

-- 同步记录基表
drop table if exists BC_SYNC_BASE;

-- 社保
drop table if exists BS_SOCIALSECURITYRULE_DETAIL;
drop table if exists BS_SOCIALSECURITYRULE;

-- 籍贯
drop table if exists BC_PLACEORIGIN;

-- 报表模板
drop table if exists BC_REPORT_HISTORY;
drop table if exists BC_REPORT_TASK;
drop table if exists BC_REPORT_TEMPLATE_ACTOR;
drop table if exists BC_REPORT_TEMPLATE;

-- 模板管理
drop table if exists BC_TEMPLATE_TEMPLATE_PARAM;
drop table if exists BC_TEMPLATE_PARAM;
drop table if exists BC_TEMPLATE;
drop table if exists BC_TEMPLATE_TYPE;

-- 用户反馈
drop table if exists BC_FEEDBACK_REPLY;
drop table if exists BC_FEEDBACK;

-- 电子公告
drop table if exists BC_BULLETIN;

-- 文档附件
drop table if exists BC_DOCS_ATTACH_HISTORY;
drop table if exists BC_DOCS_ATTACH;

-- 系统日志
drop table if exists BC_LOG_SYSTEM;
-- 操作日志
drop table if exists BC_LOG_AUDIT_ITEM;
drop table if exists BC_LOG_OPERATE;

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
drop table if exists BC_IDENTITY_ACTOR_HISTORY;
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

-- 任务调度
drop table if exists BC_SD_LOG;
drop table if exists BC_SD_JOB;

-- 删除视图
drop view if exists bc_dual;