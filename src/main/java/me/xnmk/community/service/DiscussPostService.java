package me.xnmk.community.service;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;

import java.util.List;

public interface DiscussPostService {

    /**
     * 根据用户id查找帖子（分页）
     *
     * @param userId       用户id
     * @param pageParams   分页参数
     * @param addLikeCount 是否在vo内添加点赞数量信息
     * @param orderMode    排序模式
     * @return List<DiscussPost>
     */
    List<DiscussPostVo> findDiscussPosts(int userId, PageParams pageParams, boolean addLikeCount, int orderMode);

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
    DiscussPostVo findDiscussPostById(int id);

    /**
     * 更新帖子评论数
     *
     * @param discussPostId 帖子id
     * @param commentCount  帖子评论数
     * @return int 是否添加成功
     */
    int updateCommentCount(int discussPostId, int commentCount);

    /**
     * 修改帖子是否置顶
     *
     * @param id   帖子id
     * @param type 置顶状态
     * @return int
     */
    int updateType(int id, int type);

    /**
     * 修改帖子状态
     *
     * @param id     帖子id
     * @param status 帖子状态
     * @return int
     */
    int updateStatus(int id, int status);

    /**
     * 更新帖子分数
     *
     * @param id    帖子id
     * @param score 帖子分数
     * @return int
     */
    int updateScore(int id, double score);
}
