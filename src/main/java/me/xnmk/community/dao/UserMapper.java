package me.xnmk.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.xnmk.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
