package cn.bc.device.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import cn.bc.device.domain.Device;
import cn.bc.device.service.DeviceService;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.web.ui.json.Json;

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
	public String save() throws Exception {
		Json json = new Json();
		Device device = this.getE();
		Long existsId = null;

		// 保存之前检测设备编号是否唯一：仅在新建时检测
		if (device.getId() == null) {
			existsId = this.deviceService.checkDeviceCodeIsExist(device
					.getCode());
		}

		if (existsId != null) {
			json.put("success", false);
			json.put("msg", getText("device.error.deviceCodeIsExist"));
		} else {
			// 执行基类的保存
			super.save();
			json.put("id", device.getId());
			json.put("success", true);
			json.put("msg", getText("form.save.success"));
		}
		this.json = json.toString();
		return "json";
	}
}
