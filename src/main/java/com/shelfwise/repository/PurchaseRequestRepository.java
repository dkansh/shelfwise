package com.shelfwise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.PurchaseRequest;
import com.shelfwise.model.PurchaseRequestStatus;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    List<PurchaseRequest> findByStatusOrderByCreatedAtAsc(PurchaseRequestStatus status);

    List<PurchaseRequest> findByRequestedByMemberId(Long memberId);
}
