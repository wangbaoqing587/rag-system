package com.wbq.ragsystem.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HelpCenterTicketRepository extends JpaRepository<HelpCenterTicket, Long> {

    @Query("""
            SELECT t FROM HelpCenterTicket t
            WHERE (:status IS NULL OR t.status = :status)
            AND (
              :keyword IS NULL OR :keyword = '' OR
              LOWER(t.ticketNo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(t.userId) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            ORDER BY t.createdAt DESC
            """)
    Page<HelpCenterTicket> search(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

    long countByTicketNoStartingWith(String prefix);
}
