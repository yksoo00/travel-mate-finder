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

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/tourist-detail")
    public String showTouristDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("touristId", id);
        return "touristDetail";
    }

    @GetMapping("/recruit/recruitListPage")
    public String recruitListPage() {
        // templates/recruit/recruitList.html
        return "recruit/recruitListPage";
    }

    @GetMapping("/recruit/recruitUpdatePage")
    public String recruitUpdatePage() {

        return "recruit/recruitUpdatePage";
    }

    @GetMapping("/myPage/profilePage")
    public String profilePage() {
        // templates/recruit/recruitList.html
        return "myPage/profilePage";
    }

    @GetMapping("/layout")
    public String layout(Model model) {
        model.addAttribute("pageFragment", "recruit/recruitListPage");
        return "layout";
    }

    @GetMapping("/recruit/{id}")
    public String recruitDetailPage(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("recruitId", id);
        System.out.println(id);
        model.addAttribute("pageFragment", "recruit/recruitDetailPage");
        return "layout"; // templates/recruit/recruitDetailPage.html
    }

    @GetMapping("/recruit/recruitApprovalPage")
    public String recruitApprovalPage() {
        return "recruit/recruitApprovalPage";

    }

    @GetMapping("/recruit/recruitCreatePage")
    public String recruitCreatePage(Model model) {
        model.addAttribute("pageFragment", "recruit/recruitCreatePage");
        model.addAttribute("pageTitle", "모집글 작성");
        return "layout";
    }


    @GetMapping("/recruit/recruitUpdatePage/{recruitId}")
    public String recruitUpdatePage(@PathVariable Long recruitId, Model model) {
        model.addAttribute("pageFragment", "recruit/recruitUpdatePage");
        model.addAttribute("pageTitle", "모집글 수정");
        model.addAttribute("recruitId", recruitId);
        return "layout";
    }
}
