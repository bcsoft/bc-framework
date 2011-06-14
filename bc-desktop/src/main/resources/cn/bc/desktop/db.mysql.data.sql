-- 系统桌面相关模块的初始化数据

-- 插入桌面快捷方式数据
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '0001', 0, null, null, id, null from BC_SECURITY_MODULE where name='待办事务';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '0002', 0, null, null, id, null from BC_SECURITY_MODULE where name='已办事务';
	
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '1001', 0, null, null, id, null from BC_SECURITY_MODULE where name='电子公告';
	
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '5001', 0, null, null, id, null from BC_SECURITY_MODULE where name='个性化设置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '5002', 0, null, null, id, null from BC_SECURITY_MODULE where name='我的桌面';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '5003', 0, null, null, id, null from BC_SECURITY_MODULE where name='我的日志';
	
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7001', 0, null, null, id, null from BC_SECURITY_MODULE where name='单位配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7002', 0, null, null, id, null from BC_SECURITY_MODULE where name='部门配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7003', 0, null, null, id, null from BC_SECURITY_MODULE where name='岗位配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7004', 0, null, null, id, null from BC_SECURITY_MODULE where name='用户配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7005', 0, null, null, id, null from BC_SECURITY_MODULE where name='资源配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7006', 0, null, null, id, null from BC_SECURITY_MODULE where name='角色配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7007', 0, null, null, id, null from BC_SECURITY_MODULE where name='职务配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '7008', 0, null, null, id, null from BC_SECURITY_MODULE where name='系统日志';

-- 报表
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8001', 0, '饼图', '/bc/chart/pie', 'i0501');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8002', 0, '柱图', '/bc/chart/bar', 'i0500');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8003', 0, '动态曲线图', '/bc/chart/spline', 'i0502');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8004', 0, '综合图表', '/bc/chart/mix', 'i0503');
    
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8005', 1, '谷歌搜索', 'http://www.google.com.hk/', 'i0505');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '8006', 1, '百度搜索', 'http://www.baidu.com/', 'i0506');
	
-- 设计用的快捷方式
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9001', 0, '分页设计', '/bc/duty/paging', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9002', 0, '无分页设计', '/bc/duty/list', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9003', 1, 'highcharts', '/ui-libs-demo/highcharts/2.1.4/index.htm', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9004', 1, 'jqueryUI', '/ui-libs-demo/jquery-ui/1.8.13/index.html', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9005', 1, 'jqGrid', '/ui-libs-demo/jqGrid/3.8.2/jqgrid.html', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9006', 1, 'jqLayout', '/ui-libs-demo/jquery-layout/1.2.0/index.html', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9007', 1, 'xheditor', '/ui-libs-demo/xheditor/1.1.7/index.html', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS,AID) 
    select 1, 0, '9008', 1, 'zTree', '/ui-libs-demo/zTree/2.6/index.html', 'i0604', a.id from BC_IDENTITY_ACTOR a where a.code = 'huangrongji';

-- 插入全局配置信息
insert into BC_DESKTOP_PERSONAL (STATUS_,INNER_,FONT,THEME,AID) 
	values(1, 0, '12', 'humanity', null);
-- insert into BC_DESKTOP_PERSONAL (STATUS_,INNER_,FONT,THEME,AID) 
-- 	select 1, 0, '14', 'flick', id from BC_IDENTITY_ACTOR where code='admin';
