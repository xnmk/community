package me.xnmk.community;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.xnmk.community.dao.DiscussPostMapper;
import me.xnmk.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-14 19:48
 * @Description: DiscussPostMapper测试
 */
    @SpringBootTest
    @ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPortTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    /**
     * 根据用户id查询帖子（分页、排序）
     */
    public void testSelectDiscussPosts() {
        // 参数
        int offset = 0, limit = 10;
        int userId = 0;

        // 分页
        Page<DiscussPost> page = new Page<>(offset, limit);
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
    }

    @Test
    /**
     * 根据userId查找用户帖子数量
     */
    public void testSelectDiscussPostRows() {
        int count = discussPostMapper.selectDiscussPostRows(150);
        System.out.println(count);
    }
}
