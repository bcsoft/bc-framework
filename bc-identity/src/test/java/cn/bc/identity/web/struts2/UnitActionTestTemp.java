package cn.bc.identity.web.struts2;

import java.util.UUID;

import cn.bc.identity.domain.Actor;

public class UnitActionTestTemp extends AbstractActorActionTest {
	@Override
	protected String getEntityConfigName() {
		return "Unit";
	}

	public Actor createEntity() {
		Actor e = super.createEntity();
		e.setUid(UUID.randomUUID().toString());
		e.setType(Actor.TYPE_UNIT);
		e.setCode("code");
		e.setName("name");
		return e;
	}
}
