package cn.bc.option.service;

import java.util.List;

import cn.bc.option.dao.OptionDao;
import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;

/**
 * 选项service接口的实现
 * 
 * @author dragon
 * 
 */
public class OptionServiceImpl implements OptionService {
	private OptionDao optionDao;

	public void setOptionDao(OptionDao optionDao) {
		this.optionDao = optionDao;
	}

	public List<OptionItem> findOptionItemByGroupValue(String optionGroupValue) {
		return optionDao.findOptionItemByGroupValue(optionGroupValue);
	}

	public List<OptionItem> findOptionItemByGroupValue(String optionGroupValue,
			boolean insertEmptyOption) {
		List<OptionItem> list = optionDao.findOptionItemByGroupValue(optionGroupValue);
		if(insertEmptyOption){
			OptionItem oi = new OptionItem(); 
			list.add(0, oi);
		}
		return list;
	}

	public List<OptionGroup> findOptionGroup() {
		return optionDao.findOptionGroup();
	}
}
