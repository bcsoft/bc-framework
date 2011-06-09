-- 插入测试消息
insert into BC_MESSAGE (SEND_DATE,SUBJECT,CONTENT,SENDER_ID,RECEIVER_ID) 
	select now(), '测试标题1', '测试内容1', 
	(select s.id from BC_IDENTITY_ACTOR s where s.code='admin'),
	(select r.id from BC_IDENTITY_ACTOR r where r.code='admin');
insert into BC_MESSAGE (SEND_DATE,SUBJECT,CONTENT,SENDER_ID,RECEIVER_ID) 
	select now(), '测试标题2', '测试内容2', 
	(select s.id from BC_IDENTITY_ACTOR s where s.code='admin'),
	(select r.id from BC_IDENTITY_ACTOR r where r.code='admin');
insert into BC_MESSAGE (SEND_DATE,SUBJECT,CONTENT,SENDER_ID,RECEIVER_ID) 
	select now(), '测试标题3', '测试内容3', 
	(select s.id from BC_IDENTITY_ACTOR s where s.code='admin'),
	(select r.id from BC_IDENTITY_ACTOR r where r.code='admin');
insert into BC_MESSAGE (SEND_DATE,SUBJECT,CONTENT,SENDER_ID,RECEIVER_ID) 
	select now(), '测试标题4', null, 
	(select s.id from BC_IDENTITY_ACTOR s where s.code='admin'),
	(select r.id from BC_IDENTITY_ACTOR r where r.code='admin');
