package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digdes.school.Constants.*;

public class RequestParser {
    public static Request parse(String requestString) {
        requestString = requestString.trim();
        Request request = new Request();
        request.setAction(getAction(requestString));
        requestString = removeAction(requestString, request.getAction());
        switch (request.getAction()) {
            case INSERT, UPDATE -> parseParams(requestString, request);
            case DELETE, SELECT -> parseFilters(requestString, request);
            default -> throw new RuntimeException("Nevernaya komanda, ojidaetsya INSERT, UPDATE, DELETE, SELECT");
        }
        return request;
    }

    private static void parseParams(String requestString, Request request) {
        Map<String, Object> pairs = new HashMap<>();
        String param, value;
        while (!requestString.toLowerCase().matches("where .*") && !requestString.equals("")) {
            requestString = removeEqualsOrComma(requestString);
            param = getFieldWithQuotes(requestString).toLowerCase();
            requestString = removeField(requestString, param);
            switch (param) {
                case LASTNAME -> {
                    requestString = removeEqualsOrComma(requestString);
                    if (requestString.matches("^null( |,).*")) {
                        value = getValue(requestString, "", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, NULL);
                    } else {
                        value = getFieldWithQuotes(requestString);
                        requestString = removeField(requestString, value);
                        pairs.put(param, value);
                    }

                }
                case ID, AGE -> {
                    requestString = removeEqualsOrComma(requestString);
                    if (requestString.matches("^null( |,).*")) {
                        value = getValue(requestString, "", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, NULL);
                    } else {
                        value = getValue(requestString, "\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Long.parseLong(value));
                    }
                }
                case COST -> {
                    requestString = removeEqualsOrComma(requestString);
                    if (requestString.matches("^null( |,).*")) {
                        value = getValue(requestString, "", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, NULL);
                    } else {
                        value = getValue(requestString, "\\d+\\.?\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Double.parseDouble(value));
                    }
                }
                case ACTIVE -> {
                    requestString = removeEqualsOrComma(requestString);
                    if (requestString.matches("^null( |,).*")) {
                        value = getValue(requestString, "", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, NULL);
                    } else {
                        value = getValue(requestString, "(true|false)", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Boolean.parseBoolean(value));
                    }
                }
                default -> throw new RuntimeException("V tablitse net takoi kolonki");
            }
        }
        request.setParams(pairs);
        parseFilters(requestString, request);
    }

    private static void parseFilters(String requestString, Request request) {
        String param, value, comparator, operator;
        List<List<Filter>> allFilters = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        if (requestString.toLowerCase().matches("where .*")) {
            requestString = requestString.substring(6).trim();
            while (!requestString.equals("")) {
                operator = getOperator(requestString);
                requestString = removeField(requestString, operator);
                param = getFieldWithQuotes(requestString).toLowerCase();
                requestString = removeField(requestString, param);
                comparator = getOperation(requestString);
                requestString = removeField(requestString, comparator);
                if (param.equals(LASTNAME) && !requestString.startsWith(NULL+" "))
                    value = getFieldWithQuotes(requestString);
                else
                    value = getValueOfFilter(requestString, ".*", param);
                requestString = removeField(requestString, value);
                if (operator.equalsIgnoreCase("and") || operator.equalsIgnoreCase("")) {
                    filters.add(new Filter(param, comparator, value));
                } else if (operator.equalsIgnoreCase("or")) {
                    allFilters.add(filters);
                    filters = new ArrayList<>();
                    filters.add(new Filter(param, comparator, value));
                }
            }
            allFilters.add(filters);
        }
        request.setFilters(allFilters);
    }

    private static String getAction(String requestString) {
        if (requestString.toLowerCase().matches("^(insert values |update values |delete |select ).*")) {
            return requestString.substring(0, requestString.indexOf(' ')).toLowerCase();
        } else if (requestString.equalsIgnoreCase(DELETE) || requestString.equalsIgnoreCase(SELECT))
            return requestString.toLowerCase();
        else throw new RuntimeException("Tolko zaprosi DELETE i SELECT mogut peredavatsya bez parametrov");
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
        else throw new RuntimeException("Pole doljno bit videleno znakami \"'\"");
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
            else throw new RuntimeException("Znachenie polya doljno zakanchivatsya zapyatoi ili probelom");
        } else
            throw new RuntimeException("Nevernoe znachenie polya " + currParam);
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
            throw new RuntimeException("Posle nazvaniya parametra doljen bit znak \"=\".\n" +
                    "Posle znacheniya doljna bit zapyataya.\n" +
                    "Pole doljno bit videleno znakami \"'\"");
    }

    private static String getOperation(String requestString) {
        if (requestString.startsWith("="))
            return "=";
        else if (requestString.startsWith("!="))
            return "!=";
        else if (requestString.startsWith("like"))
            return "like";
        else if (requestString.startsWith("ilike"))
            return "ilike";
        else if (requestString.startsWith(">="))
            return ">=";
        else if (requestString.startsWith("<="))
            return "<=";
        else if (requestString.startsWith("<"))
            return "<";
        else if (requestString.startsWith(">"))
            return ">";
        else throw new RuntimeException("Neverniy operator sravneniya");
    }

    private static String getValueOfFilter(String requestString, String regex, String currParam) {
        int idxOfSpace;
        if (requestString.matches(regex + ".*")) {
            idxOfSpace = requestString.indexOf(" ");
            if (idxOfSpace >= 0)
                return requestString.substring(0, idxOfSpace);
            else if (requestString.matches(regex))
                return requestString;
            else throw new RuntimeException("Znachenie polya doljno zakanchivatsya probelom");
        } else
            throw new RuntimeException("Nevernoe znachenie polya " + currParam);
    }

    private static String getOperator(String requestString) {
        if (requestString.startsWith("'"))
            return "";
        else if (requestString.toLowerCase().matches("and .*"))
            return requestString.substring(0, 3);
        else if (requestString.toLowerCase().matches("or .*"))
            return requestString.substring(0, 2);
        else throw new RuntimeException("Zadan neverniy operator, ojidaetsya AND ili OR");
    }
}