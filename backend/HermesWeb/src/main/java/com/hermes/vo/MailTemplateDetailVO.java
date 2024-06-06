package com.hermes.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailTemplateDetailVO {
    private String templateType;
    private String subject;
    private String content;
}
