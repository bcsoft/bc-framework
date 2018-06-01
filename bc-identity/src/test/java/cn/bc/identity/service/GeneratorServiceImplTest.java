package cn.bc.identity.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class GeneratorServiceImplTest {
  @PersistenceContext
  private EntityManager em;
  @Autowired
  private IdGeneratorService generatorService;

  // 随机初始化一个ID类型，并返回该类型
  private String randomType(String format) {
    String type = UUID.randomUUID().toString();// 使用uuid避免与现有数据产生冲突
    String sql = "insert into bc_identity_idgenerator(type_,value_,format) values(:type, :value, :format)";
    int count = em.createNativeQuery(sql)
      .setParameter("type", type)
      .setParameter("value", 0L)
      .setParameter("format", format)
      .executeUpdate();
    assertEquals(1, count);
    return type;
  }

  @Test
  public void testGetCurrentValue() {
    // 先插入一条数据
    String type = randomType(null);

    // 验证
    assertEquals(new Long(0), generatorService.currentValue(type));
    assertEquals("0", generatorService.current(type));
  }

  @Test
  public void testGetCurrentWithFormat() {
    // 先插入一条数据
    String type = randomType("${T}-${V}");

    // 验证
    assertEquals(type + "-0", generatorService.current(type));
  }

  @Test
  public void testGetNextValue() {
    // 先插入一条数据
    String uuid = randomType(null);

    // 验证
    assertEquals(new Long(1), generatorService.nextValue(uuid));
    assertEquals("2", generatorService.next(uuid));
  }

  @Test
  public void testGetNextWithFormat() {
    // 先插入一条数据
    String type = randomType("${T}-${V}");

    // 验证
    assertEquals(type + "-1", generatorService.next(type));
  }
}
