package cn.bc.template.service;

import java.util.Calendar;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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

	private String code;

	@Override
	protected Template createInstance(String config) {
		Template template = super.createInstance(config);
		code = idGeneratorService.next(Template.KEY_CODE_TEXT);
		template.setCode(code);
		template.setContent("testContent");
		template.setType(Template.TYPE_TEXT);
		// template.setAuthor(this.actorHistoryService.load(100729L));
		template.setAuthor(this.actorHistoryService.loadByCode("admin"));
		template.setFileDate(Calendar.getInstance());
		template.setInner(1);
		return template;
	}

	@Test
	public void testGetContent() {
		String code = UUID.randomUUID().toString();

		// == 测试获取不存在的模板
		Assert.assertNull(this.templateService.getContent(code));

		// == 测试获取存在的模板
		String content = "test";
		Template t = createInstance(null);
		t.setCode(code);
		t.setContent(content);
		this.templateService.save(t);
		Assert.assertEquals(content, this.templateService.getContent(code));
	}

	// == 以下为待清理的代码 ==

	@Test
	public void testFindOneTemplateRtnContent() {
		// Template tpl=this.createInstance(this.getDefaultConfig());
		// String r = this.templateService.findOneTemplateRtnContent(code);
		// Assert.assertEquals("testContent", r);
		// Assert.assertEquals("testContent",this.createInstance(this.getDefaultConfig()).getCode());
		// String t =
		// this.templateService.findOneTemplateRtnContent("text.111");
		// Assert.assertEquals("testtext", t);
		// InputStream is =
		// this.templateService.findOneTemplateRtnFile("excel.222");
		// Assert.assertNotNull(is);

		/*
		 * try { BufferedInputStream bis=new BufferedInputStream(is);
		 * FileOutputStream fos=new FileOutputStream("E:/templateText.xls");
		 * //BufferedOutputStream bos=new BufferedOutputStream(fos); int i=0;
		 * byte[] b=new byte[4096]; while((i=bis.read(b))!=-1){ fos.write(b); }
		 * is.close(); bis.close(); fos.close(); //bos.close();
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
	}
}
