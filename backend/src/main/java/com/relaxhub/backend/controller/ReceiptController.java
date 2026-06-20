package com.relaxhub.backend.controller;

import com.relaxhub.backend.dto.ApiResponse;
import com.relaxhub.backend.dto.CreateReceiptRequest;
import com.relaxhub.backend.dto.ReceiptResponse;
import com.relaxhub.backend.security.JwtAuthenticationToken;
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
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReceiptResponse>> create(
            @AuthenticationPrincipal JwtAuthenticationToken auth,
            @Valid @RequestBody CreateReceiptRequest request
    ) {
        ReceiptResponse response = receiptService.create(auth.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Receipt saved", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> list(
            @AuthenticationPrincipal JwtAuthenticationToken auth
    ) {
        List<ReceiptResponse> items = receiptService.listForUser(auth.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Receipts loaded", items));
    }
}
