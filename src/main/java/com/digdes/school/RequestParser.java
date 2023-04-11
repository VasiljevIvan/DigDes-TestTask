package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    public static Request parse(String requestString) {
        requestString = requestString.trim();
        Request request = new Request();
        List<Map<String, Object>> params = new ArrayList<>();
        Map<String, Object> pairs = new HashMap<>();
        List<List<Filter>> allFilters = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        String param;
        String value;
        String comparator;
        String operator;

        request.setAction(getAction(requestString));
        requestString = removeAction(requestString, request.getAction());

        if (request.getAction().equals("insert")) {
            while (!requestString.equals("")) {
                requestString = removeEqualsOrComma(requestString);
                param = getParam(requestString);
                requestString = removeField(requestString, param);
                switch (param) {
                    case "'lastname'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "'", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, value);
                    }
                    case "'id'", "'age'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Long.parseLong(value));
                    }
                    case "'cost'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "\\d+\\.?\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Double.parseDouble(value));
                    }
                    case "'active'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "(true|false)", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Boolean.parseBoolean(value));
                    }
                    default -> throw new RuntimeException("V tablitse net takoi kolonki");
                }
            }
            params.add(pairs);
            request.setParams(params);
            //System.out.println(request);
            return request;
        } else if (request.getAction().equals("update")) {
            while (!requestString.matches("[Ww][Hh][Ee][Rr][Ee] .*") && !requestString.equals("")) {
                requestString = removeEqualsOrComma(requestString);
                param = getParam(requestString);
                requestString = removeField(requestString, param);
                switch (param.toLowerCase()) {
                    case "'lastname'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "'", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, value);
                    }
                    case "'id'", "'age'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Long.parseLong(value));
                    }
                    case "'cost'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "\\d+\\.?\\d+", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Double.parseDouble(value));
                    }
                    case "'active'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        value = getValue(requestString, "(true|false)", param);
                        requestString = removeField(requestString, value);
                        pairs.put(param, Boolean.parseBoolean(value));
                    }
                    default -> throw new RuntimeException("V tablitse net takoi kolonki");
                }
            }
            params.add(pairs);
            request.setParams(params);

            if (requestString.matches("[Ww][Hh][Ee][Rr][Ee] .*")) {
                requestString = requestString.substring(6).trim();
                while (!requestString.equals("")) {
                    operator = getOperator(requestString);
                    requestString = removeField(requestString, operator);

                    param = getParam(requestString);
                    requestString = removeField(requestString, param);

                    comparator = getOperation(requestString);
                    requestString = removeField(requestString, comparator);

                    value = getValue2(requestString, ".*", param);
                    requestString = removeField(requestString, value);

                    if (operator.equalsIgnoreCase("and") || operator.equalsIgnoreCase("")) {
                        filters.add(new Filter(param, comparator, value));
                    } else if (operator.equalsIgnoreCase("or")) {
                        allFilters.add(filters);
                        filters = new ArrayList<>();
                        filters.add(new Filter(param, comparator, value));
                    }
                }
            }
            allFilters.add(filters);
            request.setFilters(allFilters);
            //System.out.println(request);
            return request;
        } else if (request.getAction().equals("delete")) {
            //System.out.println(request);
            return request;
        } else if (request.getAction().equals("select")) {
            //System.out.println(request);
            return request;
        } else throw new RuntimeException("Nevernaya komanda, ojidaetsya INSERT, UPDATE, DELETE, SELECT");
    }

    private static String getOperator(String requestString) {
        if (requestString.startsWith("'"))
            return "";
        else if (requestString.matches("[Aa][Nn][Dd] .*"))
            return requestString.substring(0, 3);
        else if (requestString.matches("[Oo][Rr] .*"))
            return requestString.substring(0, 2);
        else throw new RuntimeException("Zadan neverniy operator, ojidaetsya AND ili OR");
    }

    private static String getValue2(String requestString, String regex, String currParam) {
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

    private static String removeAction(String requestString, String requestActionString) {
        int numberOfWordsToRemove = 1;
        if (requestString.equalsIgnoreCase(requestActionString))
            return "";
        else if (requestActionString.equals("insert") || requestActionString.equals("update"))
            numberOfWordsToRemove++;
        for (int i = 0; i < numberOfWordsToRemove; i++) {
            requestString = requestString.trim();
            requestString = requestString.substring(requestString.indexOf(' ') + 1);
        }
        return requestString.trim();
    }

    private static String getAction(String requestString) {
        if (requestString.contains(" "))
            return requestString.substring(0, requestString.indexOf(' ')).toLowerCase();
        else if (requestString.equalsIgnoreCase("delete") || requestString.equalsIgnoreCase("select"))
            return requestString.toLowerCase();
        else throw new RuntimeException("Tolko zaprosi DELETE i SELECT mogut peredavatsya bez parametrov");
    }

    private static String getParam(String requestString) {
        int idxOfSecondQuote = requestString.indexOf("'", 1);
        if (requestString.startsWith("'") && idxOfSecondQuote >= 0)
            return requestString.substring(0, idxOfSecondQuote + 1).toLowerCase();
        else throw new RuntimeException("Nazvanie parametra doljno bit strokoi");
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
            else if (idxOfSpace >= 0 && idxOfComma >= 0)
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
            throw new RuntimeException("Posle nazvaniya parametra doljen bit znak \"=\" ili zapyataya");
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
}
