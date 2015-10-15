package cn.bc.identity.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class PrivilegeDaoImpl implements PrivilegeDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addUser(Long roleId, Long userId) {
		String sql = "insert into bc_identity_role_actor (aid,rid) values(?,?)";
		return jdbcTemplate.update(sql, userId, roleId);
	}

	@Override
	public long addActorByRole(Long roleId, Long actorId) {
		String sql = "insert into bc_identity_role_actor (rid,aid) values(?,?)";
		return jdbcTemplate.update(sql, roleId, actorId);
	}

	@Override
	public int deleteActorByRole(Long roleId, Long actorId) {
		String sql = "delete from bc_identity_role_actor where aid=? and rid=?";
		return jdbcTemplate.update(sql, actorId, roleId);
	}

	@Override//10114209
	public Long getActorbyHistoryActor(Long id) {
		String sql = "select actor_id from bc_identity_actor_history where id=?";

		try {
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, Long.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public int ActorRelationIsExist(Long roleId, Long actorId) {
		String sql = "select count(*) from bc_identity_role_actor where rid=? and aid=?";
		return jdbcTemplate.queryForObject(sql, new Object[]{roleId, actorId}, Integer.class);
	}

	@Override
	public int deleteResourceByRole(long roleId, long resourceId) {
		String sql = "delete from bc_identity_role_resource where rid=? and sid=?";
		return jdbcTemplate.update(sql, roleId, resourceId);
	}

	@Override
	public int RoleResouceIsExist(long roleId, long resourceId) {
		String sql = "select count(*) from bc_identity_role_resource where rid=? and sid=?";
		return jdbcTemplate.queryForObject(sql, new Object[]{roleId, resourceId}, Integer.class);
	}

	@Override
	public long addResourceByRole(long roleId, long resourceId) {
		String sql = "insert into bc_identity_role_resource (rid, sid) values(?,?)";
		return jdbcTemplate.update(sql, roleId, resourceId);
	}

	@Override
	public String getRoleNameById(long roleId) {
		String sql = "select name from bc_identity_role where id=?";
		return jdbcTemplate.query(sql, new Object[]{roleId}, new ColumnMapRowMapper()).get(0).get("name").toString();
	}
}