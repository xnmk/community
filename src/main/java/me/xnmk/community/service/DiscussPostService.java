package me.xnmk.community.service;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.vo.param.PageParams;

import java.util.List;

public interface DiscussPostService {

    /**
     * 根据用户Id查找帖子（分页）
     *
     * @param userId     用户id
     * @param pageParams 分页参数
     * @return List<DiscussPost>
     */
    List<DiscussPost> findDiscussPosts(int userId, PageParams pageParams);

    /**
     * 查找用户的帖子数量
     *
     * @param userId 用户id
     * @return rows
     */
    int findDiscussPortRows(int userId);

    /**
     * 添加帖子
     *
     * @param discussPost 帖子信息
     * @return 是否添加成功
     */
    int addDiscussPost(DiscussPost discussPost);

    /**
     * 根据帖子id查找帖子信息
     *
     * @param id 帖子id
     * @return DiscussPost
     */
    DiscussPost findDiscussPostById(int id);
}
