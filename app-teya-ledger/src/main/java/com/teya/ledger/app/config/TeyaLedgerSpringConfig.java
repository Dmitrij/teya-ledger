package com.teya.ledger.app.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.teya.ledger.app"})
@EntityScan(basePackages = {"com.teya.ledger.app"})
@ComponentScan(basePackages = {"com.teya.ledger.app"})
public class TeyaLedgerSpringConfig {
}