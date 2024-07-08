package com.hermes.config;

import com.hermes.Interceptor.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("WebMvcConfig was scanned, call addInterceptors");
        registry.
                addInterceptor(jwtTokenInterceptor).
                addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/register", "/user/login",
                        "/common/**", "/secret_key/**"
                );
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("start to set static resource mapping...");
        registry.
                addResourceHandler("/doc.html").
                addResourceLocations("classpath:/META-INF/resources/");
        registry.
                addResourceHandler("/webjars/**").
                addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.
                addResourceHandler("swagger-ui.html").
                addResourceLocations("classpath:/META-INF/resources/");
    }

    @Bean
    public Docket api() {
        ApiInfo apiInfo = new ApiInfoBuilder().title("hermes web api").version("1.0").description("hermes web api doc").build();
        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo).
                select().
                apis(RequestHandlerSelectors.basePackage("com.hermes")).
                paths(PathSelectors.any()).build();
    }
}
