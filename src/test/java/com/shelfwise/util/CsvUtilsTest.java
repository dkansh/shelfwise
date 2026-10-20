package com.shelfwise.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CsvUtilsTest {

    @Test
    void parseSimpleLine() {
        assertArrayEquals(new String[] {"a", "b", "c"}, CsvUtils.parseLine("a,b,c"));
    }

    @Test
    void parseKeepsEmptyColumns() {
        assertEquals(4, CsvUtils.parseLine("a,,c,").length);
    }

    @Test
    void escapeWrapsValuesWithCommas() {
        assertEquals("\"Guns, Germs\"", CsvUtils.escape("Guns, Germs"));
    }

    @Test
    void escapeDoublesQuotes() {
        assertEquals("\"say \"\"hi\"\"\"", CsvUtils.escape("say \"hi\""));
    }

    @Test
    void toLineJoinsValues() {
        assertEquals("1,x,", CsvUtils.toLine(1, "x", null));
    }

    @Test
    @Disabled("TODO: parseLine doesn't understand quotes yet")
    void parseQuotedValueWithComma() {
        String[] cols = CsvUtils.parseLine("\"Guns, Germs, and Steel\",Jared Diamond");
        assertEquals(2, cols.length);
        assertEquals("Guns, Germs, and Steel", cols[0]);
    }
}
