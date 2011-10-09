--Start with...Connect By子句递归查询一般用于一个表维护树形结构的应用。
--创建示例表：
CREATE TABLE TBL_TEST
(
  ID    NUMBER,
  NAME  VARCHAR2(100 BYTE),
  PID   NUMBER
);
 
--插入测试数据：
INSERT INTO TBL_TEST(ID,NAME,PID) VALUES('1','10','0');
INSERT INTO TBL_TEST(ID,NAME,PID) VALUES('2','11','1');
INSERT INTO TBL_TEST(ID,NAME,PID) VALUES('3','20','0');
INSERT INTO TBL_TEST(ID,NAME,PID) VALUES('4','12','1');
INSERT INTO TBL_TEST(ID,NAME,PID) VALUES('5','121','2');
 
--从Root往树末梢递归
select t.*,sys_connect_by_path(t.name,'/'),connect_by_isleaf,connect_by_root id from TBL_TEST t start with id=1 connect by prior id = pid;
 
--从末梢往树ROOT递归
select t.*,sys_connect_by_path(pid,'/'),connect_by_isleaf,connect_by_root from TBL_TEST t start with id=5 connect by prior pid = id;
 
--从Root往树末梢递归
select t.*,sys_connect_by_path(t.name,'/'),connect_by_isleaf,connect_by_root id from TBL_TEST t start with id=2 connect by prior id = pid;
