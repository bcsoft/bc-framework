package cn.bc.option.service;

import java.util.List;
import java.util.Map;

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

	public List<OptionGroup> findOptionGroup() {
		return optionDao.findOptionGroup();
	}

	public List<OptionItem> findOptionItemByGroupKey(String optionGroupKey) {
		return optionDao.findOptionItemByGroupKey(optionGroupKey);
	}

	public List<OptionItem> findOptionItemByGroupKeyWithCurrent(
			String optionGroupKey, String currentItemKey,
			String currentItemValue) {
		List<OptionItem> list = optionDao
				.findOptionItemByGroupValue(optionGroupKey);
		if (currentItemKey != null) {
			if (currentItemValue == null)
				currentItemValue = currentItemKey;
			boolean exist = false;
			for (OptionItem oi : list) {
				if (oi.getKey().equals(currentItemKey)) {
					exist = true;
					break;
				}
			}
			if (!exist)
				list.add(new OptionItem(currentItemKey, currentItemValue));
		}
		return list;
	}

	public Map<String, List<OptionItem>> findOptionItemByGroupKeys(
			String[] optionGroupKeys) {
		return optionDao.findOptionItemByGroupKeys(optionGroupKeys);
	}
}
