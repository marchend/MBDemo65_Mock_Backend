package com.acmebank.mock.model;

import java.math.BigDecimal;

/**
 * Domain model for a bank account.
 *
 * <p>Java record — Jackson 2.12+ serialises record components to camelCase JSON
 * keys automatically with no additional annotations required.
 */
public record Account(
        String accountId,
        String customerId,
        String displayName,
        String accountType,
        String maskedNumber,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        String currencyCode
) {
}
