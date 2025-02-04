package io.goorm.youtube.admin.controller;

import io.goorm.youtube.commom.util.PasswordUtil;
import io.goorm.youtube.domain.Admin;
import io.goorm.youtube.service.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/mgr")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    //로그인폼
    @GetMapping("")
    public String login( Model model) {

        model.addAttribute("title", "관리자-로그인" );

        return "mgr/admin/login";
    }

    //로그인
        @PostMapping("/login")
    public String login(@ModelAttribute Admin admin, HttpSession session, Model model) {


        Admin admins = adminService.login(admin);

        if ( admin != null && validateLogin(admins.getAdminPw(), admin.getAdminPw()) ) {

            log.debug("성공");
            session.setAttribute("admin", admins);

            return "redirect:/mgr/admins";

        } else {
            log.debug("실패");
            model.addAttribute("msg", "로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요");

            return "mgr/admin/login";

        }

    }

    public boolean validateLogin(String storedPassword, String password) {

        log.debug("storedPassword : " + storedPassword);
        log.debug("password : " + password);

        return storedPassword != null && PasswordUtil.matches(password, storedPassword);
    }

    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {

        session.invalidate();

        return "redirect:/mgr";
    }


    //리스트
    @GetMapping("/admins")
    public String list(Model model) {


        model.addAttribute("posts", adminService.findAll());
        model.addAttribute("totalPages", 1);
        model.addAttribute("title", "관리자관라-리스트" );

        return "mgr/admin/list";
    }

    //뷰
    @GetMapping("/admins/{adminSeq}")
    public String  get(@PathVariable("adminSeq") Long adminSeq, Model model) throws Exception {

//        Optional<Admin> optionalAdmin = adminService.find(adminSeq);
//
//        if (optionalAdmin.isPresent()) {
//            Admin admin = optionalAdmin.get();
//            admin.setAdminPw("");
//
//            // 모델에 추가
//            model.addAttribute("post", admin);
//            model.addAttribute("title", "관리자관리-상세조회");
//
//        } else {
//            model.addAttribute("msg", "해당관리자가 존재하지 않습니다.");
//        }
//
//        return "mgr/admin/view";

        Admin optionalAdmin = adminService.find(adminSeq);

        return "mgr/admin/view";



    }

    //생성화면
    @GetMapping("/admins/create")
    public String  createForm(Model model) {

        model.addAttribute("title", "관리자관라-생성" );

        return "mgr/admin/create";
    }


    //생성
    @PostMapping("/admins")
    public String create(@ModelAttribute Admin admin, Model model) {

        String encryptedPassword = PasswordUtil.encryptPassword(admin.getAdminPw());
        admin.setAdminPw(encryptedPassword);

        adminService.save(admin);

        return "redirect:/mgr/admins";
    }


    //수정화면
    @GetMapping("/admins/{adminSeq}/update")
    public String updateForm(@PathVariable("adminSeq") Long adminSeq, Model model) throws Exception {

        model.addAttribute("post", adminService.find(adminSeq));
        model.addAttribute("title", "관리자관라-수정" );

        return "mgr/admin/update";
    }

    //수정
    @PostMapping("/admins/{adminSeq}")
    public String  update(@PathVariable("adminSeq") Long adminSeq, @ModelAttribute Admin admin, Model model, RedirectAttributes redirectAttributes) {

        adminService.update(adminSeq, admin);

        redirectAttributes.addAttribute("adminSeq", admin.getAdminSeq());
        redirectAttributes.addFlashAttribute("msg", "수정에 성공하였습니다.");

        return "redirect:/mgr/admins/{adminSeq}";

    }

    //사용여부 변경
    @GetMapping("/admins/{adminSeq}/useyn")
    public String  updateUseYN(@PathVariable("adminSeq") Long adminSeq, Model model, RedirectAttributes redirectAttributes) {


        adminService.updateUseYn(adminSeq);

        redirectAttributes.addAttribute("adminSeq", adminSeq);
        redirectAttributes.addFlashAttribute("msg", "사용여부 수정에 성공하였습니다.");

        return "redirect:/mgr/admins";

    }

}
