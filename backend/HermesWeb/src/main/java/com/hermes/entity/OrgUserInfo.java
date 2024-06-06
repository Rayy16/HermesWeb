package com.hermes.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrgUserInfo {
    private Integer id;
    private String uid;
    private Integer orgId;
    private Integer orgRole;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
