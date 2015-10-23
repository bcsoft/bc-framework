package cn.bc.identity.service;

import cn.bc.core.query.condition.Condition;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.AuthData;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author dragon
 */
public interface UserService extends ActorService {
    /**
     * 批量更新密码
     *
     * @param ids      用户的id
     * @param password 新的密码（应为已经加密的密文）
     */
    int updatePassword(Long[] ids, String password);

    /**
     * 获取用户的认证信息
     *
     * @param userId 用户的id
     * @return
     */
    AuthData loadAuthData(Long userId);

    /**
     * 保存用户信息
     *
     * @param user      用户
     * @param belongIds 用户隶属的上级id
     * @param groupIds  用户所分派的岗位id
     */
    Actor save(Actor user, Long[] belongIds, Long[] groupIds);

    /**
     * 获取当前用户的单位信息查看限制条件
     * <ul>
     * 角色控制：（全局控制）
     * <li>TODO：拥有"所有单位信息查看"角色的用户无限制查看各级单位的信息列表，这是默认的行为</li>
     * <li>TODO：拥有"本级单位信息查看"角色的用户可以查看本单位及其下属单位的信息列表</li>
     * <li>拥有"本单位信息查看"角色的用户只可以查看本单位的信息列表</li>
     * </ul>
     * <ul>
     * 岗位控制：（各单位独立配置岗位）
     * <li>TODO：隶属"本级单位信息查看"岗位的用户可以查看此岗位所属单位及其下属单位的信息列表（就算用户不隶属于此单位）</li>
     * <li>隶属"本单位信息查看"岗位的用户只可以查看此岗位所属单位的信息列表（就算用户不隶属于此单位）</li>
     * </ul>
     * @param unitKey 单位限制条件对应的查询键，如m.unit_id
     * @return 如果没有限制返回null，否则返回相应的限制条件
     */
    Condition getCurrenUserUnitInfoLimitedCondition(String unitKey);

    /**
     * 获取当前用户的单位信息查看限制条件对应的单位id列表
     * @return
     */
    Long[] getCurrenUserUnitInfoLimitedIds();

    /**
     * 获取隶属指定岗位的所有用户的编码
     * @param groupCode 岗位编码
     * @param userStatuses 用户的状态
     */
    List<String> findAllUserCodeByGroup(String groupCode, Integer[] userStatuses);
}