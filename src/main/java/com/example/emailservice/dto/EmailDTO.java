package com.example.emailservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {
    private String recipient;
    private String subject;
    private String content;
}
