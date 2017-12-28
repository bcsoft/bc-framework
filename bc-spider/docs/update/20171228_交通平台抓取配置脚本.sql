-- 交通平台账号信息
with p(id) as (select id from bc_option_group where key_ = 'account')
, cfg(key, value, sn, status, desc_) as (
				select 'account.traffic.platform.name', '44011611004329', '1007', 0, '交通信息网账号'
	union select 'account.traffic.platform.password', '124081', '1008', 0, '交通信息网账号密码'
)
insert into bc_option_item (id, pid, key_, value_, order_, status_, desc_)
	select nextval('core_sequence'), (select id from p), c.key, c.value, c.sn, c.status, c.desc_
	from cfg c where not exists (select 0 from bc_option_item i where i.key_ = c.key and i.pid = (select id from p));
-- select * from bc_option_item where key_ in ('account.traffic.platform.name', 'account.traffic.platform.password');

-- 交通平台资源配置
with p(id) as (
	select id from bc_identity_resource
	where name = '网络工具' and belong = (select id from bc_identity_resource where name = '友情链接')
)
, cfg(type, sn, name, url, iconclass) as (
		select 2, '071010-01', '交通平台违法查询(公开)', '/bc/spider/common?code=traffic_platform_public_inquire', 'i0800'
		union select 2, '071010-02', '交通平台违法查询(企业)', '/bc/spider/common?code=traffic_platform_company_inquire', 'i0800'
)
insert into bc_identity_resource (status_, inner_, type_, order_, name, url, iconclass, belong, id)
	select 0, false, c.type, c.sn, c.name, c.url, c.iconclass, (select id from p), nextval('core_sequence')
	from cfg c
	where not exists (select 0 from bc_identity_resource s where s.name = c.name::text and s.belong = (select id from p));
--select * from bc_identity_resource where name in ('交通平台违法查询(公开)', '交通平台违法查询(企业)');

-- 交通平台资源配置与角色关联配置
with p(id) as (
	select id from bc_identity_resource
	where name = '网络工具' and belong = (select id from bc_identity_resource where name = '友情链接')
)
, cfg(resource_name, role_codes) as (
	select '交通平台违法查询(公开)', array['BS_INFRACT_TRAFFIC']
	union select '交通平台违法查询(企业)', array['BS_INFRACT_TRAFFIC']
)
insert into bc_identity_role_resource (rid, sid)
	select r.id, s.id
	from bc_identity_role r, bc_identity_resource s, (select unnest(role_codes) role_code, resource_name from cfg) c
	where r.code = c.role_code and s.name = c.resource_name and s.belong in (select id from p)
	and not exists (select 0 from bc_identity_role_resource rs where rs.rid = r.id and rs.sid = s.id);

