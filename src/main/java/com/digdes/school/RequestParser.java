package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digdes.school.Constants.*;

/**
 * Класс содержащий в себе public метод который возвращает объект
 * класса Request соответствующий полученной в конструкторе класса строке запроса.
 * */
public class RequestParser {
    private String requestString;

    public RequestParser(String requestString) {
        this.requestString = requestString;
    }

    /**
     *  Метод возвращает объект класса Request соответствующий полученной в конструкторе класса строке запроса.
     * */
    public Request parse() {
        String requestAction = getAction();
        Request request = new Request(requestAction);
        return switch (requestAction) {
            case INSERT -> parseParams(request);
            case UPDATE -> parseFilters(parseParams(request));
            case DELETE, SELECT -> parseFilters(request);
            default -> throw new RuntimeException("Неверная команда, ожидаются INSERT, UPDATE, DELETE, SELECT");
        };
    }

    private Request parseParams(Request request) {
        Map<String, Object> entry = new HashMap<>();
        String paramTitle, paramValue;

        while (!requestString.toLowerCase().matches("where .*") && !requestString.equals("")) {
            removeEqualsOrComma();
            paramTitle = getFieldWithQuotes().toLowerCase();
            removeEqualsOrComma();
            if (requestString.matches("^null[ ,]?.*")) {
                paramValue = getValue("null", paramTitle);
                entry.put(paramTitle, paramValue);
            } else
                switch (paramTitle) {
                    case LASTNAME -> {
                        paramValue = getFieldWithQuotes();
                        entry.put(paramTitle, paramValue);
                    }
                    case ID, AGE -> {
                        paramValue = getValue("\\d+", paramTitle);
                        entry.put(paramTitle, Long.parseLong(paramValue));
                    }
                    case COST -> {
                        paramValue = getValue("\\d+(\\.\\d+)?", paramTitle);
                        entry.put(paramTitle, Double.parseDouble(paramValue));
                    }
                    case ACTIVE -> {
                        paramValue = getValue("(true|false)", paramTitle);
                        entry.put(paramTitle, Boolean.parseBoolean(paramValue));
                    }
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                }
        }
        request.setParams(entry);
        return request;
    }

