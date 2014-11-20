/**
 * 模板管理导航菜单节点
 */
-- 删除旧数据模板管理，以及删除模板管理角色与模板资源的关联的数据
delete from BC_IDENTITY_ROLE_RESOURCE
	where sid = (select id from BC_IDENTITY_RESOURCE where name='模板管理');
delete from BC_IDENTITY_RESOURCE where name='模板管理'; 

-------------------------------------------| 模板管理 |----------------------------------------------------
-- 插入资源：分类->模板管理
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800308', '模板管理', null, 'i0309'
	from BC_IDENTITY_RESOURCE m 
	where m.name='系统维护' -- 隶属
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='模板管理' and url is null);
	
-------------------------------------------| 模板配置 |----------------------------------------------------
-- 插入资源：模板配置隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80340', '模板配置', '/bc/templates/paging', 'i0309'
	from BC_IDENTITY_RESOURCE m 
	where m.name='模板管理' -- 隶属
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='模板配置');

-- 模板管理角色更新为：模板配置管理角色
update BC_IDENTITY_ROLE
	set NAME = '模板配置管理'
	where code = 'BC_TEMPLATE';
-- 插入模板角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_TEMPLATE' 
	and m.NAME='模板配置'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
-- 插入模板配置查阅角色
insert into BC_IDENTITY_ROLE (ID,STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0006', 'BC_TEMPLATE_READ','模板配置查阅'
	from BC_DUAL 
	where not exists (select 0 from BC_IDENTITY_ROLE where CODE='BC_TEMPLATE_READ');
-- 插入模板配置查阅角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_TEMPLATE_READ' 
	and m.NAME='模板配置'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
-- 插入模板超级管理员角色包含模板配置资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_ADMIN' 
	and m.NAME='模板配置'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);

-------------------------------------------| 模板分类 |----------------------------------------------------
-- 更新之前的分类管理隶属于模板管理
update BC_IDENTITY_RESOURCE set order_='80341',
	belong=(select id from BC_IDENTITY_RESOURCE where name='模板管理' and url is null)
	where name='模板分类';

-------------------------------------------| 模板格式 |----------------------------------------------------
-- 插入资源：模板格式隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80342', '模板格式', '/bc/templateTypes/list', 'i0309'
	from BC_IDENTITY_RESOURCE m 
	where m.name='模板管理' -- 隶属
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='模板格式');

-- 插入模板格式角色
insert into BC_IDENTITY_ROLE (ID,STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0006', 'BC_TEMPLATE_FORMAT','模板格式'
	from BC_DUAL 
	where not exists (select 0 from BC_IDENTITY_ROLE where CODE='BC_TEMPLATE_FORMAT');

-- 插入模板格式角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_TEMPLATE_FORMAT' 
	and m.NAME='模板格式'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
-- 插入超级管理员角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_ADMIN' 
	and m.NAME='模板格式'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);

-------------------------------------------| 模板参数 |----------------------------------------------------
-- 插入资源：模板参数隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80343', '模板参数', '/bc/templateParams/list', 'i0309'
	from BC_IDENTITY_RESOURCE m 
	where m.name='模板管理' -- 隶属
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='模板参数');

-- 插入模板参数角色
insert into BC_IDENTITY_ROLE (ID,STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0006', 'BC_TEMPLATE_PARAM','模板参数'
	from BC_DUAL 
	where not exists (select 0 from BC_IDENTITY_ROLE where CODE='BC_TEMPLATE_PARAM');

-- 插入模板参数角色包含模板参数
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_TEMPLATE_PARAM' 
	and m.NAME='模板参数'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
-- 插入超级管理员角色包含模板参数
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE='BC_ADMIN' 
	and m.NAME='模板参数'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);


/**
 * 绑定最后修改人、最后修改日期非空
 * 
 */
-- 模板配置，空的最后修改人，最后修改时间更新成与发起人，发起时间一致
update bc_template set modifier_id = author_id, modified_date = file_date
	where modifier_id is null and modified_date is null;
-- 模板格式，空的最后修改人，最后修改时间更新成与发起人，发起时间一致
update bc_template_type set modifier_id = author_id, modified_date = file_date
	where modifier_id is null and modified_date is null;
-- 模板参数，空的最后修改人，最后修改时间更新成与发起人，发起时间一致
update bc_template_param set modifier_id = author_id, modified_date = file_date
	where modifier_id is null and modified_date is null;

-- 模板配置绑定非空约束
alter table bc_template alter column modifier_id set not null;
alter table bc_template alter column modified_date set not null; 
-- 模板格式绑定非空约束
alter table bc_template_type alter column modifier_id set not null;
alter table bc_template_type alter column modified_date set not null; 
-- 模板参数绑定非空约束
alter table bc_template_param alter column modifier_id set not null;
alter table bc_template_param alter column modified_date set not null; 


-------------------------------------------| 插入岗位 |----------------------------------------------------
-- 插入岗位：模板管理岗
insert into BC_IDENTITY_ACTOR (ID,UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_,PCODE,PNAME)
	select NEXTVAL('CORE_SEQUENCE'),'group.init.'||NEXTVAL('CORE_SEQUENCE'), 0, false, 3
	, 'TemplateManageGroup','模板管理岗', '9930','[1]baochengzongbu','宝城'
	from BC_DUAL
	where not exists (select 0 from BC_IDENTITY_ACTOR where CODE='TemplateManageGroup');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID)
    select 0,am.id,af.id
    from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af
    where am.CODE='baochengzongbu'
	and af.CODE = 'TemplateManageGroup'
	and not exists (
		select 0 from BC_IDENTITY_ACTOR_RELATION r
		where r.TYPE_=0 and r.MASTER_ID=am.id and r.FOLLOWER_ID=af.id
	);

-- 插入岗位：模板查阅岗
insert into BC_IDENTITY_ACTOR (ID,UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_,PCODE,PNAME)
	select NEXTVAL('CORE_SEQUENCE'),'group.init.'||NEXTVAL('CORE_SEQUENCE'), 0, false, 3
	, 'TemplateReadGroup','模板查阅岗', '9931','[1]baochengzongbu','宝城'
	from BC_DUAL
	where not exists (select 0 from BC_IDENTITY_ACTOR where CODE='TemplateReadGroup');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID)
    select 0,am.id,af.id
    from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af
    where am.CODE='baochengzongbu'
	and af.CODE = 'TemplateReadGroup'
	and not exists (
		select 0 from BC_IDENTITY_ACTOR_RELATION r
		where r.TYPE_=0 and r.MASTER_ID=am.id and r.FOLLOWER_ID=af.id
	);
-------------------------------------------| 岗位-角色配置 |----------------------------------------------------
-- 模板管理超级管理岗包含角色：模板配置管理，模板分类管理，模板格式，模板参数
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID)
	select a.id, r.id
	from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r
	where a.CODE in ('TemplateManageGroup') and r.CODE in (
		'BC_TEMPLATE', 'BC_TPL_MANAGE', 'BC_TEMPLATE_FORMAT', 'BC_TEMPLATE_PARAM'
	)
	and not exists (select 0 from BC_IDENTITY_ROLE_ACTOR ra where ra.AID=a.id and ra.RID=r.id);

-- 模板管理普通管理岗包含角色：模板配置查阅，模板分类查阅
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID)
	select a.id, r.id
	from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r
	where a.CODE in ('TemplateReadGroup') and r.CODE in (
		'BC_TEMPLATE_READ', 'BC_TPL_READ'
	)
	and not exists (select 0 from BC_IDENTITY_ROLE_ACTOR ra where ra.AID=a.id and ra.RID=r.id);