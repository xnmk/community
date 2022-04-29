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

    // 某个实体的赞：redisKey: like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞：redisKey: like:user:userId
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
