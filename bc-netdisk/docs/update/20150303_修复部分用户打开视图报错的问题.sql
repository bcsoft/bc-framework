-- 修复部分用户打开视图报错的问题
drop function getusersharfileid( integer );
create or replace function getusersharfileid(uid integer) returns text as
$BODY$
declare
  fileId text;
begin
  with recursive n as (
    select *
    from bc_netdisk_file
    where id in (select pid
                 from bc_netdisk_share
                 where aid = $1)
    union
    select f.*
    from bc_netdisk_file f, n
    where f.pid = n.id
  )
  select string_agg(id || '', ',') into fileId
  from n;
  return fileId;
end;
$BODY$ language plpgsql;


drop function getusersharfileid2all( integer );
create or replace function getusersharfileid2all(uid integer) returns text as
$BODY$
declare
  fileId text;
begin
  with recursive n as (
    select *
    from bc_netdisk_file
    where id in (select pid
                 from bc_netdisk_share
                 where aid = $1)
          or id in (select id
                    from bc_netdisk_file
                    where author_id in (select id
                                        from bc_identity_actor_history
                                        where actor_id = $1))
    union
    select f.*
    from bc_netdisk_file f, n
    where f.pid = n.id
  )
  select string_agg(id || '', ',') into fileId
  from n;
  return fileId;
end;
$BODY$ language plpgsql;
