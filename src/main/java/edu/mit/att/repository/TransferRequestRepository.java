package edu.mit.att.repository;

import edu.mit.att.entity.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TransferRequestRepository extends JpaRepository<TransferRequest, Integer> {
    TransferRequest findById(int id);

    @Query(value = "SELECT r FROM TransferRequest r where r.submissionAgreement.id=?1 order by transferdate asc")
    List<TransferRequest> findAllForSsaOrderByTransferdateAsc(int ssaid);

    List<TransferRequest> findByApprovedTrueAndDeletedFalseOrderByTransferdateAsc();  // approved

    List<TransferRequest> findByApprovedFalseAndDeletedFalseOrderByTransferdateAsc(); // draft
}
