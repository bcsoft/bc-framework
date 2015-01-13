explain analyze
SELECT l.id, l.start_date, l.end_date, l.cfg_name, l.success
  FROM bc_sd_log l
  where cfg_name like '%营运数据%'
  order by l.start_date desc
  limit 25;

CREATE INDEX bcidx_sd_log_start_date ON bc_sd_log (start_date);
CREATE INDEX bcidx_sd_log_start_name ON bc_sd_log (cfg_name);