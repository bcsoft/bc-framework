-- 系统桌面相关模块的初始化数据

-- 插入桌面快捷方式数据
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '00', 0, null, null, id, null from BC_SECURITY_MODULE where name='待办事务';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '01', 0, null, null, id, null from BC_SECURITY_MODULE where name='已办事务';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '02', 0, null, null, id, null from BC_SECURITY_MODULE where name='单位配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '03', 0, null, null, id, null from BC_SECURITY_MODULE where name='部门配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '04', 0, null, null, id, null from BC_SECURITY_MODULE where name='岗位配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '05', 0, null, null, id, null from BC_SECURITY_MODULE where name='用户配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '06', 0, null, null, id, null from BC_SECURITY_MODULE where name='资源配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '07', 0, null, null, id, null from BC_SECURITY_MODULE where name='角色配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '08', 0, null, null, id, null from BC_SECURITY_MODULE where name='职务配置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '50', 0, null, null, id, null from BC_SECURITY_MODULE where name='个性化设置';
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,MID,AID) 
	select 1, 0, '51', 0, null, null, id, null from BC_SECURITY_MODULE where name='桌面管理';

-- 报表
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '71', 0, '饼图', '/bc/chart/pie', 'i0501');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '72', 0, '柱图', '/bc/chart/bar', 'i0500');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '73', 0, '动态曲线图', '/bc/chart/spline', 'i0502');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '74', 0, '综合图表', '/bc/chart/mix', 'i0503');
    
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '75', 1, '谷歌搜索', 'http://www.google.com.hk/', 'i0505');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '76', 1, '百度搜索', 'http://www.baidu.com/', 'i0506');
	
-- 视图设计
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '77', 0, '分页设计', '/bc/duty/paging', 'i0604');
insert into BC_DESKTOP_SHORTCUT (STATUS_,INNER_,ORDER_,STANDALONE,NAME,URL,ICONCLASS) 
	values(1, 0, '78', 0, '无分页设计', '/bc/duty/list', 'i0604');

-- 插入全局配置信息
insert into BC_DESKTOP_PERSONAL (STATUS_,INNER_,FONT,THEME,AID) 
	values(1, 0, '12', 'humanity', null);
-- insert into BC_DESKTOP_PERSONAL (STATUS_,INNER_,FONT,THEME,AID) 
-- 	select 1, 0, '14', 'flick', id from BC_IDENTITY_ACTOR where code='admin';
