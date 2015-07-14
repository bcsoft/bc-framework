package cn.bc.identity.dao.hibernate.jpa;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.identity.dao.ResourceDao;
import cn.bc.identity.domain.Resource;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源Dao接口的实现
 *
 * @author dragon
 */
public class ResourceDaoImpl extends JpaCrudDao<Resource> implements ResourceDao {
	public List<Map<String, String>> find4option(Integer[] actorTypes, Integer[] actorStatues) {
		ArrayList<Object> args = new ArrayList<>();
		StringBuffer hql = new StringBuffer();
		hql.append("select a.id,a.type_,a.name,a.pname");
		hql.append(" from BC_IDENTITY_RESOURCE a");

		boolean isWhere = true;
		// 类型
		if (actorTypes != null && actorTypes.length > 0) {
			isWhere = false;
			if (actorTypes.length == 1) {
				hql.append(" where a.type_=?");
				args.add(actorTypes[0]);
			} else {
				hql.append(" where a.type_ in (?");
				args.add(actorTypes[0]);
				for (int i = 1; i < actorTypes.length; i++) {
					hql.append(",?");
					args.add(actorTypes[i]);
				}
				hql.append(")");
			}
		}

		// 状态
		if (actorStatues != null && actorStatues.length > 0) {
			if (actorStatues.length == 1) {
				hql.append((isWhere ? " where" : " and") + " a.status_=?");
				args.add(actorStatues[0]);
			} else {
				hql.append((isWhere ? " where" : " and") + " a.status_ in (?");
				args.add(actorStatues[0]);
				for (int i = 1; i < actorStatues.length; i++) {
					hql.append(",?");
					args.add(actorStatues[i]);
				}
				hql.append(")");
			}
		}

		// 排序
		hql.append(" order by a.order_");

		return executeNativeQuery(hql.toString(), args, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> map = new HashMap<>();
				int i = 0;
				map.put("id", rs[i++].toString());
				map.put("type", rs[i++].toString());
				map.put("name", rs[i++].toString());
				map.put("pname", rs[i] != null ? rs[i].toString() : null);
				return map;
			}
		});
	}

	@Override
	public Resource save(Resource entity) {
		// 重新设置pname的值
		if (entity.getBelong() != null) {
			entity.setPname(this.load(entity.getBelong().getId()).getFullName());
		} else {
			entity.setPname(null);
		}
		return super.save(entity);
	}
}
