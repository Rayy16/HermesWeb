package com.hermes.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QueryUserDetailsVO extends PageQueryVO {
    Integer userRole;
    String emailAccountLike;
    String userNameLike;

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    Boolean isDeleted;

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public String getEmailAccountLike() {
        return emailAccountLike;
    }

    public void setEmailAccountLike(String emailAccountLike) {
        this.emailAccountLike = emailAccountLike;
    }

    public String getUserNameLike() {
        return userNameLike;
    }

    public void setUserNameLike(String userNameLike) {
        this.userNameLike = userNameLike;
    }

    @Override
    public String toString() {
        return "QueryUserDetailsVO{" +
                "userRole=" + userRole +
                ", emailAccountLike='" + emailAccountLike + '\'' +
                ", userNameLike='" + userNameLike + '\'' +
                ", pageSize=" + pageSize +
                ", page=" + page +
                '}';
    }
}
