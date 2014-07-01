-- 往表添加新的字段
ALTER TABLE BC_FORM ADD COLUMN VER_ VARCHAR(20);
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
DROP INDEX IF EXISTS formidx_form_type_pid_code;
DROP INDEX IF EXISTS BCUK_FORM_PARENT;
CREATE UNIQUE INDEX BCUK_FORM_PARENT ON bc_form(type_, code, pid, ver_);

-- 历史数据统一版本号为1.0
update bc_form set ver_ = '1.0' where ver_ is null;
update bc_form set tpl_ = 'DEFAULT_CERT_FORM' where tpl_ <> 'DEFAULT_CERT_FORM';

-- 更改级联删除
ALTER TABLE bc_form_field DROP CONSTRAINT if exists bcfk_form_field_pid;
ALTER TABLE bc_form_field ADD CONSTRAINT bcfk_form_field_pid FOREIGN KEY (pid)
	REFERENCES bc_form (id) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE bc_form_field_log DROP CONSTRAINT if exists bc_form_field_log_pid;
ALTER TABLE bc_form_field_log DROP CONSTRAINT if exists bcfk_form_field_log_pid;
ALTER TABLE bc_form_field_log ADD CONSTRAINT bcfk_form_field_log_pid FOREIGN KEY (pid)
	REFERENCES bc_form_field (id) ON UPDATE RESTRICT ON DELETE CASCADE;

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