drop table if exists bc_log_ip_mac;
create table bc_log_ip_mac (
  id serial not null primary key,
  start timestamp not null,
  ip varchar(15) not null,
  mac varchar(17) not null,
  mark varchar(100),
  constraint bcpk_log_ip_mac unique (ip, mac, start)
);
comment on table bc_log_ip_mac is 'IP-MAC地址历史对应表';
comment on column bc_log_ip_mac.ip is 'IP地址';
comment on column bc_log_ip_mac.mac is 'MAC地址';
comment on column bc_log_ip_mac.start is 'IP-MAC地址的对应时间';
comment on column bc_log_ip_mac.mark is '网卡描述';