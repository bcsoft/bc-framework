package cn.bc.device.web.struts2;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import cn.bc.device.domain.Device;
import cn.bc.device.service.DeviceService;
import cn.bc.identity.web.struts2.FileEntityAction;

/**
 * 设备表单Action
 * 
 * @author luliang
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DeviceAction extends FileEntityAction<Long, Device> {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private DeviceService deviceService;
	
	@Autowired
	public void setDeviceService(DeviceService deviceService) {
		this.deviceService = deviceService;
		this.setCrudService(deviceService);
	}
	
	@Override
	protected boolean useFormPrint() {
		return false;
	}

	@Override
	protected void afterCreate(Device entity) {
		super.afterCreate(entity);
		// 自动生成uid
		entity.setUid(this.getIdGeneratorService().next("device.uid"));
	}
	
	@Override
	protected void beforeSave(Device entity) {
		super.beforeSave(entity);
		if(entity.getCode().isEmpty()){
			Random random = new Random();
			// 编码
			entity.setCode(entity.getModel()+"."+random.nextInt(100));
		}
	}
}
