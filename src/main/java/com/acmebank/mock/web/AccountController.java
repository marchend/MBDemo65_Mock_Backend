package com.acmebank.mock.web;

import com.acmebank.mock.data.MockDataStore;
import com.acmebank.mock.model.Account;
import com.acmebank.mock.model.AccountList;
import com.acmebank.mock.web.error.AccountNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Accounts API.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /accounts?customerId=} — returns all accounts as an {@link AccountList}</li>
 *   <li>{@code GET /accounts/{accountId}} — returns a single {@link Account} or 404</li>
 * </ul>
 *
 * <p>The {@code customerId} query parameter is required. Spring MVC returns 400 automatically
 * when a required {@code @RequestParam} is absent from the request.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final MockDataStore mockDataStore;

    public AccountController(MockDataStore mockDataStore) {
        this.mockDataStore = mockDataStore;
    }

    /**
     * Lists all accounts for the given customer.
     *
     * @param customerId the customer identifier (required)
     * @return an {@link AccountList} containing all fixture accounts
     */
    @GetMapping
    public AccountList listAccounts(@RequestParam String customerId) {
        return new AccountList(mockDataStore.findAll());
    }

    /**
     * Returns a single account by its identifier.
     *
     * @param accountId the account identifier from the URL path
     * @return the matching {@link Account}
     * @throws AccountNotFoundException if no account with that id exists
     */
    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        return mockDataStore.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
