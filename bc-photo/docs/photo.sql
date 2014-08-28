DROP TABLE IF EXISTS BC_IPCAMERA;
CREATE TABLE BC_IPCAMERA (
	ID INT NOT NULL,
	NAME_ VARCHAR(255) NOT NULL,
	URL VARCHAR(255) NOT NULL,
	OWNER_ID INT NOT NULL,
	CONSTRAINT BCPK_IPCAMERA PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IPCAMERA IS 'IP摄像头';
COMMENT ON COLUMN BC_IPCAMERA.ID IS 'ID';
COMMENT ON COLUMN BC_IPCAMERA.NAME_ IS '名称';
COMMENT ON COLUMN BC_IPCAMERA.URL IS '地址';
COMMENT ON COLUMN BC_IPCAMERA.OWNER_ID IS '拥有者ID，为空代表公共使用';
ALTER TABLE BC_IPCAMERA
	ADD CONSTRAINT BCFK_IPCAMERA_OWNER FOREIGN KEY (OWNER_ID)
	REFERENCES BC_IDENTITY_ACTOR (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE BC_IPCAMERA ADD CONSTRAINT BCUK_IPCAMERA_NAME_OWNER UNIQUE (NAME_, OWNER_ID);

-- IP摄像头数据
INSERT INTO BC_IPCAMERA(id, name_, url, owner_id)
	select NEXTVAL('CORE_SEQUENCE'), '华为X1摄像头', 'http://172.27.35.2:8081/photo.jpg'
	, (select id from bc_identity_actor where code = 'hrj')
	from BC_DUAL
	where not exists (
		select 0 from BC_IPCAMERA where NAME_ = '华为X1摄像头' 
			and OWNER_ID = (select id from bc_identity_actor where code = 'hrj')
	);
INSERT INTO BC_IPCAMERA(id, name_, url, owner_id)
	select NEXTVAL('CORE_SEQUENCE'), '摄像头(Error测试)', 'http://1.1.1.1:8081/error.jpg'
	, (select id from bc_identity_actor where code = 'hrj')
	from BC_DUAL
	where not exists (
		select 0 from BC_IPCAMERA where NAME_ = '摄像头(Error测试)' 
			and OWNER_ID = (select id from bc_identity_actor where code = 'hrj')
	);
-- select * from BC_IPCAMERA;

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
