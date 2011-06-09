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

-- 插入单位数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0000','广州宝城', '0000');
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0001','第一营运分公司', '0001');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0001'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0002','第二营运分公司', '0002');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0002'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0003','第三营运分公司', '0003');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='D0003'; 
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 2, 'D0004','第四营运分公司', '0004');
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
    
-- 插入人员的认证数据(密码默认为888888的md5值)
insert into BC_IDENTITY_AUTH (ID,PASSWORD) 
    select a.id,'21218cca77804d2ba1922c33e0151105' from BC_IDENTITY_ACTOR a where a.type_=1; 

-- 插入岗位数据
insert into BC_IDENTITY_ACTOR (UID_,STATUS_,INNER_,TYPE_,CODE, NAME, ORDER_) values('uid', 1, 0, 4, 'G0001','超级管理岗', '0001');
insert into BC_IDENTITY_ACTOR_RELATION (TYPE_,MASTER_ID,FOLLOWER_ID) 
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code='D0000' and af.code='G0001'; 
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
    select 0,am.id,af.id from BC_IDENTITY_ACTOR am,BC_IDENTITY_ACTOR af where am.code in ('G0001','G9901','G9902','G9903','G9904','G9905','G9906','G9907') and af.code = 'admin'; 

-- 更新Actor的uid为'ACTOR-'+id
UPDATE BC_IDENTITY_ACTOR SET UID_=CONCAT('ACTOR-',id);

-- 让超级管理员拥有超级管理员角色
insert into BC_SECURITY_ROLE_ACTOR (AID,RID) 
	select a.id, r.id from BC_IDENTITY_ACTOR a,BC_SECURITY_ROLE r where a.code='admin' and r.code='admin';
