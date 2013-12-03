/* Drop Tables */
DROP TABLE IF EXISTS BC_DEVICE_EVENT_NEW;
DROP TABLE IF EXISTS BC_DEVICE_EVENT;
DROP TABLE IF EXISTS BC_DEVICE;

-- 设备
CREATE TABLE BC_DEVICE(
	ID INT NOT NULL,
	UID_ VARCHAR(36) NOT NULL,
	STATUS_ INT DEFAULT 0 NOT NULL,
	CODE VARCHAR(255) NOT NULL UNIQUE,
	MODEL VARCHAR(255) NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	PURPOSE VARCHAR(255) NOT NULL,
	BUY_DATE DATE NOT NULL,
	SN VARCHAR(255) NOT NULL UNIQUE,
	FILE_DATE TIMESTAMP NOT NULL,
	AUTHOR_ID INT NOT NULL,
	MODIFIER_ID INT NOT NULL,
	MODIFIED_DATE TIMESTAMP NOT NULL,
	DESC_ VARCHAR(4000),
	CONSTRAINT BCPK_DEVICE PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_DEVICE IS '设备';
COMMENT ON COLUMN BC_DEVICE.ID IS 'ID';
COMMENT ON COLUMN BC_DEVICE.UID_ IS 'UID';
COMMENT ON COLUMN BC_DEVICE.STATUS_ IS '状态 : 0-使用中,1-已禁用';
COMMENT ON COLUMN BC_DEVICE.CODE IS '编码';
COMMENT ON COLUMN BC_DEVICE.MODEL IS '型号';
COMMENT ON COLUMN BC_DEVICE.NAME IS '名称';
COMMENT ON COLUMN BC_DEVICE.PURPOSE IS '用途';
COMMENT ON COLUMN BC_DEVICE.BUY_DATE IS '购买日期';
COMMENT ON COLUMN BC_DEVICE.SN IS '序列号';
COMMENT ON COLUMN BC_DEVICE.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BC_DEVICE.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BC_DEVICE.MODIFIER_ID IS '最后修改人';
COMMENT ON COLUMN BC_DEVICE.MODIFIED_DATE IS '最后修改时间';
COMMENT ON COLUMN BC_DEVICE.DESC_ IS '备注';
ALTER TABLE BC_DEVICE
	ADD CONSTRAINT BCFK_DEVICE_AUTHOR FOREIGN KEY (AUTHOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE BC_DEVICE
	ADD CONSTRAINT BCFK_DEVICE_MODIFIER FOREIGN KEY (MODIFIER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;

-- 设备事件
CREATE TABLE BC_DEVICE_EVENT(
	ID INT NOT NULL,
	DEVICE_ID INT NOT NULL,
	TYPE_ VARCHAR(255) NOT NULL,
	TRIGGER_TIME TIMESTAMP NOT NULL,
	APPID VARCHAR(255),
	DATA_ TEXT,
	CONSTRAINT BCPK_DEVICE_EVENT PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_DEVICE_EVENT IS '设备事件';
COMMENT ON COLUMN BC_DEVICE_EVENT.ID IS 'ID';
COMMENT ON COLUMN BC_DEVICE_EVENT.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN BC_DEVICE_EVENT.TYPE_ IS '事件类型';
COMMENT ON COLUMN BC_DEVICE_EVENT.TRIGGER_TIME IS '触发时间';
COMMENT ON COLUMN BC_DEVICE_EVENT.APPID IS '来源 : 如应用的ID';
COMMENT ON COLUMN BC_DEVICE_EVENT.DATA_ IS '事件数据 : 使用json格式';
ALTER TABLE BC_DEVICE_EVENT
	ADD CONSTRAINT BCFK_DEVICE_EVENT_DID FOREIGN KEY (DEVICE_ID)
	REFERENCES BC_DEVICE (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;

-- 新事件
CREATE TABLE BC_DEVICE_EVENT_NEW(
	ID INT NOT NULL,
	CONSTRAINT BCPK_DEVICE_EVENT_NEW PRIMARY KEY (ID)
);
ALTER TABLE BC_DEVICE_EVENT_NEW
	ADD CONSTRAINT BCFK_DEVICE_EVENT_NEW_ID FOREIGN KEY (ID)
	REFERENCES BC_DEVICE_EVENT (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
COMMENT ON TABLE BC_DEVICE_EVENT_NEW IS '新事件';
COMMENT ON COLUMN BC_DEVICE_EVENT_NEW.ID IS 'ID';
COMMENT ON TABLE BC_IDENTITY_ACTOR_HISTORY IS '参与者历史';
COMMENT ON COLUMN BC_IDENTITY_ACTOR_HISTORY.ID IS 'ID';

--插入设备事件序列
-- Sequence: devcie_event_sequence
-- DROP SEQUENCE devcie_event_sequence;
CREATE SEQUENCE devcie_event_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 20;
ALTER TABLE devcie_event_sequence
  OWNER TO bcsystem;

-- 插入资源
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS,PNAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '800500','设备管理', '', 'i0509','系统维护' 
	from BC_IDENTITY_RESOURCE m 
	where m.ORDER_='800000'
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='设备管理');
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS,PNAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800510','设备', '/bc/device/paging', 'i0509','系统维护/设备管理' 
	from BC_IDENTITY_RESOURCE m 
	where m.ORDER_='800500'
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='设备');	
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS,PNAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2, m.id, '800511','设备事件', '/bc/deviceEvent/paging', 'i0303','系统维护/设备管理' 
	from BC_IDENTITY_RESOURCE m 
	where m.ORDER_='800500'
	and not exists (select 0 from BC_IDENTITY_RESOURCE where NAME='设备事件');	
-- 插入角色
insert into BC_IDENTITY_ROLE (ID,STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 0,'0301', 'BC_DEVICE_MANAGE','设备管理'
	from BC_DUAL 
	where not exists (select 0 from BC_IDENTITY_ROLE where CODE='BC_DEVICE_MANAGE');
-- 插入角色与资源之间的关系
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id 
	from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.CODE = 'BC_DEVICE_MANAGE'
	and m.NAME in ('设备','事件')
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rm where rm.RID=r.id and rm.SID=m.id);
-- 插入岗位
insert into BC_IDENTITY_ACTOR (ID,UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_,PCODE,PNAME) 
	select NEXTVAL('CORE_SEQUENCE'),'group.init.'||NEXTVAL('CORE_SEQUENCE'), 0, false, 3
	, 'DeviceManageGroup','设备管理岗', '8808','[1]baochengzongbu','宝城'
	from BC_DUAL
	where not exists (select 0 from BC_IDENTITY_ACTOR where CODE='DeviceManageGroup');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id
    from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af
    where am.CODE='baochengzongbu' 
	and af.CODE = 'DeviceManageGroup' 
	and not exists (select 0 from BC_IDENTITY_ACTOR_RELATION r where r.TYPE_=0 and r.MASTER_ID=am.id and r.FOLLOWER_ID=af.id);
--	插入岗位与角色之间的关系
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID) 
	select a.id, r.id 
	from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r 
	where a.CODE in ('chaojiguanligang','DeviceManageGroup') 
	and r.CODE in ('BC_DEVICE_MANAGE')
	and not exists (select 0 from BC_IDENTITY_ROLE_ACTOR ra where ra.AID=a.id and ra.RID=r.id);


-- 插入设备数据
INSERT INTO bc_device(
	id, uid_, status_, code, model, name, purpose, buy_date, sn, desc_ 
	,file_date, author_id, modified_date, modifier_id)
    select NEXTVAL('hibernate_sequence'),'D'||NEXTVAL('hibernate_sequence'),0,'A15.01','A15','人脸识别机','上下班考勤'
	,date'2013-11-15','8123513040002281',null
	,now(),(select id from bc_identity_actor_history where actor_code='admin' and current=true)
	,now(),(select id from bc_identity_actor_history where actor_code='admin' and current=true)
	from bc_dual where not exists (select 0 from bc_device where code='A15.01');
INSERT INTO bc_device(
	id, uid_, status_, code, model, name, purpose, buy_date, sn, desc_ 
	,file_date, author_id, modified_date, modifier_id)
    select NEXTVAL('hibernate_sequence'),'D'||NEXTVAL('hibernate_sequence'),1,'D182.01','D182','打印机','打印工作表'
	,date'2013-11-29','7129513040002281',null
	,now(),(select id from bc_identity_actor_history where actor_code='admin' and current=true)
	,now(),(select id from bc_identity_actor_history where actor_code='admin' and current=true)
	from bc_dual where not exists (select 0 from bc_device where code='D182.01');

-- 插入设备事件数据
INSERT INTO bc_device_event(id,device_id,type_,trigger_time)
	select NEXTVAL('hibernate_sequence'),(select id from bc_device where code='A15.01'),'自定义事件',now()
	from bc_dual where not exists (select 0 from bc_device_event where device_id=(select id from bc_device where code='A15.01'));
INSERT INTO bc_device_event(id,device_id,type_,trigger_time)
	select NEXTVAL('hibernate_sequence'),(select id from bc_device where code='D182.01'),'打印事件',now()
	from bc_dual where not exists (select 0 from bc_device_event where device_id=(select id from bc_device where code='D182.01'));	

-- 插入新事件数据
INSERT INTO bc_device_event_new(id) (select id from bc_device_event where type_='打印事件')