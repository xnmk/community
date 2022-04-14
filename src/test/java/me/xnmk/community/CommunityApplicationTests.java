package me.xnmk.community;

import me.xnmk.community.controller.TestController;
import me.xnmk.community.dao.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    // 得到测试配置的容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

	@Test
	void contextLoads() {
        System.out.println(applicationContext);
        TestController bean = applicationContext.getBean(TestController.class);
        UserMapper bean1 = applicationContext.getBean(UserMapper.class);
        System.out.println(bean);
    }


}
