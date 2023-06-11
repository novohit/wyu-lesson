package com.wyu.wyulesson.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author zwx
 * @date 2022-06-20 21:40
 */
@EnableWebMvc
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket getDocket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wyu.wyulesson.controller")) // 只扫描我们写的接口，不显示SpringBoot默认的error接口
                .build();
    }

    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .contact(new Contact("novo", "https://www.bilibili.com", "zwx_info@163.com"))
                .title("课程表在线API接口文档")
                .description("描述")
                .version("1.0")
                .build();
    }
}
