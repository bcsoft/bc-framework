-- Create sequence 
create sequence HIBERNATE_SEQUENCE
    minvalue 1
    start with 1
    increment by 1
    cache 20;

create table QC_EXAMPLE (
    ID NUMBER(19) not null,
    NAME VARCHAR2(255) not null,
    CODE VARCHAR2(255),
    primary key (ID)
);