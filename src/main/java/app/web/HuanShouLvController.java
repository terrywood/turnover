package app.web;

import app.entity.FundFlowPie;
import app.service.HuanShouLvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;

@Controller
@RequestMapping("/shou")
public class HuanShouLvController {

    @Autowired
    HuanShouLvService huanShouLvService;

    @RequestMapping("/press")
    public String greeting(
                           @RequestParam(value="page", required=false, defaultValue="1") int page,
                           @RequestParam(value="size", required=false, defaultValue="20") int size,
                           Model model) throws ParseException {
        Page<FundFlowPie> pageList = huanShouLvService.findPressEat(page-1,size);
        for(FundFlowPie pie : pageList.getContent()){
           // long id  = pie.getId();
           // java.util.List<FundFlowPie> greater = huanShouLvService.findByCodeAndIdGreaterThan(pie.getCode(), id, 5);
           // pie.setGreater(greater);
        }
        model.addAttribute("pageList", pageList);
        return "huanShouLv/pressEat";
    }

    @RequestMapping(value = "/id/{id}")
    public String greeting( Model model, @PathVariable("id") Long id) {
        FundFlowPie obj = huanShouLvService.findOne(id);
        model.addAttribute("obj", obj);
        model.addAttribute("d", obj.getFundFlowPieDetail());
        model.addAttribute("m", obj.getFundFlowPieMaster());
        model.addAttribute("s", obj.getFundFlowPieSlave());

        return "detail";
    }


}
