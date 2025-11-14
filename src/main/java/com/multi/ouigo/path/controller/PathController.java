package com.multi.ouigo.path.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/tourist/touristDetail")
    public String TouristDetailPage(@RequestParam Long id, Model model) {
        model.addAttribute("touristId", id);
        model.addAttribute("pageName", "detail");
        model.addAttribute("pageFragment", "recruit/recruitDetailPage");
        return "tourist/touristDetail";
    }

    @GetMapping("/tourist/touristUpdatePage")
    public String touristUpdatePage(@RequestParam Long id, Model model) {
        model.addAttribute("touristId", id);
        return "tourist/touristUpdatePage";
    }


    @GetMapping("/recruit/recruitListPage")
    public String recruitListPage(Model model) {

        return "layout";
    }

    @GetMapping("/recruit/recruitUpdatePage")
    public String recruitUpdatePage(Model model) {
        model.addAttribute("pageName", "update");

        return "layout";
    }

    @GetMapping("/myPage/profilePage")
    public String profilePage() {
        // templates/recruit/recruitList.html
        return "myPage/profilePage";
    }

    @GetMapping("/layout")
    public String layout(Model model) {
        model.addAttribute("pageFragment", "recruit/recruitListPage");
        model.addAttribute("pageName", "list");
        return "layout";
    }

    @GetMapping("/recruit/{id}")
    public String recruitDetailPage(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("recruitId", id);
        model.addAttribute("pageFragment", "recruit/recruitDetailPage");
        model.addAttribute("pageName", "detail");

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
        model.addAttribute("pageName", "create");
        return "layout";
    }


    @GetMapping("/recruit/recruitUpdatePage/{recruitId}")
    public String recruitUpdatePage(@PathVariable Long recruitId, Model model) {
        model.addAttribute("pageFragment", "recruit/recruitUpdatePage");
        model.addAttribute("pageTitle", "모집글 수정");
        model.addAttribute("pageName", "update");

        model.addAttribute("recruitId", recruitId);
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

    // 여행 일정 수정

    @GetMapping("/myPage/tripEdit")
    public String tripEdit(@RequestParam Long memberNo, @RequestParam Long tripId, Model model) {
        model.addAttribute("tripId", tripId);
        model.addAttribute("memberNo", memberNo);
        return "myPage/tripEdit";
    }

    @GetMapping("/profile-view")
    public String profileView() {
        return "fragments/profile-view";
    }

    @GetMapping("/profile-edit")
    public String profileEdit() {
        return "fragments/profile-edit";
    }

    @GetMapping("/myPage/right/default")
    public String rightDefault() {
        return "mypage/rightDefault :: rightDefault";
    }

    @GetMapping("/myPage/trip/create")
    public String tripCreate() {
        return "mypage/tripCreate :: tripCreate";
    }

    @GetMapping("/myPage/trip/edit/{tripId}")
    public String tripEdit(@PathVariable Long tripId, Model model) {
        model.addAttribute("tripId", tripId);
        return "mypage/tripEdit :: tripEdit";
    }
}
