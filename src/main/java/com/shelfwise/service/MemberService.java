package com.shelfwise.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.util.DateUtils;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public List<Member> searchByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    public Member createMember(Member member) {
        // TODO: validate email format and make sure it's unique (findByEmail is in the repo already)
        if (member.getMembershipType() == null) {
            member.setMembershipType(MembershipType.STANDARD);
        }
        member.setJoinedDate(new Date());
        member.setActive(true);
        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updated) {
        Member existing = getMember(id);
        if (existing == null) {
            return null;
        }
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setMembershipType(updated.getMembershipType());
        return memberRepository.save(existing);
    }

    public void deactivateMember(Long id) {
        Member member = getMember(id);
        member.setActive(false);
        // TODO: should we block this if the member still has books out?
        memberRepository.save(member);
    }

    /**
     * Returns how many days ago the member joined the library.
     */
    public long getMembershipDays(Long id) {
        Member member = getMember(id);
        LocalDate joined = DateUtils.toLocalDate(member.getJoinedDate());
        return DateUtils.daysBetween(joined, LocalDate.now());
    }

    // TODO: STUDENT memberships are supposed to expire after 1 year
}
