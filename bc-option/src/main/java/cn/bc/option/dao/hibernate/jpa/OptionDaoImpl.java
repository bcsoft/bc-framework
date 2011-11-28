package cn.bc.option.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.option.dao.OptionDao;
import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

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

	public String getItemValue(final String groupKey, final String itemKey) {
		if (groupKey == null || itemKey == null)
			return null;

		// 构建sql
		final StringBuffer hql = new StringBuffer(
				"select i.value_ from BC_OPTION_ITEM i inner join BC_OPTION_GROUP g on g.id = i.pid where g.key_=? and i.key_=?");

		// 执行查询
		return this.jpaTemplate.execute(new JpaCallback<String>() {
			public String doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql.toString());

				// 注入参数:jpa的索引号从1开始
				queryObject.setParameter(1, groupKey);
				queryObject.setParameter(2, itemKey);
				@SuppressWarnings("unchecked")
				List<Object> list = queryObject.getResultList();
				if (list == null || list.isEmpty()) {
					return null;
				} else {
					if (list.size() > 1) {
						logger.warn("查询到多个值,仅返回第一个:groupKey" + groupKey
								+ ",itemKey=" + itemKey);
					}
					return list.get(0).toString();
				}
			}
		});
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

	public Map<String, List<Map<String, String>>> findOptionItemByGroupKeys(
			String[] optionGroupKeys) {
		if (optionGroupKeys == null || optionGroupKeys.length == 0)
			return null;

		String hql = "select g.key_,i.key_,i.value_,i.id from BC_OPTION_ITEM i inner join BC_OPTION_GROUP g on g.id=i.pid";
		hql += " where g.key_";
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
		hql += " order by g.order_,i.order_";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ StringUtils.arrayToCommaDelimitedString(args));
		}
		List<Map<String, String>> all = HibernateJpaNativeQuery
				.executeNativeSql(jpaTemplate, hql, args,
						new RowMapper<Map<String, String>>() {
							public Map<String, String> mapRow(Object[] rs,
									int rowNum) {
								Map<String, String> oi = new HashMap<String, String>();
								int i = 0;
								oi.put("gkey", rs[i++].toString());// 保存group的key用于下面的判断
								oi.put("key", rs[i++].toString());
								oi.put("value", rs[i++].toString());
								return oi;
							}
						});

		// 生成列表
		Map<String, List<Map<String, String>>> map = new LinkedHashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> sub = null;
		String key = null;
		for (Map<String, String> oi : all) {
			if (!oi.get("gkey").equals(key)) {
				key = oi.get("gkey");
				sub = new ArrayList<Map<String, String>>();
				map.put(key, sub);
				sub.add(oi);
			} else {
				sub.add(oi);
			}
		}

		// 对没有的groupKey,生成一个空的list
		List<String> empty = new ArrayList<String>();
		for (String key1 : optionGroupKeys) {
			key = null;
			for (String key2 : map.keySet()) {
				if (key1.equals(key2)) {
					key = key2;
					break;
				}
			}
			if (key == null) {
				empty.add(key1);
			}
		}
		for (String key1 : empty) {
			map.put(key1, new ArrayList<Map<String, String>>());
		}

		if (logger.isDebugEnabled()) {
			List<Map<String, String>> list;
			StringBuffer t = new StringBuffer();
			for (String k : optionGroupKeys) {
				list = map.get(k);
				t.append(k + "(" + list.size() + "):\r\n");
				for (Map<String, String> oi : list) {
					t.append("  " + oi.get("key") + "=" + oi.get("value")
							+ "\r\n");
				}
			}
			logger.debug(t.toString());
		}

		return map;
	}

	// 旧方法的备份，请不要使用
	@Deprecated
	public Map<String, List<OptionItem>> findOptionItemByGroupKeys2(
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
