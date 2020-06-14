package tsp.soluction.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * @Description:
 *  首页地址：
 *  http://localhost:9999/home
 * </pre>
 *
 * @version v1.0
 * @ClassName: HelloController
 * @Author: sanwu
 * @Date: 2020/5/16 19:31
 */
@Controller
@RequestMapping
public class HelloController {
    @RequestMapping(value = "/home")
    public String hello(Model model) {
        model.addAttribute("name", "Dear");
        return "index";
    }
}
