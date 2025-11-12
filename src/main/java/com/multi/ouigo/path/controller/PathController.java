package com.multi.ouigo.path.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    @GetMapping("/tourist/touristListPage")
    public String TouristPage() {

        return "tourist/touristListPage";
    }

    @GetMapping("/tourist/touristCreatePage")
    public String touristCreatePage() {
        // templates/tourist/touristCreatePage.html 파일을 반환
        return "tourist/touristCreatePage";
    }


    @GetMapping("/tourist/touristDetail")
    public String TouristDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("touristId", id);
        return "tourist/touristDetail";
    }

    @GetMapping("/tourist/touristUpdatePage")
    public String touristUpdatePage(@RequestParam Long id, Model model) {
        model.addAttribute("touristId", id);
        return "tourist/touristUpdatePage";
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

    @GetMapping("/layout")
    public String layout() {
        // templates/recruit/recruitList.html
        return "layout";
    }
}
