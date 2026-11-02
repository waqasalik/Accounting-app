package com.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** In-memory list of transactions with simple validation and totals. */
public class Ledger {

    public record Summary(BigDecimal income, BigDecimal expense, BigDecimal balance) {
        public String toJson() {
            return "{\"income\":" + income.toPlainString()
                    + ",\"expense\":" + expense.toPlainString()
                    + ",\"balance\":" + balance.toPlainString() + "}";
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();
    private int nextId = 1;

    public synchronized Transaction add(String description, BigDecimal amount, String type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be a positive number");
        }
        if (!"income".equals(type) && !"expense".equals(type)) {
            throw new IllegalArgumentException("type must be income or expense");
        }
        Transaction t = new Transaction(nextId++, description.trim(), amount, type);
        transactions.add(t);
        return t;
    }

    public synchronized List<Transaction> list() {
        return List.copyOf(transactions);
    }

    public synchronized boolean delete(int id) {
        return transactions.removeIf(t -> t.id() == id);
    }

    public synchronized Summary summary() {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if ("income".equals(t.type())) {
                income = income.add(t.amount());
            } else {
                expense = expense.add(t.amount());
            }
        }
        return new Summary(income, expense, income.subtract(expense));
    }
}
