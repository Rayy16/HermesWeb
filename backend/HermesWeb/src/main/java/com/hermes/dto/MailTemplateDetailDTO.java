package com.hermes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailTemplateDetailDTO {
    private String templateType;
    private String subject;
    private String content;
}
