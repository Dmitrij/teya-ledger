package com.teya.ledger.app;

import com.teya.ledger.app.config.TeyaLedgerSpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TeyaLedgerSpringConfig.class)
public class TeyaLedgerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeyaLedgerApplication.class, args);
    }

}
