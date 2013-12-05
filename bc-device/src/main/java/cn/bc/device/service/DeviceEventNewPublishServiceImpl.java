package cn.bc.device.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cn.bc.device.domain.DeviceEventEntity;
import cn.bc.device.domain.DeviceEventNew;
import cn.bc.device.event.DeviceEvent;

/**
 * 设备事件发布service实现
 * 
 * @author hwx
 * 
 */
public class DeviceEventNewPublishServiceImpl implements
		DeviceEventNewPublishService, ApplicationEventPublisherAware {
	private ApplicationEventPublisher eventPublisher;
	private DeviceEventNewService deviceEventNewService;
	private DeviceEventService deviceEventService;

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;

	}

	@Autowired
	public void setDeviceEventService(DeviceEventService deviceEventService) {
		this.deviceEventService = deviceEventService;
	}

	@Autowired
	public void setDeviceEventNewService(
			DeviceEventNewService deviceEventNewService) {
		this.deviceEventNewService = deviceEventNewService;
	}

	public void publishEvent() {
		List<DeviceEventNew> deviceEventNewList = this.deviceEventNewService
				.createQuery().list();
		if (deviceEventNewList != null) {
			for (int i = 0; i < deviceEventNewList.size(); i++) {
				// 数据库中存在的事件
				DeviceEventEntity e = deviceEventService
						.load(deviceEventNewList.get(i).getId());
				// 要被广播的事件
				DeviceEvent deviceEvent = new DeviceEvent(this, e.getDevice()
						.getCode(), e.getType(), e.getTriggerTime(),
						e.getAppId(), e.getData());
				this.eventPublisher.publishEvent(deviceEvent);
				deviceEventNewService.delete(deviceEventNewList.get(i).getId());
			}

		}

	}
}
