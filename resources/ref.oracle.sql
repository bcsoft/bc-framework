-- 创建序列
create sequence HIBERNATE_SEQUENCE
    minvalue 1
    start with 1
    increment by 1
    cache 20;

-- 创建表
create table TBALE1 (
    ID NUMBER(19) not null,
    NAME varchar2(255) not null,
    CODE VARCHAR2(255),
    primary key (ID)
);

-- 创建表注释
COMMENT ON TABLE TBALE1 IS '参与者的扩展属性';

-- 创建列注释
COMMENT ON COLUMN TBALE1.NAME IS '创建时间';

-- 创建外键
ALTER TABLE TBALE1 ADD CONSTRAINT FK1 FOREIGN KEY (DETAIL_ID) REFERENCES TBALE2 (ID);
ALTER TABLE TBALE1 ADD CONSTRAINT FK1 FOREIGN KEY (DETAIL_ID) REFERENCES TBALE2 (ID) ON DELETE CASCADE;

-- 创建索引
CREATE INDEX IDX1 ON TBALE1 (NAME ASC);


