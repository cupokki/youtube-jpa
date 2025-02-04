package io.goorm.youtube.controller;

import io.goorm.youtube.commom.util.PasswordUtil;
import io.goorm.youtube.service.MemberService;
import io.goorm.youtube.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Slf4j
@Controller
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원가입폼
    @GetMapping("/join")
    public String joinForm() {

        return "join";
    }

    //회원가입
    @PostMapping("/join")
    public String  join(@ModelAttribute Member member, Model model) {


        // 아이디 중복 검사
        if (memberService.existsById(member.getMemberId())) {
            model.addAttribute("msg", "이미 사용중인 아이디입니다.");
            return "join"; // 중복인 경우 다시 회원가입 폼으로
        }

        try {
            // 패스워드 암호화
            String encryptedPassword = PasswordUtil.encryptPassword(member.getMemberPw());
            member.setMemberPw(encryptedPassword);

            // 회원 저장
            memberService.save(member);

            model.addAttribute("msg", "회원가입에 성공하였습니다.");
            return "redirect:/login"; // 회원가입 성공시 로그인 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("msg", "회원가입에 실패하였습니다. 다시 시도해주세요.");
            return "join"; // 예외 발생시 회원가입 폼으로
        }

    }

    //로그인폼
    @GetMapping("/login")
    public String lognForm() {

        return "login";
    }

    //로그인
    @PostMapping("/login")
    public String login(@ModelAttribute Member member, HttpSession session, Model model) {


        Member members = memberService.login(member);

        log.debug(member.getMemberId());

        if ( members != null && validateLogin(members.getMemberPw(), member.getMemberPw()) ) {

            log.debug("성공");
            session.setAttribute("member", members);

            return "redirect:/";

        } else {
            log.debug("실패");
            model.addAttribute("msg", "로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요");

            return "login";

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

        return "redirect:/";
    }


    //profile폼 뷰
    @GetMapping("/members")
    public String profileForm(HttpSession session, Model model) throws Exception {


        Member sessionMember = (Member) session.getAttribute("member");

        if (sessionMember == null) {
            // 세션에 사용자 정보가 없으면 로그인 화면으로 리다이렉트
            return "redirect:/login";
        }

        Member member = memberService.find(sessionMember.getMemberSeq());

//        if (optionalMember.isPresent()) {
//            Member member = optionalMember.get();
//            member.setMemberPw("");

            // 모델에 추가
//            model.addAttribute("member", member);
//            model.addAttribute("title", "사용자관라-상세화면" );

//        } else {
//            model.addAttribute("msg", "해당사용자가 존재하지 않습니다.");
//        }
        member.setMemberPw(""); // 기존 비밀번호 가려야하니까
        model.addAttribute("member", member);
        model.addAttribute("title", "사용자관라-상세화면" );
        return "profile";
    }

    //profile수정
    @PostMapping("/members/{memberSeq}")
    public String  update(@ModelAttribute Member member, Model model, RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("memberSeq", member.getMemberSeq());
        redirectAttributes.addFlashAttribute("msg", "수정에 성공하였습니다.");

        return "redirect:/members/{memberSeq}";

        //return "redirect:/mgr/videos/" + video.getVideoSeq();
    }

}
