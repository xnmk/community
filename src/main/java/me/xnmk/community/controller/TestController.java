package me.xnmk.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author:xnmk_zhan
 * @create:2022-04-13 16:37
 * @Description: 测试接口
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello";
    }
}
