-- BC_FORM 表的升级
ALTER TABLE BC_FORM DROP COLUMN IF EXISTS VER_;
ALTER TABLE BC_FORM ADD COLUMN VER_ NUMERIC(10,2);
ALTER TABLE BC_FORM ADD COLUMN DESC_ VARCHAR(255);
ALTER TABLE BC_FORM ADD COLUMN EXT01 VARCHAR(255);
ALTER TABLE BC_FORM ADD COLUMN EXT02 VARCHAR(255);
ALTER TABLE BC_FORM ADD COLUMN EXT03 VARCHAR(255);
COMMENT ON COLUMN BC_FORM.VER_ IS '版本号';
COMMENT ON COLUMN BC_FORM.DESC_ IS '备注';
COMMENT ON COLUMN BC_FORM.EXT01 IS '扩展域1';
COMMENT ON COLUMN BC_FORM.EXT02 IS '扩展域2';
COMMENT ON COLUMN BC_FORM.EXT03 IS '扩展域3';

-- 构建唯一索引：PID+TYPE_+CODE+VER_
DROP INDEX IF EXISTS FORMIDX_FORM_TYPE_PID_CODE;
DROP INDEX IF EXISTS BCUK_FORM_PARENT;
CREATE UNIQUE INDEX BCUK_FORM_PARENT ON BC_FORM(TYPE_, CODE, PID, VER_);

-- 历史数据统一版本号为1.0
UPDATE BC_FORM SET VER_ = 1 WHERE VER_ IS NULL and pid not in( 17688653) and code != 'CAR_XSZ';
UPDATE BC_FORM SET TPL_ = 'DEFAULT_CERT_FORM' WHERE TPL_ <> 'DEFAULT_CERT_FORM';
ALTER TABLE BC_FORM ALTER COLUMN VER_ SET NOT NULL;
update bc_form_field set type_ = 'float' where name_ like 'attach_width%';
update bc_form_field set type_ = 'long' where name_ like 'attach_id%';

UPDATE BC_FORM SET VER_ = 1 WHERE VER_ IS NULL and pid = 17688653 and code != 'CAR_XSZ';
select * from bc_form where pid = 17688653 and code = 'CAR_XSZ' order by file_date asc

UPDATE BC_FORM SET VER_ = 1 WHERE pid = 17688653 and id = 21112444
UPDATE BC_FORM SET VER_ = 2 WHERE pid = 17688653 and id = 21112504
UPDATE BC_FORM SET VER_ = 3 WHERE pid = 17688653 and id = 21143904
UPDATE BC_FORM SET VER_ = 4 WHERE pid = 17688653 and id = 21143944
UPDATE BC_FORM SET VER_ = 5 WHERE pid = 17688653 and id = 21143964
UPDATE BC_FORM SET VER_ = 6 WHERE pid = 17688653 and id = 21143984
UPDATE BC_FORM SET VER_ = 7 WHERE pid = 17688653 and id = 21144024
UPDATE BC_FORM SET VER_ = 8 WHERE pid = 17688653 and id = 21144044
UPDATE BC_FORM SET VER_ = 9 WHERE pid = 17688653 and id = 21144064
UPDATE BC_FORM SET VER_ = 10 WHERE pid = 17688653 and id = 21144124

select * from bc_form where ver_ is null 

-- BC_FORM_FIELD 表的升级
ALTER TABLE BC_FORM_FIELD ALTER COLUMN LABEL_ drop NOT NULL;
ALTER TABLE BC_FORM_FIELD ADD COLUMN UPDATOR integer;
COMMENT ON COLUMN BC_FORM_FIELD.UPDATOR IS '最后更新人ID';
ALTER TABLE BC_FORM_FIELD ADD COLUMN UPDATE_TIME timestamp;
COMMENT ON COLUMN BC_FORM_FIELD.UPDATE_TIME IS '最后更新时间';
ALTER TABLE BC_FORM_FIELD ADD CONSTRAINT BCFK_FORM_FIELD_UPDATOR FOREIGN KEY (UPDATOR)
		REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
		ON UPDATE RESTRICT ON DELETE RESTRICT;
