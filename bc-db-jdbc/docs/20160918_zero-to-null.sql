-- drop function zero2null(numeric)
CREATE OR REPLACE FUNCTION zero2null(value numeric)
	RETURNS numeric AS
	$BODY$
	/** 将 0 值转换为 null 值
	 *	@param value 要转换的值
	 */
	BEGIN
		if $1 = 0 then
			return null;
		end if;
		return value;
	END;
	$BODY$ LANGUAGE plpgsql;

select zero2null(0::int), zero2null(0.00), zero2null(10.00)