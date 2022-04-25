package me.xnmk.community.service;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.vo.CommentVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 21:35
 * @Description: CommentService
 */
public interface CommentService {

    /**
     * 查找评论（分页）
     *
     * @param entityType 评论实体类型
     * @param entityId   帖子id
     * @param pageParams 分页参数
     * @return List<Comment>
     */
    List<Comment> findCommentsByEntity(int entityType, int entityId, PageParams pageParams);

    /**
     * 查找评论（分页）
     *
     * @param entityType 评论实体类型
     * @param entityId   帖子id
     * @param pageParams 分页参数
     * @return List<CommentVo>
     */
    List<CommentVo> findCommentVosByEntity(int entityType, int entityId, PageParams pageParams);

    /**
     * 返回评论数量
     *
     * @param entityType 评论实体类型
     * @param entityId   帖子id
     * @return int
     */
    int findCommentCount(int entityType, int entityId);

    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @return int
     */
    int addComment(Comment comment);
}
