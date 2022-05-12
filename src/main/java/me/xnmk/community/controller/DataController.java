package me.xnmk.community.controller;

import me.xnmk.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author:xnmk_zhan
 * @create:2022-05-12 23:17
 * @Description: 网站统计信息接口
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    /**
     * 统计页面
     *
     * @return ModelAndView
     */
    @RequestMapping("/data")
    public String getDataPage() {
        return "/site/admin/data";
    }

    /**
     * 统计 UV
     *
     * @param start 开始日期
     * @param end   结束日期
     * @param model 模板
     * @return ModelAndView
     */
    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        Model model) {

        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);

        return "forward:/data";
    }

    /**
     * 统计 DAU
     *
     * @param start 开始日期
     * @param end   结束日期
     * @param model 模板
     * @return ModelAndView
     */
    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                         Model model) {

        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);

        return "forward:/data";
    }
}
