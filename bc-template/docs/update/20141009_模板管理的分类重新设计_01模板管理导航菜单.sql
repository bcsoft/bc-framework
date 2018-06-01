-- 清除资源、角色、岗位配置数据
-- 资源
delete from BC_IDENTITY_ROLE_RESOURCE
where sid in
      (select id
       from BC_IDENTITY_RESOURCE
       where name in ('模板管理', '模板分类', '模板配置', '模板格式', '模板参数'));
delete from BC_IDENTITY_RESOURCE
where name in ('模板管理', '模板分类', '模板配置', '模板格式', '模板参数');
-- 角色
delete from BC_IDENTITY_ROLE_ACTOR
where rid in
      (select id
       from BC_IDENTITY_ROLE
       where code like 'BC_TEMPLATE_%');
delete from BC_IDENTITY_ROLE
where code like 'BC_TEMPLATE_%';
-- 岗位
delete from BC_IDENTITY_ACTOR_RELATION
where
  FOLLOWER_ID in (
    select id
    from BC_IDENTITY_ACTOR
    where name in ('模板管理岗', '模板维护岗')
  )
  or MASTER_ID in (
    select id
    from BC_IDENTITY_ACTOR
    where name in ('模板管理岗', '模板维护岗')
  );
delete from bc_subscribe_actor
where aid in (
  select id
  from BC_IDENTITY_ACTOR
  where name in ('模板管理岗', '模板维护岗')
);
delete from BC_IDENTITY_ROLE_ACTOR
where aid in (
  select id
  from BC_IDENTITY_ACTOR
  where name in ('模板管理岗', '模板维护岗')
);
delete from BC_IDENTITY_ACTOR
where name in ('模板管理岗', '模板维护岗');

-- ACL
delete from bc_acl_actor
where pid in (
  select aa.pid
  from bc_acl_actor aa
  inner join bc_acl_doc d on aa.pid = d.id
  inner join bc_identity_actor a on a.id = aa.aid
  where d.doc_name = '模板分类' and d.doc_type = 'Category'
        and a.code in ('ghy', 'jane', 'wing', 'qiuting', 'fei')
) and aid in (
  select aa.aid
  from bc_acl_actor aa
  inner join bc_acl_doc d on aa.pid = d.id
  inner join bc_identity_actor a on a.id = aa.aid
  where d.doc_name = '模板分类' and d.doc_type = 'Category'
        and a.code in ('ghy', 'jane', 'wing', 'qiuting', 'fei')
);

-------------------------------------------| 模板管理 |----------------------------------------------------
-- 插入资源：分类->模板管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800308', '模板管理', null, 'i0309'
  from BC_IDENTITY_RESOURCE m
  where m.name = '系统维护' -- 隶属
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '模板管理' and url is null);

-------------------------------------------| 模板配置 |----------------------------------------------------
-- 插入资源：模板配置隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80340', '模板配置', '/bc/templates/paging', 'i0309'
  from BC_IDENTITY_RESOURCE m
  where m.name = '模板管理' -- 隶属
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '模板配置');

-- 插入模板配置管理角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0006', 'BC_TEMPLATE_CONFIG_MANAGE', '模板配置管理'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_CONFIG_MANAGE');
-- 插入模板配置管理角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_CONFIG_MANAGE'
        and m.NAME = '模板配置'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);
-- 插入模板配置查阅角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0007', 'BC_TEMPLATE_CONFIG_READ', '模板配置查阅'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_CONFIG_READ');
-- 插入模板配置查阅角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_CONFIG_READ'
        and m.NAME = '模板配置'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);

-------------------------------------------| 模板分类 |----------------------------------------------------
-- 更新分类管理隶属于模板管理
update BC_IDENTITY_RESOURCE set order_ = '80341',
  belong                               = (select id
                                          from BC_IDENTITY_RESOURCE
                                          where name = '模板管理' and url is null)
where name = '模板分类';

-------------------------------------------| 模板格式 |----------------------------------------------------
-- 插入资源：模板格式隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80342', '模板格式', '/bc/templateTypes/list', 'i0309'
  from BC_IDENTITY_RESOURCE m
  where m.name = '模板管理' -- 隶属
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '模板格式');

