package cn.bc.orm.jpa.dto;

import cn.bc.orm.jpa.po.User;
import com.owlike.genson.Genson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * 数据传输对象测试
 *
 * @ref http://www.thoughts-on-java.org/jpa-native-queries/
 * @ref https://github.com/rongjihuang/ResultMapping
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class DTOTest {
	private static Logger logger = LoggerFactory.getLogger(DTOTest.class);

	@PersistenceContext
	private EntityManager em;

	private int count = 3;

	@Before()
	public void init() {
		em.createQuery("delete from User").executeUpdate();
		for (int i = count; i > 0; i--) em.persist(new User("code" + i, "姓名" + i));
	}

	@Test
	// 根据 JPA 原生查询结果自行组装 DTO 对象
	public void jpaQuery_CustomMapper() {
		String jpql = "select id, code, name from User order by code";
		List<Object[]> obj = em.createQuery(jpql).getResultList();
		logger.debug("count={}, obj={}", obj.size(), new Genson().serialize(obj));

		// map to dto
		List<PureDTO> dto = obj.stream().map(o -> {
			PureDTO u = new PureDTO();
			u.setCode((String) o[1]);
			u.setMyName((String) o[2]);
			return u;
		}).collect(Collectors.toList());
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}

	@Test
	// 利用 new 关键字通过 DTO 的构造函数自动组装 DTO 对象
	public void jpaQuery_AutoMapToDTOByConstructor() {
		String jpql = "select new cn.bc.orm.jpa.dto.PureDTO(code, name) from User order by code";
		List<PureDTO> dto = em.createQuery(jpql, PureDTO.class).getResultList();
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}

	@Test
	public void jpaQuery_AutoEntity() {
		// 使用 Entity 别名 select 返回，不能使用 select *，否则报错：
		// antlr.NoViableAltException: unexpected token: *
		// java.lang.IllegalArgumentException: org.hibernate.hql.internal.ast.QuerySyntaxException:
		// unexpected token: * near line 1, column 8 [select * from cn.bc.orm.jpa.po.User u order by code]
		String jpql = "select u from User u order by code";
		List<User> entity = em.createQuery(jpql, User.class).getResultList();
		assertEquals(3, entity.size());
		assertEquals("code1", entity.get(0).getCode());

		logger.debug("count={}, entity={}", entity.size(), new Genson().serialize(entity));
	}

	@Test(expected = IllegalArgumentException.class)
	public void jpaQuery_Failed_AutoMapToEntity() {
		// 使用 JPA 查询，不会根据 select 字段自动组装 Entity 对象，报如下错误：
		// java.lang.IllegalArgumentException: Cannot create TypedQuery for query with more than
		// one return using requested result type [cn.bc.orm.jpa.po.User]
		String jpql = "select id, code, name from User order by code";
		em.createQuery(jpql, User.class).getResultList();
	}

	@Test(expected = IllegalArgumentException.class)
	public void jpaQuery_Failed_AutoMapToDTO() {
		// 使用 JPA 查询，不会根据 select 字段自动组装 DTO 对象，报如下错误：
		// java.lang.IllegalArgumentException: Cannot create TypedQuery for query with more than
		// one return using requested result type [cn.bc.orm.jpa.dto.PureDTO]
		String jpql = "select code, name as myName from User order by code";
		em.createQuery(jpql, PureDTO.class).getResultList();
	}

	@Test
	// 使用原生查询并根据 select 字段自动组装 PureDTO 对象
	public void jpaNativeQuery_AutoMapToPureDTOByConstructor() {
		String sql = "select code, name from t_jpa_user order by code";
		List<PureDTO> dto = em.createNativeQuery(sql, "PureDTOMapperByConstructor").getResultList();
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}

	@Test
	// 使用原生查询并根据 select 字段自动组装 PureDTO 对象
	public void jpaNativeQuery_AutoMapToPureDTOByConstructorXml() {
		String sql = "select code, name as my_name from t_jpa_user order by code";
		List<PureDTO> dto = em.createNativeQuery(sql, "PureDTOMapperByConstructorXml").getResultList();
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}

	@Test(expected = PersistenceException.class)
	// 使用原生查询并根据 select 字段自动组装 PureDTO 对象
	public void jpaNativeQuery_AutoMapToPureDTOByEntityResultXml() {
		// 没有 @Entity 是不能使用 EntityResult 映射的，报错：
		// javax.persistence.PersistenceException: org.hibernate.MappingException: Unknown entity: cn.bc.orm.jpa.dto.PureDTO
		String sql = "select code, name as my_name from t_jpa_user order by code";
		List<PureDTO> dto = em.createNativeQuery(sql, "PureDTOMapper1Xml").getResultList();
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}

	@Test
	// 使用原生查询并根据 select 字段自动组装 FakeDTO 对象
	public void jpaNativeQuery_AutoMapToFakeDTO() {
		// 要求：
		// 1) FakeDTO 必须为实体对象，即有 @Entity 和 @Id 注解
		//    如果无 @Id，报如下错：
		//    Caused by: org.hibernate.AnnotationException: No identifier specified for entity: cn.bc.orm.jpa.dto.FakeDTO
		//    如果无 @Entity，报如下错：
		//    javax.persistence.PersistenceException: org.hibernate.MappingException: Unknown entity: cn.bc.orm.jpa.dto.FakeDTO
		//    Caused by: org.hibernate.MappingException: Unknown entity: cn.bc.orm.jpa.dto.FakeDTO
		// 2) 列名不区分大小写
		// 3) sql 的 select 中必须包含所有实体属性（也可使用@Column映射列名和属性名），否则报错：
		//    javax.persistence.PersistenceException: org.hibernate.exception.SQLGrammarException: could not execute query
		//    Caused by: org.postgresql.util.PSQLException: ResultSet 中找不到栏位名称 name。
		String sql = "select id, code, name as myName from t_jpa_user order by code";
		List<FakeDTO> dto = em.createNativeQuery(sql, FakeDTO.class).getResultList();
		assertEquals(3, dto.size());
		assertEquals("code1", dto.get(0).getCode());

		logger.debug("count={}, dto={}", dto.size(), new Genson().serialize(dto));
	}
}