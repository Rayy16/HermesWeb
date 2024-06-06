package com.hermes.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfo {
    private Integer id;
    private String uid;
    private String userName;
    private String emailAccount;
    private String password;
    private Boolean doubleCheck;
    private String iconLink;
    private Integer userRole;
    private Boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deleteTime;
}
