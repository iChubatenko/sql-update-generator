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


public class SQLUpdateGenerator {
    public static void main(String[] args) throws IOException {
        String csvFile = "list.csv";
        String jsonFile = "suffix_mapping.json";

        Map<Integer, List<Long>> typeToIds = loadCsvData(csvFile);
        Map<Integer, String> typeToTable = loadTableMapping(jsonFile);

        String sqlQuery = generateUpdateQuery(typeToTable, typeToIds);
        System.out.println(sqlQuery);
    }

    private static Map<Integer, String> loadTableMapping(String jsonFile) throws IOException {
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

    private static Map<Integer, List<Long>> loadCsvData(String csvFile) throws IOException {
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

    private static String generateUpdateQuery (Map<Integer, String> typeToTable, Map<Integer, List<Long>> typeToIds){
        StringBuilder sql = new StringBuilder();
        for (Map.Entry<Integer, List<Long>> entry : typeToIds.entrySet()){
            int type = entry.getKey();
            List<Long> ids = entry.getValue();

            if(typeToTable.containsKey(type)){
                String tableName = typeToTable.get(type);
                String idList = ids.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                sql.append(String.format("UPDATE %s SET type = %d WHERE id IN (%s);\n", tableName, type, idList));
            }
        }
        return sql.toString();
    }
}