-- 插入模板格式管理角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0008', 'BC_TEMPLATE_FORMAT_MANAGE', '模板格式管理'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_FORMAT_MANAGE');
-- 插入模板格式查阅角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0009', 'BC_TEMPLATE_FORMAT_READ', '模板格式查阅'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_FORMAT_READ');

-- 插入模板格式管理角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_FORMAT_MANAGE'
        and m.NAME = '模板格式'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);
-- 插入模板格式查阅角色包含模板格式资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_FORMAT_READ'
        and m.NAME = '模板格式'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);

-------------------------------------------| 模板参数 |----------------------------------------------------
-- 插入资源：模板参数隶属模板管理
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '80343', '模板参数', '/bc/templateParams/list', 'i0309'
  from BC_IDENTITY_RESOURCE m
  where m.name = '模板管理' -- 隶属
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '模板参数');

-- 插入模板参数管理角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0009', 'BC_TEMPLATE_PARAM_MANAGE', '模板参数管理'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_PARAM_MANAGE');
-- 插入模板参数查阅角色
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0009', 'BC_TEMPLATE_PARAM_READ', '模板参数查阅'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ROLE
                   where CODE = 'BC_TEMPLATE_PARAM_READ');

-- 插入模板参数角色包含模板参数资源
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_PARAM_MANAGE'
        and m.NAME = '模板参数'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.CODE = 'BC_TEMPLATE_PARAM_READ'
        and m.NAME = '模板参数'
        and not exists(select 0
                       from BC_IDENTITY_ROLE_RESOURCE rm
                       where rm.RID = r.id and rm.SID = m.id);


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
alter table bc_template
  alter column modifier_id set not null;
alter table bc_template
  alter column modified_date set not null;
-- 模板格式绑定非空约束
alter table bc_template_type
  alter column modifier_id set not null;
alter table bc_template_type
  alter column modified_date set not null;
-- 模板参数绑定非空约束
alter table bc_template_param
  alter column modifier_id set not null;
alter table bc_template_param
  alter column modified_date set not null;

-------------------------------------------| 插入岗位 |----------------------------------------------------
-- 插入岗位：模板管理岗
insert into BC_IDENTITY_ACTOR (ID, UID_, STATUS_, INNER_, TYPE_, CODE, NAME, ORDER_, PCODE, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 'group.init.' || NEXTVAL('CORE_SEQUENCE'), 0, false, 3, 'BC_TEMPLATE_MANAGE_GROUP',
    '模板管理岗', '9930', '[1]baochengzongbu', '宝城'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ACTOR
                   where CODE = 'BC_TEMPLATE_MANAGE_GROUP');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'baochengzongbu'
        and af.CODE = 'BC_TEMPLATE_MANAGE_GROUP'
        and not exists(
    select 0
    from BC_IDENTITY_ACTOR_RELATION r
    where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id
  );

