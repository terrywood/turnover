package app.web;

import app.entity.FundFlowPie;
import app.service.HuanShouLvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {
    @Autowired
    HuanShouLvService huanShouLvService;
    @RequestMapping("/index")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", "Terry WU");
        return "greeting";
    }


    @RequestMapping(value = "/id/{id}")
    public String greeting( Model model, @PathVariable("id") Long id) {
       // huanShouLvService

        FundFlowPie obj = huanShouLvService.findOne(id);



        model.addAttribute("obj", obj);
        model.addAttribute("d", obj.getFundFlowPieDetail());
        model.addAttribute("m", obj.getFundFlowPieMaster());
        model.addAttribute("s", obj.getFundFlowPieSlave());

        return "detail";
    }


    @RequestMapping("/press")
    public String session(
            @ModelAttribute FundFlowPie entity) {

        return "redirect:index";
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam("id") String id) {
        return "redirect:index";
    }

}
