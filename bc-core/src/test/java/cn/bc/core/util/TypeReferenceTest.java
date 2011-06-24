package cn.bc.core.util;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cn.bc.core.EntityImpl;
import cn.bc.core.Entity;

public class TypeReferenceTest {
	@Test
	public void testAbstractClass() {
		TypeReference<EntityImpl> tr = new TypeReference<EntityImpl>() {};
		Assert.assertSame(EntityImpl.class, tr.getRawType());
		Assert.assertSame(EntityImpl.class, tr.getType());
	}
	
	@Test
	public void testInterface() {
		TypeReference<Entity<Long>> tr = new TypeReference<Entity<Long>>() {};
		Assert.assertSame(Entity.class, tr.getRawType());
		Assert.assertNotSame(Entity.class, tr.getType());
	}
	
	@Test
	public void testMap() {
		TypeReference<Map<String, EntityImpl>> tr = new TypeReference<Map<String, EntityImpl>>() {};
		Assert.assertSame(Map.class, tr.getRawType());
		Assert.assertNotSame(Map.class, tr.getType());
		
//		//或者
//		TypeToken<Map<String, Example>> tt = new TypeToken<Map<String, Example>>() {};
//		Assert.assertEquals("java.util.Map<java.lang.String, cn.bc.test.Example>", tt.getType().toString());
//		Assert.assertEquals("java.util.Map", tt.getRawType().getName());
	}
}
