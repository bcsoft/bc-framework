-- 清空数据
delete from bc_option_item where pid in (select id from bc_option_group where key_ = 'search.limit');
delete from bc_option_group where key_ = 'search.limit';

-- 单据样式 select * from bc_option_item where pid = (select id from bc_option_group where key_ = 'financial.receipt');
insert into bc_option_group(id, key_, value_, order_, icon)
	values (nextval('CORE_SEQUENCE'), 'search.limit', '查询限制', '5222', NULL);
insert into bc_option_item(id, pid, status_, key_, value_, order_)
	values (nextval('CORE_SEQUENCE'), (select id from bc_option_group where key_ = 'search.limit')
	, 0, 'fuzzy.max.results', '25', '5223');

