package cn.bc.option.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.option.dao.OptionDao;
import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;

/**
 * 选项Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class OptionDaoImpl implements OptionDao {
	private static Log logger = LogFactory.getLog(OptionDaoImpl.class);
	private JpaTemplate jpaTemplate;

	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.jpaTemplate = new JpaTemplate(entityManagerFactory);
	}

	@SuppressWarnings("unchecked")
	public List<OptionGroup> findOptionGroup() {
		String hql = "from OptionGroup _alias order by _alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return (List<OptionGroup>) this.jpaTemplate.find(hql);
	}

	@SuppressWarnings("unchecked")
	public List<OptionItem> findOptionItemByGroupValue(String optionGroupValue) {
		String hql = "from OptionItem _alias where _alias.optionGroup.value = ? order by _alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("optionGroupValue=" + optionGroupValue);
		}
		return (List<OptionItem>) this.jpaTemplate.find(hql, optionGroupValue);
	}

	public Map<String, List<OptionItem>> findOptionItemByGroupValues(
			String[] optionGroupValues) {
		if (optionGroupValues == null || optionGroupValues.length == 0)
			return null;

		String hql = "from OptionItem _alias where _alias.optionGroup.value";
		Object[] args;
		if (optionGroupValues.length == 1) {
			hql += " = ?";
			args = new Object[1];
			args[0] = optionGroupValues[0];
		} else {
			args = new Object[optionGroupValues.length];
			args[0] = optionGroupValues[0];
			hql += " in (?";
			for (int i = 1; i < optionGroupValues.length; i++) {
				hql += ",?";
				args[i] = optionGroupValues[i];
			}
			hql += ")";
		}
		hql += " order by _alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ StringUtils.arrayToCommaDelimitedString(args));
		}
		@SuppressWarnings("unchecked")
		List<OptionItem> all = (List<OptionItem>) this.jpaTemplate.find(hql,
				args);

		// 生成列表
		Map<String, List<OptionItem>> map = new LinkedHashMap<String, List<OptionItem>>();
		List<OptionItem> sub = null;
		String key = null;
		for (OptionItem oi : all) {
			if (!oi.getOptionGroup().getValue().equals(key)) {
				if (key != null) {
					map.put(key, sub);
				}

				key = oi.getOptionGroup().getValue();
				sub = new ArrayList<OptionItem>();
				sub.add(oi);
			} else {
				sub.add(oi);
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public List<OptionItem> findOptionItemByGroupKey(String optionGroupKey) {
		String hql = "from OptionItem _alias where _alias.optionGroup.key = ? order by _alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("optionGroupKey=" + optionGroupKey);
		}
		return (List<OptionItem>) this.jpaTemplate.find(hql, optionGroupKey);
	}

	public Map<String, List<OptionItem>> findOptionItemByGroupKeys(
			String[] optionGroupKeys) {
		if (optionGroupKeys == null || optionGroupKeys.length == 0)
			return null;

		String hql = "from OptionItem _alias where _alias.optionGroup.key";
		Object[] args;
		if (optionGroupKeys.length == 1) {
			hql += " = ?";
			args = new Object[1];
			args[0] = optionGroupKeys[0];
		} else {
			args = new Object[optionGroupKeys.length];
			args[0] = optionGroupKeys[0];
			hql += " in (?";
			for (int i = 1; i < optionGroupKeys.length; i++) {
				hql += ",?";
				args[i] = optionGroupKeys[i];
			}
			hql += ")";
		}
		hql += " order by _alias.optionGroup.orderNo,_alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ StringUtils.arrayToCommaDelimitedString(args));
		}
		@SuppressWarnings("unchecked")
		List<OptionItem> all = (List<OptionItem>) this.jpaTemplate.find(hql,
				args);

		// 生成列表
		Map<String, List<OptionItem>> map = new LinkedHashMap<String, List<OptionItem>>();
		List<OptionItem> sub = null;
		String key = null;
		for (OptionItem oi : all) {
			if (!oi.getOptionGroup().getKey().equals(key)) {
				if (key != null) {
					map.put(key, sub);
				}

				key = oi.getOptionGroup().getKey();
				sub = new ArrayList<OptionItem>();
				sub.add(oi);
			} else {
				sub.add(oi);
			}
		}
		return map;
	}
}
