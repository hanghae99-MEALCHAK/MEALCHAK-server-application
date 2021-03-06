package com.mealchak.mealchakserverapplication.config;

import com.mealchak.mealchakserverapplication.util.CORSFilter;
import org.springframework.beans.factory.annotation.Value;
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
        registry.addMapping("/**")
                .allowedOriginPatterns("*://mealchak.com")
                .allowedHeaders("*")
                .allowedMethods("*");
    }

    @Value("${spring.datasource.imageRoute}")
    private String imageRoute;

    @Override
    // 특정 경로와 로컬을 이어주고 해당 경로가 사용될때 캐시관련 헤더와 함께 내려줌
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.noCache().mustRevalidate().cachePrivate().sMaxAge(Duration.ZERO);
        registry.addResourceHandler("/image/**")
                .addResourceLocations(imageRoute)
                .setCacheControl(cacheControl);
    }
}