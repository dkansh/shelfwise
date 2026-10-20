package com.shelfwise.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.CopyTransfer;
import com.shelfwise.model.TransferStatus;

public interface CopyTransferRepository extends JpaRepository<CopyTransfer, Long> {

    List<CopyTransfer> findByStatusIn(Collection<TransferStatus> statuses);

    List<CopyTransfer> findByCopyId(Long copyId);
}
