package cn.bc.placeorigin.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.placeorigin.domain.PlaceOrigin;

/**
 * 籍贯Service接口
 * 
 * @author dragon
 * 
 */
public interface PlaceOriginService extends CrudService<PlaceOrigin> {
	/**
	 * 获取指定编码的籍贯
	 *
	 * @param core 籍贯编码
	 * @return
	 */
	PlaceOrigin loadByCode(String code);
	
	/**
	 * 根据身份证号码获取最匹配的籍贯信息
	 *
	 * @param cardNo 身份证号
	 * @return
	 */
	PlaceOrigin loadByIdentityCard(String cardNo);
}
