-- 查找指定actor的祖先：部门、单位、岗位
select * from BC_IDENTITY_ACTOR a where a.id in (
select ar.master_id from BC_IDENTITY_ACTOR_RELATION ar 
	where ar.type_=0
	start with ar.follower_id = 9 
    connect by prior ar.master_id = ar.follower_id
) order by a.order_;

-- 查找指定actor的后代：部门、单位、岗位
select * from BC_IDENTITY_ACTOR a where a.id in (
select ar.follower_id,sys_connect_by_path(master_id,'/'),connect_by_isleaf from BC_IDENTITY_ACTOR_RELATION ar 
	where ar.type_=0
	start with ar.master_id = 8 
    connect by prior ar.follower_id = ar.master_id
) order by a.order_;

-- 用户直接拥有的角色
select * from BC_IDENTITY_ROLE ro
	inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid=ro.id
    where ro_at.aid = 9;
    
-- 用户间接拥有的角色
select * from BC_IDENTITY_ROLE ro
	inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid=ro.id
    where ro_at.aid in (
         select ar.master_id from BC_IDENTITY_ACTOR_RELATION ar 
             where ar.type_=0
             start with ar.follower_id = 9 
             connect by prior ar.master_id = ar.follower_id
    );
    
-- 用户直接可访问的资源
select distinct res.* from Bc_Identity_Resource res
	join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid=res.id
    where ro_res.rid in (
         select ro.id from BC_IDENTITY_ROLE ro
             inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid=ro.id
             where ro_at.aid = 100171
    );
    
-- 用户间接可访问的资源
select distinct res.* from Bc_Identity_Resource res
	join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid=res.id
    where ro_res.rid in (
        select ro.id from BC_IDENTITY_ROLE ro
            inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid=ro.id
            where ro_at.aid in (
                 select ar.master_id from BC_IDENTITY_ACTOR_RELATION ar 
                     where ar.type_=0
                     start with ar.follower_id = 100171 
                     connect by prior ar.master_id = ar.follower_id
            )
    );

-- 用户自建的快捷方式
select * from (
    select * from BC_DESKTOP_SHORTCUT sc
        where sc.aid = 100171
    union all 
    -- 通用的快捷方式
    --select * from BC_DESKTOP_SHORTCUT sc
    --    where sc.aid is null and sc.sid is null
    --union all 
    -- 有权限访问的资源对应的快捷方式（包括从上级继承的）
    select * from BC_DESKTOP_SHORTCUT sc
        where sc.aid is null and sc.sid in (
            select distinct res.id from Bc_Identity_Resource res
                join BC_IDENTITY_ROLE_RESOURCE ro_res on ro_res.sid=res.id
                where ro_res.rid in (
                    select ro.id from BC_IDENTITY_ROLE ro
                        inner join BC_IDENTITY_ROLE_ACTOR ro_at on ro_at.rid=ro.id
                        where ro_at.aid = 100171 or ro_at.aid in (
                             select ar.master_id from BC_IDENTITY_ACTOR_RELATION ar 
                                 where ar.type_=0
                                 start with ar.follower_id = 100171 
                                 connect by prior ar.master_id = ar.follower_id
                        )
                )    
        )
) t order by t.order_;


--
select * from BC_IDENTITY_RESOURCE r;
-- 资源：从上向下找
select * from BC_IDENTITY_RESOURCE r start with r.name='系统维护' connect by prior r.id = r.belong;
-- 资源：从下向上找
select * from BC_IDENTITY_RESOURCE r start with r.name='单位配置' connect by prior r.belong = r.id;

-- 隶属关系：从下向上找
select a.* ,rowid from BC_IDENTITY_ACTOR a where a.code='daxin' order by a.id;
select * from BC_IDENTITY_AUTH a order by a.id;
update BC_IDENTITY_AUTH set password = '21218cca77804d2ba1922c33e0151105';
