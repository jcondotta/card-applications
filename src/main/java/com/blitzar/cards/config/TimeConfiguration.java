package com.blitzar.cards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfiguration {

    @Bean
    public Clock currentInstantUTC(){
        return Clock.systemUTC();
    }
}