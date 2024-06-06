package com.hermes.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MailSendFilter {
    private Integer id;
    private String templateType;
    private Integer action;
    private String uid;
    private Integer userRole;
    private String emailAccount;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
