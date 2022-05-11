package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.DiscussPostMapper;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.util.SensitiveFilter;
import me.xnmk.community.vo.DiscussPostVo;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:17
 * @Description: DiscussPortService接口实现
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LikeService likeService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPostVo> findDiscussPosts(int userId, PageParams pageParams, boolean addLikeCount) {
        // 分页
        Page<DiscussPost> page = new Page<>(pageParams.getCurrent(), pageParams.getLimit());
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper();
        // 排除拉黑
        queryWrapper.ne(DiscussPost::getStatus, 2);
        // 是否根据userId查询
        if (userId != 0) {
            queryWrapper.eq(DiscussPost::getUserId, userId);
        }
        // 排序（置顶、创建时间）
        queryWrapper.orderByDesc(DiscussPost::getType, DiscussPost::getCreateTime);
        discussPostMapper.selectPage(page, queryWrapper);

        return copyList(page.getRecords(), addLikeCount);
    }

    @Override
    public int findDiscussPortRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        // 判空
        if (discussPost == null) throw new IllegalArgumentException("参数不能为空！");

        // 转义
        String title = HtmlUtils.htmlEscape(discussPost.getTitle());
        String content = HtmlUtils.htmlEscape(discussPost.getContent());
        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(title));
        discussPost.setContent(sensitiveFilter.filter(content));

        return discussPostMapper.insert(discussPost);
    }

    @Override
    public DiscussPostVo findDiscussPostById(int id) {
        return copy(discussPostMapper.selectById(id), false);
    }

    @Override
    public int updateCommentCount(int discussPostId, int commentCount) {
        return discussPostMapper.updateCommentCount(discussPostId, commentCount);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    // addLikeCount：是否添加点赞数量
    public List<DiscussPostVo> copyList(List<DiscussPost> discussPostList, boolean addLikeCount) {
        List<DiscussPostVo> discussPostVoList = new ArrayList<>();
        for (DiscussPost discussPost : discussPostList) {
            DiscussPostVo discussPostVo = copy(discussPost, addLikeCount);
            discussPostVoList.add(discussPostVo);
        }
        return discussPostVoList;
    }

    // addLikeCount：是否添加点赞数量
    public DiscussPostVo copy(DiscussPost discussPost, boolean addLikeCount) {
        DiscussPostVo discussPostVo = new DiscussPostVo();
        BeanUtils.copyProperties(discussPost, discussPostVo);
        // 添加点赞数量
        if (addLikeCount) {
            long likeCount = likeService.findEntityLikeCount(EntityTypes.ENTITY_TYPE_POST.getCode(), discussPost.getId());
            discussPostVo.setLikeCount(likeCount);
        }
        return discussPostVo;
    }
}
