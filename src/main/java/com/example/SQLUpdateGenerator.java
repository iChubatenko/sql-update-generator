package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SQLUpdateGenerator {
    public static void main(String[] args) throws IOException {

        String csvFile = "list.csv";
        String jsonFile = "suffix_mapping.json";

        Map<Integer, List<Long>> typeToIds = SQLUpdateService.loadCsvData(csvFile);
        Map<Integer, String> typeToTable = SQLUpdateService.loadTableMapping(jsonFile);

        String sqlQuery = SQLUpdateService.generateUpdateQuery(typeToTable, typeToIds);
        System.out.println(sqlQuery);
    }
}
