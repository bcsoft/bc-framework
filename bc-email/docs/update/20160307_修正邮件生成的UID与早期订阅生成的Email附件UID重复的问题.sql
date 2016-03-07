-- 20160307_修正邮件生成的UID与早期订阅生成的Email附件UID重复的问题
SELECT max(substring(uid_ from 12)::int) FROM bc_email where uid_ like 'Email.main.%';
update bc_identity_idgenerator set value_ = 30000000 where type_ = 'Email.main';
SELECT * FROM bc_identity_idgenerator where type_ ilike '%mail%' or type_ ilike '%SyncBase%' order by type_;
