-- 修复部分用户打开视图报错的问题
-- DROP FUNCTION getusersharfileid(integer);
CREATE OR REPLACE FUNCTION getusersharfileid(uid integer) RETURNS character varying AS
$BODY$
	DECLARE
		fileId varchar(4000);
	BEGIN
		with recursive n as(
			select * from bc_netdisk_file where id in (select pid from bc_netdisk_share where aid = uid)
			union
			select f.* from bc_netdisk_file f,n where f.pid = n.id
		)
		select string_agg(id || '',',') into fileId from n;
		return fileId;
	END;
$BODY$ LANGUAGE plpgsql;


-- DROP FUNCTION getusersharfileid2all(integer);
CREATE OR REPLACE FUNCTION getusersharfileid2all(uid integer) RETURNS character varying AS
$BODY$
	DECLARE
		fileId varchar(4000);
	BEGIN
		with recursive n as(
			select * from bc_netdisk_file
				where id in (select pid from bc_netdisk_share where aid = uid) 
				or id in (select id from bc_netdisk_file where author_id in (select id from bc_identity_actor_history where actor_id = uid))
			union
			select f.* from bc_netdisk_file f,n where f.pid = n.id
		)
		select string_agg(id || '',',') into fileId from n;
		return fileId;
	END;
$BODY$ LANGUAGE plpgsql;
