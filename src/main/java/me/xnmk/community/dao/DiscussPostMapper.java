package me.xnmk.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.xnmk.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {

    int selectDiscussPostRows(int userId);
}
