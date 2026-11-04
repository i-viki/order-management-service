package io.github.vikij.ordermanagement.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    @NotBlank(message = "Query cannot be blank")
    private String query;
}
