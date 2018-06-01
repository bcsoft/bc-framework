package cn.bc.photo.service;

import java.util.Map;

/**
 * 图片service接口的实现
 *
 * @author dragon
 */
public class PhotoServiceImpl implements PhotoService {
  private Map<String, PhotoExecutor> photoExecutors;

  public void setPhotoExecutors(Map<String, PhotoExecutor> photoExecutors) {
    this.photoExecutors = photoExecutors;
  }

  public Map<String, PhotoExecutor> getPhotoExecutors() {
    return photoExecutors;
  }
}
