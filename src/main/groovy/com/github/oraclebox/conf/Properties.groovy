package com.github.oraclebox.conf

import com.opentok.OpenTok
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties([OpenTokConfig.class])
class Properties {
}

@Configuration
@ConfigurationProperties(prefix = "application.opentok")
class OpenTokConfig{
    int apiKey;
    String apiSecret;

    @Bean
    OpenTok openTok(){
        return new OpenTok(apiKey, apiSecret);
    }
}