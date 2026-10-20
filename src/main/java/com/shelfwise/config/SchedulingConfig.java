package com.shelfwise.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Turns on the scheduled jobs (overdue loans, reservation expiry, membership reminders).
 * Controlled by shelfwise.scheduling.enabled so they don't run on every dev machine.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "shelfwise.scheduling", name = "enabled", havingValue = "true")
public class SchedulingConfig {
}
