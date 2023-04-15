package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.digdes.school.Constants.*;

public class TableManager {
    private final List<Map<String, Object>> table;
    private final List<String> columns;

    public TableManager(List<Map<String, Object>> table) {
        this.table = table;
        this.columns = new ArrayList<>();
        columns.add(ID);
        columns.add(LASTNAME);
        columns.add(AGE);
        columns.add(COST);
        columns.add(ACTIVE);
    }

    public List<Map<String, Object>> handleRequest(Request request) {
        List<Map<String, Object>> result = null;
        switch (request.getAction()) {
            case INSERT -> result = create(request);
            case UPDATE -> result = update(request);
            case DELETE -> result = delete(request);
            case SELECT -> result = select(request);
        }
        return result;
    }

    public List<Map<String, Object>> create(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> entryToPut = new HashMap<>();
        Map<String, Object> resultEntry = new HashMap<>();
        Map<String, Object> requestEntry = request.getParams();

        for (String column : columns) {
            Object requestValue = requestEntry.get(column);
            if (requestValue == null || requestValue.equals(NULL))
                entryToPut.put(column, NULL);
            else {
                entryToPut.put(column, requestValue);
                resultEntry.put(column, requestValue);
            }
        }

        table.add(entryToPut);

        result.add(resultEntry);
        return result;
    }

    private List<Map<String, Object>> update(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> entriesToRemove = new ArrayList<>();
        boolean isRemoved = false;

        for (Map<String, Object> currEntry : table)
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    isMatches = checkMatches(currEntry, currFilter);
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter >= condition.size()) {
                    Map<String, Object> requestParam = request.getParams();
                    Map<String, Object> resultEntry = new HashMap<>();
                    int nullValues = 0;

                    for (String column : requestParam.keySet())
                        currEntry.put(column, requestParam.get(column));

                    for (String column : columns)
                        if (!currEntry.get(column).equals(NULL))
                            resultEntry.put(column, currEntry.get(column));
                        else
                            nullValues++;
                    isRemoved = nullValues == columns.size();

                    result.add(resultEntry);
                }
                if (isRemoved)
                    entriesToRemove.add(currEntry);
            }

        for (Map<String, Object> entryToRemove : entriesToRemove)
            table.remove(entryToRemove);

        return result;
    }

    private List<Map<String, Object>> delete(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> entriesToRemove = new ArrayList<>();

        if (request.getParams() == null && request.getFilters().isEmpty()) {
            result = copyOfTableWithoutNulls(table);
            table.clear();
            return result;
        }

        for (Map<String, Object> currEntry : table) {
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    isMatches = checkMatches(currEntry, currFilter);
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter == condition.size()) {
                    Map<String, Object> resultEntry = new HashMap<>();

                    for (String column : columns)
                        if (!currEntry.get(column).equals(NULL))
                            resultEntry.put(column, currEntry.get(column));

                    entriesToRemove.add(currEntry);

                    result.add(resultEntry);
                }
            }
        }

        for (Map<String, Object> entryToRemove : entriesToRemove)
            table.remove(entryToRemove);
        return result;
    }

    private List<Map<String, Object>> select(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (request.getParams() == null && request.getFilters().isEmpty()) {
            return copyOfTableWithoutNulls(table);
        }

        for (Map<String, Object> currEntry : table)
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    isMatches = checkMatches(currEntry, currFilter);
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter >= condition.size()) {
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : columns)
                        if (!currEntry.get(column).equals(NULL))
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }

        return result;
    }

    private boolean checkMatches(Map<String, Object> currEntry, Filter currFilter) {
        boolean isMatches = false;
        Object currEntryValue = currEntry.get(currFilter.getParam());

        switch (currFilter.getParam()) {
            case LASTNAME -> {
                if (currFilter.getValue().matches("'.*'") && !currEntryValue.equals(NULL)) {
                    String valueTable = (String) currEntryValue;
                    String valueFilter = currFilter.getValue();
                    isMatches = compareStrings(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
            }
            case ID, AGE -> {
                if (currFilter.getValue().matches("\\d+") && !currEntryValue.equals(NULL)) {
                    Long valueTable = Long.parseLong((String) currEntryValue);
                    Long valueFilter = Long.parseLong(currFilter.getValue());
                    isMatches = compareLong(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
            }
            case COST -> {
                if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntryValue.equals(NULL)) {
                    Double valueTable = Double.parseDouble((String) currEntryValue);
                    Double valueFilter = Double.parseDouble(currFilter.getValue());
                    isMatches = compareDouble(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
            }
            case ACTIVE -> {
                if (currFilter.getValue().matches("(true|false)") && !currEntryValue.equals(NULL)) {
                    boolean valueTable = Boolean.parseBoolean((String) currEntryValue);
                    boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                    isMatches = compareBoolean(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
            }
        }

        return isMatches;
    }

    private List<Map<String, Object>> copyOfTableWithoutNulls(List<Map<String, Object>> table) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultEntry = new HashMap<>();
        for (Map<String, Object> currEntry : table) {
            for (String column : columns)
                if (!currEntry.get(column).equals(NULL))
                    resultEntry.put(column, currEntry.get(column));
            result.add(resultEntry);
            resultEntry = new HashMap<>();
        }
        return result;
    }

    private boolean compareLong(Long valueTable, Long valueFilter, Filter filter) {
        boolean isMatches;
        switch (filter.getComparator()) {
            case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
            case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
            case GE -> isMatches = valueTable >= valueFilter;
            case LE -> isMatches = valueTable <= valueFilter;
            case LT -> isMatches = valueTable < valueFilter;
            case GT -> isMatches = valueTable > valueFilter;
            default -> throw new RuntimeException("Для параметра " + filter.getParam() +
                    " нельзя использовать оператор \"" + filter.getComparator() + "\"");
        }
        return isMatches;
    }

    private boolean compareDouble(Double valueTable, Double valueFilter, Filter filter) {
        boolean isMatches;
        switch (filter.getComparator()) {
            case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
            case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
            case GE -> isMatches = valueTable >= valueFilter;
            case LE -> isMatches = valueTable <= valueFilter;
            case LT -> isMatches = valueTable < valueFilter;
            case GT -> isMatches = valueTable > valueFilter;
            default -> throw new RuntimeException("Для параметра " + filter.getParam() +
                    " нельзя использовать оператор \"" + filter.getComparator() + "\"");
        }
        return isMatches;
    }

    private boolean compareStrings(String valueTable, String valueFilter, Filter filter) {
        boolean isMatches;
        switch (filter.getComparator()) {
            case EQ -> isMatches = valueTable.equals(valueFilter);
            case NE -> isMatches = !valueTable.equals(valueFilter);
            case LIKE -> isMatches = valueTable.matches(
                    valueFilter.replaceAll("%", ".*"));
            case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                    valueFilter.toLowerCase().replaceAll("%", ".*"));
            default -> throw new RuntimeException("Для параметра " + filter.getParam() +
                    " нельзя использовать оператор \"" + filter.getComparator() + "\"");
        }
        return isMatches;
    }

    private boolean compareBoolean(boolean valueTable, boolean valueFilter, Filter filter) {
        boolean isMatches;
        switch (filter.getComparator()) {
            case EQ -> isMatches = valueTable == valueFilter;
            case NE -> isMatches = valueTable != valueFilter;
            default -> throw new RuntimeException("Для параметра " + filter.getParam() +
                    " нельзя использовать оператор \"" + filter.getComparator() + "\"");
        }
        return isMatches;
    }
}