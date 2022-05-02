package me.xnmk.community.service;

/**
 * @author:xnmk_zhan
 * @create:2022-05-02 17:00
 * @Description: FollowService
 */
public interface FollowService {

    /**
     * 关注
     *
     * @param userId     用户id
     * @param entityType 关注目标实体类型
     * @param entityId   关注目标实体id
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取消关注
     *
     * @param userId     用户id
     * @param entityType 关注目标实体类型
     * @param entityId   关注目标实体id
     */
    void unfollow(int userId, int entityType, int entityId);

    /**
     * 查询用户关注实体的数量
     *
     * @param userId     用户id
     * @param entityType 关注实体类型
     * @return long
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 查询实体的粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return long
     */
    long findFollowerCount(int entityType, int entityId);

    /**
     * 查询当前用户是否已关注实体
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return boolean
     */
    boolean hasFollowed(int userId, int entityType, int entityId);
}
