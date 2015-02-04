-- 工作日模块自定义函数
-- drop function workday__calculate_default_working_days(date, date, int)
CREATE OR REPLACE FUNCTION workday__calculate_default_working_days(from_date date, to_date date, workdays_every_week int default 5)
returns int AS
$BODY$
	/** 
	 * 获取指定日期段对应的默认工作日数
	 * from_date 开始日期
	 * to_date 结束日期，必须大于或等于开始日期
	 * workdays_every_week 每周默认工作日数，如5代表5天工作制(周一到周五)、6代表6天工作制(周一到周六)
	 */
	DECLARE
		_days int;
	BEGIN
		-- 每周默认工作日数：最小1最大7
		select least(greatest($3, 1), 7) into $3;
		--raise info 'workdays_every_week=%', $3;

		-- 常规工作日的计算: 剔除周末、不考虑节假日和调休
		select 
			-- (结束日期所在周的周末 - 开始日期对上一周的周末) / 7 * 每周默认工作日数
			(($2 + 7 - date_part('isodow', $2)::int) - ($1 - date_part('isodow', $1)::int)) / 7 * $3
			-- 减 "开始日期对上一周的周末"到"开始日期"间的工作日数
			- LEAST(date_part('isodow', $1) - 1, $3) 
			-- 减 "结束日期"到"结束日期所在周的周末"间的工作日数
			- greatest($3 - date_part('isodow', $2), 0)
			into _days;
		raise info 'days=%', _days;

		-- 返回
		return _days;
	END;
$BODY$ LANGUAGE plpgsql;


-- drop function workday__calculate_real_working_days(date, date, int)
CREATE OR REPLACE FUNCTION workday__calculate_real_working_days(from_date date, to_date date, workdays_every_week int default 5)
returns int AS
$BODY$
	/** 
	 * 获取指定日期段对应的实际工作日数
	 * from_date 开始日期
	 * to_date 结束日期，必须大于或等于开始日期
	 * workdays_every_week 每周默认工作日数，如5代表5天工作制(周一到周五)、6代表6天工作制(周一到周六)
	 */
	DECLARE
		_days int;
	BEGIN
		-- 每周默认工作日数：最小1最大7
		select least(greatest($3, 1), 7) into $3;
		--raise info 'workdays_every_week=%', $3;

		-- 实际工作日的计算: 剔除周末、考虑节假日和调休
		select 
			-- 常规工作日数: 剔除周末、不考虑节假日和调休
			workday__calculate_default_working_days($1, $2)
			-- 减节假日、加调休日
			+ coalesce((
				select sum((
					case when w.dayoff then 
						-1 * workday__calculate_default_working_days(w.from_date, w.to_date)
					else
						w.to_date - w.from_date + 1 - workday__calculate_default_working_days(w.from_date, w.to_date)
					end)) from (select * from BC_WORKDAY w0 where w0.from_date >= $1 and w0.to_date <= $2) w
			), 0) into _days;
		--raise info 'days=%', _days;

		-- 返回
		return _days;
	END;
$BODY$ LANGUAGE plpgsql;

select workday__calculate_default_working_days(date '2015-01-19', date '2015-01-22');
select workday__calculate_real_working_days(date '2015-01-19', date '2015-01-22');

/*
-- 9>8
select workday__calculate_default_working_days(date '2014-12-30', date '2015-01-10');
select workday__calculate_real_working_days(date '2014-12-30', date '2015-01-10');
-- 7>6
select workday__calculate_default_working_days(date '2015-01-01', date '2015-01-10');
select workday__calculate_real_working_days(date '2015-01-01', date '2015-01-10');
-- 0>1
select workday__calculate_default_working_days(date '2015-01-04', date '2015-01-04');
select workday__calculate_real_working_days(date '2015-01-04', date '2015-01-04');
*/