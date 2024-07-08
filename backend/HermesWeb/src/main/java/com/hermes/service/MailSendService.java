package com.hermes.service;

import com.hermes.common.enum_type.TemplateType;
import com.hermes.dto.MailTemplateDetailDTO;

import javax.mail.MessagingException;
import java.util.List;

public interface MailSendService {
    void sendTextMail(List<String> emails, String subject, String text) throws MessagingException;
    List<String> filterEmailsByTemplateType(String templateType, List<String> emails);
    MailTemplateDetailDTO getMailTemplateDetailByTemplateType(TemplateType templateType);
}
