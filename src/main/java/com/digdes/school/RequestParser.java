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
        String currParam;
        String currVal;
        String currOperation;

        request.setAction(getAction(requestString));
        requestString = removeAction(requestString, request.getAction());

        if (request.getAction().equals("insert")) {
            while (!requestString.equals("")) {
                requestString = removeEqualsOrComma(requestString);
                currParam = getParam(requestString);
                requestString = removeField(requestString, currParam);
                switch (currParam.toLowerCase()) {
                    case "'lastname'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "'", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, currVal);
                    }
                    case "'id'", "'age'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "\\d+", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Long.parseLong(currVal));
                    }
                    case "'cost'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "\\d+\\.?\\d+", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Double.parseDouble(currVal));
                    }
                    case "'active'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "(true|false)", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Boolean.parseBoolean(currVal));
                    }
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                }
            }
            params.add(pairs);
            request.setParams(params);
            System.out.println(request);
            return request;
        } else if (request.getAction().equals("update")) {
            while (!requestString.matches("[Ww][Hh][Ee][Rr][Ee] .*") && !requestString.equals("")) {
                requestString = removeEqualsOrComma(requestString);
                currParam = getParam(requestString);
                requestString = removeField(requestString, currParam);
                switch (currParam.toLowerCase()) {
                    case "'lastname'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "'", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, currVal);
                    }
                    case "'id'", "'age'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "\\d+", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Long.parseLong(currVal));
                    }
                    case "'cost'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "\\d+\\.?\\d+", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Double.parseDouble(currVal));
                    }
                    case "'active'" -> {
                        requestString = removeEqualsOrComma(requestString);
                        currVal = getValue(requestString, "(true|false)", currParam);
                        requestString = removeField(requestString, currVal);
                        pairs.put(currParam, Boolean.parseBoolean(currVal));
                    }
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                }
            }
            params.add(pairs);
            request.setParams(params);

            if (requestString.matches("[Ww][Hh][Ee][Rr][Ee] .*")) {
                requestString = requestString.substring(6);
                while (!requestString.equals("")) {
                    currParam = getParam(requestString);
                    requestString = removeField(requestString, currParam);
                    currOperation = getOperation(requestString);
                    requestString = removeField(requestString, currOperation);
                    currVal = getValue2(requestString, ".*", currParam);
                    requestString = removeField(requestString, currVal);
                    System.out.println(currParam);
                    System.out.println(currOperation);
                    System.out.println(currVal);
                    System.out.println(requestString);
                }
            }
            System.out.println(request);
            return request;
        } else if (request.getAction().equals("delete")) {

        } else if (request.getAction().equals("select")) {

        } else throw new RuntimeException("Неверная команда");

        return request;
    }

    private static String getValue2(String requestString, String regex, String currParam) {
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
        else throw new RuntimeException("Только запросы DELETE и SELECT могут передаваться без параметров");
    }

    private static String getParam(String requestString) {
        int idxOfSecondQuote = requestString.indexOf("'", 1);
        if (requestString.startsWith("'") && idxOfSecondQuote >= 0)
            return requestString.substring(0, idxOfSecondQuote + 1);
        else throw new RuntimeException("Название параметра должно быть строкой");
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
            throw new RuntimeException("После названия параметра должен быть знак \"=\" или запятая");
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
        else throw new RuntimeException("Неверный оператор сравнения");
    }
}
