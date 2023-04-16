package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digdes.school.Constants.*;

public class RequestParser {
    public static Request parse(String requestString) {
        String requestAction = getAction(requestString);
        Request request = new Request(requestAction);
        requestString = removeAction(requestString, requestAction);
        switch (requestAction) {
            case INSERT, UPDATE -> parseParams(requestString, request);
            case DELETE, SELECT -> parseFilters(requestString, request);
            default -> throw new RuntimeException("Неверная команда, ожидаются INSERT, UPDATE, DELETE, SELECT");
        }
        return request;
    }

    private static void parseParams(String requestString, Request request) {
        Map<String, Object> entry = new HashMap<>();
        String paramTitle, paramValue;

        while (!requestString.toLowerCase().matches("where .*") && !requestString.equals("")) {
            requestString = removeEqualsOrComma(requestString);

            paramTitle = getFieldWithQuotes(requestString).toLowerCase();
            requestString = removeField(requestString, paramTitle);

            requestString = removeEqualsOrComma(requestString);
            if (requestString.matches("^null[ ,]?.*")) {
                paramValue = getValue(requestString, "null", paramTitle);
                entry.put(paramTitle, paramValue);
            } else
                switch (paramTitle) {
                    case LASTNAME -> {
                        paramValue = getFieldWithQuotes(requestString);
                        entry.put(paramTitle, paramValue);
                    }
                    case ID, AGE -> {
                        paramValue = getValue(requestString, "\\d+", paramTitle);
                        entry.put(paramTitle, Long.parseLong(paramValue));
                    }
                    case COST -> {
                        paramValue = getValue(requestString, "\\d+(\\.\\d+)?", paramTitle);
                        entry.put(paramTitle, Double.parseDouble(paramValue));
                    }
                    case ACTIVE -> {
                        paramValue = getValue(requestString, "(true|false)", paramTitle);
                        entry.put(paramTitle, Boolean.parseBoolean(paramValue));
                    }
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                }
            requestString = removeField(requestString, paramValue);
        }
        request.setParams(entry);
        parseFilters(requestString, request);
    }

    private static void parseFilters(String requestString, Request request) {
        String paramTitle, paramValue, compareOperator, logicalOperator;
        List<List<Filter>> allFilters = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        if (requestString.toLowerCase().matches("where .*")) {
            requestString = requestString.substring(6).trim();
            while (!requestString.equals("")) {
                logicalOperator = getLogicalOperator(requestString);
                requestString = removeField(requestString, logicalOperator);

                paramTitle = getFieldWithQuotes(requestString).toLowerCase();
                requestString = removeField(requestString, paramTitle);

                compareOperator = getCompareOperator(requestString);
                requestString = removeField(requestString, compareOperator);
                if (requestString.matches("^null ?.*"))
                    throw new RuntimeException("В WHERE нельзя передавать 'null'");
                paramValue = switch (paramTitle) {
                    case LASTNAME -> getFieldWithQuotes(requestString);
                    case ID, AGE -> getValueOfFilter(requestString, "\\d+", paramTitle);
                    case COST -> getValueOfFilter(requestString, "\\d+(\\.\\d+)?", paramTitle);
                    case ACTIVE -> getValueOfFilter(requestString, "(true|false)", paramTitle);
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                };
                requestString = removeField(requestString, paramValue);
                if (logicalOperator.equalsIgnoreCase("and") || logicalOperator.equalsIgnoreCase("")) {
                    filters.add(new Filter(paramTitle, compareOperator, paramValue));
                } else if (logicalOperator.equalsIgnoreCase("or")) {
                    allFilters.add(filters);
                    filters = new ArrayList<>();
                    filters.add(new Filter(paramTitle, compareOperator, paramValue));
                }
            }
            allFilters.add(filters);
        }
        request.setFilters(allFilters);
    }

