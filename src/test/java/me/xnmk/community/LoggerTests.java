package me.xnmk.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author:xnmk_zhan
 * @create:2022-04-16 15:04
 * @Description: 日志功能测试
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTests {

    public static final Logger logger = LoggerFactory.getLogger(LoggerTests.class);

    @Test
    public void testLogger(){
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
