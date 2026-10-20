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

    @Autowired
    private AuditService auditService;

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
        member.setMembershipExpiry(LocalDate.now().plusYears(1));
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
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setMembershipType(updated.getMembershipType());
        return memberRepository.save(existing);
    }

    public void deactivateMember(Long id) {
        Member member = getMember(id);
        member.setActive(false);
        // TODO: should we block this if the member still has books out?
        memberRepository.save(member);
        auditService.log("DEACTIVATE_MEMBER", "Member", id, null);
    }

    /**
     * Returns how many days ago the member joined the library.
     */
    public long getMembershipDays(Long id) {
        Member member = getMember(id);
        LocalDate joined = DateUtils.toLocalDate(member.getJoinedDate());
        return DateUtils.daysBetween(joined, LocalDate.now());
    }

    /**
     * Extends the membership by the given number of months. If it already expired
     * the new period starts today.
     */
    public Member renewMembership(Long id, int months) {
        Member member = getMember(id);
        LocalDate base = member.getMembershipExpiry().isBefore(LocalDate.now())
                ? LocalDate.now()
                : member.getMembershipExpiry();
        member.setMembershipExpiry(base.plusMonths(months));
        member.setActive(true);
        return memberRepository.save(member);
    }

    public List<Member> getMembersExpiringWithin(int days) {
        return memberRepository.findByMembershipExpiryBetween(LocalDate.now(), LocalDate.now().plusDays(days));
    }

    public int deactivateExpiredMembers() {
        List<Member> expired = memberRepository.findByMembershipExpiryBeforeAndActiveTrue(LocalDate.now());
        for (Member m : expired) {
            m.setActive(false);
            memberRepository.save(m);
        }
        return expired.size();
    }

    // TODO: STUDENT memberships are supposed to expire after 1 year
}
