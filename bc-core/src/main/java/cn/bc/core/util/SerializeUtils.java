package cn.bc.core.util;

import java.io.*;

/**
 * 对象序列化与反序列化工具类
 * <p>如果对象没有实现 java.io.Serializable 接口，请不要使用此工具类<p/>
 * Created by dragon on 2014/12/25.
 */
public class SerializeUtils {
  /**
   * 序列化对象为字节数组
   *
   * @param obj 要处理的对象
   * @return
   */
  public static byte[] serialize(Object obj) {
    if (obj == null) return null;
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream objStream = null;
    try {
      objStream = new ObjectOutputStream(byteStream);
      objStream.writeObject(obj);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      try {
        objStream.close();
      } catch (IOException e) {
        // Exception is silently ignored
      }
    }
    return byteStream.toByteArray();
  }

  /**
   * 将字节数组反序列化为对象
   *
   * @param bytes 对象的序列化数据
   * @return
   */
  public static Object deserialize(byte[] bytes) {
    if (bytes == null) return null;
    ByteArrayInputStream bytesIS = new ByteArrayInputStream(bytes);
    ObjectInputStream objIS = null;
    Object obj;
    try {
      objIS = new ObjectInputStream(bytesIS);
      obj = objIS.readObject();
      return obj;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      try {
        objIS.close();
      } catch (IOException e) {
        // Exception is silently ignored
      }
    }
  }
}
