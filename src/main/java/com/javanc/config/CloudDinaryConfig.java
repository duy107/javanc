package com.javanc.config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudDinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("api_key", "276659269773163");
        config.put("cloud_name", "dxx1lgamz");
        config.put("api_secret", "X6Hb2LUFRTtzgA6Bqx7pcufhEKo");
        return new Cloudinary(config);
    }
}
