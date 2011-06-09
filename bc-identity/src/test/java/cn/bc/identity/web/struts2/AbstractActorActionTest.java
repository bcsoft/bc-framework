package cn.bc.identity.web.struts2;

import java.util.UUID;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Actor;
import cn.bc.test.mock.CrudServiceMock;
import cn.bc.web.struts2.AbstractCrudActionTest;

public abstract class AbstractActorActionTest extends AbstractCrudActionTest<Long, Actor> {
	@Override
	protected String getContextLocations() {
		return "classpath:spring-test4struts.xml";
	}

	protected Class<Actor> getEntityClass() {
		return Actor.class;
	}

	protected String getEntityConfigName() {
		return "Actor";
	}

	public Actor createEntity() {
		Actor e = new Actor();
		e.setUid(UUID.randomUUID().toString());
		
		//设置一些必填域
		e.setCode("code");
		e.setName("name");
		
		return e;
	}

	public CrudService<Actor> createCrudService() {
		CrudServiceMock<Actor> sm = new CrudServiceMock<Actor>();//使用内存模拟避免写入数据库
		sm.setEntityClass(Actor.class);
		return sm;
	}
}
