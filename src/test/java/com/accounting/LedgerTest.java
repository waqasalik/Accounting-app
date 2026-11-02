package com.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LedgerTest {

    @Test
    void summaryCalculatesBalance() {
        Ledger ledger = new Ledger();
        ledger.add("Salary", new BigDecimal("1000"), "income");
        ledger.add("Rent", new BigDecimal("400.50"), "expense");

        Ledger.Summary s = ledger.summary();
        assertEquals(new BigDecimal("1000"), s.income());
        assertEquals(new BigDecimal("400.50"), s.expense());
        assertEquals(new BigDecimal("599.50"), s.balance());
    }

    @Test
    void rejectsInvalidInput() {
        Ledger ledger = new Ledger();
        assertThrows(IllegalArgumentException.class, () -> ledger.add("", new BigDecimal("5"), "income"));
        assertThrows(IllegalArgumentException.class, () -> ledger.add("x", new BigDecimal("-5"), "income"));
        assertThrows(IllegalArgumentException.class, () -> ledger.add("x", new BigDecimal("5"), "gift"));
    }

    @Test
    void deleteRemovesTransaction() {
        Ledger ledger = new Ledger();
        Transaction t = ledger.add("Coffee", new BigDecimal("3"), "expense");
        assertTrue(ledger.delete(t.id()));
        assertFalse(ledger.delete(t.id()));
        assertEquals(0, ledger.list().size());
    }
}
