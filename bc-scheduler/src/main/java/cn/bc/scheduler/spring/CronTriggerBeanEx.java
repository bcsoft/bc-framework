package cn.bc.scheduler.spring;

import java.text.ParseException;

import org.springframework.scheduling.quartz.CronTriggerBean;

/**
 * Spring CronTriggerBean的扩展
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class CronTriggerBeanEx extends CronTriggerBean {
	private static final long serialVersionUID = 8862687602599566646L;

	/*
	 * 复写基类的方法，控制必须手工调用bcInit方法初始化
	 * 
	 * @see
	 * org.springframework.scheduling.quartz.CronTriggerBean#afterPropertiesSet
	 * ()
	 */
	public void afterPropertiesSet() throws ParseException {
		// do nothig;
	}

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void bcInit() throws Exception {
		super.afterPropertiesSet();
	}
}
