-- Function: category_belong()

-- DROP FUNCTION category_belong();

CREATE OR REPLACE FUNCTION category_belong()
  RETURNS TABLE(id integer, pid integer, status_ integer, code text, name_ text, category text, sn text) AS
$BODY$
	/** 
   * 分类的所属分类信息
	 */
	begin
		return query
		with RECURSIVE category (id, pid, status_, code, name_, category, sn) as (
			select c.id, c.pid, c.status_, c.code::text, c.name_::text, c.name_::text, c.sn::text
				from bc_category c
				where c.pid is null
			union all
			select cc.id, cc.pid, cc.status_, cc.code::text, cc.name_::text, 
				COALESCE(p.category, p.name_)||'/'||cc.name_, p.sn||cc.sn
				from bc_category cc
				inner join category p on p.id = cc.pid
		) 
		select * 
			from category order by sn asc;
	end;
$BODY$
  LANGUAGE plpgsql VOLATILE