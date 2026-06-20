package com.relaxhub.backend.service;

import com.relaxhub.backend.dto.CreateFeedbackRequest;
import com.relaxhub.backend.dto.FeedbackResponse;
import com.relaxhub.backend.entity.Feedback;
import com.relaxhub.backend.entity.User;
import com.relaxhub.backend.exception.UserNotFoundException;
import com.relaxhub.backend.repository.FeedbackRepository;
import com.relaxhub.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedbackResponse create(Long userId, CreateFeedbackRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setRating(request.getRating());
        feedback.setMessage(request.getMessage().trim());

        Feedback saved = feedbackRepository.save(feedback);
        return toResponse(saved);
    }

    public List<FeedbackResponse> listForUser(Long userId) {
        return feedbackRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private FeedbackResponse toResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getRating(),
                feedback.getMessage(),
                feedback.getCreatedAt()
        );
    }
}
