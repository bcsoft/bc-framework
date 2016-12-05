drop function getPublicFileId();
create or replace function getPublicFileId()
  returns character varying as
$BODY$
declare
	--定义变量
	fileId varchar(4000);
begin
	with recursive n as(select * from bc_netdisk_file where folder_type = 1 union select f.* from bc_netdisk_file f,n where f.pid=n.id)
	select string_agg(id||'',',') into fileId from n;

	return fileId;
end;
$BODY$ language plpgsql;
