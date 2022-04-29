package me.xnmk.community.service;

/**
 * @author:xnmk_zhanli
 * @create:2022-04-28 09:53
 * @Description: LikeService
 */
public interface LikeService {

    /**
     * 点赞
     *
     * @param userId       用户id
     * @param entityType   实体类型
     * @param entityId     实体id
     * @param entityUserId 实体所属用户id
     */
    void like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某实体点赞数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 点赞数量
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 某用户对某实体的点赞状态
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 点赞状态
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * 获得某个用户的所有赞
     *
     * @param userId 用户id
     * @return 点赞数量
     */
    int findUserLikeCount(int userId);
}
