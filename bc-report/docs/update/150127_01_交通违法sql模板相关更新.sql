
------------------------------| 存储函数：统计司机交通违法宗数 |------------------------------
-- DROP FUNCTION count_traffic_process_by_car_driver_date(integer, integer, integer, text);

CREATE OR REPLACE FUNCTION count_traffic_process_by_car_driver_date(car_id integer, driver_id integer, mid integer, group_date text)
  RETURNS SETOF integer AS
$BODY$
	/** 统计交通违法宗数
	 *  @param car_id 车辆Id
	 *  @param driver_id 司机Id
	 *  @param mid bc_wf_module_relation表的模块Id
	 *  @param group_date 分组条件（'year', 'month', 'day'）
	 *  	group_date == 'year' 按年份分组
	 *  	group_date == 'month' 按年，月分组
	 *
	 *    例如：2001-09-29，2002-09-29，2002-10-30
	 *    group_date == 'year' 返回 1和2 再由前两个参数所在的违法日期决定取用 2001年还是2002年
	 *    group_date == 'month' 返回 1和1和1 再由前两个参数所在的违法日期决定取用 9月还是10月
	 */
	DECLARE
		sql text;
	begin
		sql := '
			-- 获得事发年月
			with m (year_, month_) as (
				select date_part(''year'', b.happen_date), date_part(''month'', b.happen_date)
					from bs_case_base b
					where id = $1
			)

			select count(date_part($2, b.happen_date))::int
				from bs_case_base b
				inner join bs_case_infract_traffic t on t.id = b.id -- 交通违法
				inner join bc_wf_module_relation m on m.mid = b.id -- 关联流程
				inner join act_hi_procinst p on p.id_ = m.pid
					where m.mtype = ''Case4InfractTraffic''';

					-- 通过车辆Id或司机Id过滤条件
					if $1 is not null then
						sql := sql || 'and b.car_id = ' || $1;
					end if;
					if $2 is not null then
						sql := sql || 'and b.driver_id = ' || $2;
					end if;

					-- 对应模块的违章流程的日期过滤
					if $4 = 'year' then
						sql := sql || 'and date_part(''year'', b.happen_date) = (select year_ from m)';
					ELSIF $4 = 'month' then
						sql := sql || '
						and date_part(''year'', b.happen_date) = (select year_ from m)
						and date_part(''month'', b.happen_date) = (select month_ from m)';
					end if;
			return QUERY EXECUTE sql using $3, $4;
	end;
$BODY$
  LANGUAGE plpgsql VOLATILE


------------------------------| 相关更新 |------------------------------
-- 更新 “年度统计（人）”
update bc_report_template as r set config = t.config
	from (
		-- 查找出 统计报表
		SELECT id, regexp_replace
		    (
		        config,
				'getnewprocessglobalvalue4midmtypekey\(t.id,''Case4InfractTraffic'',''happenNumber''\)',
				'count_traffic_process_by_car_driver_date(null, t.driver_id, t.id, ''year'')'
			) as config
			FROM bc_report_template
				where code in (
					'cartraffichandle.list',
					'cartraffichandle.firstbranchcompany.list',
					'cartraffichandle.secondbranchcompany.list',
					'cartraffichandle.thridbranchcompany.list',
					'cartraffichandle.fourthbranchcompany.list'
				)
	) t
	where t.id = r.id;

-- 更新 “月度统计（车）”
update bc_report_template as r set config = t.config
	from (
		-- 查找出 统计报表
		SELECT id, regexp_replace
		    (
		        config,
				'getnewprocessglobalvalue4midmtypekey\(t.id,''Case4InfractTraffic'',''case4InfractTrafficr_monthCountByCar''\)',
				'count_traffic_process_by_car_driver_date(t.car_id, null, t.id, ''month'')'
			) as config
			FROM bc_report_template
				where code in (
					'cartraffichandle.list',
					'cartraffichandle.firstbranchcompany.list',
					'cartraffichandle.secondbranchcompany.list',
					'cartraffichandle.thridbranchcompany.list',
					'cartraffichandle.fourthbranchcompany.list'
				)
	) t
	where t.id = r.id;