package com.shelfwise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.model.Member;
import com.shelfwise.service.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<Member> getMembers(@RequestParam(required = false) String name) {
        if (name != null) {
            return memberService.searchByName(name);
        }
        return memberService.getAllMembers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        Member member = memberService.getMember(id);
        if (member == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    @DeleteMapping("/{id}")
    public void deactivateMember(@PathVariable Long id) {
        memberService.deactivateMember(id);
    }

    @GetMapping("/{id}/membership-days")
    public long getMembershipDays(@PathVariable Long id) {
        return memberService.getMembershipDays(id);
    }

    @PutMapping("/{id}/renew-membership")
    public Member renewMembership(@PathVariable Long id, @RequestParam(defaultValue = "12") int months) {
        return memberService.renewMembership(id, months);
    }

    @GetMapping("/expiring")
    public List<Member> expiring(@RequestParam(defaultValue = "30") int days) {
        return memberService.getMembersExpiringWithin(days);
    }
}
