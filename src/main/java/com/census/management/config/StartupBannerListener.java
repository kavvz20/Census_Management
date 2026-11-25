package com.census.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupBannerListener implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupBannerListener.class);
    private final Environment environment;

    public StartupBannerListener(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String port = environment.getProperty("server.port", "8080");
        String activeProfile = String.join(", ", environment.getActiveProfiles());
        if (activeProfile.isEmpty()) {
            activeProfile = "default (SQLite Database: census.db)";
        }

        String banner = """
                
                ========================================================================================
                  CENSUS MANAGEMENT SYSTEM IS READY AND RUNNING!
                ========================================================================================
                  Application URL : http://localhost:%s/
                  Database        : SQLite (census.db - Zero Server Setup Needed)
                  Active Profile  : %s
                ----------------------------------------------------------------------------------------
                  DEMO LOGIN CREDENTIALS:
                  1. Administrator : username: admin          | password: admin123
                     (Full national demographic analytics, manage regions, manage enumerators, CSV export)
                     
                  2. Field Officer : username: enum_mumbai    | password: enum123
                     (Assigned Region: Andheri, Mumbai Suburban - Maharashtra)
                     
                  3. Field Officer : username: enum_bangalore | password: enum123
                     (Assigned Region: Koramangala, Bengaluru Urban - Karnataka)
                     
                  4. Deactivated   : username: enum_delhi     | password: enum123
                     (Assigned Region: Connaught Place, Delhi - Tests account deactivation security)
                ========================================================================================
                """.formatted(port, activeProfile);

        System.out.println(banner);
        logger.info("Census Management System started successfully on port {}", port);
    }
}
