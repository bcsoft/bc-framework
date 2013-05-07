/**
 * 
 */
package cn.bc.subscribe.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

import cn.bc.subscribe.event.SubscribeEvent;

/**
 * 用户订阅事件的监听器
 * 
 * @author lbj
 * 
 */
public class SubscribeListener implements ApplicationListener<SubscribeEvent> {
	private static Log logger = LogFactory.getLog(SubscribeListener.class);

	public void onApplicationEvent(SubscribeEvent event) {


	}
}
