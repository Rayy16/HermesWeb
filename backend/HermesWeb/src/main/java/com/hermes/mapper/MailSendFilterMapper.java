package com.hermes.mapper;

import com.hermes.entity.MailSendFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MailSendFilterMapper {
    List<MailSendFilter> selectMailSendFilterByTemplateType(String templateType);
}
