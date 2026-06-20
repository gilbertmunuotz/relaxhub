package com.relaxhub.backend.service;

import com.relaxhub.backend.dto.ComplaintResponse;
import com.relaxhub.backend.dto.CreateComplaintRequest;
import com.relaxhub.backend.entity.Complaint;
import com.relaxhub.backend.entity.User;
import com.relaxhub.backend.exception.UserNotFoundException;
import com.relaxhub.backend.repository.ComplaintRepository;
import com.relaxhub.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ComplaintResponse create(Long userId, CreateComplaintRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setSubject(request.getSubject().trim());
        complaint.setDescription(request.getDescription().trim());
        complaint.setStatus("OPEN");

        Complaint saved = complaintRepository.save(complaint);
        return toResponse(saved);
    }

    public List<ComplaintResponse> listForUser(Long userId) {
        return complaintRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ComplaintResponse toResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getSubject(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getCreatedAt()
        );
    }
}
