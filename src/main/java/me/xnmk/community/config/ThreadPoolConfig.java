package me.xnmk.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author:xnmk_zhan
 * @create:2022-05-13 16:35
 * @Description: ThreadPool-Config
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {
}
