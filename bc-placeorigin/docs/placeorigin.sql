drop TABLE if exists bc_placeorigin; 
CREATE TABLE bc_placeorigin (
  id integer NOT NULL,
  pid integer,
  type_ integer NOT NULL,
  status_ integer NOT NULL,
  code character varying(12) NOT NULL,
  name character varying(50) NOT NULL,
  pname character varying(1000),
  file_date timestamp NOT NULL,
  author_id integer NOT NULL,
  modifier_id integer NOT NULL,
  modified_date timestamp NOT NULL,
  CONSTRAINT bcpk_placeorigin PRIMARY KEY (id),
  CONSTRAINT bcfk_placeorigin_authorid FOREIGN KEY (author_id)
      REFERENCES bc_identity_actor_history (id),
  CONSTRAINT bcfk_placeorigin_modifier FOREIGN KEY (modifier_id)
      REFERENCES bc_identity_actor_history (id),
  CONSTRAINT bcfk_placeorigin_pid FOREIGN KEY (pid)
      REFERENCES bc_placeorigin (id)
);
COMMENT ON TABLE bc_placeorigin IS '籍贯';
COMMENT ON COLUMN bc_placeorigin.pid IS '所隶上级ID';
COMMENT ON COLUMN bc_placeorigin.type_ IS '类型: 0-国家,1-省级,2-地级,3-县级,4-乡级,5-村级';
COMMENT ON COLUMN bc_placeorigin.status_ IS '状态：0-启用中,1-已禁用';
COMMENT ON COLUMN bc_placeorigin.code IS '编码: 行政区划代码和城乡划分代码, 不带后缀0，如广州市荔湾区为"440103"';
COMMENT ON COLUMN bc_placeorigin.name IS '名称: 如"荔湾区"';
COMMENT ON COLUMN bc_placeorigin.pname IS '所属上级的全称，如"广东省/广州市"';
COMMENT ON COLUMN bc_placeorigin.file_date IS '创建时间';
COMMENT ON COLUMN bc_placeorigin.author_id IS '创建人ID';
COMMENT ON COLUMN bc_placeorigin.modifier_id IS '最后修改人ID';
COMMENT ON COLUMN bc_placeorigin.modified_date IS '最后修改时间';
CREATE UNIQUE INDEX BCIDX_PLACEPRIGIN_CODE ON bc_placeorigin (CODE);
CREATE INDEX BCIDX_PLACEPRIGIN_NAME ON bc_placeorigin (name);

-- change
ALTER TABLE bc_placeorigin DROP COLUMN full_code;
ALTER TABLE bc_placeorigin DROP COLUMN desc_;
ALTER TABLE bc_placeorigin ALTER COLUMN code SET NOT NULL;
ALTER TABLE bc_placeorigin ALTER COLUMN code TYPE varchar(12);
ALTER TABLE bc_placeorigin ALTER COLUMN name TYPE varchar(50);
ALTER TABLE bc_placeorigin RENAME full_name TO pname;
ALTER TABLE bc_placeorigin ALTER COLUMN pname DROP NOT NULL;
ALTER TABLE bc_placeorigin ALTER COLUMN pname TYPE varchar(1000);
ALTER TABLE bc_placeorigin ALTER COLUMN type_ DROP DEFAULT;
ALTER TABLE bc_placeorigin ALTER COLUMN status_ DROP DEFAULT;
update bc_placeorigin set modifier_id = author_id where modifier_id is null;
ALTER TABLE bc_placeorigin ALTER COLUMN modifier_id SET NOT NULL;
update bc_placeorigin set modified_date = file_date where modified_date is null;
ALTER TABLE bc_placeorigin ALTER COLUMN modified_date SET NOT NULL;

