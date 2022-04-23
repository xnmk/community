package me.xnmk.community;

import me.xnmk.community.entity.Comment;
import me.xnmk.community.entity.User;
import me.xnmk.community.service.CommentService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.vo.CommentVo;
import me.xnmk.community.vo.param.PageParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 23:10
 * @Description: Comment接口测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommentTests {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @Test
    public void testFindCommentVo(){
        int entityType = 1, entityId = 232;
        PageParams pageParams = new PageParams();
        pageParams.setCurrent(1);
        pageParams.setLimit(5);
        List<CommentVo> commentVosByEntity = commentService.findCommentVosByEntity(entityType, entityId, pageParams);
        for (CommentVo commentVo : commentVosByEntity) {
            System.out.println(commentVo);
        }
    }

    // 获得回复者列表
    @Test
    public void testFindComment(){
        int entityType = 2, entityId = 12;
        List<CommentVo> replyVoList = new ArrayList<>();
        PageParams pageParams = new PageParams();
        pageParams.setCurrent(1);
        pageParams.setLimit(Integer.MAX_VALUE);
        List<Comment> replyList = commentService.findCommentsByEntity(entityType, entityId, pageParams);
        // 将得到的replyList转为replyVoList
        if (replyList != null){
            for (Comment reply : replyList){
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

        for (CommentVo commentVo : replyVoList) {
            System.out.println(commentVo);
        }
    }
}
