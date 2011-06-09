-- 系统安全相关模块的初始化数据

-- 插入分类模块数据
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '10','工作事务', null, 'icon');
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '90','系统配置', null, 'icon');
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 1, m.id, '90-01','组织架构', null, 'icon' from BC_SECURITY_MODULE m where m.code='90';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 1, m.id, '90-02','权限配置', null, 'icon' from BC_SECURITY_MODULE m where m.code='90';

-- 插入链接资源数据
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	values(1, 0, 2, null, '98','个性化设置', '/bc/personal/edit', 'i0504');
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	values(1, 0, 2, null, '99','桌面管理', '/bc/shortcut/list', 'i0504');
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '10-01','待办事务', '/bc/todoWork/list', 'i0605' from BC_SECURITY_MODULE m where m.code='10';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '10-02','已办事务', '/bc/doneWork/paging', 'i0606' from BC_SECURITY_MODULE m where m.code='10';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-09','消息记录', '/bc/message/paging', 'i0603' from BC_SECURITY_MODULE m where m.code='90';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-01-01','职务配置', '/bc/duty/paging', 'i0603' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-01-02','单位配置', '/bc/unit/paging', 'i0601' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-01-03','部门配置', '/bc/department/paging', 'i0602' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-01-04','岗位配置', '/bc/group/paging', 'i0508' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-01-05','用户配置', '/bc/user/paging', 'i0507' from BC_SECURITY_MODULE m where m.code='90-01';
    
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '90-02-01','资源配置', '/bc/module/paging', 'i0600' from BC_SECURITY_MODULE m where m.code='90-02';
insert into BC_SECURITY_MODULE (STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '90-02-02','角色配置', '/bc/role/paging', 'i0509' from BC_SECURITY_MODULE m where m.code='90-02';

-- 插入超级管理员角色数据
insert into BC_SECURITY_ROLE (STATUS_,INNER_,TYPE_,CODE,NAME) 
	values(1, 0, 0, 'admin','超级管理员');
-- 让超级管理员角色拥有所有模块
insert into BC_SECURITY_ROLE_MODULE (RID,MID) 
	select r.id,m.id from BC_SECURITY_ROLE r,BC_SECURITY_MODULE m where r.code='admin' order by m.code;
