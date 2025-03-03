package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQLUpdateService {

    static Map<Integer, String> loadTableMapping(String jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonArray = objectMapper.readTree(new File(jsonFile));

        Map<Integer, String> typeToTable = new HashMap<>();
        for (JsonNode node : jsonArray) {
            String tableSuffix = node.get("table").asText();
            int type = Integer.parseInt(tableSuffix.replace("default", "0"));
            typeToTable.put(type, "table_" + tableSuffix);
        }
        return typeToTable;
    }

    static Map<Integer, List<Long>> loadCsvData(String csvFile) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(csvFile));
        Map<Integer, List<Long>> typeToIds = new HashMap<>();

        for (String line : lines.subList(1, lines.size())) {
            String[] parts = line.split(",");
            long id = Long.parseLong(parts[0].trim());
            int type = Integer.parseInt(parts[1].trim());

            typeToIds.computeIfAbsent(type, k -> new ArrayList<>()).add(id);

        }
        return typeToIds;
    }

    static String generateUpdateQuery(Map<Integer, String> typeToTable, Map<Integer, List<Long>> typeToIds) {
        StringBuilder sql = new StringBuilder();
        Map<Long, Integer> idToType = new HashMap<>();
        for (Map.Entry<Integer, List<Long>> entry : typeToIds.entrySet()) {
            int type = entry.getKey();
            for (Long id : entry.getValue()) {
                idToType.put(id, type);
            }
        }

        for (String tableName : typeToTable.values()) {
            StringBuilder caseStatement = new StringBuilder();
            String idList = idToType.keySet().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));

            for (Map.Entry<Long, Integer> entry : idToType.entrySet()) {
                caseStatement.append(String.format("WHEN id = %d THEN %d ", entry.getKey(), entry.getValue()));
            }

            if (!idList.isEmpty() && caseStatement.length() > 0) {
                sql.append(String.format(
                        "UPDATE %s SET type = CASE \n %s END WHERE id IN (%s);\n",
                        tableName, caseStatement, idList
                ));
            }
        }
        return sql.toString();
    }
}
