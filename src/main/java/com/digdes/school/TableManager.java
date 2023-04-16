package com.digdes.school;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return switch (request.getAction()) {
            case INSERT -> insert(request);
            case UPDATE -> update(request);
            case DELETE -> delete(request);
            case SELECT -> select(request);
            default -> null;
        };
    }

    public List<Map<String, Object>> insert(Request request) {
        Map<String, Object> requestEntry = request.getParams();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultEntry = new HashMap<>();
        Map<String, Object> entryToPut = new HashMap<>();

        for (String column : columns) {
            Object requestValue = requestEntry.get(column);
            if (requestValue == null || requestValue.equals(NULL))
                entryToPut.put(column, null);
            else {
                entryToPut.put(column, requestValue);
                resultEntry.put(column, requestValue);
            }
        }

        table.add(entryToPut);
        result.add(resultEntry);
        printTable();
        return result;
    }

    /**
     * Метод обрабатыват UPDATE запрос. Поле isRemoved нужно для того, чтобы проверять
     * удалить ли запись из таблицы, т.к. по условиям задачи, если все поля в одной
     * записи пустые, то такую запись следует удалить.
     **/
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
                if (conditionsMatchesCounter == condition.size()) {
                    Map<String, Object> requestParam = request.getParams();
                    Map<String, Object> resultEntry = new HashMap<>();
                    int nullValues = 0;

                    for (String column : requestParam.keySet())
                        currEntry.put(column, requestParam.get(column));

                    for (String column : columns)
                        if (currEntry.get(column) != null && !currEntry.get(column).equals(NULL))
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

        printTable();
        return result;
    }

    private List<Map<String, Object>> delete(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> entriesToRemove = new ArrayList<>();

        if (request.getParams() == null && request.getFilters().isEmpty()) {
            result = copyOfTableWithoutNulls(table);
            table.clear();
            printTable();
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
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));

                    entriesToRemove.add(currEntry);

                    result.add(resultEntry);
                }
            }
        }

        for (Map<String, Object> entryToRemove : entriesToRemove)
            table.remove(entryToRemove);

        printTable();
        return result;
    }

    private List<Map<String, Object>> select(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (request.getParams() == null && request.getFilters().isEmpty()) {
            printTable();
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
                if (conditionsMatchesCounter == condition.size()) {
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : columns)
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }

        printTable();
        return result;
    }

    /**
     * Метод проверяет соответствует ли элемент из таблицы фильтру.
     */
    private boolean checkMatches(Map<String, Object> currEntry, Filter currFilter) {
        boolean isMatches = false;
        Object currEntryValue = currEntry.get(currFilter.getParamTitle());

        switch (currFilter.getParamTitle()) {
            case LASTNAME -> {
                if (currFilter.getParamValue().matches("'.*'") && currEntryValue != null) {
                    String valueTable = (String) currEntryValue;
                    String valueFilter = currFilter.getParamValue();
                    isMatches = compareStrings(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue == null && currFilter.getComparator().equals(NE);     // Вернет true только если текущий элемент
            }                                                                                           // равен null, а опертор сравнения '!='
            case ID, AGE -> {
                if (currFilter.getParamValue().matches("\\d+") && currEntryValue != null) {
                    Long valueTable = (Long) currEntryValue;
                    Long valueFilter = Long.parseLong(currFilter.getParamValue());
                    isMatches = compareNumbers(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue == null && currFilter.getComparator().equals(NE);
            }
            case COST -> {
                if (currFilter.getParamValue().matches("\\d+(\\.\\d+)?") && currEntryValue != null) {
                    Double valueTable = (Double) currEntryValue;
                    Double valueFilter = Double.parseDouble(currFilter.getParamValue());
                    isMatches = compareNumbers(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue == null && currFilter.getComparator().equals(NE);
            }
            case ACTIVE -> {
                if (currFilter.getParamValue().matches("(true|false)") && currEntryValue != null) {
                    boolean valueTable = (Boolean) currEntryValue;
                    boolean valueFilter = Boolean.parseBoolean(currFilter.getParamValue());
                    isMatches = compareBoolean(valueTable, valueFilter, currFilter);
                } else isMatches = currEntryValue == null && currFilter.getComparator().equals(NE);
            }
        }

        return isMatches;
    }

    private List<Map<String, Object>> copyOfTableWithoutNulls(List<Map<String, Object>> table) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultEntry = new HashMap<>();
        for (Map<String, Object> currEntry : table) {
            for (String column : columns)
                if (currEntry.get(column) != null)
                    resultEntry.put(column, currEntry.get(column));
            result.add(resultEntry);
            resultEntry = new HashMap<>();
        }
        return result;
    }

    private boolean compareStrings(String valueTable, String valueFilter, Filter filter) {
        return switch (filter.getComparator()) {
            case EQ -> valueTable.equals(valueFilter);
            case NE -> !valueTable.equals(valueFilter);
            case LIKE -> valueTable.matches(
                    valueFilter.replaceAll("%", ".*"));
            case ILIKE -> valueTable.toLowerCase().matches(
                    valueFilter.toLowerCase().replaceAll("%", ".*"));
            default -> throwWrongParamException(filter);
        };
    }

    private boolean compareBoolean(boolean valueTable, boolean valueFilter, Filter filter) {
        return switch (filter.getComparator()) {
            case EQ -> valueTable == valueFilter;
            case NE -> valueTable != valueFilter;
            default -> throwWrongParamException(filter);
        };
    }

    private boolean compareNumbers(Number valueTable, Number valueFilter, Filter filter) {
        if (valueTable instanceof BigDecimal || valueTable instanceof BigInteger
                || valueFilter instanceof BigDecimal || valueFilter instanceof BigInteger)
            throw new RuntimeException("Нельзя передавать BigDecimal или BigInteger в этот метод");
        Double doubleValueTable = valueTable.doubleValue();
        Double doubleValueFilter = valueFilter.doubleValue();
        int isMatchesInt = doubleValueTable.compareTo(doubleValueFilter);
        return switch (filter.getComparator()) {
            case EQ -> isMatchesInt == 0;
            case NE -> isMatchesInt != 0;
            case GE -> isMatchesInt >= 0;
            case LE -> isMatchesInt <= 0;
            case LT -> isMatchesInt < 0;
            case GT -> isMatchesInt > 0;
            default -> throwWrongParamException(filter);
        };
    }

    private boolean throwWrongParamException(Filter filter) {
        throw new RuntimeException("Для параметра " + filter.getParamTitle() +
                " нельзя использовать оператор \"" + filter.getComparator() + "\"");
    }

    private void printTable() {
        System.out.println("\n\n\nTable:");
        for (Map<String, Object> currEntry : table)
            System.out.println("\t\t\t" + currEntry);
        System.out.println();
    }
}