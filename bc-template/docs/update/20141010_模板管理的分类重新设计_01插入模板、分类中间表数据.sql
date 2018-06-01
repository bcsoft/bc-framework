/**
 * 插入bc_template_template_category模板与分类表的中间表的数据
 */
insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆回场检工作单')
  from bc_template t
  where t.category = '安排管理/车辆回场检工作单'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆回场检工作单')
         and tid in (select id
                     from bc_template t
                     where t.category = '安排管理/车辆回场检工作单'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆作业项目单')
  from bc_template t
  where t.category = '安排管理/车辆作业项目单'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆作业项目单')
         and tid in (select id
                     from bc_template t
                     where t.category = '安排管理/车辆作业项目单'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '考勤管理')
  from bc_template t
  where t.category = '办公系统/考勤管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '考勤管理')
         and tid in (select id
                     from bc_template t
                     where t.category = '办公系统/考勤管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '编排')
  from bc_template t
  where t.category = '编排管理/车辆编排'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '编排')
         and tid in (select id
                     from bc_template t
                     where t.category = '编排管理/车辆编排'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车船迁移、变更登记表模板')
  from bc_template t
  where t.category = '车船税/车船迁移、变更登记表模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车船迁移、变更登记表模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '车船税/车船迁移、变更登记表模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车船税申报表模板')
  from bc_template t
  where t.category = '车船税/车船税申报表模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车船税申报表模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '车船税/车船税申报表模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车船停用、报废登记表模板')
  from bc_template t
  where t.category = '车船税/车船停用、报废登记表模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车船停用、报废登记表模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '车船税/车船停用、报废登记表模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆交车处理流程')
  from bc_template t
  where t.category = '车辆交车处理流程'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆交车处理流程')
         and tid in (select id
                     from bc_template t
                     where t.category = '车辆交车处理流程'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '交通违法代码导入模板')
  from bc_template t
  where t.category = '交通违法代码导入模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '交通违法代码导入模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '交通违法代码导入模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '交通违法数据导入模板')
  from bc_template t
  where t.category = '交通违法数据导入模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '交通违法数据导入模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '交通违法数据导入模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '经营权导入数据模板')
  from bc_template t
  where t.category = '经营权导入数据模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '经营权导入数据模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '经营权导入数据模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '宝城公司公文处理流程')
  from bc_template t
  where t.category = '流程附件/宝城公司公文处理流程'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '宝城公司公文处理流程')
         and tid in (select id
                     from bc_template t
                     where t.category = '流程附件/宝城公司公文处理流程'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆交车处理流程')
  from bc_template t
  where t.category = '流程附件/车辆交车处理流程'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆交车处理流程')
         and tid in (select id
                     from bc_template t
                     where t.category = '流程附件/车辆交车处理流程'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '外勤审批流程')
  from bc_template t
  where t.category = '流程附件/外勤审批流程'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '外勤审批流程')
         and tid in (select id
                     from bc_template t
                     where t.category = '流程附件/外勤审批流程'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '登录统计')
  from bc_template t
  where t.category = '平台/登录统计'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '登录统计')
         and tid in (select id
                     from bc_template t
                     where t.category = '平台/登录统计'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '电子邮件模板')
  from bc_template t
  where t.category = '平台/电子邮件模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '电子邮件模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '平台/电子邮件模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '分类管理')
  from bc_template t
  where t.category = '平台/分类管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '分类管理')
         and tid in (select id
                     from bc_template t
                     where t.category = '平台/分类管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '选择分类')
  from bc_template t
  where t.category = '平台/分类管理/选择分类'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '选择分类')
         and tid in (select id
                     from bc_template t
                     where t.category = '平台/分类管理/选择分类'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '选择分类计数')
  from bc_template t
  where t.category = '平台/分类管理/选择分类计数'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '选择分类计数')
         and tid in (select id
                     from bc_template t
                     where t.category = '平台/分类管理/选择分类计数'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机证件模板')
  from bc_template t
  where t.category = '司机证件模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机证件模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '司机证件模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '库存预警')
  from bc_template t
  where t.category = '修理厂/库存预警'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '库存预警')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/库存预警'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '进货管理')
  from bc_template t
  where t.category = '修理厂/配件管理/进货管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '进货管理')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件管理/进货管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '进货退货')
  from bc_template t
  where t.category = '修理厂/配件管理/进货退货'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '进货退货')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件管理/进货退货'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '领料管理')
  from bc_template t
  where t.category = '修理厂/配件管理/领料管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '领料管理')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件管理/领料管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '统计报表'
                      and pid = (select id
                                 from bc_category
                                 where code = 'PMC'))
  from bc_template t
  where t.category = '修理厂/配件管理/统计报表'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '统计报表'
                      and pid = (select id
                                 from bc_category
                                 where code = 'PMC'))
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件管理/统计报表'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '销售退回')
  from bc_template t
  where t.category = '修理厂/配件管理/销售退回'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '销售退回')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件管理/销售退回'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '配件库存超出预警范围模板')
  from bc_template t
  where t.category = '修理厂/配件库存超出预警范围模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '配件库存超出预警范围模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件库存超出预警范围模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '配件库存超出预警范围模板')
  from bc_template t
  where t.category = '修理厂/配件库存超出预警范围模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '配件库存超出预警范围模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '修理厂/配件库存超出预警范围模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '编排')
  from bc_template t
  where t.category = '营运系统/编排管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '编排')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/编排管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆保单')
  from bc_template t
  where t.category = '营运系统/车辆保单'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆保单')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/车辆保单'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆保单附件')
  from bc_template t
  where t.category = '营运系统/车辆保单附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆保单附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/车辆保单附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '发票管理')
  from bc_template t
  where t.category = '营运系统/发票管理'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '发票管理')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/发票管理'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '附件')
  from bc_template t
  where t.category = '营运系统/经济合同附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '补充协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/补充协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '补充协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/补充协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆维修协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/车辆维修协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆维修协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/车辆维修协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '车辆转让协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/车辆转让协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '车辆转让协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/车辆转让协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '承诺书')
  from bc_template t
  where t.category = '营运系统/经济合同附件/承诺书'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '承诺书')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/承诺书'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '附件')
  from bc_template t
  where t.category = '营运系统/经济合同附件/附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '工资及代签署')
  from bc_template t
  where t.category = '营运系统/经济合同附件/工资及代签署'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '工资及代签署')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/工资及代签署'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '工资支付')
  from bc_template t
  where t.category = '营运系统/经济合同附件/工资支付'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '工资支付')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/工资支付'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '合同')
  from bc_template t
  where t.category = '营运系统/经济合同附件/合同'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '合同')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/合同'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '申请书')
  from bc_template t
  where t.category = '营运系统/经济合同附件/申请书'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '申请书')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/申请书'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '收费通知')
  from bc_template t
  where t.category = '营运系统/经济合同附件/收费通知'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '收费通知')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/收费通知'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '替班协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/替班协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '替班协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/替班协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '终止协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/终止协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '终止协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/终止协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '主体变更协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/主体变更'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '主体变更协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/主体变更'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '主体变更协议')
  from bc_template t
  where t.category = '营运系统/经济合同附件/主体变更协议'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '主体变更协议')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/经济合同附件/主体变更协议'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '劳动合同')
  from bc_template t
  where t.category = '营运系统/劳动合同附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '劳动合同')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/劳动合同附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '人车对应异常附件')
  from bc_template t
  where t.category = '营运系统/人车对应异常附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '人车对应异常附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/人车对应异常附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '社保附件')
  from bc_template t
  where t.category = '营运系统/社保附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '社保附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/社保附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '数据导入导出模板')
  from bc_template t
  where t.category = '营运系统/数据导入导出模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '数据导入导出模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/数据导入导出模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '数据导入模板')
  from bc_template t
  where t.category = '营运系统/数据导入模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '数据导入模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/数据导入模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '数据导入模板')
  from bc_template t
  where t.category = '营运系统/数据导入模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '数据导入模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/数据导入模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机人意险附件')
  from bc_template t
  where t.category = '营运系统/司机人意险附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机人意险附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/司机人意险附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机责任人附件')
  from bc_template t
  where t.category = '营运系统/司机责任人附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机责任人附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/司机责任人附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
  from bc_template t
  where t.category = '营运系统/司机招聘附件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/司机招聘附件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
  from bc_template t
  where t.category = '营运系统/司机证件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/司机证件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
  from bc_template t
  where t.category = '营运系统/司机证件'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '司机招聘附件')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/司机证件'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '统计报表'
                      and pid = (select id
                                 from bc_category
                                 where code = 'TPL'))
  from bc_template t
  where t.category = '营运系统/统计报表'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '统计报表'
                      and pid = (select id
                                 from bc_category
                                 where code = 'TPL'))
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/统计报表'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '投保异常的车辆资料模板')
  from bc_template t
  where t.category = '营运系统/投保异常的车辆资料模板'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '投保异常的车辆资料模板')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/投保异常的车辆资料模板'));

insert into bc_template_template_category (tid, cid)
  select t.id, (select id
                from bc_category c
                where c.name_ = '网络抓取')
  from bc_template t
  where t.category = '营运系统/网络抓取'
        and not exists
  (select 0
   from bc_template_template_category
   where cid = (select id
                from bc_category c
                where c.name_ = '网络抓取')
         and tid in (select id
                     from bc_template t
                     where t.category = '营运系统/网络抓取'));
