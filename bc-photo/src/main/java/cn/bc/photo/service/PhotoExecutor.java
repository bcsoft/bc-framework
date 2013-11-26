package cn.bc.photo.service;

import java.util.Map;

/**
 * 附件信息解析器
 *
 * @author dragon
 */
public interface PhotoExecutor {
    /**
     * 根据附件id获取附件信息
     *
     * @param id
     * @return {name: 【附件名称】, type: 【附件扩展名】, path: 【附件相对于bcdata目录下的相对路径】}
     */
    public Map<String, Object> execute(String id);
}
