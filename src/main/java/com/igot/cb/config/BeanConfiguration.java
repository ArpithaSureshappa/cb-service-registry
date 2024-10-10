package com.igot.cb.config;


import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.*;

@Configuration
public class BeanConfiguration {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
