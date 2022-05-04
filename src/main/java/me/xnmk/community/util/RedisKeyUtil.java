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
    // 验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";
    // 登录凭证
    private static final String PREFIX_TICKET = "ticket";
    // 用户信息
    private static final String PREFIX_USER = "user";

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

    // 登录验证码：kaptcha:<owner> -> string(kaptchaText)
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录凭证：ticket:<ticket> -> string(LoginTicket)
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户：user:<userId> ->
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
