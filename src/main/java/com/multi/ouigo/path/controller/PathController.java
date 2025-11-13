package com.multi.ouigo.path.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    @GetMapping("/tourist")
    public String showTouristPage() {

        return "tourist";
    }


    @GetMapping("tourist/tourist-detail")
    public String showTouristDetailPage(@RequestParam(name = "touristId") Long id, Model model) {
        model.addAttribute("touristId", id);
        return "tourist/touristDetail";
    }

    @GetMapping("/recruit/recruitListPage")
    public String recruitListPage() {
        // templates/recruit/recruitList.html
        return "recruit/recruitListPage";
    }

    @GetMapping("/myPage/profilePage")
    public String profilePage() {
        // templates/recruit/recruitList.html
        return "myPage/profilePage";
    }

    @GetMapping("/layout/{touristSpotId}")
    public String layout(Model model,@PathVariable Long touristSpotId) {
        model.addAttribute("touristSpotId",touristSpotId);
        // templates/recruit/recruitList.html
        return "layout";
    }
}
