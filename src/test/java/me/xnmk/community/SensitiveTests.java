package me.xnmk.community;

import me.xnmk.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-19 17:07
 * @Description: 过滤敏感词测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String s = "我们可以赌博，可以嫖@娼，可以开@@@@@@票，哈哈哈！";
        String filter = sensitiveFilter.filter(s);
        System.out.println(filter);
    }
}
