package me.xnmk.community.service;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-05-09 16:32
 * @Description: Elasticsearch-Service
 */
public interface ElasticsearchService {

    /**
     * 将帖子存入 Elasticsearch
     *
     * @param discussPost 帖子
     */
    void saveDiscussPost(DiscussPost discussPost);

    /**
     * 将帖子从 Elasticsearch 删除
     *
     * @param id 帖子 id
     */
    void deleteDiscussPost(int id);

    /**
     * 从 Elasticsearch 搜索帖子
     *
     * @param keyword 搜索内容
     * @param current 页码
     * @param limit   每页的条数
     * @return List<DiscussPost>
     */
    List<DiscussPostVo> searchDiscussPost(String keyword, int current, int limit);

    /**
     * 返回从 Elasticsearch 搜索帖子的数量
     *
     * @param keyword 搜索内容
     * @return long
     */
    long getDiscussPostCount(String keyword);
}
