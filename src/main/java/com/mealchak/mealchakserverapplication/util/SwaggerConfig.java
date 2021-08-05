package com.mealchak.mealchakserverapplication.util;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    //    swagger 접속 링크
//    http://localhost:8080/swagger-ui/index.html
    //1. controller class 에 달아주세요
    //@Api(tags = {"1. Book_책api"}) // Swagger # 1. Book

    //2. 각 api 에 mapping있는 부분에 달아주세요
    //@ApiOperation(value = "카테고리별 책 pageable조회", notes = "카테고리별 책을 page로 조회합니다.")
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
