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

    // 마이페이지 홈

    @GetMapping("/myPage")
    public String myPageHome(@RequestParam(required = false) Long memberNo, Model model) {
        if (memberNo != null) {
            model.addAttribute("memberNo", memberNo);
        }
        return "myPage/myPageHome";
    }


     // 프로필 수정

    @GetMapping("/myPage/profileEdit")
    public String profileEdit(@RequestParam Long memberNo, Model model) {
        model.addAttribute("memberNo", memberNo);
        return "myPage/profileEdit";
    }


    // 여행 일정 등록

    @GetMapping("/myPage/tripCreate")
    public String tripCreate(@RequestParam Long memberNo, Model model) {
        model.addAttribute("memberNo", memberNo);
        return "myPage/tripCreate";
    }


     // 여행 일정 수정

    @GetMapping("/myPage/tripEdit")
    public String tripEdit(@RequestParam Long memberNo, @RequestParam Long tripId, Model model) {
        model.addAttribute("tripId", tripId);
        model.addAttribute("memberNo", memberNo);
        return "myPage/tripEdit";
    }
}
