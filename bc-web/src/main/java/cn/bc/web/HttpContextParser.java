/**
 *
 */
package cn.bc.web;

import cn.bc.Context;

import javax.servlet.http.HttpServletRequest;

/**
 * 从请求信息中解析出 Context。
 *
 * @author dragon
 */
public interface HttpContextParser {
  Context parse(HttpServletRequest request);
}
