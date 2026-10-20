package com.shelfwise.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shelfwise.model.Member;
import com.shelfwise.service.MemberService;
import com.shelfwise.service.NotificationService;

/**
 * Every morning: remind members whose membership runs out within a week,
 * and deactivate the ones that already expired.
 */
@Component
public class MembershipExpiryScheduler {

    private final MemberService memberService;
    private final NotificationService notificationService;

    public MembershipExpiryScheduler(MemberService memberService, NotificationService notificationService) {
        this.memberService = memberService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkMemberships() {
        List<Member> expiringSoon = memberService.getMembersExpiringWithin(7);
        for (Member member : expiringSoon) {
            notificationService.sendMembershipExpiring(member, member.getMembershipExpiry());
        }

        int deactivated = memberService.deactivateExpiredMembers();
        System.out.println("Membership check done. Reminded " + expiringSoon.size()
                + ", deactivated " + deactivated);
    }
}
