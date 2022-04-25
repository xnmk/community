package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.CommentMapper;
import me.xnmk.community.dao.DiscussPostMapper;
import me.xnmk.community.entity.Comment;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.CommentTypes;
import me.xnmk.community.service.CommentService;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.SensitiveFilter;
import me.xnmk.community.vo.CommentVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 21:38
 * @Description: CommentService实现
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, PageParams pageParams) {
        Page<Comment> page = new Page<>(pageParams.getCurrent(), pageParams.getLimit());
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 条件
        queryWrapper.eq(Comment::getStatus, 0);
        queryWrapper.eq(Comment::getEntityId, entityId);
        queryWrapper.eq(Comment::getEntityType, entityType);
        // 评论时间排序
        queryWrapper.orderByAsc(Comment::getCreateTime);

        commentMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public List<CommentVo> findCommentVosByEntity(int entityType, int entityId, PageParams pageParams) {
        Page<Comment> page = new Page<>(pageParams.getCurrent(), pageParams.getLimit());
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 条件
        queryWrapper.eq(Comment::getStatus, 0);
        queryWrapper.eq(Comment::getEntityId, entityId);
        queryWrapper.eq(Comment::getEntityType, entityType);
        // 评论时间排序
        queryWrapper.orderByAsc(Comment::getCreateTime);

        commentMapper.selectPage(page, queryWrapper);
        return copyList(page.getRecords());
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    // 添加事务
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        // 空值判断
        if (comment == null) throw new IllegalArgumentException("参数不能为空");
        // 过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        // 添加评论
        int rows = commentMapper.insert(comment);
        // 更新帖子评论数
        if (comment.getEntityType() == CommentTypes.ENTITY_TYPE_POST.getCode()) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    private List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        // 设置用户
        commentVo.setUser(userService.findUserById(comment.getUserId()));

        // 回复列表
        List<CommentVo> replyVoList = new ArrayList<>();
        PageParams pageParams = new PageParams();
        pageParams.setCurrent(1);
        pageParams.setLimit(Integer.MAX_VALUE);
        List<Comment> replyList = findCommentsByEntity(
                CommentTypes.ENTITY_TYPE_COMMENT.getCode(), comment.getId(), pageParams);
        // 将得到的replyList转为replyVoList
        if (replyList != null) {
            for (Comment reply : replyList) {
                CommentVo replyVo = new CommentVo();
                BeanUtils.copyProperties(reply, replyVo);
                // 回复者
                replyVo.setUser(userService.findUserById(reply.getUserId()));
                // 回复目标
                User targetUser = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                replyVo.setTargetUser(targetUser);
                replyVoList.add(replyVo);
            }
        }
        commentVo.setReplyVoList(replyVoList);

        // 回复数量
        int replyCount = findCommentCount(CommentTypes.ENTITY_TYPE_COMMENT.getCode(), comment.getId());
        commentVo.setReplyCount(replyCount);

        return commentVo;
    }
}
