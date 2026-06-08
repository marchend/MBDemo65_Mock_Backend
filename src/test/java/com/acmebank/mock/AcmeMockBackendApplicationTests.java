package com.acmebank.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Bootstrap smoke test — proves the Spring application context loads cleanly.
 *
 * <p>Real behaviour tests (@WebMvcTest slices per controller, MockDataStore
 * fixture assertions) are added in feature stories.
 */
@SpringBootTest
class AcmeMockBackendApplicationTests {

    @Test
    void contextLoads() {
        // If the context fails to start, this test fails — that's the whole point.
    }
}
