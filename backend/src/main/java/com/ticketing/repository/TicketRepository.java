package com.ticketing.repository;

import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreator(User creator);
    List<Ticket> findByAssignee(User assignee);
    List<Ticket> findByStatus(Ticket.Status status);
    List<Ticket> findByPriority(Ticket.Priority priority);
    
    @Query("SELECT t FROM Ticket t WHERE t.creator = :user OR t.assignee = :user")
    List<Ticket> findByCreatorOrAssignee(@Param("user") User user);
    
    @Query("SELECT t FROM Ticket t WHERE LOWER(t.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ticket> searchByKeyword(@Param("keyword") String keyword);
}
