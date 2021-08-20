package com.mealchak.mealchakserverapplication.config;

import com.mealchak.mealchakserverapplication.util.CORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean getFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean<>(new CORSFilter());
        registrationBean.addUrlPatterns("/**");
        return registrationBean;
    }

    // CORS 추가
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*://*").allowedHeaders("*").allowedMethods("*");
    }

    // 경로 수정 필요
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.noCache().mustRevalidate().cachePrivate().sMaxAge(Duration.ZERO);
        registry.addResourceHandler("/image/**")
//                .addResourceLocations("file:/home/ubuntu/image/");  // AWS EC2
                .addResourceLocations("file:/root/image/")   // NAVER EC2
                .setCacheControl(cacheControl);
    }
}