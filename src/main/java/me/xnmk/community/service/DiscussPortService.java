package me.xnmk.community.service;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.vo.param.PageParams;

import java.util.List;

public interface DiscussPortService {

    /**
     * 根据用户Id查找帖子（分页）
     * @param userId
     * @param pageParams
     * @return List<DiscussPost>
     */
    List<DiscussPost> findDiscussPosts(int userId, PageParams pageParams);

    /**
     * 查找用户的帖子数量
     * @param userId
     * @return rows
     */
    int findDiscussPortRows(int userId);
}
