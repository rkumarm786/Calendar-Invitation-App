package com.example.calendly.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class UserDto {
    private Long id;
    private String email;
    private String token;
}
