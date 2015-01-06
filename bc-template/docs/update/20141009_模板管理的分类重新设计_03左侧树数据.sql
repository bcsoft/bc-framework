-- 插入模板配置的左侧树相关数据

-- 模板/安排管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'APGL','安排管理','08',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='APGL'
  );
-- 模板/安排管理/车辆回场检工作单
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='APGL'),
    0,'APGL_CLHC','车辆回场检工作单','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='APGL') and c.code='APGL_CLHC'
  );
-- 模板/安排管理/车辆作业项目单
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='APGL'),
    0,'APGL_CLZY','车辆作业项目单','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='APGL') and c.code='APGL_CLZY'
  );

-- 模板/考勤管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'KQGL','考勤管理','09',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='KQGL'
  );

-- 模板/编排/车辆编排
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='ARRANGE'),
    0,'ARRANGE_CLBP','车辆编排','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='ARRANGE') and c.code='ARRANGE_CLBP'
  );

-- 模板/车船税/车船迁移、变更登记表模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='CARTAX'),
    0,'CARTAX_QYBG','车船迁移、变更登记表模板','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='CARTAX') and c.code='CARTAX_QYBG'
  );
-- 模板/车船税/车船税申报表模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='CARTAX'),
    0,'CARTAX_SB','车船税申报表模板','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='CARTAX') and c.code='CARTAX_SB'
  );
-- 模板/车船税/车船停用、报废登记表模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='CARTAX'),
    0,'TYBF','车船停用、报废登记表模板','03',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='CARTAX') and c.code='TYBF'
  );

-- 模板/流程附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'LCFJ','流程附件','10',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='LCFJ'
  );
-- 模板/流程附件/宝城公司公文处理流程
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='LCFJ'),
    0,'GWCL','宝城公司公文处理流程','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='LCFJ') and c.code='GWCL'
  );
-- 模板/流程附件/车辆交车处理流程
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='LCFJ'),
    0,'CLJC','车辆交车处理流程','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='LCFJ') and c.code='CLJC'
  );
-- 模板/流程附件/外勤审批流程
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='LCFJ'),
    0,'WQSP','外勤审批流程','03',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='LCFJ') and c.code='WQSP'
  );

-- 模板/交通违法代码导入模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'JTWF_DM','交通违法代码导入模板','11',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='JTWF_DM'
  );

-- 模板/交通违法数据导入模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'JTWF_SJ','交通违法数据导入模板','12',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='JTWF_SJ'
  );

-- 模板/经营权导入数据模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'JYQ','经营权导入数据模板','13',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='JYQ'
  );

-- 模板/平台
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'BC','平台','14',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='BC'
  );
-- 模板/平台/登录统计
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='BC'),
    0,'DLTJ','登录统计','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='BC') and c.code='DLTJ'
  );
-- 模板/平台/电子邮件模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='BC'),
    0,'EMAIL','电子邮件模板','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='BC') and c.code='EMAIL'
  );
-- 模板/平台/数据导出模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='BC'),
    0,'SJDC','数据导出模板','03',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='BC') and c.code='SJDC'
  );
-- 模板/平台/分类管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='BC'),
    0,'FLGL','分类管理','04',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='BC') and c.code='FLGL'
  );
-- 模板/平台/分类管理/选择分类
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='FLGL'),
    0,'XZFL','选择分类','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='FLGL') and c.code='XZFL'
  );
-- 模板/平台/分类管理/选择分类计数
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='FLGL'),
    0,'XZFLJS','选择分类计数','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='FLGL') and c.code='XZFLJS'
  );

-- 模板/司机证件模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJZJ','司机证件模板','15',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJZJ'
  );

-- 模板/配件/库存预警
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'KCYJ','库存预警','01',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='KCYJ'
  );
-- 模板/配件/进货管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'JHGL','进货管理','02',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='JHGL'
  );
-- 模板/配件/进货退货
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'JHTH','进货退货','03',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='JHTH'
  );
-- 模板/配件/领料管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'LLGL','领料管理','04',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='LLGL'
  );
-- 模板/配件/统计报表
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'TJBB','统计报表','05',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='TJBB'
  );
-- 模板/配件/销售退回
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'XSTH','销售退回','06',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='XSTH'
  );
-- 模板/配件/配件库存超出预警范围模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='PMC'),
    0,'CCFW','配件库存超出预警范围模板','07',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='PMC') and c.code='CCFW'
  );

-- 模板/车辆保单
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'CLBD','车辆保单','16',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='CLBD'
  );

-- 模板/车辆保单附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'CLBDFJ','车辆保单附件','17',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='CLBDFJ'
  );

-- 模板/发票管理
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'FPGL','发票管理','18',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='FPGL'
  );

-- 模板/经济合同(按类别)/车辆转让协议
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='JJHT_BY_TYPE'),
    0,'CLZRXY','车辆转让协议','07',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='JJHT_BY_TYPE') and c.code='CLZRXY'
  );
-- 模板/经济合同(按类别)/附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='JJHT_BY_TYPE'),
    0,'FJ','附件','12',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='JJHT_BY_TYPE') and c.code='FJ'
  );
-- 模板/经济合同(按类别)/工资及代签署
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='JJHT_BY_TYPE'),
    0,'GZJDQS','工资及代签署','10',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='JJHT_BY_TYPE') and c.code='GZJDQS'
  );

-- 模板/人车对应异常附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'RCDYYC','人车对应异常附件','19',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='RCDYYC'
  );

-- 模板/社保附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SBFJ','社保附件','20',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SBFJ'
  );

-- 模板/数据导入导出模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJDCDRFJ','数据导入导出模板','21',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJDCDRFJ'
  );

-- 模板/数据导入导出模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJDRFJ','数据导入模板','22',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJDRFJ'
  );

-- 模板/司机人意险附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJYXFJ','司机人意险附件','23',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJYXFJ'
  );

-- 模板/司机责任人附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJZRRFJ','司机责任人附件','24',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJZRRFJ'
  );

-- 模板/司机人责任人附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJZRRFJ','司机人责任人附件','24',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJZRRFJ'
  );

-- 模板/司机招聘附件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJZPFJ','司机招聘附件','25',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJZPFJ'
  );

-- 模板/司机证件
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'SJZJ','司机证件','26',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='SJZJ'
  );

-- 模板/投保异常的车辆资料模板
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'TBYCCLZL','投保异常的车辆资料模板','27',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='TBYCCLZL'
  );

-- 模板/网络抓取
insert into BC_CATEGORY (ID,PID,STATUS_,CODE,NAME_,SN,MODIFIED_DATE,MODIFIER_ID)
  select NEXTVAL('CORE_SEQUENCE'),(select id from bc_category where code='TPL'),
    0,'WLZQ','网络抓取','28',now(),
    (select id from bc_identity_actor_history where actor_code='admin' and current=true)
  from bc_dual
  where not exists (
    select 0 from BC_CATEGORY c 
    where c.pid=(select id from bc_category where code='TPL') and c.code='WLZQ'
  );

