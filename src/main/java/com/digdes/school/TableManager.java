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
        Map<String, Object> resultEntry = new HashMap<>();

        table.add(request.getParams());

        for (Map<String, Object> currEntry : table)
            for (String column : columns) {
                if (!currEntry.containsKey(column))
                    currEntry.put(column, NULL);
                if (currEntry.get(column) != null)
                    resultEntry.put(column, currEntry.get(column));
            }

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
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                String valueTable = (String) currEntry.get(currFilter.getParam());
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
                            if (currFilter.getValue().matches("\\d+") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntry.get(currFilter.getParam()));
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = false;
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
                        if (currEntry.get(column) != null)
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
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                String valueTable = (String) currEntry.get(currFilter.getParam());
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
                            if (currFilter.getValue().matches("\\d+") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntry.get(currFilter.getParam()));
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = false;
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
                    switch (currFilter.getParam()) {
                        case LASTNAME -> {
                            if (currFilter.getValue().matches("'.*'") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                String valueTable = (String) currEntry.get(currFilter.getParam());
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
                            if (currFilter.getValue().matches("\\d+") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Long valueTable = Long.parseLong((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case COST -> {
                            if (currFilter.getValue().matches("\\d+(\\.\\d+)?") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                Double valueTable = Double.parseDouble((String) currEntry.get(currFilter.getParam()));
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
                            } else isMatches = false;
                        }
                        case ACTIVE -> {
                            if (currFilter.getValue().matches("(true|false)") && !currEntry.get(currFilter.getParam()).equals(NULL)) {
                                boolean valueTable = Boolean.parseBoolean((String) currEntry.get(currFilter.getParam()));
                                boolean valueFilter = Boolean.parseBoolean(currFilter.getValue());
                                switch (currFilter.getComparator()) {
                                    case EQ -> isMatches = valueTable == valueFilter;
                                    case NE -> isMatches = valueTable != valueFilter;
                                    default -> throw new RuntimeException("Dlya parametra " + currFilter.getParam() +
                                            " nelzya ispolzovat oerator \"" + currFilter.getComparator() + "\"");
                                }
                            } else isMatches = false;
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