    private static String getAction(String requestString) {
        requestString = requestString.trim();
        if (requestString.toLowerCase().matches("^(insert +values +|update +values +|delete +where +|select +where +).*")) {
            return requestString.substring(0, requestString.indexOf(' ')).toLowerCase();
        } else if (requestString.equalsIgnoreCase(DELETE) || requestString.equalsIgnoreCase(SELECT))
            return requestString.toLowerCase();
        else throw new RuntimeException("Только запросы DELETE и SELECT могут передаваться без параметров");
    }

    private static String removeAction(String requestString, String requestActionString) {
        int numberOfWordsToRemove = 1;
        if (requestString.equalsIgnoreCase(requestActionString))
            return "";
        else if (requestActionString.equals(INSERT) || requestActionString.equals(UPDATE))
            numberOfWordsToRemove++;
        for (int i = 0; i < numberOfWordsToRemove; i++) {
            requestString = requestString.trim();
            requestString = requestString.substring(requestString.indexOf(' ') + 1);
        }
        return requestString.trim();
    }

    private static String getFieldWithQuotes(String requestString) {
        int idxOfSecondQuote = requestString.indexOf("'", 1);
        if (requestString.startsWith("'") && idxOfSecondQuote >= 0)
            return requestString.substring(0, idxOfSecondQuote + 1);
        else throw new RuntimeException("Поле должно быть в одинарных кавычках");
    }

    private static String getValue(String requestString, String regex, String currParam) {
        int idxOfSpace, idxOfComma;
        if (requestString.matches(regex + ".*")) {
            idxOfSpace = requestString.indexOf(" ");
            idxOfComma = requestString.indexOf(",");
            if (idxOfSpace == -1 && idxOfComma >= 0)
                return requestString.substring(0, idxOfComma);
            else if (idxOfComma == -1 && idxOfSpace >= 0)
                return requestString.substring(0, idxOfSpace);
            else if (idxOfSpace >= 0)
                return requestString.substring(0, Math.min(idxOfSpace, idxOfComma));
            else if (requestString.matches(regex))
                return requestString;
            else throw new RuntimeException("Значение поля должно заканчиваться запятой или пробелом");
        } else
            throw new RuntimeException("Неверное значение поля " + currParam);
    }

    private static String removeField(String requestString, String field) {
        return requestString.substring(field.length()).trim();
    }

    private static String removeEqualsOrComma(String requestString) {
        if (requestString.startsWith("=") || requestString.startsWith(","))
            return requestString.substring(1).trim();
        else if (requestString.startsWith("'"))
            return requestString;
        else
            throw new RuntimeException("После названия параметра должен быть знак равно. " +
                    "После значения должна быть зяпятая. " +
                    "Поле должно быть в одинарных кавычках");
    }

    private static String getCompareOperator(String requestString) {
        if (requestString.matches("^(!=|<=|>=).*"))
            return requestString.substring(0, 2);
        else if (requestString.matches("^[=<>].*"))
            return requestString.substring(0, 1);
        else if (requestString.toLowerCase().matches("^like.*"))
            return requestString.substring(0, 4);
        else if (requestString.toLowerCase().matches("^ilike.*"))
            return requestString.substring(0, 5);
        else throw new RuntimeException("Неверный оператор сравнения");
    }

    private static String getValueOfFilter(String requestString, String regex, String currParam) {
        int idxOfSpace;
        if (requestString.matches(regex + ".*")) {
            idxOfSpace = requestString.indexOf(" ");
            if (idxOfSpace >= 0)
                return requestString.substring(0, idxOfSpace);
            else if (requestString.matches(regex))
                return requestString;
            else throw new RuntimeException("Значение поля должно заканчиваться пробелом");
        } else
            throw new RuntimeException("Неверное значение поля " + currParam);
    }

    private static String getLogicalOperator(String requestString) {
        if (requestString.startsWith("'"))
            return "";
        else if (requestString.toLowerCase().matches("and .*"))
            return requestString.substring(0, 3);
        else if (requestString.toLowerCase().matches("or .*"))
            return requestString.substring(0, 2);
        else throw new RuntimeException("Задан неверный логический оператор, ожидается AND или OR");
    }
}