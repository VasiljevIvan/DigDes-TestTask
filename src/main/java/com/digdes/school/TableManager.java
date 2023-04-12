package com.digdes.school;

import java.util.*;

import static com.digdes.school.Constants.*;

public class TableManager {
    private List<Map<String, Object>> table;
    private List<String> columns;

    public TableManager(List<Map<String, Object>> table, List<String> columns) {
        this.table = table;
        this.columns = columns;
    }

    public List<Map<String, Object>> processRequest(Request request) {
        List<Map<String, Object>> result = null;
        switch (request.getAction()) {
            case INSERT -> result = create(request);
            case UPDATE -> result = update(request);
            case DELETE -> result = delete(request);
            case SELECT -> result = select(request);
        }
        return result;
    }

    private List<Map<String, Object>> select(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        boolean isMatches = false;
        for (Map<String, Object> currEntry : table) {
            for (List<Filter> condition : request.getFilters()) {
                for (Filter currFilter : condition) {
                    isMatches = false;
                    for (String column : columns) {
                        if (column.equals(currFilter.getParam()) && currEntry.get(column)!=null) {
                            switch (column) {
                                case ID, AGE -> {
                                    if (currFilter.getValue().matches("-?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Long valueTable = (Long) currEntry.get(column);
                                        Long valueFilter = Long.parseLong(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case LASTNAME -> {
                                    if (currFilter.getValue().matches("'.*'") && !currEntry.get(column).equals(NULL)) {
                                        String valueTable = (String) currEntry.get(column);
                                        String valueFilter = currFilter.getValue();
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable.equals(valueFilter);
                                            case NE -> isMatches = !valueTable.equals(valueFilter);
                                            case LIKE -> isMatches = valueTable.matches(
                                                    valueFilter.replaceAll("%", ".*"));
                                            case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                                    valueFilter.toLowerCase().replaceAll("%", ".*"));
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case COST -> {
                                    if (currFilter.getValue().matches("-?\\d+\\.?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Double valueTable = (Double) currEntry.get(column);
                                        Double valueFilter = Double.parseDouble(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case ACTIVE -> {
                                    if (currFilter.getValue().matches("(true|false)") && !currEntry.get(column).equals(NULL)) {
                                        Boolean valueTable = (Boolean) currEntry.get(column);
                                        Boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                            }
                        }
                    }
                }
                if (isMatches) {
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : currEntry.keySet())
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }
        }
        return result;
    }


    private List<Map<String, Object>> delete(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> entriesToRemove = new ArrayList<>();
        boolean isMatches = false;
        for (Map<String, Object> currEntry : table) {
            for (List<Filter> condition : request.getFilters()) {
                for (Filter currFilter : condition) {
                    isMatches = false;
                    for (String column : columns) {
                        if (column.equals(currFilter.getParam()) && currEntry.get(column)!=null) {
                            switch (column) {
                                case ID, AGE -> {
                                    if (currFilter.getValue().matches("-?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Long valueTable = (Long) currEntry.get(column);
                                        Long valueFilter = Long.parseLong(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case LASTNAME -> {
                                    if (currFilter.getValue().matches("'.*'") && !currEntry.get(column).equals(NULL)) {
                                        String valueTable = (String) currEntry.get(column);
                                        String valueFilter = currFilter.getValue();
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable.equals(valueFilter);
                                            case NE -> isMatches = !valueTable.equals(valueFilter);
                                            case LIKE -> isMatches = valueTable.matches(
                                                    valueFilter.replaceAll("%", ".*"));
                                            case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                                    valueFilter.toLowerCase().replaceAll("%", ".*"));
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case COST -> {
                                    if (currFilter.getValue().matches("-?\\d+\\.?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Double valueTable = (Double) currEntry.get(column);
                                        Double valueFilter = Double.parseDouble(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case ACTIVE -> {
                                    if (currFilter.getValue().matches("(true|false)") && !currEntry.get(column).equals(NULL)) {
                                        Boolean valueTable = (Boolean) currEntry.get(column);
                                        Boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                            }
                        }
                    }
                }
                if (isMatches) {
                    entriesToRemove.add(currEntry);
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : currEntry.keySet())
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }
        }
        for (Map<String,Object> entryToRemove :entriesToRemove)
            table.remove(entryToRemove);
        return result;
    }

    public List<Map<String, Object>> create(Request request) {
        table.add(request.getParams());

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultEntry = new HashMap<>();

        for (Map<String, Object> cur : table)
            for (String column : columns) {
                if (!cur.containsKey(column))
                    cur.put(column, null);
                if (cur.get(column) != null)
                    resultEntry.put(column, cur.get(column));
            }

        result.add(resultEntry);
        return result;
    }

    private List<Map<String, Object>> update(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> entriesToRemove = new ArrayList<>();
        boolean isMatches = false, isRemoved = false;
        for (Map<String, Object> currEntry : table) {
            for (List<Filter> condition : request.getFilters()) {
                isRemoved = false;
                for (Filter currFilter : condition) {
                    isMatches = false;
                    for (String column : columns) {
                        if (column.equals(currFilter.getParam()) && currEntry.get(column)!=null) {
                            switch (column) {
                                case ID, AGE -> {
                                    if (currFilter.getValue().matches("-?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Long valueTable = (Long) currEntry.get(column);
                                        Long valueFilter = Long.parseLong(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case LASTNAME -> {
                                    if (currFilter.getValue().matches("'.*'") && !currEntry.get(column).equals(NULL)) {
                                        String valueTable = (String) currEntry.get(column);
                                        String valueFilter = currFilter.getValue();
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable.equals(valueFilter);
                                            case NE -> isMatches = !valueTable.equals(valueFilter);
                                            case LIKE -> isMatches = valueTable.matches(
                                                    valueFilter.replaceAll("%", ".*"));
                                            case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                                    valueFilter.toLowerCase().replaceAll("%", ".*"));
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case COST -> {
                                    if (currFilter.getValue().matches("-?\\d+\\.?\\d+") && !currEntry.get(column).equals(NULL)) {
                                        Double valueTable = (Double) currEntry.get(column);
                                        Double valueFilter = Double.parseDouble(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            case GE -> isMatches = valueTable >= valueFilter;
                                            case LE -> isMatches = valueTable <= valueFilter;
                                            case LT -> isMatches = valueTable < valueFilter;
                                            case GT -> isMatches = valueTable > valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                                case ACTIVE -> {
                                    if (currFilter.getValue().matches("(true|false)") && !currEntry.get(column).equals(NULL)) {
                                        Boolean valueTable = (Boolean) currEntry.get(column);
                                        Boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                        switch (currFilter.getComparator()) {
                                            case EQ -> isMatches = valueTable == valueFilter;
                                            case NE -> isMatches = valueTable != valueFilter;
                                            default -> throw new RuntimeException("Dlya parametra " + column +
                                                    " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                        }
                                    } else isMatches = false;
                                }
                            }
                        }
                    }
                }
                if (isMatches) {
                    Map<String, Object> requestParam = request.getParams();
                    Map<String, Object> resultEntry = new HashMap<>();
                    int nullValues = 0;

                    for (String column : requestParam.keySet())
                        if (column != null) {
                            currEntry.put(column, requestParam.get(column));
                            if (requestParam.get(column).equals(NULL))
                                currEntry.put(column, null);
                        }

                    for (String column : currEntry.keySet())
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                        else
                            nullValues++;

                    isRemoved = nullValues >= columns.size();
                    result.add(resultEntry);
                }
                if (isRemoved)
                    entriesToRemove.add(currEntry);
            }
        }
        for (Map<String,Object> entryToRemove :entriesToRemove)
            table.remove(entryToRemove);
        return result;
    }
}