-- drop function zero2null(numeric)
create or replace function zero2null(value numeric)
  returns numeric as
$BODY$
/** 将 0 值转换为 null 值
 *	@param value 要转换的值
 */
begin
  if $1 = 0
  then
    return null;
  end if;
  return value;
end;
$BODY$ language plpgsql;

select zero2null(0 :: int), zero2null(0.00), zero2null(10.00)