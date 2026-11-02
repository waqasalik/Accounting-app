package com.accounting;

import java.math.BigDecimal;

/** One income or expense entry. */
public record Transaction(int id, String description, BigDecimal amount, String type) {

    public String toJson() {
        return "{\"id\":" + id
                + ",\"description\":\"" + JsonUtil.escape(description) + "\""
                + ",\"amount\":" + amount.toPlainString()
                + ",\"type\":\"" + type + "\"}";
    }
}
