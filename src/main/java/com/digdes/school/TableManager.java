package com.digdes.school;

import java.util.*;

import static com.digdes.school.Constants.*;

public class TableManager {
    private List<Map<String, Object>> table;
    private final List<String> columns;

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

    public List<Map<String, Object>> create(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> entryToPut = new HashMap<>();
        Map<String, Object> resultEntry = new HashMap<>();
        Map<String, Object> requestEntry = request.getParams();

        for (String column : columns) {
            Object requestValue = requestEntry.get(column);
            if (requestValue == null)
                entryToPut.put(column, NULL);
            else if (requestValue.equals(NULL))
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

        for (Map<String, Object> currEntry : table)
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches = false, isRemoved = false;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    Object currEntryValue = currEntry.get(currFilter.getParam());
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntryValue.equals(NULL)) {
                                String valueTable = (String) currEntryValue;
                                String valueFilter = currFilter.getValue();
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable.equals(valueFilter);
                                    case NE -> isMatches = !valueTable.equals(valueFilter);
                                    case LIKE -> isMatches = valueTable.matches(
                                            valueFilter.replaceAll("%", ".*"));
                                    case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                            valueFilter.toLowerCase().replaceAll("%", ".*"));
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = false;
                        }
                        case ID, AGE -> {
                            if (currFilter.getValue().matches("\\d+") && !currEntryValue.equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntryValue);
                                Long valueFilter = Long.parseLong(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntryValue.equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntryValue);
                                Double valueFilter = Double.parseDouble(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntryValue.equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntryValue);
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                    }
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter >= condition.size()) {
                    Map<String, Object> requestParam = request.getParams();
                    Map<String, Object> resultEntry = new HashMap<>();
                    int nullValues = 0;

                    for (String column : requestParam.keySet())
                        currEntry.put(column, requestParam.get(column));

                    for (String column : currEntry.keySet())
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
            result = table;
            table = new ArrayList<>();
            return result;
        }

        for (Map<String, Object> currEntry : table)
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches = false;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    Object currEntryValue = currEntry.get(currFilter.getParam());
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntryValue.equals(NULL)) {
                                String valueTable = (String) currEntryValue;
                                String valueFilter = currFilter.getValue();
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable.equals(valueFilter);
                                    case NE -> isMatches = !valueTable.equals(valueFilter);
                                    case LIKE -> isMatches = valueTable.matches(
                                            valueFilter.replaceAll("%", ".*"));
                                    case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                            valueFilter.toLowerCase().replaceAll("%", ".*"));
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case ID, AGE -> {
                            if (currFilter.getValue().matches("\\d+") && !currEntryValue.equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntryValue);
                                Long valueFilter = Long.parseLong(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntryValue.equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntryValue);
                                Double valueFilter = Double.parseDouble(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntryValue.equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntryValue);
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                    }
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter >= condition.size()) {
                    entriesToRemove.add(currEntry);
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : currEntry.keySet())
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }

        for (Map<String, Object> entryToRemove : entriesToRemove)
            table.remove(entryToRemove);
        return result;
    }

    private List<Map<String, Object>> select(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (request.getParams() == null && request.getFilters().isEmpty())
            return table;

        for (Map<String, Object> currEntry : table)
            for (List<Filter> condition : request.getFilters()) {
                boolean isMatches = false;
                int conditionsMatchesCounter = 0;
                for (Filter currFilter : condition) {
                    Object currEntryValue = currEntry.get(currFilter.getParam());
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntryValue.equals(NULL)) {
                                String valueTable = (String) currEntryValue;
                                String valueFilter = currFilter.getValue();
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable.equals(valueFilter);
                                    case NE -> isMatches = !valueTable.equals(valueFilter);
                                    case LIKE -> isMatches = valueTable.matches(
                                            valueFilter.replaceAll("%", ".*"));
                                    case ILIKE -> isMatches = valueTable.toLowerCase().matches(
                                            valueFilter.toLowerCase().replaceAll("%", ".*"));
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case ID, AGE -> {
                            if (currFilter.getValue().matches("\\d+") && !currEntryValue.equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntryValue);
                                Long valueFilter = Long.parseLong(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntryValue.equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntryValue);
                                Double valueFilter = Double.parseDouble(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = Objects.equals(valueTable, valueFilter);
                                    case NE -> isMatches = !Objects.equals(valueTable, valueFilter);
                                    case GE -> isMatches = valueTable >= valueFilter;
                                    case LE -> isMatches = valueTable <= valueFilter;
                                    case LT -> isMatches = valueTable < valueFilter;
                                    case GT -> isMatches = valueTable > valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntryValue.equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntryValue);
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = currEntryValue.equals(NULL) && currFilter.getComparator().equals(NE);
                        }
                    }
                    if (isMatches)
                        conditionsMatchesCounter++;
                }
                if (conditionsMatchesCounter >= condition.size()) {
                    Map<String, Object> resultEntry = new HashMap<>();
                    for (String column : currEntry.keySet())
                        if (currEntry.get(column) != null)
                            resultEntry.put(column, currEntry.get(column));
                    result.add(resultEntry);
                }
            }

        return result;
    }
}