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
        String currVal = "";

        request.setAction(parseAction(requestString));
        requestString = removeAction(requestString, request.getAction());


//        if (request.getAction().equals("insert")) {
//
//            while (!requestString.equals("")) {
//
//                currParam = getNextField(requestString, '\'', '\'');
//                requestString = removeFieldAndTrash(requestString, currParam);
//                currParam = currParam.toLowerCase();
//
//                currVal = getNextField(requestString, '=', ',');
//                requestString = removeFieldAndTrash(requestString, currVal);
//
//                switch (currParam) {
//                    case "lastname" -> {
//                        if (currVal.startsWith("'") && currVal.endsWith("'"))
//                            pairs.put(currParam, currVal);
//                        else
//                            throw new RuntimeException("Имя должно быть строкой");
//                    }
//                    case "id", "age" -> pairs.put(currParam, Long.parseLong(currVal));
//                    case "cost" -> pairs.put(currParam, Double.parseDouble(currVal));
//                    case "active" -> pairs.put(currParam, Boolean.parseBoolean(currVal));
//                    default -> System.out.println("Неверный параметр");
//                }
//            }
//            params.add(pairs);
//            request.setParams(params);
//            System.out.println(request);
//        }


        if (request.getAction().equals("update")) {

            while (!requestString.matches("[Ww][Hh][Ee][Rr][Ee].*")) {

                currParam = getNextField(requestString, '\'', '\'');
                requestString = removeFieldAndTrash(requestString, currParam);
                currParam = currParam.toLowerCase();

                switch (currParam) {
                    case "lastname" -> {
                        if (requestString.startsWith("=")) {
                            requestString = requestString.replaceFirst("=", "");
                            requestString = requestString.trim();
                        } else throw new RuntimeException("После названия параметра должен быть знак \"=\"");

                        int idxOfSecondQuote = requestString.indexOf("'", 1);
                        if (requestString.startsWith("'") && idxOfSecondQuote >= 0) {
                            currVal = requestString.substring(0, idxOfSecondQuote + 1);
                            requestString = requestString.substring(currVal.length());
                            requestString = requestString.trim();
                        } else throw new RuntimeException("Имя должно быть строкой");
                        pairs.put(currParam, currVal);
                    }

                    case "id" -> {
                        if (requestString.startsWith("=")) {
                            requestString = requestString.substring(1);
                            requestString = requestString.trim();
                        } else throw new RuntimeException("После названия параметра должен быть знак \"=\"");

                        int idxOfSpace, idxOfComma;
                        if (requestString.startsWith("'")) throw new RuntimeException("Значение поля id не должно быть строкой");
                        else if (requestString.matches("\\d+.*")) {
                            idxOfSpace = requestString.indexOf(" ");
                            idxOfComma = requestString.indexOf(",");

                            if (idxOfSpace==-1 && idxOfComma>=0)
                                currVal = requestString.substring(0,idxOfComma);
                            else if (idxOfComma==-1 && idxOfSpace>=0)
                                currVal = requestString.substring(0,idxOfSpace);
                            else
                                throw new RuntimeException("Значение поля id должно заканчиваться запятой или пробелом");

                            requestString = requestString.substring(currVal.length());
                            requestString = requestString.trim();

                        }
                        pairs.put(currParam, currVal);
                    }
                    case "age" -> {
                    }

                    case "cost" -> {
                    }

                    case "active" -> {

                    }
                    default -> {

                    }
                }
            }
            params.add(pairs);
            request.setParams(params);
            System.out.println(request);

        }

        if (request.getAction().equals("delete")) {

        }

        if (request.getAction().equals("select")) {

        }

        return request;
    }


    private static String removeAction(String requestString, String requestActionString) {
        int numberOfWordsToRemove = 1;

        if (requestString.equalsIgnoreCase(requestActionString))
            requestString = "";

        if (requestActionString.equals("insert") || requestActionString.equals("update"))
            numberOfWordsToRemove++;

        for (int i = 0; i < numberOfWordsToRemove; i++)
            requestString = requestString.substring(requestString.indexOf(' ') + 1);

        return requestString;
    }

    private static String parseAction(String requestString) {
        String requestActionString = "";

        if (requestString.contains(" "))
            requestActionString = requestString.substring(0, requestString.indexOf(' ')).toLowerCase();
        else if (requestString.equalsIgnoreCase("delete") || requestString.equalsIgnoreCase("select"))
            requestActionString = requestString.toLowerCase();

        if (!(requestActionString.equals("insert") || requestActionString.equals("update")
                || requestActionString.equals("delete") || requestActionString.equals("select")))
            throw new RuntimeException("Неверная команда");

        return requestActionString;
    }

    private static String getNextField(String requestString, char a, char b) {
        String result;
        int firstIdx = requestString.indexOf(a);
        int secondIdx = requestString.indexOf(b, firstIdx + 1);

        if (firstIdx >= 0 && secondIdx >= 0)
            result = requestString.substring(firstIdx + 1, secondIdx).trim();
        else if (firstIdx >= 0 && secondIdx < 0)
            result = requestString.substring(firstIdx + 1).trim();
        else
            throw new RuntimeException("Больше нет значений для чтения");

        return result;
    }

    private static String removeFieldAndTrash(String requestString, String fieldToDelete) {

        int idxOfField = requestString.indexOf(fieldToDelete);
        if ((idxOfField + fieldToDelete.length() + 1) > requestString.length())
            requestString = "";
        else
            requestString = requestString.substring(requestString.indexOf(fieldToDelete) + fieldToDelete.length() + 1);

        return requestString.trim();
    }
}
