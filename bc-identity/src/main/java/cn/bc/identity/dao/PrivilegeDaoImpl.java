package cn.bc.identity.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PrivilegeDaoImpl implements PrivilegeDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public int addUser(Long roleId, Long userId) {
		
		String sqlsql = "insert into bc_identity_role_actor (aid,rid) values(?,?)";
		return jdbcTemplate.update(sqlsql, userId, roleId);
	}

	@Override
	public long addActorByRole(Long roleId, Long actorId) {
			String insql = "insert into bc_identity_role_actor (rid,aid) values(?,?)" ;
			return jdbcTemplate.update(insql, roleId, actorId);
	}

	@Override
	public int deleteActorByRole(Long roleId, Long actorId) {
		String sql = "delete from bc_identity_role_actor where aid=? and rid=?";
		return jdbcTemplate.update(sql, actorId, roleId);
	}

	@Override//10114209
	public Long getActorbyHistoryActor(Long id) {
		String sql = "select actor_id from bc_identity_actor_history where id=?";
		
		return jdbcTemplate.queryForLong(sql, id);
	}

	@Override
	public int ActorRelationIsExist(Long roleId, Long actorId) {
		String sql = "select count(*) from bc_identity_role_actor where rid=? and aid=?";
		return jdbcTemplate.queryForInt(sql, roleId, actorId);
	}

	@Override
	public int deleteResourceByRole(long roleId, long resourceId) {
		String sql = "delete from bc_identity_role_resource where rid=? and sid=?";
		return jdbcTemplate.update(sql, roleId, resourceId);
	}

	@Override
	public int RoleResouceIsExist(long roleId, long resourceId) {
		String sql = "select count(*) from bc_identity_role_resource where rid=? and sid=?";
		return jdbcTemplate.queryForInt(sql, roleId, resourceId);
	}

	@Override
	public long addResourceByRole(long roleId, long resourceId) {
		String sql = "insert into bc_identity_role_resource (rid, sid) values(?,?)";
		return jdbcTemplate.update(sql, roleId, resourceId);
	}


}
