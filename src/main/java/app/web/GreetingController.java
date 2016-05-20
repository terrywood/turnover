package app.web;

import app.entity.FundFlowPie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {


    @RequestMapping("/index")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {

        model.addAttribute("name", "Terry WU");
        return "greeting";
    }


    @RequestMapping("/save")
    public String session(
            @ModelAttribute FundFlowPie entity) {

        return "redirect:index";
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam("id") String id) {
        return "redirect:index";
    }

}
