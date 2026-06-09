package com.acmebank.mock.data;

import com.acmebank.mock.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MockDataStore}.
 *
 * <p>Pins the exact fixture values so silent drift is caught immediately.
 * No Spring context is loaded — plain JUnit 5.
 */
class MockDataStoreTest {

    private MockDataStore dataStore;

    @BeforeEach
    void setUp() {
        dataStore = new MockDataStore();
    }

    @Test
    void findAll_returnsExactlyThreeAccounts() {
        List<Account> accounts = dataStore.findAll();
        assertThat(accounts).hasSize(3);
    }

    @Test
    void findAll_containsAllExpectedAccountIds() {
        List<String> ids = dataStore.findAll().stream()
                .map(Account::accountId)
                .toList();
        assertThat(ids).containsExactly("acct-1", "acct-2", "acct-3");
    }

    @Test
    void acct1_hasCorrectFixtureValues() {
        Account acct = dataStore.findById("acct-1").orElseThrow();

        assertThat(acct.accountId()).isEqualTo("acct-1");
        assertThat(acct.customerId()).isEqualTo("cust-alex");
        assertThat(acct.displayName()).isEqualTo("Unlimited Chequing");
        assertThat(acct.accountType()).isEqualTo("CHEQUING");
        assertThat(acct.maskedNumber()).isEqualTo("4821");
        assertThat(acct.currentBalance()).isEqualByComparingTo(new BigDecimal("4287.52"));
        assertThat(acct.availableBalance()).isEqualByComparingTo(new BigDecimal("4287.52"));
        assertThat(acct.currencyCode()).isEqualTo("USD");
    }

    @Test
    void acct2_hasCorrectFixtureValues() {
        Account acct = dataStore.findById("acct-2").orElseThrow();

        assertThat(acct.accountId()).isEqualTo("acct-2");
        assertThat(acct.accountType()).isEqualTo("SAVINGS");
        assertThat(acct.maskedNumber()).isEqualTo("9203");
        assertThat(acct.currentBalance()).isEqualByComparingTo(new BigDecimal("18940.00"));
        assertThat(acct.availableBalance()).isEqualByComparingTo(new BigDecimal("18940.00"));
    }

    @Test
    void acct3_hasCorrectFixtureValues() {
        Account acct = dataStore.findById("acct-3").orElseThrow();

        assertThat(acct.accountId()).isEqualTo("acct-3");
        assertThat(acct.accountType()).isEqualTo("CREDIT");
        assertThat(acct.maskedNumber()).isEqualTo("1188");
        assertThat(acct.currentBalance()).isEqualByComparingTo(new BigDecimal("-612.34"));
        assertThat(acct.availableBalance()).isEqualByComparingTo(new BigDecimal("9387.66"));
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        Optional<Account> result = dataStore.findById("unknown-acct-id");
        assertThat(result).isEmpty();
    }

    @Test
    void findById_knownId_returnsAccount() {
        Optional<Account> result = dataStore.findById("acct-1");
        assertThat(result).isPresent();
        assertThat(result.get().accountId()).isEqualTo("acct-1");
    }
}
