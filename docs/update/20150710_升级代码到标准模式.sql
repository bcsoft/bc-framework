-- 角色、资源
update bc_identity_resource set url = '/bc/identity/role/list' where name = '角色配置';
update bc_identity_resource set url = '/bc/identity/resource/list' where name = '资源配置';