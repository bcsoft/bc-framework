package cn.bc.device.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import cn.bc.device.domain.DeviceEventEntity;
import cn.bc.device.service.DeviceEventService;
import cn.bc.web.struts2.EntityAction;

/**
 * 设备事件表单Action
 * 
 * @author luliang
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DeviceEventAction extends EntityAction<Long, DeviceEventEntity> {

	private static final long serialVersionUID = 1L;
	private DeviceEventService deviceEventService;

	@Autowired
	public void setDeviceEventService(DeviceEventService deviceEventService) {
		this.setCrudService(deviceEventService);
		this.deviceEventService = deviceEventService;
	}

	@Override
	protected boolean useFormPrint() {
		return false;
	}
}
