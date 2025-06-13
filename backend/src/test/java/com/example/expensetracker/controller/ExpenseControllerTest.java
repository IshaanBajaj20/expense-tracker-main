package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExpenseControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestRestTemplate authRestTemplate;

    @Autowired
    private ExpenseRepository repository;

    @BeforeAll
    void setUpAuth() {
        this.authRestTemplate = restTemplate.withBasicAuth("admin", "admin");
    }

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @DisplayName("Should create and fetch a new expense successfully")
    @Test
    void testCreateAndFetchExpense() {
        Expense e = new Expense("Lunch", 25.0, LocalDate.now(), "Food");
        ResponseEntity<Expense> response = authRestTemplate.postForEntity("/api/expenses", e, Expense.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals("Lunch", response.getBody().getDescription());
    }

    @Test
    void testSoftDeleteEndpoint() {
        Expense e = repository.save(new Expense("Taxi", 30.0, LocalDate.now(), "Transport"));
        authRestTemplate.delete("/api/expenses/archive/" + e.getId());

        Expense updated = repository.findById(e.getId()).orElseThrow();
        assertTrue(updated.isDeleted());
    }

    @Test
    @DisplayName("Should return archived expenses")
    void testGetArchivedExpenses() {
        Expense e = new Expense("Old Expense", 10.0, LocalDate.now().minusDays(40), "Misc");
        e.setDeleted(true);
        repository.save(e);

        ResponseEntity<Map<String, Object>> response = authRestTemplate.exchange(
                "/api/expenses/archived",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Integer) response.getBody().get("totalItems")) >= 1);
    }

    @Test
    @DisplayName("Should fail to create expense with empty description")
    void testInvalidExpenseEmptyDescription() {
        Expense e = new Expense("", 20.0, LocalDate.now(), "Food");
        ResponseEntity<String> response = authRestTemplate.postForEntity("/api/expenses", e, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Description is required"));
    }

    @Test
    @DisplayName("Should fail to create expense with zero amount")
    void testInvalidExpenseZeroAmount() {
        Expense e = new Expense("Zero", 0.0, LocalDate.now(), "Food");
        ResponseEntity<String> response = authRestTemplate.postForEntity("/api/expenses", e, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Amount must be greater than 0"));
    }

    @Test
    @DisplayName("Should fail to create expense with future date")
    void testInvalidExpenseFutureDate() {
        Expense e = new Expense("Future", 50.0, LocalDate.now().plusDays(1), "Travel");
        ResponseEntity<String> response = authRestTemplate.postForEntity("/api/expenses", e, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Date must be in the past or present"));
    }

    @Test
    @DisplayName("Should fail to create expense with empty category")
    void testInvalidExpenseEmptyCategory() {
        Expense e = new Expense("Bus fare", 10.0, LocalDate.now(), "");
        ResponseEntity<String> response = authRestTemplate.postForEntity("/api/expenses", e, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Category is required"));
    }

    @Test
    @DisplayName("Should fail with multiple validation errors")
    void testInvalidExpenseMultipleFields() {
        Expense invalid = new Expense("", -10.0, null, "");
        ResponseEntity<String> response = authRestTemplate.postForEntity("/api/expenses", invalid, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Description is required"));
        assertTrue(response.getBody().contains("Amount must be greater than 0"));
        assertTrue(response.getBody().contains("Date is required"));
        assertTrue(response.getBody().contains("Category is required"));
    }
}
