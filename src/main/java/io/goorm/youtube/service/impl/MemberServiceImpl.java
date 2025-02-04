package io.goorm.youtube.service.impl;

import io.goorm.youtube.domain.Admin;
import io.goorm.youtube.repository.MemberRepository;
import io.goorm.youtube.domain.Member;
import io.goorm.youtube.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Slf4j
@org.springframework.stereotype.Service
public class MemberServiceImpl implements MemberService {



    private MemberRepository memberRepository;

    @Autowired
    public MemberServiceImpl(MemberRepository mmberRepository) {
        this.memberRepository = mmberRepository;
    }


    public List<Member> findAll() {

        return memberRepository.findAll();
    }

    public Member login(Member member) {

        return memberRepository.findByMemberId(member.getMemberId());
    }

    public Member find(Long memberSeq) throws Exception {

        return memberRepository.findById(memberSeq).orElseThrow(()->new Exception("일치하는 회원이 없습니다."));
    }

    public boolean existsById(String memberId) {

        return memberRepository.existsByMemberId(memberId);
    }

    public Member save(Member member) {

        return memberRepository.save(member);
    }

    public Member update(Member member) {

        return memberRepository.save(member);
    }

    public Member updateUseYn(Long memberSeq) {

        Member existingMember = memberRepository.findById(memberSeq).orElseThrow(() -> new RuntimeException("Admin not found"));

        /*Optional<Member> optionalMember = memberRepository.findById(memberSeq);

        if (optionalMember.isPresent()) {
            Member existingMember = optionalMember.get();
            // 필요한 로직 수행
        } else {
            throw new RuntimeException("Member not found");
        }*/


        if (existingMember != null && existingMember.getUseYn().equals("N")) {
            existingMember.setUseYn("Y");
        } else {
            existingMember.setUseYn("N");
        }

        return memberRepository.save(existingMember);

    }

}