-- DROP FUNCTION IF EXISTS placeorigin_auto_update_pname();
CREATE OR REPLACE FUNCTION placeorigin_auto_update_pname()
	RETURNS trigger AS 
	$BODY$
	/** 籍贯触发器
	 *	1）所属父类别变动后自动更新 PNAME 列的值
	 *	2）编码或名称变动后自动更新其所有子类别 PNAME 列的值
	 */
	DECLARE
		info text;
		full_name text;
	BEGIN
		--RAISE INFO 'update...%,%,%',TG_OP,OLD.pid,NEW.pid;
		-- 插入或pid变动时：更新自身 PNAME 的值
		IF (TG_OP = 'INSERT' OR (
			TG_OP = 'UPDATE' AND (
				(OLD.pid is null AND NEW.pid is not null)
				OR (OLD.pid is not null AND NEW.pid is null)
				OR NEW.pid != OLD.pid)
		)) THEN
			IF (NEW.pid is not null) THEN
				-- 递归获取祖先节点，并用'/'字符连接名称
				select string_agg(name, '/') into info from (
					WITH RECURSIVE t (id, pid, name, code, depth) AS (
						SELECT id, pid, name, code, 1 AS depth
							FROM bc_placeorigin
							WHERE id = NEW.pid
						UNION ALL
						SELECT s.id, s.pid, s.name, s.code, t.depth + 1 AS depth
							FROM bc_placeorigin s
							INNER JOIN t ON t.pid = s.id
					) SELECT * FROM t ORDER BY depth desc
				) t;

				NEW.PNAME := info;
				RAISE INFO 'update my PNAME: %', 'id='||NEW.id||',pname='||info;
			ELSE
				NEW.PNAME := null;
			END IF;
		END IF;

		-- 全称
		IF (NEW.pid is null) THEN
			full_name := NEW.name;
		ELSE
			full_name := NEW.PNAME || '/' || NEW.name;
		END IF;

		-- 如果name有修改就更新所有后代节点 PNAME 的值
		IF (TG_OP = 'UPDATE' AND NEW.name != OLD.name) THEN
			RAISE INFO 'update children''s PNAME: id=%, % > %', NEW.id, OLD.name, NEW.name;
			WITH RECURSIVE t (id, pid, name, code, pname, depth) AS (
				SELECT id, pid, name, code, full_name, 1 AS depth
					FROM bc_placeorigin
					WHERE pid = NEW.id
				UNION ALL
				SELECT s.id, s.pid, s.name, s.code, t.pname || '/' || t.name as pname, t.depth + 1 AS depth
					FROM bc_placeorigin s
					INNER JOIN t ON t.id = s.pid
			) update bc_placeorigin r set pname=t.pname from t where r.id=t.id;
		END IF;
		return NEW;
	END;
	$BODY$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS placeorigin_trigger ON BC_PLACEORIGIN;
CREATE TRIGGER placeorigin_trigger BEFORE INSERT OR UPDATE ON BC_PLACEORIGIN
	FOR EACH ROW EXECUTE PROCEDURE placeorigin_auto_update_pname();

-- DROP FUNCTION IF EXISTS placeorigin_insert_or_update(varchar, varchar, varchar);
CREATE OR REPLACE FUNCTION placeorigin_insert_or_update(_code varchar, _name varchar, pcode varchar)
	RETURNS integer AS
	$BODY$
	/** 导入籍贯信息
	 *	1）如果已经存在就更新
	 *	2）如果不存在就自动创建
	 */
	DECLARE
		code_exists boolean := false;
		_pid integer;
	BEGIN
		-- 删除_code参数的后缀0，如 440100 将被修改为 4401
		_code := reverse(reverse(_code)::bigint::text);

		-- 判断是否已经存在
		select true into code_exists from bc_placeorigin where code = _code;

		-- 获取父类的id
		if pcode is not null and length(pcode) > 0 then
			pcode := reverse(reverse(pcode)::bigint::text);
			select id into _pid from bc_placeorigin where code = pcode;
			if _pid is null then
				RAISE exception 'can not find parent: pcode=%, code=%, name=%', pcode, _code, _name;
			end if;
		else
			pcode := null;
			_pid := null;
 		end if;
 
		if code_exists then 	-- 更新数据
			RAISE info 'update by code ''%'': name > %, pid > %', _code, _name, _pid;
			-- 更新名称和pid
			update bc_placeorigin set name = _name
				, pid = _pid 
				, modified_date = now()
				, modifier_id = (select id from bc_identity_actor_history where current=true and actor_code='admin')
				where code = _code;
			return 0;
		else					-- 插入新数据
			RAISE info 'insert: code = %, name = %, pid = %', _code, _name, _pid;
			INSERT INTO bc_placeorigin(id, pid, status_, code, name, type_
			  , file_date, author_id, modified_date, modifier_id)
			  select nextval('hibernate_sequence'), _pid, 0, _code, _name
				, length(_code) / 2
				, now(), (select id from bc_identity_actor_history where current=true and actor_code='admin')
				, now(), (select id from bc_identity_actor_history where current=true and actor_code='admin')
				from bc_dual;
			return 1;
		end if; 
	END;
	$BODY$ LANGUAGE plpgsql;

-- 去除编码的后缀0，如440100将变为4401
update bc_placeorigin set code = reverse(reverse(code)::int::text)
	, modified_date = now()
	, modifier_id = (select id from bc_identity_actor_history where current=true and actor_code='admin')
	where code != reverse(reverse(code)::int::text);

-- 全局更新一下pname
update bc_placeorigin set pname = null where pid is null;
update bc_placeorigin set name = name || 'zz' where length(code) = 2;
update bc_placeorigin set name = substring(name from 1 for length(name) - 2) where length(code) = 2

-- test
/*
select placeorigin_insert_or_update('110100', '市辖区','1100');
update bc_placeorigin set pid=null,pname=null where code='110102';
select placeorigin_insert_or_update('110102','西城区','1101');
select * from bc_placeorigin order by code;
*/