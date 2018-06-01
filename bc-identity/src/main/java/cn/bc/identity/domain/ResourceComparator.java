/**
 *
 */
package cn.bc.identity.domain;

import java.util.Comparator;

/**
 * 资源的排序比较器
 *
 * @author dragon
 */
public class ResourceComparator implements Comparator<Resource> {
  public int compare(Resource s1, Resource s2) {
    String o1 = s1.getOrderNo();
    String o2 = s2.getOrderNo();
    return o1.compareTo(o2);
  }
}
