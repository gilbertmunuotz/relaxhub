package com.relaxhub.backend.service;

import com.relaxhub.backend.dto.CreateReceiptRequest;
import com.relaxhub.backend.dto.ReceiptResponse;
import com.relaxhub.backend.entity.Receipt;
import com.relaxhub.backend.entity.User;
import com.relaxhub.backend.exception.UserNotFoundException;
import com.relaxhub.backend.repository.ReceiptRepository;
import com.relaxhub.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final UserRepository userRepository;

    public ReceiptService(ReceiptRepository receiptRepository, UserRepository userRepository) {
        this.receiptRepository = receiptRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReceiptResponse create(Long userId, CreateReceiptRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Receipt receipt = new Receipt();
        receipt.setUser(user);
        receipt.setPlaceName(request.getPlaceName().trim());
        receipt.setVisitDate(request.getVisitDate());
        receipt.setVisitTime(request.getVisitTime());
        receipt.setAmount(request.getAmount());
        receipt.setNotes(request.getNotes());

        Receipt saved = receiptRepository.save(receipt);
        return toResponse(saved);
    }

    public List<ReceiptResponse> listForUser(Long userId) {
        return receiptRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ReceiptResponse toResponse(Receipt receipt) {
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getPlaceName(),
                receipt.getVisitDate(),
                receipt.getVisitTime(),
                receipt.getAmount(),
                receipt.getNotes(),
                receipt.getCreatedAt()
        );
    }
}
