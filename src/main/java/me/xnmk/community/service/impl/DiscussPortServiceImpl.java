package me.xnmk.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.DiscussPostMapper;
import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.service.DiscussPortService;
import me.xnmk.community.util.SensitiveFilter;
import me.xnmk.community.vo.param.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 21:17
 * @Description: DiscussPortService接口实现
 */
@Service
public class DiscussPortServiceImpl implements DiscussPortService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, PageParams pageParams) {
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

        return page.getRecords();
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
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectById(id);
    }
}
