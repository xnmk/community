package me.xnmk.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.xnmk.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author:xnmk_zhan
 * @create:2022-04-22 21:21
 * @Description: CommentMapper
 */
@Mapper
@Component
public interface CommentMapper extends BaseMapper<Comment> {

    int selectCountByEntity(int entityType, int entityId);

    int selectCountByUserId(int userId);
}
