package io.github.vikij.ordermanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @jakarta.validation.constraints.Email
    private String email;

    private String firstName;
    private String lastName;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
}

