package com.shelfwise.fines;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByMemberId(Long memberId);

    List<Fine> findByMemberIdAndStatusIn(Long memberId, Collection<FineStatus> statuses);

    List<Fine> findByLoanId(Long loanId);
}
