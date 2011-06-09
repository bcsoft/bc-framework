package cn.bc.core;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.core.Page;

public class PageTest {
	@Test
	public void test() {
		Page<Object> page = new Page<Object>(0,0,0,null);
		Assert.assertEquals(1,page.getPageNo());
		Assert.assertEquals(1,page.getPageSize());
		Assert.assertEquals(0,page.getTotalCount());
		Assert.assertEquals(0,page.getFirstResult());
		Assert.assertEquals(0,Page.getFirstResult(0,0));
		Assert.assertEquals(0,page.getPageCount());
		Assert.assertNull(page.getData());

		page = new Page<Object>(-1,-1,-1,null);
		Assert.assertEquals(1,page.getPageNo());
		Assert.assertEquals(1,page.getPageSize());
		Assert.assertEquals(0,page.getTotalCount());
		Assert.assertEquals(0,page.getFirstResult());
		Assert.assertEquals(0,Page.getFirstResult(-1,-1));
		Assert.assertEquals(0,page.getPageCount());
		Assert.assertNull(page.getData());

		page = new Page<Object>(1,50,101,null);
		Assert.assertEquals(1,page.getPageNo());
		Assert.assertEquals(50,page.getPageSize());
		Assert.assertEquals(101,page.getTotalCount());
		Assert.assertEquals(0,page.getFirstResult());
		Assert.assertEquals(0,Page.getFirstResult(1,50));
		Assert.assertEquals(3,page.getPageCount());
		Assert.assertNull(page.getData());
		
		page = new Page<Object>(2,50,100,new ArrayList<Object>());
		Assert.assertEquals(2,page.getPageNo());
		Assert.assertEquals(50,page.getPageSize());
		Assert.assertEquals(100,page.getTotalCount());
		Assert.assertEquals(50,page.getFirstResult());
		Assert.assertEquals(50,Page.getFirstResult(2,50));
		Assert.assertEquals(2,page.getPageCount());
		Assert.assertNotNull(page.getData());
	}
}
