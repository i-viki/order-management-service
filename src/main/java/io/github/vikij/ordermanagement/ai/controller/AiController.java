package io.github.vikij.ordermanagement.ai.controller;

import io.github.vikij.ordermanagement.ai.dto.AiChatRequest;
import io.github.vikij.ordermanagement.ai.dto.AiChatResponse;
import io.github.vikij.ordermanagement.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Support Assistant", description = "AI-powered support for order queries")
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Query the AI support assistant about your orders")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request, Authentication authentication) {
        String response = aiService.getChatResponse(request.getQuery(), authentication);
        return ResponseEntity.ok(AiChatResponse.builder()
                .response(response)
                .build());
    }
}
