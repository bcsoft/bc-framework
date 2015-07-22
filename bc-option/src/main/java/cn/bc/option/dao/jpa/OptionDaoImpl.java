package cn.bc.option.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.option.dao.OptionDao;
import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;
import cn.bc.orm.jpa.JpaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * 选项Dao接口的实现
 *
 * @author dragon
 */
public class OptionDaoImpl implements OptionDao {
	private static Logger logger = LoggerFactory.getLogger(OptionDaoImpl.class);
	@PersistenceContext
	private EntityManager entityManager;

	public String getItemValue(final String groupKey, final String itemKey) {
		if (groupKey == null || itemKey == null)
			return null;

		// 构建sql
		String hql = "select i.value_ from BC_OPTION_ITEM i inner join BC_OPTION_GROUP g on g.id = i.pid where g.key_=? and i.key_=?";

		// 执行查询
		List<String> list = JpaUtils.executeNativeQuery(entityManager, hql, new Object[]{groupKey, itemKey});
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			if (list.size() > 1) {
				logger.warn("查询到多个值,仅返回第一个:groupKey={}, itemKey={}", groupKey, itemKey);
			}
			return list.get(0);
		}
	}

	public List<OptionGroup> findOptionGroup() {
		String hql = "from OptionGroup _alias order by _alias.orderNo";
		return JpaUtils.executeQuery(entityManager, hql, (Objects[]) null);
	}

	public List<OptionItem> findOptionItemByGroupValue(String optionGroupValue) {
		String hql = "from OptionItem _alias where _alias.optionGroup.value = ? order by _alias.orderNo";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("optionGroupValue=" + optionGroupValue);
		}
		return JpaUtils.executeQuery(entityManager, hql, new Object[]{optionGroupValue});
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
		List<OptionItem> all = JpaUtils.executeQuery(entityManager, hql, args);

		// 生成列表
		Map<String, List<OptionItem>> map = new LinkedHashMap<>();
		List<OptionItem> sub = null;
		String key = null;
		for (OptionItem oi : all) {
			if (!oi.getOptionGroup().getValue().equals(key)) {
				if (key != null) {
					map.put(key, sub);
				}

				key = oi.getOptionGroup().getValue();
				sub = new ArrayList<>();
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
		return JpaUtils.executeQuery(entityManager, hql, new Object[]{optionGroupKey});
	}

	public Map<String, List<Map<String, String>>> findOptionItemByGroupKeys(
			String[] optionGroupValues) {
		return this.findOptionItemByGroupKeys(optionGroupValues, null);
	}

	public Map<String, List<Map<String, String>>> findActiveOptionItemByGroupKeys(String[] optionGroupKeys) {
		return this.findOptionItemByGroupKeys(optionGroupKeys, new int[]{BCConstants.STATUS_ENABLED});
	}

	private Map<String, List<Map<String, String>>> findOptionItemByGroupKeys(String[] optionGroupKeys, int[] statuses) {
		if (optionGroupKeys == null || optionGroupKeys.length == 0)
			return null;

		String hql = "select g.key_ as gkey,i.key_ as key,i.value_ as value,i.id as id from BC_OPTION_ITEM i inner join BC_OPTION_GROUP g on g.id=i.pid";
		hql += " where g.key_";
		List<Object> args = new ArrayList<>();

		// 分组
		if (optionGroupKeys.length == 1) {
			hql += " = ?";
			args.add(optionGroupKeys[0]);
		} else {
			args.add(optionGroupKeys[0]);
			hql += " in (?";
			for (int i = 1; i < optionGroupKeys.length; i++) {
				hql += ",?";
				args.add(optionGroupKeys[i]);
			}
			hql += ")";
		}

		// 状态
		if (statuses != null && statuses.length > 0) {
			if (statuses.length == 1) {
				hql += " and i.status_=?";
				args.add(statuses[0]);
			} else {
				args.add(statuses[0]);
				hql += " and i.status_ in (?";
				for (int i = 1; i < statuses.length; i++) {
					hql += ",?";
					args.add(statuses[i]);
				}
				hql += ")";
			}
		}

		hql += " order by g.order_,i.order_";
		List<Map<String, String>> all = JpaUtils.executeNativeQuery(entityManager, hql, args, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<>();
				int i = 0;
				oi.put("gkey", rs[i++].toString());// 保存group的key用于下面的判断
				oi.put("key", rs[i++].toString());
				oi.put("value", rs[i].toString());
				return oi;
			}
		});

		// 生成列表
		Map<String, List<Map<String, String>>> map = new LinkedHashMap<>();
		List<Map<String, String>> sub = null;
		String key = null;
		for (Map<String, String> oi : all) {
			if (!oi.get("gkey").equals(key)) {
				key = oi.get("gkey");
				sub = new ArrayList<>();
				map.put(key, sub);
				sub.add(oi);
			} else {
				sub.add(oi);
			}
		}

		// 对没有的groupKey,生成一个空的list
		List<String> empty = new ArrayList<>();
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
				t.append(k).append("(").append(list.size()).append("):\r\n");
				for (Map<String, String> oi : list) {
					t.append("  ").append(oi.get("key")).append("=").append(oi.get("value")).append("\r\n");
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
		List<OptionItem> all = JpaUtils.executeQuery(entityManager, hql, args);

		// 生成列表
		Map<String, List<OptionItem>> map = new LinkedHashMap<>();
		List<OptionItem> sub = null;
		String key = null;
		for (OptionItem oi : all) {
			if (!oi.getOptionGroup().getKey().equals(key)) {
				if (key != null) {
					map.put(key, sub);
				}

				key = oi.getOptionGroup().getKey();
				sub = new ArrayList<>();
				sub.add(oi);
			} else {
				sub.add(oi);
			}
		}
		return map;
	}
}