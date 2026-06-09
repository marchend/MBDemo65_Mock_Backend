package com.acmebank.mock.model;

import java.util.List;

/**
 * Wrapper record matching the OpenAPI {@code AccountList} schema.
 *
 * <p>Serialises to {@code {"accounts": [...]} } — single top-level field.
 */
public record AccountList(List<Account> accounts) {
}
