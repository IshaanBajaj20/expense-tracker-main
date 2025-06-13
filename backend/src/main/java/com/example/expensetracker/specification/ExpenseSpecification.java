package com.example.expensetracker.specification;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic JPA Specification for filtering Expense records based on optional criteria.
 */
public class ExpenseSpecification {

    /**
     * Constructs a Specification with optional filtering on category, date range, and amount range.
     *
     * @param category  Optional expense category
     * @param startDate Optional start date (inclusive)
     * @param endDate   Optional end date (inclusive)
     * @param minAmount Optional minimum amount
     * @param maxAmount Optional maximum amount
     * @return Specification<Expense> for filtered querying
     */
    public static Specification<Expense> withFilters(String category, LocalDate startDate, LocalDate endDate, Double minAmount, Double maxAmount) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Exclude soft-deleted expenses
            predicates.add(cb.isFalse(root.get("deleted")));

            // Apply category filter if provided
            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            // Apply start date filter if provided
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            // Apply end date filter if provided
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            // Apply minimum amount filter if provided
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            // Apply maximum amount filter if provided
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
