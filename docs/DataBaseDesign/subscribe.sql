DROP TABLE IF EXISTS BC_SUBSCRIBE_ACTOR;
DROP TABLE IF EXISTS BC_SUBSCRIBE;

-- 订阅部署表
CREATE TABLE BC_SUBSCRIBE(
	ID INT NOT NULL,
	STATUS_ INT DEFAULT 0 NOT NULL,
	TYPE_ INT DEFAULT 0 NOT NULL,
	ORDER_ VARCHAR(255),
	SUBJECT VARCHAR(500) NOT NULL,
	EVENT_CODE	VARCHAR(255) NOT NULL UNIQUE,
	AUTHOR_ID INT NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	MODIFIER_ID INT,
	MODIFIED_DATE TIMESTAMP,
	CONSTRAINT BCPK_SUBSCRIBE PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_SUBSCRIBE IS '订阅';
COMMENT ON COLUMN BC_SUBSCRIBE.ID IS 'ID';
COMMENT ON COLUMN BC_SUBSCRIBE.STATUS_ IS '状态 : -1-草稿,0-已发布,1-禁用';
COMMENT ON COLUMN BC_SUBSCRIBE.TYPE_ IS '类型 : 0-邮件,1-短信';
COMMENT ON COLUMN BC_SUBSCRIBE.ORDER_ IS '排序号';
COMMENT ON COLUMN BC_SUBSCRIBE.SUBJECT IS '订阅标题';
COMMENT ON COLUMN BC_SUBSCRIBE.EVENT_CODE IS '事件编码';
COMMENT ON COLUMN BC_SUBSCRIBE.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BC_SUBSCRIBE.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BC_SUBSCRIBE.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BC_SUBSCRIBE.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BC_SUBSCRIBE ADD CONSTRAINT BCFK_SUBSCRIBE_AUTHOR FOREIGN KEY (AUTHOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE BC_SUBSCRIBE ADD CONSTRAINT BCFK_SUBSCRIBE_MODIFIER FOREIGN KEY (MODIFIER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

	
-- 订阅者表
CREATE TABLE BC_SUBSCRIBE_ACTOR(
	PID INT NOT NULL,
	AID INT NOT NULL,
	TYPE_ INT DEFAULT 0 NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	CONSTRAINT BCPK_SUBSCRIBE_ACTOR PRIMARY KEY (PID, AID)
);
COMMENT ON TABLE BC_SUBSCRIBE_ACTOR IS '订阅者';
COMMENT ON COLUMN BC_SUBSCRIBE_ACTOR.PID IS '订阅ID';
COMMENT ON COLUMN BC_SUBSCRIBE_ACTOR.AID IS '订阅者ID : 对应Actor的ID';
COMMENT ON COLUMN BC_SUBSCRIBE_ACTOR.TYPE_ IS '类型: 0-用户订阅，1-系统推送';
COMMENT ON COLUMN BC_SUBSCRIBE_ACTOR.FILE_DATE IS '订阅日期';
ALTER TABLE BC_SUBSCRIBE_ACTOR ADD CONSTRAINT BCFK_SUBSCRIBE_ACTOR_PID FOREIGN KEY (PID)
	REFERENCES BC_SUBSCRIBE (ID) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE BC_SUBSCRIBE_ACTOR ADD CONSTRAINT BCFK_SUBSCRIBE_ACTOR FOREIGN KEY (AID)
	REFERENCES BC_IDENTITY_ACTOR (ID) ON UPDATE RESTRICT ON DELETE RESTRICT;	



-- 资源配置：我的订阅
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
    select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '010500','我的订阅','/bc/subscribePersonals/list', 'i0004' from BC_IDENTITY_RESOURCE m where m.order_='010000'
		and not exists(select 1 from bc_identity_resource where order_='010500');

-- 资源配置：订阅管理
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800335','订阅管理', '/bc/subscribes/paging', 'i0409' from BC_IDENTITY_RESOURCE m where m.order_='800000'
	and not exists(select 1 from bc_identity_resource where order_='800335');

-- 角色配置：我的订阅 BC_SUBSCRIBE_MY 管理自己的订阅
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0160', 'BC_SUBSCRIBE_MY','我的订阅'
	from bc_dual
	where not exists(select 1 from bc_identity_role where code='BC_SUBSCRIBE_MY');

-- 角色配置：订阅管理 BC_SUBSCRIBE 
insert into  BC_IDENTITY_ROLE (ID, STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false,  0,'0161', 'BC_SUBSCRIBE','订阅管理'
	from bc_dual
	where not exists(select 1 from bc_identity_role where code='BC_SUBSCRIBE');

-- 权限访问配置：通用角色
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_COMMON' 
	and m.type_ > 1 and m.order_ in ('010500')
	and not exists(select 1 from BC_IDENTITY_ROLE_RESOURCE 
				where rid=(select r2.id from BC_IDENTITY_ROLE r2 where r2.code='BC_COMMON')
				and sid in(select m2.id from BC_IDENTITY_RESOURCE m2 where m2.type_ > 1 and m2.order_ in ('010500')))
	order by m.order_;

-- 权限访问配置：超级管理员
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_ADMIN' 
	and m.type_ > 1 and m.order_ in ('800335')
	and not exists(select 1 from BC_IDENTITY_ROLE_RESOURCE 
				where rid=(select r2.id from BC_IDENTITY_ROLE r2 where r2.code='BC_ADMIN')
				and sid in(select m2.id from BC_IDENTITY_RESOURCE m2 where m2.type_ > 1 and m2.order_ in ('800335')))
	order by m.order_;

-- 权限访问配置：我的订阅
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_SUBSCRIBE_MY' 
	and m.type_ > 1 and m.order_ in ('010500')
	and not exists(select 1 from BC_IDENTITY_ROLE_RESOURCE 
				where rid=(select r2.id from BC_IDENTITY_ROLE r2 where r2.code='BC_SUBSCRIBE_MY')
				and sid in(select m2.id from BC_IDENTITY_RESOURCE m2 where m2.type_ > 1 and m2.order_ in ('010500')))
	order by m.order_;

-- 权限访问配置：订阅管理
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='BC_SUBSCRIBE' 
	and m.type_ > 1 and m.order_ in ('800335')
	and not exists(select 1 from BC_IDENTITY_ROLE_RESOURCE 
				where rid=(select r2.id from BC_IDENTITY_ROLE r2 where r2.code='BC_SUBSCRIBE')
				and sid in(select m2.id from BC_IDENTITY_RESOURCE m2 where m2.type_ > 1 and m2.order_ in ('800335')))
	order by m.order_;


-- 增加订阅实例选项
INSERT INTO bc_option_item(id,pid,key_,value_,order_,status_)
select NEXTVAL('CORE_SEQUENCE'),id,'Subscribe','订阅管理','0012',0
from bc_option_group where key_='operateLog.ptype' 
and not EXISTS(select 1 from bc_option_item where key_='Subscribe');
