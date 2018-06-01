-- Function: template_get_acl_by_id_actorid(integer, text)

drop function if exists template_get_acl_by_id_actorid( integer, text );

create or replace function template_get_acl_by_id_actorid(id integer, actor_code text)
  returns setof text as
$BODY$
/** 通过模板ID(bc_template.id)和参与者CODE(bc_identity_actor.code)获得模板的ACL权限
 *  @param id 模板ID
 *  @param actor_code 参与者code
 *  return 该模板的ACL
 */
begin
  return query
  -- 查找该模板所有ACL配置
  with tc_acl(acl) as (
    select category_get_full_acl_by_id_actorid(cid, 'xzc')
    from bc_template_template_category
    where tid = $1
  )
  -- 所有ACL进行'与'关系合并
  select bit_and(acl :: bit(2)) :: text
  from tc_acl;
end;
$BODY$
language plpgsql volatile;
