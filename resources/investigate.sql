-- 调查问卷的数据库脚本
DROP TABLE IF EXISTS BC_IVG_GRADE;
DROP TABLE IF EXISTS BC_IVG_ANSWER;
DROP TABLE IF EXISTS BC_IVG_RESPOND;
DROP TABLE IF EXISTS BC_IVG_QUESTION_ITEM;
DROP TABLE IF EXISTS BC_IVG_QUESTION;
DROP TABLE IF EXISTS BC_IVG_QUESTIONARY_ACTOR;
DROP TABLE IF EXISTS BC_IVG_QUESTIONARY;

-- 问卷
CREATE TABLE BC_IVG_QUESTIONARY (
	ID INTEGER NOT NULL,
	TYPE_ INTEGER NOT NULL DEFAULT 0,
	STATUS_ INTEGER NOT NULL DEFAULT 0,
	SUBJECT VARCHAR(255),
	START_DATE TIMESTAMP NOT NULL,
	END_DATE TIMESTAMP,
	PERMITTED BOOLEAN NOT NULL DEFAULT FALSE,
	ISSUE_DATE TIMESTAMP,
	ISSUER_ID INTEGER,
	PIGEONHOLE_DATE TIMESTAMP,
	PIGEONHOLER_ID INTEGER,
	FILE_DATE TIMESTAMP NOT NULL,
	AUTHOR_ID INTEGER NOT NULL,
	MODIFIER_ID INTEGER ,
	MODIFIED_DATE TIMESTAMP,
	CONSTRAINT BCPK_IVG_QUESTIONARY PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_QUESTIONARY IS '问卷';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.TYPE_ IS '类型 : 0-网上调查,1-网上考试';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.STATUS_ IS '状态：-1-草稿,0-已发布,1-已归档';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.SUBJECT IS '标题';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.START_DATE IS '开始日期';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.END_DATE IS '结束日期';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.PERMITTED IS '提交前允许查看统计';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.ISSUE_DATE IS '发布时间';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.ISSUER_ID IS '发布人ID';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.PIGEONHOLE_DATE IS '归档时间';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.PIGEONHOLER_ID IS '归档人ID';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.AUTHOR_ID IS '创建人ID';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.MODIFIER_ID IS '最后修改人ID';
COMMENT ON COLUMN BC_IVG_QUESTIONARY.MODIFIED_DATE IS '最后修改时间';
ALTER TABLE BC_IVG_QUESTIONARY ADD CONSTRAINT BCFK_IVG_QUESTIONARY_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_IVG_QUESTIONARY ADD CONSTRAINT BCFK_IVG_QUESTIONARY_MODIFIER FOREIGN KEY (MODIFIER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_IVG_QUESTIONARY ADD CONSTRAINT BCFK_IVG_QUESTIONARY_ISSUER FOREIGN KEY (ISSUER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_IVG_QUESTIONARY ADD CONSTRAINT BCFK_IVG_QUESTIONARY_PIGEONHOLER FOREIGN KEY (PIGEONHOLER_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 问卷所限制的参与人
CREATE TABLE BC_IVG_QUESTIONARY_ACTOR (
    QID integer NOT NULL,
    AID integer NOT NULL,
    CONSTRAINT BCPK_IVG_QUESTIONARY_ACTOR PRIMARY KEY (QID,AID)
);
COMMENT ON TABLE BC_IVG_QUESTIONARY_ACTOR IS '问卷所限制的参与人';
COMMENT ON COLUMN BC_IVG_QUESTIONARY_ACTOR.QID IS '问卷ID';
COMMENT ON COLUMN BC_IVG_QUESTIONARY_ACTOR.AID IS '参与人ID';
ALTER TABLE BC_IVG_QUESTIONARY_ACTOR ADD CONSTRAINT BCFK_IVG_QA_QUESTIONARY FOREIGN KEY (QID) REFERENCES BC_IVG_QUESTIONARY (ID);
ALTER TABLE BC_IVG_QUESTIONARY_ACTOR ADD CONSTRAINT BCFK_IVG_QA_ACTOR FOREIGN KEY (AID) REFERENCES BC_IDENTITY_ACTOR (ID);

-- 问题
CREATE TABLE BC_IVG_QUESTION (
	ID INTEGER NOT NULL,
	PID INTEGER NOT NULL,
	TYPE_ INTEGER NOT NULL DEFAULT 0,
	REQUIRED BOOLEAN NOT NULL DEFAULT TRUE,
	SUBJECT VARCHAR(255),
	ORDER_ INTEGER NOT NULL DEFAULT 0,
	SCORE INT DEFAULT 0 NOT NULL,
	SEPERATE_SCORE BOOLEAN DEFAULT true,
	CONFIG VARCHAR(1000),
	CONSTRAINT BCPK_IVG_QUESTION PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_QUESTION IS '问题';
COMMENT ON COLUMN BC_IVG_QUESTION.PID IS '所属问卷ID';
COMMENT ON COLUMN BC_IVG_QUESTION.TYPE_ IS '类型 : 0-单选题,1-多选题,2-填空题,3-问答题';
COMMENT ON COLUMN BC_IVG_QUESTION.REQUIRED IS '必选题，默认为是';
COMMENT ON COLUMN BC_IVG_QUESTION.SUBJECT IS '标题';
COMMENT ON COLUMN BC_IVG_QUESTION.ORDER_ IS '排序号';
COMMENT ON COLUMN BC_IVG_QUESTION.CONFIG IS '配置:使用json格式，如控制选项水平、垂直布局，控制问答题输入框的默认大小等，格式为：{layout_orientation:"horizontal|vertical",row:5}';
COMMENT ON COLUMN BC_IVG_QUESTION.SCORE IS '分数';
COMMENT ON COLUMN BC_IVG_QUESTION.SEPERATE_SCORE IS '各个选项独立给分:(仅适用于网上考试的多选题)注意答错任一项将为0分';
ALTER TABLE BC_IVG_QUESTION ADD CONSTRAINT BCFK_IVG_QUESTION_PID FOREIGN KEY (PID)
      REFERENCES BC_IVG_QUESTIONARY (ID);

-- 问题项
CREATE TABLE BC_IVG_QUESTION_ITEM (
	ID INTEGER NOT NULL,
	PID INTEGER NOT NULL,
	SUBJECT VARCHAR(255),
	ORDER_ INTEGER NOT NULL DEFAULT 0,
	STANDARD BOOLEAN DEFAULT false NOT NULL,
	SCORE INT DEFAULT 0 NOT NULL,
	CONFIG VARCHAR(1000),
	CONSTRAINT BCPK_IVG_QUESTION_ITEM PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_QUESTION_ITEM IS '问题项';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.PID IS '所属问题ID';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.SUBJECT IS '单选多选题显示的选项文字,如果为问答题则为默认填写的内容';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.ORDER_ IS '排序号';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.STANDARD IS '标准答案';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.SCORE IS '分数 : 选择此答案的得分';
COMMENT ON COLUMN BC_IVG_QUESTION_ITEM.CONFIG IS '特殊配置 : 用于填空题的标准答案配置，使用json数组格式[{},...]，每个json元素格式为{key:"占位符",value:"标准答案",score:分数}';
ALTER TABLE BC_IVG_QUESTION_ITEM ADD CONSTRAINT BCFK_IVG_QUESTION_ITEM_PID FOREIGN KEY (PID)
      REFERENCES BC_IVG_QUESTION (ID);

-- 作答记录
CREATE TABLE BC_IVG_RESPOND (
	ID INTEGER NOT NULL,
	PID INTEGER NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	AUTHOR_ID INTEGER NOT NULL,
	CONSTRAINT BCPK_IVG_RESPOND PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_RESPOND IS '作答记录';
COMMENT ON COLUMN BC_IVG_RESPOND.PID IS '所属问卷ID';
COMMENT ON COLUMN BC_IVG_RESPOND.FILE_DATE IS '作答时间';
COMMENT ON COLUMN BC_IVG_RESPOND.AUTHOR_ID IS '作答人';
ALTER TABLE BC_IVG_RESPOND ADD CONSTRAINT BCFK_IVG_RESPOND_PID FOREIGN KEY (PID)
      REFERENCES BC_IVG_QUESTIONARY (ID);
ALTER TABLE BC_IVG_RESPOND ADD CONSTRAINT BCFK_IVG_RESPOND_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 问题项的答案
CREATE TABLE BC_IVG_ANSWER (
	ID INTEGER NOT NULL,
	QID INTEGER NOT NULL,
	RID INTEGER NOT NULL,
	CONTENT VARCHAR(255),
	SCORE INT DEFAULT 0 NOT NULL,
	CONSTRAINT BCPK_IVG_ANSWER PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_ANSWER IS '答案';
COMMENT ON COLUMN BC_IVG_ANSWER.QID IS '问题项ID';
COMMENT ON COLUMN BC_IVG_ANSWER.RID IS '作答记录ID';
COMMENT ON COLUMN BC_IVG_ANSWER.SCORE IS '给分';
COMMENT ON COLUMN BC_IVG_ANSWER.CONTENT IS '问答题填写的内容';
ALTER TABLE BC_IVG_ANSWER ADD CONSTRAINT BCFK_IVG_ANSWER_QID FOREIGN KEY (QID)
      REFERENCES BC_IVG_QUESTION_ITEM (ID);
ALTER TABLE BC_IVG_ANSWER ADD CONSTRAINT BCFK_IVG_ANSWER_RID FOREIGN KEY (RID)
      REFERENCES BC_IVG_RESPOND (ID);

-- 对答案的评分记录
CREATE TABLE BC_IVG_GRADE (
	ID INT NOT NULL,
	ANSWER_ID INT NOT NULL,
	SCORE INT DEFAULT 0 NOT NULL,
	FILE_DATE TIMESTAMP NOT NULL,
	AUTHOR_ID INT NOT NULL,
	DESC_ VARCHAR(1000),
	PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_IVG_GRADE IS '评分记录';
COMMENT ON COLUMN BC_IVG_GRADE.ID IS 'ID';
COMMENT ON COLUMN BC_IVG_GRADE.ANSWER_ID IS '答案ID';
COMMENT ON COLUMN BC_IVG_GRADE.SCORE IS '给分';
COMMENT ON COLUMN BC_IVG_GRADE.FILE_DATE IS '评分时间';
COMMENT ON COLUMN BC_IVG_GRADE.AUTHOR_ID IS '评分人ID';
COMMENT ON COLUMN BC_IVG_GRADE.DESC_ IS '备注';
ALTER TABLE BC_IVG_GRADE ADD CONSTRAINT BCFK_IVG_GRADE_AID FOREIGN KEY (ANSWER_ID)
      REFERENCES BC_IVG_ANSWER (ID);
ALTER TABLE BC_IVG_GRADE ADD CONSTRAINT BCFK_IVG_GRADE_AUTHORID FOREIGN KEY (AUTHOR_ID)
      REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);