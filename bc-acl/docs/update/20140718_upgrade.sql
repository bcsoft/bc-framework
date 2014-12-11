/*
 * BC_ACL_ACTOR 表增加最后修改人、最后修改时间的升级处理
 */
 
-- 往表添加新的字段
ALTER TABLE BC_ACL_ACTOR ADD COLUMN MODIFIER_ID INT;
ALTER TABLE BC_ACL_ACTOR ADD COLUMN MODIFIED_DATE TIMESTAMP;
COMMENT ON COLUMN BC_ACL_ACTOR.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BC_ACL_ACTOR.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BC_ACL_ACTOR
	ADD CONSTRAINT BCFK_ACL_ACTOR_MODIFIER FOREIGN KEY (MODIFIER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
COMMENT ON COLUMN BC_ACL_ACTOR.ROLE IS '访问权限 : 右边数起第1位控制查阅,第2位控制编辑;0代表无此权限,1代表有此权限';

-- 处理新加字段的值
update bc_acl_doc set MODIFIER_ID = AUTHOR_ID, MODIFIED_DATE = FILE_DATE where MODIFIER_ID is null;
update BC_ACL_ACTOR a set MODIFIER_ID = d.MODIFIER_ID, MODIFIED_DATE = d.MODIFIED_DATE
	from bc_acl_doc d where d.id = a.pid;

-- 将新加字段的值设为非空
ALTER TABLE BC_ACL_DOC ALTER COLUMN MODIFIER_ID SET NOT NULL;
ALTER TABLE BC_ACL_DOC ALTER COLUMN MODIFIED_DATE SET NOT NULL;
ALTER TABLE BC_ACL_ACTOR ALTER COLUMN MODIFIER_ID SET NOT NULL;
ALTER TABLE BC_ACL_ACTOR ALTER COLUMN MODIFIED_DATE SET NOT NULL;

/* test
select * from BC_ACL_DOC d
	inner join BC_ACL_ACTOR a on a.pid = d.id
	where doc_type = 'CertCfg'
*/

INSERT INTO bc_acl_doc(id, doc_id, doc_type, doc_name, file_date, author_id, modified_date, modifier_id)
	select nextval('hibernate_sequence'), id, 'CertCfg', name
	, now(), (select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
	, now(), (select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
	from bc_cert_cfg 
	where name = '身份证';
with ad(id) as (
	select (select id from bc_acl_doc where doc_type = 'CertCfg' and doc_id = (select id||'' from bc_cert_cfg where name = '身份证'))
)INSERT INTO bc_acl_actor(pid, aid, role, order_, modified_date, modifier_id)
	select (select id from ad), a.id, '01', 0
	, now(), (select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
	from bc_identity_actor a 
	where code in ('admin', 'dragon', 'hrj', 'ldx', 'ni')
	and not exists(
		select 0 from bc_acl_actor aa where aa.pid = (select id from ad) and aa.aid = a.id
	);
