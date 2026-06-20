package com.relaxhub.backend.repository;

import com.relaxhub.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
