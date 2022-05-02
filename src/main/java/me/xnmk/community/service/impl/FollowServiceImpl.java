package me.xnmk.community.service.impl;

import me.xnmk.community.entity.User;
import me.xnmk.community.enumeration.EntityTypes;
import me.xnmk.community.service.FollowService;
import me.xnmk.community.service.UserService;
import me.xnmk.community.util.RedisKeyUtil;
import me.xnmk.community.util.UserThreadLocal;
import me.xnmk.community.vo.FollowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author:xnmk_zhan
 * @create:2022-05-02 17:01
 * @Description: FollowServiceImpl
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserThreadLocal userThreadLocal;


    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    @Override
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    @Override
    public List<FollowVo> findFollowees(int userId, int offset, int limit) {
        User loginUser = userThreadLocal.getUser();
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, EntityTypes.ENTITY_TYPE_USER.getCode());
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (targetIds == null) return null;
        List<FollowVo> followVoList = new ArrayList<>();
        for (Integer targetId : targetIds) {
            FollowVo followVo = new FollowVo();
            // 用户、关注时间
            User user = userService.findUserById(targetId);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            followVo.setUser(user);
            followVo.setFollowTime(new Date(score.longValue()));
            followVoList.add(followVo);
            // 当前登录用户是否关注列表内的用户
            if (loginUser == null) {
                followVo.setHasFollowed(false);
            } else {
                followVo.setHasFollowed(hasFollowed(loginUser.getId(), EntityTypes.ENTITY_TYPE_USER.getCode(), user.getId()));
            }
        }
        return followVoList;
    }

    @Override
    public List<FollowVo> findFollowers(int userId, int offset, int limit) {
        User loginUser = userThreadLocal.getUser();
        String followerKey = RedisKeyUtil.getFollowerKey(EntityTypes.ENTITY_TYPE_USER.getCode(), userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) return null;
        List<FollowVo> followVoList = new ArrayList<>();
        for (Integer targetId : targetIds) {
            FollowVo followVo = new FollowVo();
            // 用户、关注时间
            User user = userService.findUserById(targetId);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            followVo.setUser(user);
            followVo.setFollowTime(new Date(score.longValue()));
            followVoList.add(followVo);
            // 当前登录用户是否关注列表内的用户
            if (loginUser == null) {
                followVo.setHasFollowed(false);
            } else {
                followVo.setHasFollowed(hasFollowed(loginUser.getId(), EntityTypes.ENTITY_TYPE_USER.getCode(), user.getId()));
            }
        }
        return followVoList;
    }
}
