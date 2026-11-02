package com.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

    @Test
    void readsFieldsFromJson() {
        String json = "{\"description\":\"Lunch\",\"amount\":12.5,\"type\":\"expense\"}";
        assertEquals("Lunch", JsonUtil.stringField(json, "description"));
        assertEquals(new BigDecimal("12.5"), JsonUtil.numberField(json, "amount"));
        assertEquals("expense", JsonUtil.stringField(json, "type"));
    }

    @Test
    void missingFieldsReturnNull() {
        assertNull(JsonUtil.stringField("{}", "description"));
        assertNull(JsonUtil.numberField("{}", "amount"));
    }

    @Test
    void escapesQuotes() {
        assertEquals("a\\\"b", JsonUtil.escape("a\"b"));
    }
}
