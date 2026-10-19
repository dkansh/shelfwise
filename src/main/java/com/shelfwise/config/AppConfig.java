package com.shelfwise.config;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LibraryProperties.class)
public class AppConfig {

    // TODO: inject this into LoanService instead of calling LocalDate.now() everywhere,
    //  then the date tests won't depend on the real date
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
