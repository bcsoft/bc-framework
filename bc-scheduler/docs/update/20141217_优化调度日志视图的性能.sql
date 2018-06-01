explain analyze
select l.id, l.start_date, l.end_date, l.cfg_name, l.success
from bc_sd_log l
where cfg_name like '%营运数据%'
order by l.start_date desc
limit 25;

create index bcidx_sd_log_start_date on bc_sd_log (start_date);
create index bcidx_sd_log_start_name on bc_sd_log (cfg_name);