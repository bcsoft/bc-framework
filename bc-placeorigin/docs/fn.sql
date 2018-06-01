-- DROP FUNCTION getplaceoriginbycertidentity(character varying);
create or replace function getplaceoriginbycertidentity(cert_identity character varying)
  returns character varying as
/**
 * 获取身份证的籍贯信息
 * 身份证前2位为省级、前4位为地级、前6位为县级，
 * 按6、4、2的顺序进行匹配，获取最接近那一条作为籍贯信息
 */
$BODY$
declare
  _c      character varying;
  _c6     character varying;
  _c4     character varying;
  _c2     character varying;
  _origin character varying;
begin
  -- 身份证号长度小于6就直接返回空
  if $1 isnull or length($1) < 6
  then
    return '';
  end if;

  _c = trim($1);
  _c6 = substring(_c from 1 for 6); -- 县级
  _c4 = substring(_c from 1 for 4); -- 地级
  _c2 = substring(_c from 1 for 2); -- 省级
  select full_name into _origin
  from bc_placeorigin
  where code in (_c6, _c4, _c2)
  order by code desc limit 1;
  return coalesce(_origin, '');
end;
$BODY$ language plpgsql;

select getplaceoriginbycertidentity('412822197302033097');
--select * from bc_placeorigin where code like '41%';