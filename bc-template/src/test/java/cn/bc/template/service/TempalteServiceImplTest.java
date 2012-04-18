package cn.bc.template.service;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import cn.bc.docs.domain.Attach;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.template.domain.Template;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class TempalteServiceImplTest extends
		AbstractEntityCrudTest<Long, Template> {

	TemplateService templateService;
	IdGeneratorService idGeneratorService;
	ActorHistoryService actorHistoryService;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
		this.crudOperations = templateService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	protected Template createInstance(String config) {
		Template template = super.createInstance(config);
		template.setCode(UUID.randomUUID().toString());
		template.setContent("testContent");
		template.setType(Template.TYPE_CUSTOM);
		template.setAuthor(this.actorHistoryService.loadByCode("admin"));
		template.setFileDate(Calendar.getInstance());
		template.setInner(false);
		return template;
	}

	@Test
	public void testGetContent00_notExists() {
		// 测试获取不存在的模板
		Assert.assertNull(this.templateService.getContent(UUID.randomUUID()
				.toString()));
	}

	@Test
	public void testGetContent01_customText() {
		// 测试获取自定义文本模板的内容
		String content = "test";
		Template t = createInstance(null);
		t.setCode(UUID.randomUUID().toString());
		t.setType(Template.TYPE_CUSTOM);
		t.setContent(content);
		this.templateService.save(t);
		Assert.assertEquals(content,
				this.templateService.getContent(t.getCode()));
	}

	@Test
	public void testGetContent02_pureTextFile() throws Exception {
		// 测试获取纯文本附件模板的内容

		// 创建一个纯文本附件
		String content = "test";
		File file = new File(Attach.DATA_REAL_PATH + Template.DATA_SUB_PATH
				+ "/test.txt");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		Writer out = new FileWriter(file);
		FileCopyUtils.copy(content, out);

		// 保存一个模板
		Template t = createInstance(null);
		t.setCode(UUID.randomUUID().toString());
		t.setType(Template.TYPE_TEXT);
		// t.setSubject("text.txt");
		// t.setPath("text.txt");
		this.templateService.save(t);

		// 验证
		Assert.assertEquals(content,
				this.templateService.getContent(t.getCode()));
	}
}
