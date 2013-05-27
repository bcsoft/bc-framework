DROP TABLE IF EXISTS BC_SPIDER_CFG;

-- 网络抓取配置
CREATE TABLE BC_SPIDER_CFG(
	ID INT NOT NULL,
	STATUS_ INT DEFAULT 0 NOT NULL,
	ORDER_ VARCHAR(100),
	CODE VARCHAR(255) NOT NULL,
	CFG TEXT NOT NULL,
	AUTHOR_ID INT NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	MODIFIER_ID INT NOT NULL,
	MODIFIED_DATE TIMESTAMP NOT NULL,
	CONSTRAINT BCPK_SPIDER_CFG PRIMARY KEY (ID),
	CONSTRAINT BCUK_SPIDER_CFG_CODE UNIQUE (CODE)
);
COMMENT ON TABLE BC_SPIDER_CFG IS '网络抓取配置';
COMMENT ON COLUMN BC_SPIDER_CFG.ID IS 'ID';
COMMENT ON COLUMN BC_SPIDER_CFG.STATUS_ IS '状态 : 0-正常,1-已禁用';
COMMENT ON COLUMN BC_SPIDER_CFG.ORDER_ IS '排序号';
COMMENT ON COLUMN BC_SPIDER_CFG.CODE IS '编码';
COMMENT ON COLUMN BC_SPIDER_CFG.CFG IS '配置';
COMMENT ON COLUMN BC_SPIDER_CFG.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BC_SPIDER_CFG.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BC_SPIDER_CFG.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BC_SPIDER_CFG.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BC_SPIDER_CFG
	ADD CONSTRAINT BCFK_SPIDER_CFG_AUTHOR FOREIGN KEY (AUTHOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE BC_SPIDER_CFG
	ADD CONSTRAINT BCFK_SPIDER_CFG_MODIFIER FOREIGN KEY (MODIFIER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID)
	ON UPDATE RESTRICT ON DELETE RESTRICT;

-- 资源：友情链接/网络工具
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
    select NEXTVAL('CORE_SEQUENCE'), 0, false, 1, m.id, '071000','网络工具', null, 'i0100' 
    from BC_IDENTITY_RESOURCE m 
	where m.order_='070000'
	and not exists (select 0 from BC_IDENTITY_RESOURCE m1 where m1.NAME=m.NAME);
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2
	,(select id from BC_IDENTITY_RESOURCE where order_='071000')
	, '071001','车管所违法查询(公开)', '/bc/spider/common?code=wscgs_logout_jtwf', 'i0100' 
	from bc_dual 
	where not exists (select 0 from BC_IDENTITY_RESOURCE where order_='071001');
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2
	,(select id from BC_IDENTITY_RESOURCE where order_='071000')
	, '071002','车管所违法查询(企业)', '/bc/spider/common?code=wscgs_login_jtwf', 'i0100' 
	from bc_dual 
	where not exists (select 0 from BC_IDENTITY_RESOURCE where order_='071002');
insert into BC_IDENTITY_RESOURCE (ID,STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select NEXTVAL('CORE_SEQUENCE'), 0, false, 2
	,(select id from BC_IDENTITY_RESOURCE where order_='071000')
	, '071099','车管所登录', '/bc/spider/common?code=wscgs_login', 'i0100' 
	from bc_dual 
	where not exists (select 0 from BC_IDENTITY_RESOURCE where order_='071099');

-- 角色与资源关系配置
-- 	普通用户、超级管理员
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m 
	where r.code in ('BC_COMMON')
	and m.type_ >= 1 and m.order_ in ('071001','071002','071099')
	and not exists (select 0 from BC_IDENTITY_ROLE_RESOURCE rr where rr.RID=r.id and rr.SID=m.id);

-- 初始数据：网上车管所交通违法查询（公开,非登录状态下的查询）
-- delete from bc_spider_cfg where code='wscgs_logout_jtwf';
INSERT INTO bc_spider_cfg(id, status_, order_, code, file_date, author_id, modified_date, modifier_id, cfg)
	select NEXTVAL('CORE_SEQUENCE'), 0, 01, 'wscgs_logout_jtwf'
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, ''
	from bc_dual
	where not exists (select 0 from bc_spider_cfg c where c.code='wscgs_logout_jtwf');
update bc_spider_cfg set cfg='{"group": "wscgs_logout_jtwf",
	"method": "post",
	"title": "网上车管所交通违法公开查询",
	"description": "此为网上车管所非登录状态下机动车违法信息的快捷查询，在这里只需输入车牌号码即可，无需输入发动机号、机架号、验证码等繁琐的信息！你也可以<a href=''http://www.gzjd.gov.cn/cgs/html/includes/submain_visitor.html''>点击这里</a>打开网上车管所的原始查询页面！",
	"url": "http://www.gzjd.gov.cn/cgs/violation/getVisitorVioList.htm",
	"type": "map",
	"auto": false,
	"successExpression": "#root.containsKey(''returnCode'')",
	"resultExpression": "#root.get(''data'')",
	"parser": "tpl:wscgs_logout_jtwf",
	"formData": [
		{"label": "车牌号码", "name": "platenum", "validate": "required", "placeholder": "格式为:粤A12345", "value": "粤AS3H54"},
		{"label": "号牌种类", "name": "platenumtype", "type": "select", "options": [
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
		{"label": "发动机号", "name": "engineno", "title": "只需输入发动机号的后四位", "placeholder": "后四位,可不填"},
		{"label": "车架号", "name": "vehicleidnum", "title": "只需输入车架号的后六位", "placeholder": "后六位,可不填"},
		{"label": "验证码", "name": "captchaId", "placeholder": "可不填", "captcha": {"url": "http://www.gzjd.gov.cn/cgs/captcha.jpg", "auto": false}}
	],
	"headers": {
		"Host": "www.gzjd.gov.cn",
		"Origin": "http://www.gzjd.gov.cn",
		"Referer": "http://www.gzjd.gov.cn/cgs/html/violation/visitor_violation.shtml",
		"X-Requested-With": "XMLHttpRequest",
		"User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31"
	}
}' where code='wscgs_logout_jtwf';

-- 初始数据：网上车管所的登录
-- delete from bc_spider_cfg where code='wscgs_login';
INSERT INTO bc_spider_cfg(id, status_, order_, code, file_date, author_id, modified_date, modifier_id, cfg)
	select NEXTVAL('CORE_SEQUENCE'), 0, 02, 'wscgs_login'
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, ''
	from bc_dual
	where not exists (select 0 from bc_spider_cfg c where c.code='wscgs_login');
update bc_spider_cfg set cfg='{"group": "wscgs_login",
	"method": "post",
	"title": "网上车管所登录",
	"description": "此为登录网上车管所的快捷方式！你也可以<a href=''http://www.gzjd.gov.cn/cgs/html/hall/index.html''>点击这里</a>打开网上车管所的原始登录页面！",
	"url": "http://www.gzjd.gov.cn/cgs/j_ajax_security_check",
	"type": "text",
	"auto": false,
	"successExpression": "#root==''\"1\"''",
	"resultExpression": "#root",
	"formData": [
		{"label": "用户名", "name": "j_username", "type": "select", "validate": "required", "options": [
			{"value": "", "label": ""},
			{"value": "72197317-9", "label": "宝城(72197317-9)"},
			{"value": "19068001-8", "label": "广发(19068001-8)", "selected": true}
		]},
		{"label": "密码", "name": "j_password", "type": "select", "validate": "required", "options": [
			{"value": "", "label": ""},
			{"value": "818000", "label": "****(宝城72197317-9)"},
			{"value": "818000", "label": "****(广发19068001-8)", "selected": true}
		]},
		{"label": "验证码", "name": "captchaId", "placeholder": "可不填", "captcha": {"url": "http://www.gzjd.gov.cn/cgs/captcha.jpg", "auto": false}}
	]
}' where code='wscgs_login';

-- 初始数据：网上车管所交通违法查询(企业登录,登录状态下的查询)
-- delete from bc_spider_cfg where code='wscgs_login_jtwf';
INSERT INTO bc_spider_cfg(id, status_, order_, code, file_date, author_id, modified_date, modifier_id, cfg)
	select NEXTVAL('CORE_SEQUENCE'), 0, 03, 'wscgs_login_jtwf'
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, now(), (select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	, ''
	from bc_dual
	where not exists (select 0 from bc_spider_cfg c where c.code='wscgs_login_jtwf');
update bc_spider_cfg set cfg='{"group": "wscgs_login_jtwf",
	"method": "post",
	"title": "网上车管所交通违法登录后查询(宝城、广发)",
	"description": "此为网上车管所登录状态下机动车违法信息的快捷查询，在这里只需输入帐号密码即可，无需输入验证码等繁琐的信息！你也可以<a href=''http://www.gzjd.gov.cn/cgs/html/hall/index.html''>点击这里</a>打开网上车管所的原始登录页面！",
	"url": "http://www.gzjd.gov.cn/cgs/service/getViolationList",
	"type": "map",
	"auto": false,
	"successExpression": "#root.containsKey(''returnCode'')",
	"resultExpression": "#root.get(''data'')",
	"parser": "tpl:wscgs_login_jtwf",
	"prev": "wscgs_login",
	"formData": [
		{"label": "用户名", "name": "j_username", "type": "select", "validate": "required", "options": [
			{"value": "", "label": ""},
			{"value": "72197317-9", "label": "宝城(72197317-9)"},
			{"value": "19068001-8", "label": "广发(19068001-8)", "selected": true}
		]},
		{"label": "密码", "name": "j_password", "type": "select", "validate": "required", "options": [
			{"value": "", "label": ""},
			{"value": "818000", "label": "****(宝城72197317-9)"},
			{"value": "818000", "label": "****(广发19068001-8)", "selected": true}
		]},
		{"label": "验证码", "name": "captchaId", "placeholder": "可不填", "captcha": {"url": "http://www.gzjd.gov.cn/cgs/captcha.jpg", "auto": false}}
	]
}' where code='wscgs_login_jtwf';

-- 模板数据：网上车管所交通违法公开查询UI模板
-- delete from bc_template where code='wscgs_logout_jtwf';
INSERT INTO bc_template(id, status_, inner_, order_, TYPE_ID, category, subject, code, version_, file_date, author_id, uid_, content)
    select NEXTVAL('CORE_SEQUENCE'),0,true,'2101'
	,(select id from bc_template_type where code='custom')
	,'营运系统/网络抓取','网上车管所交通违法公开查询UI模板','wscgs_logout_jtwf','1'
    ,now(),(select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	,'Template.mt.'|| NEXTVAL('CORE_SEQUENCE'), ''
	from bc_dual
	where not exists (select 0 from bc_template t where t.code='wscgs_logout_jtwf');
update bc_template set content='<#if (_d?size > 0)>
<table cellspacing="0" cellpadding="0">
<colgroup>
	<col style="width: 2em;">
	<col style="width: 6em;">
	<col style="width: 12em;">
	<col style="width: 12em;">
	<col style="width: 15em;">
	<col style="width: 5em;">
	<col style="width: 5em;">
	<col style="width: 5em;">
	<col style="width: 8.5em;">
	<col style="width: 5em;">
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
		<td class="ui-widget-content">当事人</td>
		<td class="ui-widget-content">确认时间</td>
		<td class="ui-widget-content">罚款金额</td>
		<td class="ui-widget-content">本次交通违法记分</td>
		<td class="ui-widget-content">违法来源</td>
		<td class="ui-widget-content">决定书编号</td>
	</tr>
</thead>
<tbody>
	<#list _d as m>
	<tr class="ui-widget-content autowrap">
		<th class="ui-widget-content">${m_index}</th>
		<td class="ui-widget-content">${m.HPHM!}</td>
		<td class="ui-widget-content">${m.WFSJ!}</td>
		<td class="ui-widget-content">${m.WFDZ!}</td>
		<td class="ui-widget-content">${m.WFXWMC!}</td>
		<td class="ui-widget-content">${m.WFXW!}</td>
		<td class="ui-widget-content"><#if (m.CLBJ! == ''1'')>已确认<#else>未确认</#if></td>
		<td class="ui-widget-content">${m.DSR!}</td>
		<td class="ui-widget-content">${m.CLSJMC!}</td>
		<td class="ui-widget-content">${m.FKJE!}</td>
		<td class="ui-widget-content">${m.WFJFS!}</td>
		<td class="ui-widget-content"><#if (m.CJFS! == ''9'')>电子警察<#else>${m.CJFS!}</#if></td>
		<td class="ui-widget-content">${m.JDSBH!}</td>
	</tr>
	</#list>
</tbody>
</table>
<#else>
<span style="padding:4px;">无数据！</span>
</#if>' where code='wscgs_logout_jtwf';

-- 模板数据：网上车管所交通违法查询(帐号登录后的查询)UI模板
-- delete from bc_template where code='wscgs_login_jtwf';
INSERT INTO bc_template(id, status_, inner_, order_, TYPE_ID, category, subject, code, version_, file_date, author_id, uid_, content)
    select NEXTVAL('CORE_SEQUENCE'),0,true,'2102'
	,(select id from bc_template_type where code='custom')
	,'营运系统/网络抓取','网上车管所交通违法帐号查询UI模板','wscgs_login_jtwf','1'
    ,now(),(select id from bc_identity_actor_history h where h.actor_code='admin' and h.current=true)
	,'Template.mt.'|| NEXTVAL('CORE_SEQUENCE'), ''
	from bc_dual
	where not exists (select 0 from bc_template t where t.code='wscgs_login_jtwf');
update bc_template set content='<#if (_d?size > 0)>
<table cellspacing="0" cellpadding="0">
<colgroup>
	<col style="width: 2.5em;">
	<col style="width: 6em;">
	<col style="width: 14em;">
	<col style="width: 12em;">
	<col style="width: 5em;">
	<col style="width: 10em;">
	<col style="width: 5em;">
	<col style="width: 5em;">
	<col style="width: 11em;">
	<col style="width: 5em;">
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
		<td class="ui-widget-content">违法代码</td>
		<td class="ui-widget-content">违法行为</td>
		<td class="ui-widget-content">处理状态</td>
		<td class="ui-widget-content">当事人</td>
		<td class="ui-widget-content">确认时间</td>
		<td class="ui-widget-content">罚款金额</td>
		<td class="ui-widget-content">本次交通违法记分</td>
		<td class="ui-widget-content">违法来源</td>
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
		<td class="ui-widget-content">${m.wfxw!}</td>
		<td class="ui-widget-content">${m.wfxwmc!}</td>
		<td class="ui-widget-content"><#if (m.clbj! == ''1'')>已确认<#else>未确认</#if></td>
		<td class="ui-widget-content">${m.dsr!}</td>
		<td class="ui-widget-content">${m.clsj!}</td>
		<td class="ui-widget-content">${m.fkje!}</td>
		<td class="ui-widget-content">${m.wfjfs!}</td>
		<td class="ui-widget-content"><#if (m.xxly! == ''1'')>现场处理<#else>电子警察</#if></td>
		<td class="ui-widget-content">${m.jdsbh!}${m.wsjyw!}</td>
	</tr>
	</#list>
</tbody>
</table>
<#else>
<span style="padding:4px;">无数据！</span>
</#if>' where code='wscgs_login_jtwf';

-- select * from bc_spider_cfg;
-- delete from bc_spider_cfg;