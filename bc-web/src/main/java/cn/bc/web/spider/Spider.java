package cn.bc.web.spider;

/**
 * 网络内容抓取器接口
 *
 * @author dragon
 */
public interface Spider<T> {
  T excute() throws Exception;
}
