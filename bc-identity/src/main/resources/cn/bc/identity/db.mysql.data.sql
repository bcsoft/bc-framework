-- 系统安全相关模块的初始化数据

-- 插入分类资源数据
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '010000','工作事务', null, 'ixxxx');
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '020000','系统公告', null, 'ixxxx');
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '030000','营运管理', null, 'ixxxx');
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '700000','我的配置', null, 'ixxxx');
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	values(1, 0, 1, null, '800000','系统配置', null, 'ixxxx');
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 1, m.id, '800100','组织架构', null, 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 1, m.id, '800200','权限管理', null, 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';

-- 插入链接资源数据
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '010100','待办事务', '/bc/todoWork/list', 'i0605' from BC_IDENTITY_RESOURCE m where m.order_='010000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '010200','已办事务', '/bc/doneWork/paging', 'i0606' from BC_IDENTITY_RESOURCE m where m.order_='010000';

insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '020100','电子公告', '/bc/bulletin/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='020000';

insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '030100','车辆信息', '/bc/car/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='030000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '030200','司机信息', '/bc/driver/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='030000';

insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '700100','个性化设置', '/bc/personal/edit', 'i0504' from BC_IDENTITY_RESOURCE m where m.order_='700000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '700200','我的桌面', '/bc/shortcut/list', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='700000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '700300','我的日志', '/bc/mysyslog/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='700000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '700400','我的反馈', '/bc/feedback/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='700000';
	
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800101','职务配置', '/bc/duty/paging', 'i0603' from BC_IDENTITY_RESOURCE m where m.order_='800100';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800102','单位配置', '/bc/unit/paging', 'i0601' from BC_IDENTITY_RESOURCE m where m.order_='800100';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800103','部门配置', '/bc/department/paging', 'i0602' from BC_IDENTITY_RESOURCE m where m.order_='800100';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800104','岗位配置', '/bc/group/paging', 'i0508' from BC_IDENTITY_RESOURCE m where m.order_='800100';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800105','用户配置', '/bc/user/paging', 'i0507' from BC_IDENTITY_RESOURCE m where m.order_='800100';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '800106','资源配置', '/bc/resource/paging', 'i0600' from BC_IDENTITY_RESOURCE m where m.order_='800200';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800107','角色配置', '/bc/role/paging', 'i0509' from BC_IDENTITY_RESOURCE m where m.order_='800200';

insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '800300','反馈管理', '/bc/feedback/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS)
	select 1, 0, 2, m.id, '800400','附件管理', '/bc/attach/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800500','系统日志', '/bc/syslog/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';
insert into BC_IDENTITY_RESOURCE (STATUS_,INNER_,TYPE_,BELONG,ORDER_,NAME,URL,ICONCLASS) 
	select 1, 0, 2, m.id, '800600','消息记录', '/bc/message/paging', 'ixxxx' from BC_IDENTITY_RESOURCE m where m.order_='800000';
    

-- 插入超级管理员角色数据（可访问所有资源）
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0001', 'R_ADMIN','超级管理员');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_ADMIN' order by m.ORDER_;

-- 插入通用角色数据
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0000', 'R_COMMON','通用角色');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_COMMON' 
	and m.order_ in ('010100','010200','020100','700100','700200','700300','700400','030100','030200')
	order by m.order_;

-- 插入公告管理员角色数据
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0002', 'R_MANAGER_BULLETIN','电子公告管理');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_MANAGER_BULLETIN' 
	and m.order_ in ('020000','020100')
	order by m.order_;

-- 插入用户反馈管理角色数据
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0003', 'R_MANAGER_FEEDBACK','用户反馈管理');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_MANAGER_FEEDBACK' 
	and m.order_ in ('700400','800300')
	order by m.order_;

-- 插入附件管理角色数据
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0004', 'R_MANAGER_ATTACH','附件管理');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_MANAGER_ATTACH' 
	and m.order_ in ('800400')
	order by m.order_;

-- 插入营运管理角色数据
insert into BC_IDENTITY_ROLE (STATUS_,INNER_,TYPE_,ORDER_,CODE,NAME) 
	values(1, 0, 0,'0005', 'R_MANAGER_SERVICE','营运管理');
insert into BC_IDENTITY_ROLE_RESOURCE (RID,SID) 
	select r.id,m.id from BC_IDENTITY_ROLE r,BC_IDENTITY_RESOURCE m where r.code='R_MANAGER_SERVICE' 
	and m.order_ in ('030100','030200')
	order by m.order_;


