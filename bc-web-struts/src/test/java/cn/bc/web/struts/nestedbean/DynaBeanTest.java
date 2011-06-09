package cn.bc.web.struts.nestedbean;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.struts.util.LabelValueBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.bc.web.struts.beanutils.BeanUtilsEx;
import cn.bc.web.struts.beanutils.ConverterUtils;


/**
 * 用于学习DynaBean的测试类
 * @author dragon
 *
 */
public class DynaBeanTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	//简单属性的复制
	public void simpleCopy01() throws IllegalAccessException,
			InvocationTargetException {
		DynaBean _author = new LazyDynaBean();
		_author.set("id", 1);
		_author.set("name", "dragon");

		Author author = new Author();
		author.setName("name");//这句可以注释掉
		author.setName2("name2");
		BeanUtils.copyProperties(author, _author);
		
		Assert.assertEquals(1, author.getId());
		Assert.assertEquals("dragon", author.getName());
		Assert.assertEquals("name2", author.getName2());
	}

	@Test(expected=IllegalArgumentException.class)
	//嵌套属性的复制
	public void error_nestedCopy01() throws IllegalAccessException,
			InvocationTargetException {
		DynaBean _author = new LazyDynaBean();
		_author.set("name", "dragon");
		
		DynaBean _address = new LazyDynaBean();
		_address.set("name", "gz");
		_author.set("address", _address);

		Author author = new Author();
		
		//这里抛出IllegalArgumentException异常，因为两个对象的address属性类型不匹配
		BeanUtils.copyProperties(author, _author);
	}

	@Test(expected=NestedNullException.class)
	//嵌套属性的复制
	public void error_nestedCopy02() throws IllegalAccessException,
			InvocationTargetException {
		DynaBean _author = new LazyDynaBean();
		_author.set("address.name", "gz");

		Author author = new Author();
		author.setAddress(new Address());
		
		//这里因为_author中没有address，抛出NestedNullException异常
		BeanUtils.copyProperties(author, _author);
	}

	@Test(expected=IllegalArgumentException.class)
	//嵌套属性的复制
	public void error_request2form01() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", "dragon");
		map.put("nestedBean.name", "gz");

		DynaBean bean = new LazyDynaBean();
		
		//这里因nestedBean未初始化，故抛出异常:java.lang.IllegalArgumentException: No bean specified
		BeanUtils.populate(bean, map);
	}

	@Test
	//请求参数到LazyDynaBean的转换测试
	public void request2form_nestedDynaBean() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		map.put("name", "dragon");
		map.put("nestedBean.name", "gz");//模拟嵌套属性

		DynaBean bean = new LazyDynaBean();
		DynaBean nestedBean = new LazyDynaBean();
		nestedBean.set("name2", "name2");//用于检测没有破坏未设置的属性
		bean.set("nestedBean", nestedBean);
		
		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将请求参数复制到ActionForm中
		BeanUtils.populate(bean, map);
		
		Assert.assertEquals("dragon", bean.get("name"));
		Assert.assertNotNull(bean.get("nestedBean"));
		Assert.assertEquals(LazyDynaBean.class,bean.get("nestedBean").getClass());
		Assert.assertEquals("gz", ((DynaBean)bean.get("nestedBean")).get("name"));
		Assert.assertEquals("name2", ((DynaBean)bean.get("nestedBean")).get("name2"));
	}

	@SuppressWarnings("unchecked")
	@Test
	//请求参数到LazyDynaBean的转换测试
	public void request2form_nestedIndexedProperties() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		//模拟索引属性
		map.put("myList[0]", "value0");
		map.put("myList[2]", "value2");

		DynaBean bean = new LazyDynaBean();
		
		//以下两行可以不初始化IndexedProperties，BeanUtils.populate会自动以ArrayList初始化
		List<String> indexedPro = new ArrayList<String>();
		bean.set("myList", indexedPro);
		indexedPro.add("oldValue0");
		indexedPro.add("oldValue1");
		indexedPro.add("oldValue2");
		
		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将请求参数复制到ActionForm中
		BeanUtils.populate(bean, map);
		
		Assert.assertNotNull(bean.get("myList"));
		Assert.assertEquals(ArrayList.class,bean.get("myList").getClass());
		Assert.assertEquals("value0", ((List<String>)bean.get("myList")).get(0));
		Assert.assertEquals("oldValue1", ((List<String>)bean.get("myList")).get(1));
		Assert.assertEquals("value2", ((List<String>)bean.get("myList")).get(2));
		try {
			//下面这句会抛异常：java.lang.IndexOutOfBoundsException: Index: 3, Size: 3
			((List<String>)bean.get("myList")).get(3);
		} catch (IndexOutOfBoundsException e) {
			Assert.assertEquals(IndexOutOfBoundsException.class, e.getClass());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	//请求参数到LazyDynaBean的转换测试
	public void request2form_nestedIndexedProperties2() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		//模拟索引属性
		map.put("myList[0].label", "label0");
		map.put("myList[0].value", "value0");

		DynaBean bean = new LazyDynaBean();
		
		//这里初始化是必须的否则BeanUtils.populate抛异常：java.lang.IllegalArgumentException: No bean specified
		List<LabelValueBean> indexedPro = new ArrayList<LabelValueBean>();
		bean.set("myList", indexedPro);
		indexedPro.add(new LabelValueBean("oldLabel0","oldValue0"));
		indexedPro.add(new LabelValueBean("oldLabel1","oldValue1"));
		
		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将请求参数复制到ActionForm中
		BeanUtils.populate(bean, map);
		
		Assert.assertNotNull(bean.get("myList"));
		Assert.assertEquals(ArrayList.class,bean.get("myList").getClass());
		Assert.assertEquals("label0", ((List<LabelValueBean>)bean.get("myList")).get(0).getLabel());
		Assert.assertEquals("value0", ((List<LabelValueBean>)bean.get("myList")).get(0).getValue());
		Assert.assertEquals("oldLabel1", ((List<LabelValueBean>)bean.get("myList")).get(1).getLabel());
		Assert.assertEquals("oldValue1", ((List<LabelValueBean>)bean.get("myList")).get(1).getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	//请求参数到LazyDynaBean的转换测试
	public void request2form_nestedMappedProperties() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		//模拟索引属性
		map.put("myMap(key0)", "value0");
		map.put("myMap(key1)", "value1");

		DynaBean bean = new LazyDynaBean();
		
		//以下两行可以不初始化MappedProperties，BeanUtils.populate会自动以HashMap初始化，
		//初始化的目的是提供没有复制过来的值
		Map<String,String> mappedPro = new HashMap<String,String>();
		bean.set("myMap", mappedPro);
		mappedPro.put("key10", "value10");//检验没有提供的值
		
		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将请求参数复制到ActionForm中
		BeanUtils.populate(bean, map);
		
		Assert.assertNotNull(bean.get("myMap"));
		Assert.assertEquals(HashMap.class,bean.get("myMap").getClass());
		Assert.assertEquals("value0", ((Map)bean.get("myMap")).get("key0"));
		Assert.assertEquals("value1", ((Map)bean.get("myMap")).get("key1"));
		Assert.assertEquals("value10", ((Map)bean.get("myMap")).get("key10"));
		Assert.assertNull(((Map)bean.get("myMap")).get("key2"));
	}

	@SuppressWarnings("unchecked")
	@Test
	//请求参数到LazyDynaBean的转换测试
	public void request2form_nestedMappedProperties2() throws IllegalAccessException,
			InvocationTargetException {
		//模拟请求的参数
		Map<String,String> map = new HashMap<String,String>();
		//模拟索引属性
		map.put("myMap(key0).label", "label0");
		map.put("myMap(key0).value", "value0");
		map.put("myMap(key1).label", "label1");
		map.put("myMap(key1).value", "value1");

		DynaBean bean = new LazyDynaBean();
		
		//必须的初始化
		Map mappedPro = new HashMap();
		bean.set("myMap", mappedPro);
		mappedPro.put("key0", new LabelValueBean("oldLabel","oldValue1"));//怪异，连这个也要初始化才行哦，否则抛异常：java.lang.IllegalArgumentException: No bean specified
		mappedPro.put("key1", new LazyDynaBean());//这也行，可见主要对象被初始化并可以设置属性就行
		
		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将请求参数复制到ActionForm中
		BeanUtils.populate(bean, map);
		
		Assert.assertNotNull(bean.get("myMap"));
		Assert.assertEquals(HashMap.class,bean.get("myMap").getClass());
		Assert.assertEquals(LabelValueBean.class, ((Map)bean.get("myMap")).get("key0").getClass());
		Assert.assertEquals("label0", ((LabelValueBean)((Map)bean.get("myMap")).get("key0")).getLabel());
		Assert.assertEquals("value0", ((LabelValueBean)((Map)bean.get("myMap")).get("key0")).getValue());

		Assert.assertEquals(LazyDynaBean.class, ((Map)bean.get("myMap")).get("key1").getClass());
		Assert.assertEquals("label1", ((LazyDynaBean)((Map)bean.get("myMap")).get("key1")).get("label"));
		Assert.assertEquals("value1", ((LazyDynaBean)((Map)bean.get("myMap")).get("key1")).get("value"));
	}

	@Test
	//LazyDynaBean到model的转换测试
	public void form2model() throws IllegalAccessException,
			InvocationTargetException {
		//初始化LazyDynaBean
		DynaBean _author = new LazyDynaBean();
		_author.set("name", "dragon");
		DynaBean _address = new LazyDynaBean();
		_address.set("id", 1);//_address.set("id", "1")也行
		_address.set("name", "gz");
		_author.set("address", _address);
		
		//初始化model：Author有个嵌套的Address对象
		Author author = new Author();
		author.setName("initName");//用于检测值应被覆盖
		Address address = new Address();//初始化嵌套对象
		address.setName("name");
		address.setCode("code");//用于检测嵌套对象的值没被覆盖
		author.setAddress(address);

		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将LazyDynaBean复制到model
		BeanUtilsEx.copyProperties(author, _author);
		
		Assert.assertEquals("dragon", author.getName());
		Assert.assertNotNull(author.getAddress());
		Assert.assertEquals("gz", author.getAddress().getName());
		Assert.assertEquals("code", author.getAddress().getCode());
		Assert.assertEquals(1, author.getAddress().getId());
	}

	@Test(expected=InvocationTargetException.class)
	//model到LazyDynaBean的转换测试
	public void model2form() throws IllegalAccessException,
			InvocationTargetException {
		//初始化LazyDynaBean
		DynaBean _author = new LazyDynaBean();
		_author.set("name", "initName");//用于检测值应被覆盖
		DynaBean _address = new LazyDynaBean();//初始化嵌套对象
		_address.set("code", "code");//用于检测嵌套对象的值没被覆盖
		_author.set("address", _address);
		
		//初始化model：Author有个嵌套的Address对象
		Author author = new Author();
		author.setName("dragon");
		Address address = new Address();
		address.setId(1);
		address.setName("gz");
		address.setCode("code");
		author.setAddress(address);

		//注册默认的转换器
		ConverterUtils.registDefault();
		
		//模拟将model复制到LazyDynaBean
		//java.lang.reflect.InvocationTargetException: Cannot set address of class org.apache.commons.beanutils.LazyDynaBean
		BeanUtilsEx.copyProperties(_author, author);
	}

}
