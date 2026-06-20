package com.relaxhub.backend.repository;

import com.relaxhub.backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
