package com.mealchak.mealchakserverapplication.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class WebMvcContextConfig {

    private static final int FILE_MAX_UPLOAD_SIZE = 10_485_760; // 1024 * 1024 * 10
    // 필요에 따라 변경

    @Bean
    @ConditionalOnBean(MultipartResolver.class)
    @ConditionalOnMissingBean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    public MultipartResolver multipartResolver(MultipartResolver resolver) {
        org.springframework.web.multipart.commons.CommonsMultipartResolver resolver1 =
                new org.springframework.web.multipart.commons.CommonsMultipartResolver();
        resolver1.setMaxUploadSize(FILE_MAX_UPLOAD_SIZE);
        return resolver;
    }

}
