-- postgres数据库特殊类型的测试用表
DROP TABLE IF EXISTS BC_EXAMPLE;
-- 测试用的表
CREATE TABLE BC_EXAMPLE (
    ID INTEGER NOT NULL,
    NAME varchar(255) NOT NULL,
    CODE varchar(255),
		JSON_ JSON,
		ARRAY_INT INTEGER[],
    PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_EXAMPLE IS '测试用的表';
COMMENT ON COLUMN BC_EXAMPLE.NAME IS '名称';

ALTER TABLE bc_example ADD COLUMN JSON_ JSON;
ALTER TABLE bc_example ADD COLUMN ARRAY_INT integer[];


--TEST
delete from bc_example;
INSERT INTO bc_example(id, name, code, json_, array_int)
    VALUES (1, 'n1', 'c1', '{"f1":{"f11":11,"f12":12},"f2":2}', array[1, 2])
    ,(2, 'n2', 'c2', '{"f1":2,"g2":{"g11":11,"g12":12}}', array[21, 21])
    ,(3, 'n3', 'c3', '{"f1":[1,"Robert \"M\"",true]}', array[21, 21]);

select * from bc_example;
-- json 查询：第一个运算符是 “->”, 用来直接从 JSON 数据库获取字段值，使用文本值来标注字段的键：
select id, json_, json_->'f1' as f1, json_->'g1' as g1, json_->'f1'->'f12' as f12 from bc_example;
-- json 查询：第二个运算符是 “->>”, 返回指定的文本，“->>” 返回纯文本。
select id, json_, json_->>'f1' as f1 from bc_example;
-- 最后两个运算符是 “#>” 和 “#>>”. 用来直接获取数组中的元素
select id, json_, json_#>'{f1,1}' f1, json_#>>'{f1,1}' as f1_s from bc_example;
