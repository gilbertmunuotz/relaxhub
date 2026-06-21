package com.relaxhub.backend.controller;

import com.relaxhub.backend.dto.ApiResponse;
import com.relaxhub.backend.dto.ComplaintResponse;
import com.relaxhub.backend.dto.CreateComplaintRequest;
import com.relaxhub.backend.security.JwtAuthenticationToken;
import com.relaxhub.backend.service.ComplaintService;
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
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponse>> create(
            @AuthenticationPrincipal JwtAuthenticationToken auth,
            @Valid @RequestBody CreateComplaintRequest request
    ) {
        ComplaintResponse response = complaintService.create(auth.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> list(
            @AuthenticationPrincipal JwtAuthenticationToken auth
    ) {
        List<ComplaintResponse> items = complaintService.listForUser(auth.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Complaints loaded", items));
    }
}
