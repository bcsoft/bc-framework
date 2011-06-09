-- 系统安全相关模块的初始化数据

-- 插入分类模块数据
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	values(HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 1, null, '90','系统配置', null, '{iconClass:"icon"}');
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 1, m.id, '90-01','组织架构', null, '{iconClass:"icon"}' from BC_SECURITY_MODULE m where m.code='90';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 1, m.id, '90-02','权限配置', null, '{iconClass:"icon"}' from BC_SECURITY_MODULE m where m.code='90';

-- 插入链接模块数据
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-01-01','职务配置', '/bc/duty/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-01-02','单位配置', '/bc/unit/list', '{iconClass:"icon"}' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-01-03','部门配置', '/bc/department/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-01-04','岗位配置', '/bc/group/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-01';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-01-05','人员配置', '/bc/user/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-01';
    
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-02-01','模块配置', '/bc/module/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-02';
insert into BC_SECURITY_MODULE (ID,STATUS_,INNER_,TYPE_,BELONG,CODE,NAME,URL,OPTION_) 
	select HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 2, m.id, '90-02-02','角色配置', '/bc/role/list', '{iconClass:"icon i0001"}' from BC_SECURITY_MODULE m where m.code='90-02';

-- 插入超级管理员角色数据
insert into BC_SECURITY_ROLE (ID,STATUS_,INNER_,TYPE_,CODE,NAME) 
	values(HIBERNATE_SEQUENCE.NEXTVAL, 1, 0, 0, 'admin','超级管理员');
-- 让超级管理员角色拥有所有模块
insert into BC_SECURITY_ROLE_MODULE (RID,MID) 
	select r.id,m.id from BC_SECURITY_ROLE r,BC_SECURITY_MODULE m where r.code='admin' order by m.code;
