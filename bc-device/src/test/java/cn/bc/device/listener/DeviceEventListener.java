package cn.bc.device.listener;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import cn.bc.device.domain.DeviceEventEntity;
import cn.bc.device.domain.DeviceEventNew;
import cn.bc.device.event.DeviceEvent;
import cn.bc.device.service.DeviceEventNewPublishService;
import cn.bc.device.service.DeviceEventNewPublishServiceImpl;
import cn.bc.device.service.DeviceEventNewService;
import cn.bc.device.service.DeviceEventService;

/**
 * 设备事件的监听器---测试示例
 * 
 * @author hwx
 * 
 */
public class DeviceEventListener  implements ApplicationListener<DeviceEvent> {
	private DeviceEventNewPublishService deviceEventNewPublishService;
	private DeviceEventNewService deviceEventNewService;
	private DeviceEventService deviceEventService;
	public static boolean ok;
	
	@Autowired
	public void setDeviceEventNewPublishService(
			DeviceEventNewPublishService deviceEventNewPublishService) {
		this.deviceEventNewPublishService = deviceEventNewPublishService;
	}

	@Autowired
	public void setDeviceEventNewService(DeviceEventNewService deviceEventNewService) {
		this.deviceEventNewService = deviceEventNewService;
	}

	@Autowired
	public void setDeviceEventService(DeviceEventService deviceEventService) {
		this.deviceEventService = deviceEventService;
	}
	
	public void onApplicationEvent(DeviceEvent event) {
		ok = false;
		System.out.println("设备类型"  + event.getType());
		System.out.println("设备编码" + event.getCode());
		System.out.println("设备数据" + event.getData());
		System.out.println("事件触发时间" + event.getTriggerTime());
		System.out.println("appid" +event.getAppId());
		
	}

}
