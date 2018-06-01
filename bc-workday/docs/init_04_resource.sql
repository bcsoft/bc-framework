-- 资源配置：工作日管理041800
insert into BC_IDENTITY_RESOURCE (ID, STATUS_, INNER_, TYPE_, BELONG, ORDER_, NAME, URL, ICONCLASS, PNAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '041800', '工作日管理', '/bc/workday/paging', 'i0300', '办公系统'
  from BC_IDENTITY_RESOURCE m
  where m.ORDER_ = '040000'
        and not exists(select 0
                       from BC_IDENTITY_RESOURCE
                       where NAME = '工作日管理');

-- 角色配置：工作日管理 BC_CERT_MANAGE
insert into BC_IDENTITY_ROLE (ID, STATUS_, INNER_, TYPE_, ORDER_, CODE, NAME)
  select NEXTVAL('CORE_SEQUENCE'), 0, false, 0, '0028', 'BC_WORKDAY_MANAGE', '工作日管理'
  from bc_dual
  where not exists(select 1
                   from bc_identity_role
                   where code = 'BC_WORKDAY_MANAGE');

-- 权限配置：工作日管理角色访问工作日管理
insert into BC_IDENTITY_ROLE_RESOURCE (RID, SID)
  select r.id, m.id
  from BC_IDENTITY_ROLE r, BC_IDENTITY_RESOURCE m
  where r.code = 'BC_WORKDAY_MANAGE'
        and m.type_ > 1 and m.order_ in ('041800')
        and not exists(select 1
                       from BC_IDENTITY_ROLE_RESOURCE
                       where rid = (select r2.id
                                    from BC_IDENTITY_ROLE r2
                                    where r2.code = 'BC_WORKDAY_MANAGE')
                             and sid = (select m2.id
                                        from BC_IDENTITY_RESOURCE m2
                                        where m2.type_ > 1 and m2.order_ in ('041800')))
  order by m.order_;
	
	