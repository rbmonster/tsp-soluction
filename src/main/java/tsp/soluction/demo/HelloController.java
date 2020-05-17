package tsp.soluction.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * @Description:
 * TODO
 * </pre>
 *
 * @version v1.0
 * @ClassName: HelloController
 * @Author: sanwu
 * @Date: 2020/5/16 19:31
 */
@Controller
@RequestMapping(value = "/hello")
public class HelloController {
    @RequestMapping(value = "/home")
    public String hello(Model model) {
        model.addAttribute("name", "Dear");
        return "index";
    }
}
