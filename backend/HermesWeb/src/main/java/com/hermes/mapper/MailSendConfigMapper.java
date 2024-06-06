package com.hermes.mapper;

import com.hermes.entity.MailSendConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailSendConfigMapper {
    MailSendConfig selectMailSendConfigByTemplateType(String templateType);
    Integer insertMailSendConfig(MailSendConfig mailSendConfig);
    Integer updateMailSendConfig(MailSendConfig mailSendConfig);
    Integer softDeleteMailSendConfig(MailSendConfig mailSendConfig);
}
