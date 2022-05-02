package me.xnmk.community.util;

/**
 * @author:xnmk_zhan
 * @create:2022-04-28 09:44
 * @Description: 生成redisKey工具类
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    // 点赞数量（帖子、评论、回复）
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    // 点赞数量（用户）
    private static final String PREFIX_USER_LIKE = "like:user";
    // 关注目标
    private static final String PREFIX_FOLLOWEE = "followee";
    // 粉丝
    private static final String PREFIX_FOLLOWER = "follower";

    // 某个实体的赞：like:entity:<entityType>:<entityId> -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞：like:user:<userId> -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体：followee:<userId>:<entityType> -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个用户拥有的粉丝：follower:<entityType>:<entityId> -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
