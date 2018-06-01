-- View: view_bc_email_to_receiver_info

-- DROP VIEW view_bc_email_to_receiver_info;

create or replace view view_bc_email_to_receiver_info as
  select tt.id, array_to_string(array_agg(tt.type_), ',') as type_str,
                array_to_string(array_agg(tt.info), ';')  as receiver_str
  from (
         select t.pid as id, t.type_, array_to_string(array_agg(distinct (case when t.upper_id is null
           then a.name
                                                                          else upper.name end)), ',') as info
         from bc_email_to t
         inner join bc_identity_actor a on a.id = t.receiver_id
         left join bc_identity_actor upper on upper.id = t.upper_id
         group by t.pid, t.type_
       ) tt
  group by tt.id;
comment on view view_bc_email_to_receiver_info is '邮件收件箱收件人信息视图';
comment on column view_bc_email_to_receiver_info.type_str is '邮件发送类型字符串 : 0,1,2';
comment on column view_bc_email_to_receiver_info.receiver_str is '收件人或收件岗位或部门字符串,对应发送类型字符串字段 : 超级管理员;一分公司;开发组';