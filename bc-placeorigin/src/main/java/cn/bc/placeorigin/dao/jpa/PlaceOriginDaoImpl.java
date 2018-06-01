package cn.bc.placeorigin.dao.jpa;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.placeorigin.dao.PlaceOriginDao;
import cn.bc.placeorigin.domain.PlaceOrigin;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 籍贯DAO接口的实现
 *
 * @author lbj
 * @change dragon 2014-03-19 重构出通用的方法，删除无用的接口
 */
public class PlaceOriginDaoImpl extends JpaCrudDao<PlaceOrigin> implements PlaceOriginDao {
  public PlaceOrigin loadByCode(String code) {
    return this.createQuery().condition(new EqualsCondition("code", code)).singleResult();
  }

  public PlaceOrigin loadByIdentityCard(String cardNo) {
    Assert.hasText(cardNo);
    List<Object> args = new ArrayList<>();
    args.add(cardNo.substring(0, 6));// 县级
    args.add(cardNo.substring(0, 4));// 地级
    args.add(cardNo.substring(0, 2));// 省级
    List<PlaceOrigin> all = this.createQuery().condition(new AndCondition(new InCondition("code", args)
      , new OrderCondition("code", Direction.Desc))).list(1, 1);
    return all == null || all.isEmpty() ? null : all.get(0);
  }
}