-- 插入职务数据
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0101','董事长');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0201','总经理');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0202','副总经理');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0203','主管');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0204','主任');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0205','副主任');
insert into BC_IDENTITY_DUTY (STATUS_,INNER_,CODE, NAME) values(1, 0, '0901','职员');

-- 插入职务编码自动增长数据
insert into BC_IDENTITY_IDGENERATOR (TYPE_,VALUE, FORMAT) values('duty.code', 0, '${T}.${V}');
-- 插入用户uid自动增长数据
insert into BC_IDENTITY_IDGENERATOR (TYPE_,VALUE, FORMAT) values('user.uid', 0, '${T}.${V}');
-- 插入公告uid自动增长数据
insert into BC_IDENTITY_IDGENERATOR (TYPE_,VALUE, FORMAT) values('bulletin.uid', 0, '${T}.${V}');

-- 插入单位数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0000','总公司', '0000');
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0001','一分公司', '0001');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0001'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0002','二分公司', '0002');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0002'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0003','三分公司', '0003');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0003'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0004','四分公司', '0004');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0004'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0005','修理厂', '0005');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0005'; 

-- 插入部门数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0001','综合办公室', '0101');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='B0001'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0002','信息技术部', '0102');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='B0002'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0003','营运安全服务部', '0103');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='B0003'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0004','计划财务部', '0104');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='B0004'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0099','信息化项目小组', '0199');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='B0099'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0099-01','测试1', '0199-01');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='B0099-01'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 3, 'B0099-02','测试2', '0199-02');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
     select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='B0099-02'; 
    
-- 插入人员数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'admin','超级管理员', '000000');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='admin'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'zhoushaogui','周董', '000001');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='zhoushaogui'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'caishaohong','蔡总', '000002');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID)  
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='caishaohong'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'guohuiyan','阿妍', '000101');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0001' and af.code='guohuiyan'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'zhenminni','敏妮', '000102');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0001' and af.code='zhenminni'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 1, 'huangrongji','黄荣基', '009901');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='huangrongji'; 
    
-- 插入人员的Detail信息
insert into BC_IDENTITY_ACTOR_DETAIL (ID,CREATE_DATE,WORK_DATE,SEX,CARD,DUTY_ID,COMMENT) 
    select a.id,now(),null,0,null,null,null from BC_IDENTITY_ACTOR a where a.type_=1; 
update BC_IDENTITY_ACTOR a,BC_IDENTITY_ACTOR_DETAIL ad set a.detail_id = ad.id 
    where ad.id = a.id and a.type_=1;
    
-- 插入人员的认证数据(密码默认为888888的md5值)
insert into BC_IDENTITY_AUTH (ID,PASSWORD) 
    select a.id,'21218cca77804d2ba1922c33e0151105' from BC_IDENTITY_ACTOR a where a.type_=1; 

-- 插入岗位数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G_ADMIN','超级管理岗', '0001');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='G_ADMIN'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9901','测试岗位1', '9901');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9901'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9902','测试岗位2', '9902');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9902'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9903','测试岗位3', '9903');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9903'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9904','测试岗位4', '9904');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9904'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9905','测试岗位5', '9905');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9905'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9906','测试岗位6', '9906');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9906'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G9907','测试岗位7', '9907');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='B0099' and af.code='G9907'; 
-- 让超级管理员拥有超级管理岗
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where af.code = 'admin' and am.code = 'G_ADMIN'; 

insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where af.code = 'huangrongji' and am.code in ('G_ADMIN','G9901','G9902','G9903','G9904','G9905','G9906','G9907'); 

-- 更新Actor的uid为'ACTOR-'+id
UPDATE BC_IDENTITY_ACTOR SET UID_=CONCAT('ACTOR-',id);

-- 让顶层单位拥有通用角色
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID) 
	select a.id, r.id from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r where a.code='D0000' and r.code='R_COMMON';

-- 让超级管理员拥有超级管理员角色
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID) 
	select a.id, r.id from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r where a.code='admin' and r.code='R_ADMIN';

-- 让超级管理岗拥有所有角色
insert into BC_IDENTITY_ROLE_ACTOR (AID,RID) 
	select a.id, r.id from BC_IDENTITY_ACTOR a,BC_IDENTITY_ROLE r where a.code='G_ADMIN';
