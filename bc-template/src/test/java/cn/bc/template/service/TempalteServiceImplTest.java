package cn.bc.template.service;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
    TemplateTypeService templateTypeService;
	
	@Autowired
	public void setTemplateTypeService(TemplateTypeService templateTypeService) {
		this.templateTypeService = templateTypeService;
	}

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
		template.setTemplateType(this.templateTypeService.loadByCode("custom"));
		template.setAuthor(this.actorHistoryService.loadByCode("admin"));
		template.setFileDate(Calendar.getInstance());
		template.setInner(false);
		return template;
	}

	@Test
	public void testGetContent_notExists() {
		// 测试获取不存在的模板
		Assert.assertNull(this.templateService.getContent(UUID.randomUUID()
				.toString()));
	}

	@Test
	public void testGetContent_customText() {
		// 测试获取自定义文本模板的内容
		String content = "test";
		Template t = createInstance(null);
		t.setCode(UUID.randomUUID().toString());
		t.setTemplateType(this.templateTypeService.loadByCode("custom"));
		t.setContent(content);
		this.templateService.save(t);
		Assert.assertEquals(content,
				this.templateService.getContent(t.getCode()));
	}

	@Test
	public void testGetContent_txt() throws Exception {
		// 测试获取纯文本附件模板的内容

		// 创建一个纯文本附件
		String content = "test${name}";
		writeString2File(content, Attach.DATA_REAL_PATH + "/"
				+ Template.DATA_SUB_PATH + "/test.txt");

		// // 保存一个模板
		Template t = createInstance(null);
		t.setCode(UUID.randomUUID().toString());
		t.setTemplateType(this.templateTypeService.loadByCode("txt"));
		t.setSubject("test.txt");
		t.setPath("test.txt");
		t.setContent(null);
		this.templateService.save(t);

		// 验证模版内容
		Assert.assertEquals(content,
				this.templateService.getContent(t.getCode()));
	}

	@Test
	public void testFormat_txt() throws Exception {
		// 测试获取纯文本附件模板的内容

		// 创建一个纯文本附件
		String content = "test${name}";
		writeString2File(content, Attach.DATA_REAL_PATH + "/"
				+ Template.DATA_SUB_PATH + "/test.txt");

		// // 保存一个模板
		Template t = createInstance(null);
		t.setCode(UUID.randomUUID().toString());
		t.setTemplateType(this.templateTypeService.loadByCode("txt"));
		t.setSubject("test.txt");
		t.setPath("test.txt");
		t.setContent(null);
		this.templateService.save(t);

		// 验证模版内容
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("name", "小明");
		Assert.assertEquals("test小明",
				this.templateService.format(t.getCode(), args));
	}

	/**
	 * 将字符串写入文件
	 * 
	 * @param content
	 * @param _file
	 * @throws Exception
	 */
	private void writeString2File(String content, String _file)
			throws Exception {
		// 创建一个纯文本附件
		File file = new File(_file);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		Writer out = new FileWriter(file);
		out.write(content);
		out.close();

		// 验证写入的文件存在
		Assert.assertTrue(new File(_file).exists());
	}
}
