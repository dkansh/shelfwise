package com.shelfwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

// so emails are sent in the background and don't slow down the requests
@Configuration
@EnableAsync
public class AsyncConfig {
}
