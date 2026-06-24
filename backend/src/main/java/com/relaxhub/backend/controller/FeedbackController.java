package com.relaxhub.backend.controller;

import com.relaxhub.backend.dto.ApiResponse;
import com.relaxhub.backend.dto.ComplaintResponse;
import com.relaxhub.backend.dto.CreateComplaintRequest;
import com.relaxhub.backend.dto.CreateFeedbackRequest;
import com.relaxhub.backend.dto.CreateReceiptRequest;
import com.relaxhub.backend.dto.FeedbackResponse;
import com.relaxhub.backend.dto.ReceiptResponse;
import com.relaxhub.backend.security.AuthenticatedUser;
import com.relaxhub.backend.service.ComplaintService;
import com.relaxhub.backend.service.FeedbackService;
import com.relaxhub.backend.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateFeedbackRequest request
    ) {
        FeedbackResponse response = feedbackService.create(user.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> list(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        List<FeedbackResponse> items = feedbackService.listForUser(user.userId());
        return ResponseEntity.ok(ApiResponse.success("Feedback loaded", items));
    }
}
