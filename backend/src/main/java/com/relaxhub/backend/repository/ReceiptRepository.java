package com.relaxhub.backend.repository;

import com.relaxhub.backend.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
