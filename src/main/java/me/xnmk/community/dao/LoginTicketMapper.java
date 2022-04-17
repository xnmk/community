package me.xnmk.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.xnmk.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * @author:xnmk_zhan
 * @create:2022-04-17 15:06
 * @Description: LoginTicketMapper
 */
@Mapper
@Component
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {

    /**
     * 对凭证状态更新
     * 原计划直接使用Mybatis-Plus完成，却遇到一个问题：
     * *在对凭证状态更新时，Mybatis-Plus生成的sql同时把表中user_id更新为0
     *
     * @param ticket 登录凭证
     * @param status 状态
     * @return int
     */
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket}",
            "<if test=\"ticket!=null\">",
            "and 1 = 1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
