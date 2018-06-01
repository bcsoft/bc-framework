-- BC_FORM 表的升级
alter table BC_FORM
  drop column if exists VER_;
alter table BC_FORM
  add column VER_ numeric(10, 2);
alter table BC_FORM
  add column DESC_ varchar(255);
alter table BC_FORM
  add column EXT01 varchar(255);
alter table BC_FORM
  add column EXT02 varchar(255);
alter table BC_FORM
  add column EXT03 varchar(255);
comment on column BC_FORM.VER_ is '版本号';
comment on column BC_FORM.DESC_ is '备注';
comment on column BC_FORM.EXT01 is '扩展域1';
comment on column BC_FORM.EXT02 is '扩展域2';
comment on column BC_FORM.EXT03 is '扩展域3';

-- 构建唯一索引：PID+TYPE_+CODE+VER_
drop index if exists FORMIDX_FORM_TYPE_PID_CODE;
drop index if exists BCUK_FORM_PARENT;
create unique index BCUK_FORM_PARENT on BC_FORM (TYPE_, CODE, PID, VER_);

-- 历史数据统一版本号为1.0
update BC_FORM set VER_ = 1
where VER_ is null and pid not in (17688653) and code != 'CAR_XSZ';
update BC_FORM set TPL_ = 'DEFAULT_CERT_FORM'
where TPL_ <> 'DEFAULT_CERT_FORM';
alter table BC_FORM
  alter column VER_ set not null;
update bc_form_field set type_ = 'float'
where name_ like 'attach_width%';
update bc_form_field set type_ = 'long'
where name_ like 'attach_id%';

--select * from bc_form where ver_ is null;

-- BC_FORM_FIELD 表的升级
alter table BC_FORM_FIELD
  alter column LABEL_ drop not null;
alter table BC_FORM_FIELD
  add column UPDATOR integer;
comment on column BC_FORM_FIELD.UPDATOR is '最后更新人ID';
alter table BC_FORM_FIELD
  add column UPDATE_TIME timestamp;
comment on column BC_FORM_FIELD.UPDATE_TIME is '最后更新时间';
alter table BC_FORM_FIELD
  add constraint BCFK_FORM_FIELD_UPDATOR foreign key (UPDATOR)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;
update BC_FORM_FIELD ff set UPDATOR = f.MODIFIER_ID, UPDATE_TIME = f.MODIFIED_DATE
from BC_FORM f
where f.id = ff.pid;
alter table BC_FORM_FIELD
  alter column UPDATOR set not null;
alter table BC_FORM_FIELD
  alter column UPDATE_TIME set not null;

-- 更改级联删除
alter table BC_FORM_FIELD
  drop constraint if exists BCFK_FORM_FIELD_PID;
alter table BC_FORM_FIELD
  add constraint BCFK_FORM_FIELD_PID foreign key (PID)
references BC_FORM (ID) on update restrict on delete cascade;
alter table BC_FORM_FIELD_LOG
  drop constraint if exists BC_FORM_FIELD_LOG_PID;
alter table BC_FORM_FIELD_LOG
  drop constraint if exists BCFK_FORM_FIELD_LOG_PID;
alter table BC_FORM_FIELD_LOG
  add constraint BCFK_FORM_FIELD_LOG_PID foreign key (PID)
references BC_FORM_FIELD (ID) on update restrict on delete cascade;

--"粤A.0GF54强检使用证"
--select * from bc_form where id = 22409296
--"粤A.0GF54车辆购置税完税证明"
--select * from bc_form where id = 22407336
--结束
--维护bc_form_field中关于司机证件的数据
--开始
with form_field(pid) as (
  select distinct (pid)
  from bc_form_field
)

insert into bc_form_field (
  id, pid, name_, label_, type_, value_, updator, update_time)
  select nextval('hibernate_sequence'), ff.pid, 'pname', m.name, 'string', m.name, (select id
                                                                                    from bc_identity_actor_history
                                                                                    where actor_code = 'admin' and
                                                                                          current = true), now()

  from form_field ff
  inner join bc_form f on f.id = ff.pid
  inner join bs_temp_driver m on m.id = f.pid --司机招聘
  where f.type_ = 'CarManCert' and not exists(
    select 1
    from bc_form_field ff1
    where ff1.pid = ff.pid and ff1.name_ = 'pname'
  );

--"陈国强驾驶证"
--select * from bc_form where id = 18664902  --开始id：18664903
--"陈月仁从业资格证"
--select * from bc_form where id = 18664980    --开始id：18664980
--结束	
	