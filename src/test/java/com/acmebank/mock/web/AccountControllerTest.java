package com.acmebank.mock.web;

import com.acmebank.mock.data.MockDataStore;
import com.acmebank.mock.model.Account;
import com.acmebank.mock.web.error.AccountNotFoundException;
import com.acmebank.mock.web.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code @WebMvcTest} slice for {@link AccountController}.
 *
 * <p>Verifies the HTTP wire shape: status codes, camelCase JSON keys,
 * BigDecimal serialised as a plain numeric value (not a string), and
 * RFC 7807 problem+json on 404/400.
 */
@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MockDataStore mockDataStore;

    private static final List<Account> THREE_ACCOUNTS = List.of(
            new Account("acct-1", "cust-alex", "Unlimited Chequing", "CHEQUING", "4821",
                    new BigDecimal("4287.52"), new BigDecimal("4287.52"), "USD"),
            new Account("acct-2", "cust-alex", "High-Interest Savings", "SAVINGS", "9203",
                    new BigDecimal("18940.00"), new BigDecimal("18940.00"), "USD"),
            new Account("acct-3", "cust-alex", "Visa Platinum", "CREDIT", "1188",
                    new BigDecimal("-612.34"), new BigDecimal("9387.66"), "USD")
    );

    // -------------------------------------------------------------------------
    // GET /accounts?customerId=cust-alex  →  200 AccountList
    // -------------------------------------------------------------------------

    @Test
    void listAccounts_returnsOkWithThreeAccounts() throws Exception {
        when(mockDataStore.findAll()).thenReturn(THREE_ACCOUNTS);

        mockMvc.perform(get("/accounts").param("customerId", "cust-alex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts", hasSize(3)));
    }

    @Test
    void listAccounts_firstAccountHasCamelCaseAccountIdKey() throws Exception {
        when(mockDataStore.findAll()).thenReturn(THREE_ACCOUNTS);

        mockMvc.perform(get("/accounts").param("customerId", "cust-alex"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].accountId", is("acct-1")));
    }

    @Test
    void listAccounts_currentBalanceIsNumericNotString() throws Exception {
        when(mockDataStore.findAll()).thenReturn(THREE_ACCOUNTS);

        mockMvc.perform(get("/accounts").param("customerId", "cust-alex"))
                .andExpect(status().isOk())
                // jsonPath with Double.class asserts value is a JSON number, not a quoted string
                .andExpect(jsonPath("$.accounts[0].currentBalance", instanceOf(Double.class)))
                .andExpect(jsonPath("$.accounts[0].currentBalance", is(4287.52)));
    }

    // -------------------------------------------------------------------------
    // GET /accounts/acct-1  →  200 Account
    // -------------------------------------------------------------------------

    @Test
    void getAccount_knownId_returnsOkWithCorrectFields() throws Exception {
        Account acct1 = THREE_ACCOUNTS.get(0);
        when(mockDataStore.findById("acct-1")).thenReturn(Optional.of(acct1));

        mockMvc.perform(get("/accounts/acct-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Unlimited Chequing")))
                .andExpect(jsonPath("$.currentBalance", instanceOf(Double.class)))
                .andExpect(jsonPath("$.currentBalance", is(4287.52)));
    }

    // -------------------------------------------------------------------------
    // GET /accounts/unknown-id  →  404 application/problem+json
    // -------------------------------------------------------------------------

    @Test
    void getAccount_unknownId_returns404ProblemJson() throws Exception {
        when(mockDataStore.findById("unknown-id"))
                .thenThrow(new AccountNotFoundException("unknown-id"));

        mockMvc.perform(get("/accounts/unknown-id"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void getAccount_unknownId_responseBodyContainsNoStackTrace() throws Exception {
        when(mockDataStore.findById("unknown-id"))
                .thenThrow(new AccountNotFoundException("unknown-id"));

        mockMvc.perform(get("/accounts/unknown-id"))
                .andExpect(status().isNotFound())
                // Body must NOT contain typical stack-trace markers
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }

    // -------------------------------------------------------------------------
    // GET /accounts  (missing customerId)  →  400
    // -------------------------------------------------------------------------

    @Test
    void listAccounts_missingCustomerId_returns400() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isBadRequest());
    }
}
