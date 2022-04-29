package me.xnmk.community.controller;

import me.xnmk.community.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:xnmk_zhan
 * @create:2022-04-13 16:37
 * @Description: 测试接口
 */
// @Controller
// @RequestMapping("/test")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    // @ResponseBody
    public Result sayHello() {
        return Result.success("hello", null);
    }
}
