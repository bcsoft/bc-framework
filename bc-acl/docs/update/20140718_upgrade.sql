/*
 * BC_ACL_ACTOR 表增加最后修改人、最后修改时间的升级处理
 */

-- 往表添加新的字段
alter table BC_ACL_ACTOR
  add column MODIFIER_ID int;
alter table BC_ACL_ACTOR
  add column MODIFIED_DATE timestamp;
comment on column BC_ACL_ACTOR.MODIFIER_ID is '最后修改人ID';
comment on column BC_ACL_ACTOR.MODIFIED_DATE is '最后修改时间';
alter table BC_ACL_ACTOR
  add constraint BCFK_ACL_ACTOR_MODIFIER foreign key (MODIFIER_ID)
references BC_IDENTITY_ACTOR_HISTORY (ID)
on update restrict on delete restrict;
comment on column BC_ACL_ACTOR.ROLE is '访问权限 : 右边数起第1位控制查阅,第2位控制编辑;0代表无此权限,1代表有此权限';

-- 处理新加字段的值
update bc_acl_doc set MODIFIER_ID = AUTHOR_ID, MODIFIED_DATE = FILE_DATE
where MODIFIER_ID is null;
update BC_ACL_ACTOR a set MODIFIER_ID = d.MODIFIER_ID, MODIFIED_DATE = d.MODIFIED_DATE
from bc_acl_doc d
where d.id = a.pid;

-- 将新加字段的值设为非空
alter table BC_ACL_DOC
  alter column MODIFIER_ID set not null;
alter table BC_ACL_DOC
  alter column MODIFIED_DATE set not null;
alter table BC_ACL_ACTOR
  alter column MODIFIER_ID set not null;
alter table BC_ACL_ACTOR
  alter column MODIFIED_DATE set not null;

/* test
select * from BC_ACL_DOC d
	inner join BC_ACL_ACTOR a on a.pid = d.id
	where doc_type = 'CertCfg'
*/

insert into bc_acl_doc (id, doc_id, doc_type, doc_name, file_date, author_id, modified_date, modifier_id)
  select nextval('hibernate_sequence'), id, 'CertCfg', name, now(), (select id
                                                                     from bc_identity_actor_history
                                                                     where actor_code = 'admin' and current = true),
    now(), (select id
            from bc_identity_actor_history
            where actor_code = 'admin' and current = true)
  from bc_cert_cfg
  where name = '身份证';
with ad(id) as (
  select (select id
          from bc_acl_doc
          where doc_type = 'CertCfg' and doc_id = (select id || ''
                                                   from bc_cert_cfg
                                                   where name = '身份证'))
) insert into bc_acl_actor (pid, aid, role, order_, modified_date, modifier_id)
  select (select id
          from ad), a.id, '01', 0, now(), (select id
                                           from bc_identity_actor_history
                                           where actor_code = 'admin' and current = true)
  from bc_identity_actor a
  where code in ('admin', 'dragon', 'hrj', 'ldx', 'ni')
        and not exists(
    select 0
    from bc_acl_actor aa
    where aa.pid = (select id
                    from ad) and aa.aid = a.id
  );
