package me.xnmk.community.enumeration;

/**
 * @author:xnmk_zhan
 * @create:2022-05-05 15:28
 * @Description: 事件主题
 */
public interface CommunityConstant {

    /**
     * 事件主题：评论
     */
    String TOPIC_COMMENT = "comment";
    /**
     * 事件主题：点赞
     */
    String TOPIC_LIKE = "like";
    /**
     * 事件主题：关注
     */
    String TOPIC_FOLLOW = "follow";
    /**
     * 事件主题：发布
     */
    String TOPIC_PUBLISH = "publish";
    /**
     * 事件主题：删帖
     */
    String TOPIC_DELETE = "delete";
    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;
}
