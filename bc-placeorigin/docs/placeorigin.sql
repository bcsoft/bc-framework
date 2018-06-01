/*
-- 司机身份证读卡程序报错临时修正，报 o.full_name 不存在
ALTER TABLE bc_placeorigin ADD COLUMN full_name varchar(1000);
update bc_placeorigin set full_name = pname || '/' || name where pname is not null and pname != '';
update bc_placeorigin set full_name = name where pname is null or pname = '';
update bc_placeorigin set code = code || '0' where mod(length(code), 2) = 1;
*/

drop table if exists bc_placeorigin;
create table bc_placeorigin (
  id            integer               not null,
  pid           integer,
  type_         integer               not null,
  status_       integer               not null,
  code          character varying(12) not null,
  name          character varying(50) not null,
  pname         character varying(1000),
  file_date     timestamp             not null,
  author_id     integer               not null,
  modifier_id   integer               not null,
  modified_date timestamp             not null,
  constraint bcpk_placeorigin primary key (id),
  constraint bcfk_placeorigin_authorid foreign key (author_id)
  references bc_identity_actor_history (id),
  constraint bcfk_placeorigin_modifier foreign key (modifier_id)
  references bc_identity_actor_history (id),
  constraint bcfk_placeorigin_pid foreign key (pid)
  references bc_placeorigin (id)
);
comment on table bc_placeorigin is '籍贯';
comment on column bc_placeorigin.pid is '所隶上级ID';
comment on column bc_placeorigin.type_ is '类型: 0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级';
comment on column bc_placeorigin.status_ is '状态：0-启用中,1-已禁用';
comment on column bc_placeorigin.code is '编码: 行政区划代码和城乡划分代码, 不带后缀0，如广州市荔湾区为"440103"';
comment on column bc_placeorigin.name is '名称: 如"荔湾区"';
comment on column bc_placeorigin.pname is '所属上级的全称，如"广东省/广州市"';
comment on column bc_placeorigin.file_date is '创建时间';
comment on column bc_placeorigin.author_id is '创建人ID';
comment on column bc_placeorigin.modifier_id is '最后修改人ID';
comment on column bc_placeorigin.modified_date is '最后修改时间';
create unique index BCIDX_PLACEPRIGIN_CODE on bc_placeorigin (CODE);
create index BCIDX_PLACEPRIGIN_NAME on bc_placeorigin (name);

-- change
alter table bc_placeorigin
  drop column full_code;
alter table bc_placeorigin
  drop column desc_;
alter table bc_placeorigin
  alter column code set not null;
alter table bc_placeorigin
  alter column code type varchar(12);
alter table bc_placeorigin
  alter column name type varchar(50);
alter table bc_placeorigin
  rename full_name to pname;
alter table bc_placeorigin
  alter column pname drop not null;
alter table bc_placeorigin
  alter column pname type varchar(1000);
alter table bc_placeorigin
  alter column type_ drop default;
alter table bc_placeorigin
  alter column status_ drop default;
update bc_placeorigin set modifier_id = author_id
where modifier_id is null;
alter table bc_placeorigin
  alter column modifier_id set not null;
update bc_placeorigin set modified_date = file_date
where modified_date is null;
alter table bc_placeorigin
  alter column modified_date set not null;

-- DROP FUNCTION IF EXISTS placeorigin_auto_update_pname();
create or replace function placeorigin_auto_update_pname()
  returns trigger as
$BODY$
/** 籍贯触发器
 *	1）所属父类别变动后自动更新 PNAME 列的值
 *	2）编码或名称变动后自动更新其所有子类别 PNAME 列的值
 */
declare
  info      text;
  full_name text;
