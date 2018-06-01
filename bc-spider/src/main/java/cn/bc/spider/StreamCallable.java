package cn.bc.spider;

import java.io.InputStream;

/**
 * 抓取网络文件流
 *
 * @author dragon
 */
public class StreamCallable<V> extends HttpClientCallable<V> {
  public StreamCallable() {
    this.setStream(true);
  }

  @Override
  protected void parseStream(InputStream stream) throws Exception {
    this.content = stream;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected V parseData() {
    return (V) this.content;
  }
}