UPDATE BC_FORM_FIELD ff SET UPDATOR = f.MODIFIER_ID, UPDATE_TIME = f.MODIFIED_DATE
	from BC_FORM f
	WHERE f.id = ff.pid;
ALTER TABLE BC_FORM_FIELD ALTER COLUMN UPDATOR SET NOT NULL;
ALTER TABLE BC_FORM_FIELD ALTER COLUMN UPDATE_TIME SET NOT NULL;


-- 更改级联删除
ALTER TABLE BC_FORM_FIELD DROP CONSTRAINT IF EXISTS BCFK_FORM_FIELD_PID;
ALTER TABLE BC_FORM_FIELD ADD CONSTRAINT BCFK_FORM_FIELD_PID FOREIGN KEY (PID)
	REFERENCES BC_FORM (ID) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE BC_FORM_FIELD_LOG DROP CONSTRAINT IF EXISTS BC_FORM_FIELD_LOG_PID;
ALTER TABLE BC_FORM_FIELD_LOG DROP CONSTRAINT IF EXISTS BCFK_FORM_FIELD_LOG_PID;
ALTER TABLE BC_FORM_FIELD_LOG ADD CONSTRAINT BCFK_FORM_FIELD_LOG_PID FOREIGN KEY (PID)
	REFERENCES BC_FORM_FIELD (ID) ON UPDATE RESTRICT ON DELETE CASCADE;

/* test
update bc_form set ver_ = '1.0'
	where type_ = 'CarManCert'
	and pid = 15885244
	and code = 'certCYZG';
select ver_, file_date, modified_date, author_id, modifier_id, * from bc_form
	where type_ = 'CarManCert'
	and pid = 15885244
	and code = 'certCYZG';
*/
	
--维护bc_form_field中关于车辆证件的数据
--说明：在特殊打印时发现要找车牌号/司机名的显示不能直接找到，通过字符串的截取不能满足通用性
--      ，经过讨论，需要在bc_form_field增加一行name_ = "pname"保存以上信息，因此更新
--      以前的司机证件，增加标识“pname”行来控制 。
--开始
with form_field(pid) as(
	select distinct(pid) from bc_form_field 
)

INSERT INTO bc_form_field(
            id, pid, name_, label_, type_, value_, updator, update_time)
    select nextval('hibernate_sequence')
		,ff.pid
		,'pname'
		,c.plate_type || '.' || c.plate_no
		,'string'
		, c.plate_type || '.' || c.plate_no
		,(select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
		,now()

	from form_field ff 
	inner join bc_form f on f.id = ff.pid  
	inner join bs_car c on c.id = f.pid
	where f.type_ = 'CarCert' and not exists(
		select 1 
		from bc_form_field ff1 
		where ff1.pid = ff.pid and ff1.name_ = 'pname'
	);

	--"粤A.0GF54强检使用证"
	--select * from bc_form where id = 22409296
	--"粤A.0GF54车辆购置税完税证明"
	--select * from bc_form where id = 22407336
--结束
--维护bc_form_field中关于司机证件的数据
--开始
with form_field(pid) as(
	select distinct(pid) from bc_form_field 
)

INSERT INTO bc_form_field(
            id, pid, name_, label_, type_, value_, updator, update_time)
    select nextval('hibernate_sequence')
		,ff.pid
		,'pname'
		, m.name
		,'string'
		, m.name
		,(select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
		,now()

	from form_field ff 
	inner join bc_form f on f.id = ff.pid  
	inner join bs_temp_driver m on m.id = f.pid  --司机招聘
	where f.type_ = 'CarManCert' and not exists(
		select 1 
		from bc_form_field ff1 
		where ff1.pid = ff.pid and ff1.name_ = 'pname'
	)

	--"陈国强驾驶证"
	--select * from bc_form where id = 18664902  --开始id：18664903
	--"陈月仁从业资格证"
	--select * from bc_form where id = 18664980    --开始id：18664980
--结束	
	