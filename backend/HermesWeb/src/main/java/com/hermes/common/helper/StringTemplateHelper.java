package com.hermes.common.helper;

import lombok.extern.slf4j.Slf4j;
import org.stringtemplate.v4.ST;

import java.util.Map;

@Slf4j
public class StringTemplateHelper {
    public static String parse(String template, Map<String, Object> vars) {
        log.info("template = {}, vars = {}", template, vars);
        ST st = new ST(template);
        for(Map.Entry<String, Object> var : vars.entrySet()) {
            st.add(var.getKey(), var.getValue());
        }
        return st.render();
    }
}
