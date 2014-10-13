/**
 * 插入模板管理、模板分类的关联表
 */
-- DROP TABLE bc_template_template_category;

create table bc_template_template_category(
	cid integer not null,
	tid integer not null,
	CONSTRAINT bcpk_template_template_category PRIMARY KEY (cid, tid),
  CONSTRAINT bcfk_template_template_category FOREIGN KEY (tid)
      REFERENCES bc_template (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT bcfk_template_category_template FOREIGN KEY (cid)
      REFERENCES bc_category (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

COMMENT ON TABLE bc_template_template_category
  IS '模板管理关联分类表';
comment on COLUMN bc_template_template_category.cid is '分类id';
comment on COLUMN bc_template_template_category.tid is '模板id';