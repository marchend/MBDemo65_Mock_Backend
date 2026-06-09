package com.acmebank.mock.web.error;

/**
 * Thrown when a requested account identifier does not exist in the data store.
 *
 * <p>Unchecked so callers do not need to declare or catch it.
 * {@link GlobalExceptionHandler} maps this to an HTTP 404 response.
 */
public class AccountNotFoundException extends RuntimeException {

    private final String accountId;

    /**
     * Creates the exception for the given missing account identifier.
     *
     * @param accountId the account identifier that was not found
     */
    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
        this.accountId = accountId;
    }

    /** @return the account identifier that triggered this exception */
    public String getAccountId() {
        return accountId;
    }
}
