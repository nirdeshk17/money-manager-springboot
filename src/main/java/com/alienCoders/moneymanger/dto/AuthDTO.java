package com.alienCoders.moneymanger.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDTO {
    private String email;
    private String password;
    private String token;
}
