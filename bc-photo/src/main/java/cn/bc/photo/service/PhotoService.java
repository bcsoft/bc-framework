package cn.bc.photo.service;


import java.util.Map;

/**
 * 图片Service接口
 * 
 * @author dragon
 * 
 */
public interface PhotoService {
	public Map<String, PhotoExecutor> getPhotoExecutors();
}
