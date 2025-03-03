package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLUpdateServiceTest {

    private SQLUpdateService sqlUpdateService;

    @BeforeEach
    void setUp() {
        sqlUpdateService = new SQLUpdateService();
    }

    @Test
    void testGenerateUpdateQuery() {

        Map<Integer, String> typeToTable = new HashMap<>();
        typeToTable.put(0, "table_default");
        typeToTable.put(1, "table_1");
        typeToTable.put(2, "table_2");
        typeToTable.put(5, "table_5");

        Map<Integer, List<Long>> typeToIds = new HashMap<>();
        typeToIds.put(0, Arrays.asList(1231L, 2468L));
        typeToIds.put(1, Arrays.asList(234L, 6789L));
        typeToIds.put(2, Arrays.asList(43324L, 3456L));
        typeToIds.put(5, Arrays.asList(12312L, 1357L));

        String actualQuery = sqlUpdateService.generateUpdateQuery(typeToTable, typeToIds);

        String expectedQuery = """
                        UPDATE table_default SET type = CASE\s
                         WHEN id = 3456 THEN 2 WHEN id = 2468 THEN 0 WHEN id = 6789 THEN 1 WHEN id = 12312 THEN 5 WHEN id = 234 THEN 1 WHEN id = 43324 THEN 2 WHEN id = 1357 THEN 5 WHEN id = 1231 THEN 0  END WHERE id IN (3456, 2468, 6789, 12312, 234, 43324, 1357, 1231);
                        UPDATE table_1 SET type = CASE\s
                         WHEN id = 3456 THEN 2 WHEN id = 2468 THEN 0 WHEN id = 6789 THEN 1 WHEN id = 12312 THEN 5 WHEN id = 234 THEN 1 WHEN id = 43324 THEN 2 WHEN id = 1357 THEN 5 WHEN id = 1231 THEN 0  END WHERE id IN (3456, 2468, 6789, 12312, 234, 43324, 1357, 1231);
                        UPDATE table_2 SET type = CASE\s
                         WHEN id = 3456 THEN 2 WHEN id = 2468 THEN 0 WHEN id = 6789 THEN 1 WHEN id = 12312 THEN 5 WHEN id = 234 THEN 1 WHEN id = 43324 THEN 2 WHEN id = 1357 THEN 5 WHEN id = 1231 THEN 0  END WHERE id IN (3456, 2468, 6789, 12312, 234, 43324, 1357, 1231);
                        UPDATE table_5 SET type = CASE\s
                         WHEN id = 3456 THEN 2 WHEN id = 2468 THEN 0 WHEN id = 6789 THEN 1 WHEN id = 12312 THEN 5 WHEN id = 234 THEN 1 WHEN id = 43324 THEN 2 WHEN id = 1357 THEN 5 WHEN id = 1231 THEN 0  END WHERE id IN (3456, 2468, 6789, 12312, 234, 43324, 1357, 1231);
                                
                """;

        assertEquals(expectedQuery.replaceAll("\\s+", " ").trim(),
                actualQuery.replaceAll("\\s+", " ").trim());    }
}