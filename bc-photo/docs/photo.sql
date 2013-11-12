/* 清除资源、角色、岗位配置数据
delete from BC_IDENTITY_ROLE_RESOURCE where sid in 
	(select id from BC_IDENTITY_RESOURCE where ORDER_ in ('074199'));
delete from BC_IDENTITY_RESOURCE where ORDER_ in ('074199');
*/
--	插入测试用资源
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS,PNAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '074199','图片处理', '/bc/photo/main', 'i0001','功能演示' 
	from BC_IDENTITY_RESOURCE m 
	where m.ORDER_='074100'
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='图片处理');
--	插入角色与资源之间的关系
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE = 'BC_COMMON' 
	and m.NAME = '图片处理'
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
