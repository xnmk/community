package me.xnmk.community.quartz;

import me.xnmk.community.entity.DiscussPost;
import me.xnmk.community.enumeration.DiscussStatus;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.DiscussPostService;
import me.xnmk.community.service.ElasticsearchService;
import me.xnmk.community.service.LikeService;
import me.xnmk.community.util.RedisKeyUtil;
import me.xnmk.community.vo.DiscussPostVo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-05-14 19:21
 * @Description: Post-Score-Refresh-Job
 */
public class PostScoreRefreshJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    // 纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！", e);
        }
    }

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子！");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数" + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }

        logger.info("[任务结束] 帖子刷新分数完毕！");
    }

    private void refresh(int postId) {
        DiscussPostVo post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            logger.error("该帖子不存在，id = " + postId);
            return;
        }

        // 帖子分数：log(精华分 + (评论数 * 10) + (点赞数 * 2) + (发布时间 - 纪元)
        // 是否加精
        boolean essence = post.getStatus() == DiscussStatus.DISCUSS_STATUS_ESSENCE.getCode();
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(EntityTypes.ENTITY_TYPE_POST.getCode(), postId);
        // 发布时间
        Date createTime = post.getCreateTime();
        // 权重
        double w = (essence ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数
        double score = Math.log10(Math.max(w, 1)) + (createTime.getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 同步搜索数据
        DiscussPost discussPost = new DiscussPost();
        BeanUtils.copyProperties(post, discussPost);
        discussPost.setScore(score);
        elasticsearchService.saveDiscussPost(discussPost);
    }
}
