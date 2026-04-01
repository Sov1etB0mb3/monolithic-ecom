package com.calt.burox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * Properties specific to Monolithic Ecom.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@EnableR2dbcAuditing
public class ApplicationProperties {
    // jhipster-needle-application-properties-property

    // jhipster-needle-application-properties-property-getter

    // jhipster-needle-application-properties-property-class
}