-- 交通平台交通违法查询UI模板配置
with type(id) as (
	select id from bc_template_type where name = '自定义文本'
)
,cfg(sn, code, subject, inner_, status, version, type_id, size, formatted, uid, content) as (
	select '2104'::text, 'traffic_platform_public_inquire'::text, '交通平台交通违法公开查询UI模板'::text, true, 0, 1,
	(select id from type), 0, false, 'Template.mt.9512'::text
	, '<#if (notLogin?? && notLogin == true && zs > 0)>
			<span style="padding:4px;">该机动车非现场未处理违法记录共计${zs}条。其中，牌证发放地违法记录${bd}条，' ||
			'本省异地违法记录${bs}条，跨省违法${ws}条。</span>
		<#elseif (_d?? && _d?size > 0)>
			<table cellspacing="0" cellpadding="0">
				<colgroup>
					<col style="width: 2em;">
					<col style="width: 6em;">
					<col style="width: 12em;">
					<col style="width: 12em;">
					<col style="width: 15em;">
					<col style="width: 5em;">
					<col style="width: 5em;">
					<col style="width: 8.5em;">
					<col style="width: 5em;">
					<col style="width: 5em;">
					<col style="width: 12em;">
				</colgroup>
				<thead>
					<tr class="ui-state-default autowrap">
						<th class="ui-widget-content">&nbsp;</th>
						<td class="ui-widget-content">车牌</td>
						<td class="ui-widget-content">违法时间</td>
						<td class="ui-widget-content">违法地点</td>
						<td class="ui-widget-content">违法行为</td>
						<td class="ui-widget-content">违法代码</td>
						<td class="ui-widget-content">处理状态</td>
						<td class="ui-widget-content">确认时间</td>
						<td class="ui-widget-content">罚款金额</td>
						<td class="ui-widget-content">本次交通违法记分</td>
						<td class="ui-widget-content">决定书编号</td>
					</tr>
				</thead>
				<tbody>
					<#list _d as m>
					<tr class="ui-widget-content autowrap">
						<th class="ui-widget-content">${m_index+1}</th>
						<td class="ui-widget-content">${m.hphm!}</td>
						<td class="ui-widget-content">${m.wfsj!}</td>
						<td class="ui-widget-content">${m.wfdz!}</td>
						<td class="ui-widget-content">${m.wfms!}</td>
						<td class="ui-widget-content">${m.wfxw!}</td>
						<td class="ui-widget-content">${m.clbjStr}</td>
						<td class="ui-widget-content">${m.clsj!}</td>
						<td class="ui-widget-content">${m.fkje!}</td>
						<td class="ui-widget-content">${m.wfjfs!}</td>
						<td class="ui-widget-content">${m.jdsbh!}</td>
					</tr>
					</#list>
				</tbody>
			</table>
		<#else>
			<span style="padding:4px;">无交通违法记录！</span>
		</#if>'::text
		-- todo: 交通平台交通违法企业查询UI模板配置
)
insert into bc_template(id, order_, code, subject, inner_, status_, version_, type_id, size_, formatted, uid_,
													content, file_date, author_id, modified_date, modifier_id)
	select nextval('core_sequence'), c.*
		, now(), (select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
		, now(), (select id from bc_identity_actor_history where actor_code = 'admin' and current = true)
	from cfg c where not exists (select 0 from bc_template where code = c.code);
-- select * from bc_template where code in ('traffic_platform_public_inquire');

-- 交通平台抓取配置
with cfg(status, sn, code, cfg) as (
	select 0, 5, 'traffic_platform_public_inquire'::text
		,'{"group": "traffic_platform_public_inquire",
			"method": "post",
			"title": "交通平台交通违法公开查询",
			"description": "此为交通平台非登录状态下机动车违法简易信息的快捷查询，每次点击查询前都需先点击验证码图片重新获取' ||
			 '新验证码！你也可以<a href=''http://gd.122.gov.cn/views/inquiry.html''>点击这里</a>打开交通平台的原始查询页面！",
			"url": "http://gd.122.gov.cn/m/publicquery/vio",
			"type": "map",
			"auto": false,
			"successExpression": "#root.containsKey(''code'') and (#root.get(''code'') eq 200)",
			"resultExpression": "#root.get(''data'').get(''content'')",
			"parser": "tpl:traffic_platform_public_inquire",
			"formData": [
				{"label": "车牌号码", "name": "hphm", "validate": "required", "placeholder": "格式为:粤A12345","encode":"UTF-8"},
				{"label": "号牌种类", "name": "hpzl", "type": "select", "options": [
					{"value": "", "label": ""},
					{"value": "02", "label": "小型汽车", "selected": true},
					{"value": "10", "label": "领馆摩托车"},
					{"value": "11", "label": "境外摩托车"},
					{"value": "12", "label": "外籍摩托车"},
					{"value": "13", "label": "低速车"},
					{"value": "14", "label": "拖拉机"},
					{"value": "15", "label": "挂车"},
					{"value": "16", "label": "教练汽车"},
					{"value": "17", "label": "教练摩托车"},
					{"value": "18", "label": "试验汽车"},
					{"value": "19", "label": "试验摩托车"},
					{"value": "20", "label": "临时入境汽车"},
					{"value": "21", "label": "临时入境摩托车"},
					{"value": "22", "label": "临时行驶车"},
					{"value": "23", "label": "警用汽车"},
					{"value": "24", "label": "警用摩托"},
					{"value": "25", "label": "原农机号牌"},
					{"value": "26", "label": "香港入出境车"},
					{"value": "27", "label": "澳门入出境车"},
					{"value": "31", "label": "武警号牌"},
					{"value": "32", "label": "军队号牌"},
					{"value": "99", "label": "其他号牌"},
					{"value": "01", "label": "大型汽车"},
					{"value": "03", "label": "使馆汽车"},
					{"value": "04", "label": "领馆汽车"},
					{"value": "05", "label": "境外汽车"},
					{"value": "06", "label": "外籍汽车"},
					{"value": "07", "label": "普通摩托车"},
					{"value": "08", "label": "轻便摩托车"},
					{"value": "09", "label": "使馆摩托车"}
				]},
				{"label": "发动机号", "name": "fdjh", "validate": "required", "title": "只需输入发动机号的后六位", ' ||
				 '"placeholder": "后六位,必填"},
				{"label": "验证码", "name": "captcha", "validate": "required", "placeholder": "必填", ' ||
				 '"captcha": {"url": "http://gd.122.gov.cn/captcha", "auto": false}}
			],
			"headers": {
				"Referer": "http://gd.122.gov.cn/views/inquiry.html",
				"X-Requested-With": "XMLHttpRequest",
				"User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) ' ||
				 'Chrome/27.0.1453.110 Safari/537.36"
			},"width": 600,"height": 350	
		}'::text
		-- todo：交通平台企业查询抓取配置
)
insert into bc_spider_cfg(id, status_, order_, code, cfg, author_id, file_date, modifier_id, modified_date) 
	select nextval('core_sequence'), c.*
	, (select id from bc_identity_actor_history where actor_code = 'admin' and current = true), now()
	, (select id from bc_identity_actor_history where actor_code = 'admin' and current = true), now()
	from cfg c where not exists (select 0 from bc_spider_cfg where code = c.code);
-- select * from bc_spider_cfg where code in ('traffic_platform_public_inquire');