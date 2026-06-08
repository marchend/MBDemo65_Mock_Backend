package com.acmebank.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Acme Mock Backend — stand-in systems-of-record for the Acme Banking demo.
 *
 * <p>Bootstrap entry point. Future stories add controllers, models, and mock
 * data fixtures (see CLAUDE.md for planned architecture).
 */
@SpringBootApplication
public class AcmeMockBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcmeMockBackendApplication.class, args);
    }
}
