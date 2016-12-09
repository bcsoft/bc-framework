-- 职务升级为 REST 版

-- 职务资源 select * from bc_identity_resource where name = '职务配置'
update bc_identity_resource set url = 'static/duty/view.htm' where name = '职务配置';