-- 插入岗位：模板维护岗
insert into BC_IDENTITY_ACTOR (ID, UID_, STATUS_, INNER_, TYPE_, CODE, NAME, ORDER_, PCODE, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 'group.init.' || NEXTVAL('CORE_SEQUENCE'), 0, false, 3, 'BC_TEMPLATE_MAINTAIN_GROUP',
    '模板维护岗', '9931', '[1]baochengzongbu', '宝城'
  from BC_DUAL
  where not exists(select 0
                   from BC_IDENTITY_ACTOR
                   where CODE = 'BC_TEMPLATE_MAINTAIN_GROUP');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'baochengzongbu'
        and af.CODE = 'BC_TEMPLATE_MAINTAIN_GROUP'
        and not exists(
    select 0
    from BC_IDENTITY_ACTOR_RELATION r
    where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id
  );

-------------------------------------------| 岗位-角色配置 |----------------------------------------------------
-- 模板管理超级管理岗包含角色：模板配置管理，模板分类管理，模板格式，模板参数
insert into BC_IDENTITY_ROLE_ACTOR (AID, RID)
  select a.id, r.id
  from BC_IDENTITY_ACTOR a, BC_IDENTITY_ROLE r
  where a.CODE in ('BC_TEMPLATE_MANAGE_GROUP') and r.CODE in (
    'BC_TEMPLATE_CONFIG_MANAGE', 'BC_TEMPLATE_CATEGORY_MANAGE',
    'BC_TEMPLATE_FORMAT_MANAGE', 'BC_TEMPLATE_PARAM_MANAGE'
  )
        and not exists(select 0
                       from BC_IDENTITY_ROLE_ACTOR ra
                       where ra.AID = a.id and ra.RID = r.id);

-- 模板管理普通管理岗包含角色：模板配置查阅，模板分类查阅，模板格式查阅，模板参数查阅
insert into BC_IDENTITY_ROLE_ACTOR (AID, RID)
  select a.id, r.id
  from BC_IDENTITY_ACTOR a, BC_IDENTITY_ROLE r
  where a.CODE in ('BC_TEMPLATE_MAINTAIN_GROUP') and r.CODE in (
    'BC_TEMPLATE_CONFIG_READ', 'BC_TEMPLATE_CATEGORY_READ',
    'BC_TEMPLATE_FORMAT_READ', 'BC_TEMPLATE_PARAM_READ'
  )
        and not exists(select 0
                       from BC_IDENTITY_ROLE_ACTOR ra
                       where ra.AID = a.id and ra.RID = r.id);

-- 超级管理岗包含角色：模板配置管理，模板分类管理，模板格式，模板参数
insert into BC_IDENTITY_ROLE_ACTOR (AID, RID)
  select a.id, r.id
  from BC_IDENTITY_ACTOR a, BC_IDENTITY_ROLE r
  where a.CODE in ('chaojiguanligang') and r.CODE in (
    'BC_TEMPLATE_CONFIG_MANAGE', 'BC_TEMPLATE_CATEGORY_MANAGE',
    'BC_TEMPLATE_FORMAT_MANAGE', 'BC_TEMPLATE_PARAM_MANAGE'
  )
        and not exists(select 0
                       from BC_IDENTITY_ROLE_ACTOR ra
                       where ra.AID = a.id and ra.RID = r.id);

-------------------------------------------| 岗位-用户配置 |----------------------------------------------------
-- 模板维护岗：郭惠妍、谢晓俭、何懿颖、丘婷、周文飞
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_, MASTER_ID, FOLLOWER_ID)
  select 0, am.id, af.id
  from BC_IDENTITY_ACTOR am, BC_IDENTITY_ACTOR af
  where am.CODE = 'BC_TEMPLATE_MAINTAIN_GROUP'
        and af.CODE in ('ghy', 'jane', 'wing', 'qiuting', 'fei') -- 用户帐号
        and not exists(
    select 0
    from BC_IDENTITY_ACTOR_RELATION r
    where r.TYPE_ = 0 and r.MASTER_ID = am.id and r.FOLLOWER_ID = af.id
  );

-- 插入ACL管理权限：郭惠妍、谢晓俭、何懿颖、丘婷、周文飞
insert into bc_acl_doc (id, doc_id, doc_type, doc_name, author_id, file_date, modifier_id, modified_date)
  select NEXTVAL('CORE_SEQUENCE'), c.id :: text, 'Category', c.name_, a.id, now(), a.id, now()
  from bc_category c
  inner join bc_identity_actor a on true
  where c.name_ = '模板分类' and c.code = 'TPL'
        and a.code = 'admin'
        and not exists(
    select 0
    from bc_acl_doc
    where name_ = '模板分类' and doc_type = 'Category'
  );
insert into bc_acl_actor (pid, aid, role, order_, modifier_id, modified_date)
  select d.id, a.id, '10', 0, (select id
                               from bc_identity_actor_history
                               where actor_code = 'admin' and current = true), now()
  from bc_acl_doc d
  inner join bc_identity_actor a on true
  where d.doc_name = '模板分类' and d.doc_type = 'Category'
        and a.code in ('ghy', 'jane', 'wing', 'qiuting', 'fei')
        and not exists(
    select 0
    from bc_acl_actor
    where pid = d.id and aid in (a.id)
  );