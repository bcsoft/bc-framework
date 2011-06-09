package cn.bc.web.ui;

import java.util.List;

/**
 * ui组件通用接口
 * @author dragon
 *
 */
public interface Component extends Render {
	/**
	 * @return 组件的子组件
	 */
	List<Render> getChildren();
	
	/**
	 * 添加一个子组件
	 * @param child
	 */
	Component addChild(Render child);
	
	/**
	 * 删除一个子组件
	 * @param child
	 */
	Component removeChild(Render child);
	
	/**
	 * @return 组件的标记，如div、span、td等
	 */
	String getTag();
	
	/**
	 * @return 组件id
	 */
	String getId();
	
	/**
	 * 设置组件的id属性
	 * @param id
	 */
	Component setId(String id);
	
	/**
	 * 获取组件的属性值
	 * @param key 属性名
	 * @return 属性值
	 */
	String getAttr(String key);
	
	/**
	 * 设置组件的属性值
	 * @param key 属性名
	 * @param value 属性值
	 */
	Component setAttr(String key, String value);
	
	/**
	 * @return 组件的名称
	 */
	String getName();
	/**
	 * 设置组件的名称属性
	 * @param name
	 */
	Component setName(String name);
	
	/**
	 * @return 组件的样式类名
	 */
	String getClazz();
	/**
	 * 添加一个样式类名
	 * @param clazz
	 */
	Component addClazz(String clazz);
	
	/**
	 * @return 组件的样式
	 */
	String getStyle();
	
	/**
	 * 设置组件的样式属性
	 * @param key
	 * @param value
	 */
	Component addStyle(String key,String value);
	
	/**
	 * @return 组件的动作
	 */
	String getAction();
	
	/**
	 * 设置组件的动作属性
	 * @param action
	 */
	Component setAction(String action);
	
	public String getTitle();

	public Component setTitle(String title);
}