    private Request parseFilters(Request request) {
        String paramTitle, paramValue, compareOperator, logicalOperator;
        List<List<Filter>> allFilters = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        if (requestString.toLowerCase().matches("where .*")) {
            requestString = requestString.substring(6).trim();
            while (!requestString.equals("")) {
                logicalOperator = getLogicalOperator();
                paramTitle = getFieldWithQuotes().toLowerCase();
                compareOperator = getCompareOperator();
                if (requestString.matches("^null ?.*"))
                    throw new RuntimeException("В WHERE нельзя передавать 'null'");
                paramValue = switch (paramTitle) {
                    case LASTNAME -> getFieldWithQuotes();
                    case ID, AGE -> getValueOfFilter("\\d+", paramTitle);
                    case COST -> getValueOfFilter("\\d+(\\.\\d+)?", paramTitle);
                    case ACTIVE -> getValueOfFilter("(true|false)", paramTitle);
                    default -> throw new RuntimeException("В таблице нет такой колонки");
                };
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
        return request;
    }

    private String getAction() {
        String requestAction;
        requestString = requestString.trim();
        if (requestString.toLowerCase().matches("^(insert +values +|update +values +|delete +where +|select +where +).*")) {
            requestAction = requestString.substring(0, requestString.indexOf(' ')).toLowerCase();
        } else if (requestString.equalsIgnoreCase(DELETE) || requestString.equalsIgnoreCase(SELECT)) {
            requestAction = requestString.toLowerCase();
        }else throw new RuntimeException("Только запросы DELETE и SELECT могут передаваться без параметров");

        removeAction(requestAction);
        return requestAction;
    }

    private void removeAction(String requestAction) {
        int numberOfWordsToRemove = 1;
        if (requestString.equalsIgnoreCase(requestAction))
            return;
        else if (requestAction.equals(INSERT) || requestAction.equals(UPDATE))
            numberOfWordsToRemove++;
        for (int i = 0; i < numberOfWordsToRemove; i++) {
            requestString = requestString.trim();
            requestString = requestString.substring(requestString.indexOf(' ') + 1);
        }
    }

    private String getFieldWithQuotes() {
        String paramTitle;

        int idxOfSecondQuote = requestString.indexOf("'", 1);
        if (requestString.startsWith("'") && idxOfSecondQuote >= 0)
            paramTitle = requestString.substring(0, idxOfSecondQuote + 1);
        else throw new RuntimeException("Поле должно быть в одинарных кавычках");

        removeField(paramTitle);
        return paramTitle;
    }

    private String getValue(String regex, String currParam) {
        String paramValue;

        int idxOfSpace, idxOfComma;
        if (requestString.matches(regex + ".*")) {
            idxOfSpace = requestString.indexOf(" ");
            idxOfComma = requestString.indexOf(",");
            if (idxOfSpace == -1 && idxOfComma >= 0)
                paramValue = requestString.substring(0, idxOfComma);
            else if (idxOfComma == -1 && idxOfSpace >= 0)
                paramValue = requestString.substring(0, idxOfSpace);
            else if (idxOfSpace >= 0)
                paramValue = requestString.substring(0, Math.min(idxOfSpace, idxOfComma));
            else if (requestString.matches(regex))
                paramValue = requestString;
            else throw new RuntimeException("Значение поля должно заканчиваться запятой или пробелом");
        } else
            throw new RuntimeException("Неверное значение поля " + currParam);

        removeField(paramValue);
        return paramValue;
    }

    private void removeField(String field) {
        requestString = requestString.substring(field.length()).trim();
    }

    private void removeEqualsOrComma() {
        requestString = requestString.trim();
        if (requestString.startsWith("=") || requestString.startsWith(","))
            requestString = requestString.substring(1).trim();
        else if (requestString.startsWith("'"))
            return;
        else
            throw new RuntimeException("После названия параметра должен быть знак равно. " +
                    "После значения должна быть зяпятая. " +
                    "Поле должно быть в одинарных кавычках");
    }

    private String getCompareOperator() {
        String compareOperator;

        if (requestString.matches("^(!=|<=|>=).*"))
            compareOperator = requestString.substring(0, 2);
        else if (requestString.matches("^[=<>].*"))
            compareOperator = requestString.substring(0, 1);
        else if (requestString.toLowerCase().matches("^like.*"))
            compareOperator = requestString.substring(0, 4);
        else if (requestString.toLowerCase().matches("^ilike.*"))
            compareOperator = requestString.substring(0, 5);
        else throw new RuntimeException("Неверный оператор сравнения");

        removeField(compareOperator);
        return compareOperator;
    }

    private String getValueOfFilter(String regex, String currParam) {
        String paramValue;

        int idxOfSpace;
        if (requestString.matches(regex + ".*")) {
            idxOfSpace = requestString.indexOf(" ");
            if (idxOfSpace >= 0)
                paramValue = requestString.substring(0, idxOfSpace);
            else if (requestString.matches(regex))
                paramValue = requestString;
            else throw new RuntimeException("Значение поля должно заканчиваться пробелом");
        } else
            throw new RuntimeException("Неверное значение поля " + currParam);

        removeField(paramValue);
        return paramValue;
    }

    private String getLogicalOperator() {
        String logicalOperator;

        if (requestString.startsWith("'"))
            logicalOperator = "";
        else if (requestString.toLowerCase().matches("and .*"))
            logicalOperator = requestString.substring(0, 3);
        else if (requestString.toLowerCase().matches("or .*"))
            logicalOperator = requestString.substring(0, 2);
        else throw new RuntimeException("Задан неверный логический оператор, ожидается AND или OR");

        removeField(logicalOperator);
        return logicalOperator;
    }
}