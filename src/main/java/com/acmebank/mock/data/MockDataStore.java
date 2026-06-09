package com.acmebank.mock.data;

import com.acmebank.mock.model.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory fixture data store for the Acme Banking demo.
 *
 * <p>Holds exactly three hardcoded {@link Account} instances for customer
 * {@code cust-alex} matching the OpenAPI mock-backend specification.
 * No database is required — this is an intentional stand-in.
 */
@Component
public class MockDataStore {

    private static final List<Account> ACCOUNTS = List.of(
            new Account(
                    "acct-1",
                    "cust-alex",
                    "Unlimited Chequing",
                    "CHEQUING",
                    "4821",
                    new BigDecimal("4287.52"),
                    new BigDecimal("4287.52"),
                    "USD"
            ),
            new Account(
                    "acct-2",
                    "cust-alex",
                    "High-Interest Savings",
                    "SAVINGS",
                    "9203",
                    new BigDecimal("18940.00"),
                    new BigDecimal("18940.00"),
                    "USD"
            ),
            new Account(
                    "acct-3",
                    "cust-alex",
                    "Visa Platinum",
                    "CREDIT",
                    "1188",
                    new BigDecimal("-612.34"),
                    new BigDecimal("9387.66"),
                    "USD"
            )
    );

    private final Map<String, Account> accountIndex;

    public MockDataStore() {
        this.accountIndex = ACCOUNTS.stream()
                .collect(Collectors.toMap(Account::accountId, Function.identity()));
    }

    /**
     * Returns all accounts in the data store.
     *
     * @return unmodifiable list of all {@link Account} fixtures
     */
    public List<Account> findAll() {
        return ACCOUNTS;
    }

    /**
     * Looks up a single account by its identifier.
     *
     * @param accountId the account identifier to search for
     * @return an {@link Optional} containing the account, or empty if not found
     */
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accountIndex.get(accountId));
    }
}
