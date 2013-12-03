-- 插入测试设备数据
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