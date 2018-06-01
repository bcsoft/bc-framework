/**
 * 插入模板管理、模板分类的关联表
 */
drop table if exists bc_template_template_category;

create table bc_template_template_category (
  cid integer not null,
  tid integer not null,
  constraint bcpk_template_template_category primary key (cid, tid),
  constraint bcfk_template_template_category foreign key (tid)
  references bc_template (id) match simple
  on update no action on delete cascade,
  constraint bcfk_template_category_template foreign key (cid)
  references bc_category (id) match simple
  on update no action on delete no action
);

comment on table bc_template_template_category
is '模板管理关联分类表';
comment on column bc_template_template_category.cid is '分类id';
comment on column bc_template_template_category.tid is '模板id';