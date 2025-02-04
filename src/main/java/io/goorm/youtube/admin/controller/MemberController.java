package io.goorm.youtube.admin.controller;

import io.goorm.youtube.domain.Admin;
import io.goorm.youtube.service.MemberService;
import io.goorm.youtube.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller("adminMemberController")
@RequestMapping("/mgr")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //리스트
    @GetMapping("/members")
    public String  list(Model model) {

        model.addAttribute("totalPages", 1);
        model.addAttribute("posts", memberService.findAll());
        model.addAttribute("title", "사용자관라-리스트" );
/*        model.addAttribute("page", defaultVO.getPage());
        model.addAttribute("totalPages", defaultVO.getTotalPages());*/

        return "mgr/member/list";
    }

    //뷰
    @GetMapping("/members/{memberSeq}")
    public String  get(@PathVariable Long memberSeq, Model model) throws Exception {


        Member member = memberService.find(memberSeq);

//        if (optionalMember.isPresent()) {
//            Member member = optionalMember.get();
//            member.setMemberPw("");
//
//            // 모델에 추가
//            model.addAttribute("post", member);
//            model.addAttribute("title", "사용자관라-상세화면" );
//
//        } else {
//            model.addAttribute("msg", "해당사용자가 존재하지 않습니다.");
//        }
        member.setMemberPw("");
        // 모델에 추가
        model.addAttribute("post", member);
        model.addAttribute("title", "사용자관라-상세화면" );
        return "mgr/member/view";
    }

}




