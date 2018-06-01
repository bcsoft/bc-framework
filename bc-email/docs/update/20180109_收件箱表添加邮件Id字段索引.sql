-- 收件箱表添加邮件 Id 字段索引
create index bcidx_email_to_pid on bc_email_to (pid)