begin
  --RAISE INFO 'update...%,%,%',TG_OP,OLD.pid,NEW.pid;
  -- 插入或pid变动时：更新自身 PNAME 的值
  if (TG_OP = 'INSERT' or (
    TG_OP = 'UPDATE' and (
      (OLD.pid is null and NEW.pid is not null)
      or (OLD.pid is not null and NEW.pid is null)
      or NEW.pid != OLD.pid)
  ))
  then
    if (NEW.pid is not null)
    then
      -- 递归获取祖先节点，并用'/'字符连接名称
      select string_agg(name, '/') into info
      from (
             with recursive t (id, pid, name, code, depth) as (
               select id, pid, name, code, 1 as depth
               from bc_placeorigin
               where id = NEW.pid
               union all
               select s.id, s.pid, s.name, s.code, t.depth + 1 as depth
               from bc_placeorigin s
               inner join t on t.pid = s.id
             ) select *
               from t
               order by depth desc
           ) t;

      NEW.PNAME := info;
      raise info 'update my PNAME: %', 'id=' || NEW.id || ',pname=' || info;
    else
      NEW.PNAME := null;
    end if;
  end if;

  -- 全称
  if (NEW.pid is null)
  then
    full_name := NEW.name;
  else
    full_name := NEW.PNAME || '/' || NEW.name;
  end if;

  -- 如果name有修改就更新所有后代节点 PNAME 的值
  if (TG_OP = 'UPDATE' and NEW.name != OLD.name)
  then
    raise info 'update children''s PNAME: id=%, % > %', NEW.id, OLD.name, NEW.name;
    with recursive t (id, pid, name, code, pname, depth) as (
      select id, pid, name, code, full_name, 1 as depth
      from bc_placeorigin
      where pid = NEW.id
      union all
      select s.id, s.pid, s.name, s.code, t.pname || '/' || t.name as pname, t.depth + 1 as depth
      from bc_placeorigin s
      inner join t on t.id = s.pid
    ) update bc_placeorigin r set pname = t.pname from t
    where r.id = t.id;
  end if;
  return NEW;
end;
$BODY$ language plpgsql;
drop trigger if exists placeorigin_trigger on BC_PLACEORIGIN;
create trigger placeorigin_trigger
  before insert or update on BC_PLACEORIGIN
  for each row execute procedure placeorigin_auto_update_pname();

-- DROP FUNCTION IF EXISTS placeorigin_insert_or_update(varchar, varchar, varchar);
create or replace function placeorigin_insert_or_update(_code varchar, _name varchar, pcode varchar)
  returns integer as
$BODY$
/** 导入籍贯信息
 *	1）如果已经存在就更新
 *	2）如果不存在就自动创建
 */
declare
  code_exists boolean := false;
  _pid        integer;
begin
  -- 删除_code参数的后缀0，如 440100 将被修改为 4401
  _code := reverse(reverse(_code) :: bigint :: text);

  -- 判断是否已经存在
  select true into code_exists
  from bc_placeorigin
  where code = _code;

  -- 获取父类的id
  if pcode is not null and length(pcode) > 0
  then
    pcode := reverse(reverse(pcode) :: bigint :: text);
    select id into _pid
    from bc_placeorigin
    where code = pcode;
    if _pid is null
    then
      raise exception 'can not find parent: pcode=%, code=%, name=%', pcode, _code, _name;
    end if;
  else
    pcode := null;
    _pid := null;
  end if;

  if code_exists
  then -- 更新数据
    raise info 'update by code ''%'': name > %, pid > %', _code, _name, _pid;
    -- 更新名称和pid
    update bc_placeorigin set name = _name
      , pid                        = _pid
      , modified_date              = now()
      , modifier_id                = (select id
                                      from bc_identity_actor_history
                                      where current = true and actor_code = 'admin')
    where code = _code;
    return 0;
  else -- 插入新数据
    raise info 'insert: code = %, name = %, pid = %', _code, _name, _pid;
    insert into bc_placeorigin (id, pid, status_, code, name, type_
      , file_date, author_id, modified_date, modifier_id)
      select nextval('hibernate_sequence'), _pid, 0, _code, _name, length(_code) / 2, now(), (select id
                                                                                              from
                                                                                                bc_identity_actor_history
                                                                                              where current = true and
                                                                                                    actor_code =
                                                                                                    'admin'), now(),
        (select id
         from bc_identity_actor_history
         where current = true and actor_code = 'admin')
      from bc_dual;
    return 1;
  end if;
end;
$BODY$ language plpgsql;

-- 去除编码的后缀0，如440100将变为4401
update bc_placeorigin set code = reverse(reverse(code) :: int :: text)
  , modified_date              = now()
  , modifier_id                = (select id
                                  from bc_identity_actor_history
                                  where current = true and actor_code = 'admin')
where code != reverse(reverse(code) :: int :: text);

-- 全局更新一下pname
update bc_placeorigin set pname = null
where pid is null;
update bc_placeorigin set name = name || 'zz'
where length(code) = 2;
update bc_placeorigin set name = substring(name from 1 for length(name) - 2)
where length(code) = 2

-- test
/*
select placeorigin_insert_or_update('110100', '市辖区','1100');
update bc_placeorigin set pid=null,pname=null where code='110102';
select placeorigin_insert_or_update('110102','西城区','1101');
select * from bc_placeorigin order by code desc;
*/