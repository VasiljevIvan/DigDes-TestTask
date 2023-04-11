package com.digdes.school;

import java.util.*;
import java.util.stream.Collectors;

public class TableManager {
    private List<Map<String, Object>> table;
    private List<String> columns;

    public TableManager(List<Map<String, Object>> table, List<String> columns) {
        this.table = table;
        this.columns = columns;
    }

    public TableManager(List<Map<String, Object>> table) {
        this.table = table;
    }

    public List<Map<String, Object>> processRequest(Request request) {
        List<Map<String, Object>> result = null;
        switch (request.getAction()) {
            case "insert" -> result = create(request);
            case "update" -> result = update(request);
        }
        return result;
    }


    public List<Map<String, Object>> create(Request request) {
        table.addAll(request.getParams());
        for (Map<String, Object> cur : table)
            for (String column : columns)
                if (!cur.containsKey(column))
                    cur.put(column, null);
        return table;
    }

    private List<Map<String, Object>> update(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        boolean status = false;
        for (Map<String, Object> currEntry : table) {
            for (List<Filter> condition : request.getFilters()) {
                for (Filter currFilter : condition) {
                    status = false;
                    for (String currEntryParam : currEntry.keySet()) {
                        if (currEntryParam.equals(currFilter.getParam())) {
                            switch (currEntryParam) {
                                case "'id'", "'age'" -> {
                                    if (currFilter.getValue().matches("-?\\d+")) {
                                        Long valueTable = (Long) currEntry.get(currEntryParam);
                                        Long valueFilter = Long.parseLong(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case "=" -> status = valueTable == valueFilter;
                                            case "!=" -> status = valueTable != valueFilter;
                                            case ">=" -> status = valueTable >= valueFilter;
                                            case "<=" -> status = valueTable <= valueFilter;
                                            case "<" -> status = valueTable < valueFilter;
                                            case ">" -> status = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + currEntryParam +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else status = false;
                                }
                                case "'lastname'" -> {
                                    if (currFilter.getValue().matches("'.*'")) {
                                        String valueTable = (String) currEntry.get(currEntryParam);
                                        String valueFilter = currFilter.getValue();
                                        switch (currFilter.getComparator()) {
                                            case "=" -> status = valueTable.equals(valueFilter);
                                            case "!=" -> status = !valueTable.equals(valueFilter);
                                            case "like" -> status = valueTable.matches(
                                                    valueFilter.replaceAll("%", ".*"));
                                            case "ilike" -> status = valueTable.toLowerCase().matches(
                                                    valueFilter.replaceAll("%", ".*"));
                                            default -> throw new RuntimeException("Dlya parametra " + currEntryParam +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else status = false;
                                }
                                case "'cost'" -> {
                                    if (currFilter.getValue().matches("-?\\d+\\.?\\d+")) {
                                        Double valueTable = (Double) currEntry.get(currEntryParam);
                                        Double valueFilter = Double.parseDouble(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case "=" -> status = valueTable == valueFilter;
                                            case "!=" -> status = valueTable != valueFilter;
                                            case ">=" -> status = valueTable >= valueFilter;
                                            case "<=" -> status = valueTable <= valueFilter;
                                            case "<" -> status = valueTable < valueFilter;
                                            case ">" -> status = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + currEntryParam +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else status = false;
                                }
                                case "'active'" -> {
                                    if (currFilter.getValue().matches("(true|false)")) {
                                        Boolean valueTable = (Boolean) currEntry.get(currEntryParam);
                                        Boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case "=" -> status = valueTable == valueFilter;
                                            case "!=" -> status = valueTable != valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + currEntryParam +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else status = false;
                                }
                            }
                        }
                    }
                }
                if (status) {
                    for (Map<String, Object> requestParam : request.getParams()) {
                        currEntry.put("'id'", requestParam.get("'id'"));
                        currEntry.put("'lastname'", requestParam.get("'lastname'"));
                        currEntry.put("'age'", requestParam.get("'age'"));
                        currEntry.put("'cost'", requestParam.get("'cost'"));
                        currEntry.put("'active'", requestParam.get("'active'"));
                    }
                }
            }
        }
        return result;
    }
}
