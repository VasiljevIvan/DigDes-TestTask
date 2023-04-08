package com.digdes.school;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RequestParser {
    private List<Map<String, Object>> table;
    private List<String>  coloumns;

    public RequestParser(List<Map<String, Object>> table, List<String>  coloumns) {
        this.table = table;
        this.coloumns = coloumns;
    }


    /*



     */
    public TableManager parse(String request) {

        request = request.trim();
        while (request.contains("  "))
            request = request.replaceAll("  ", " ");

        String[] params;

        if (request.startsWith("INSERT VALUES ")) {
            request = request.replaceFirst("INSERT VALUES ", "");
            params = request.split(",|=");
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim();
                if (params[i].startsWith("'") && params[i].endsWith("'"))
                    params[i] = params[i].substring(1,params[i].length()-1);
            }
            return new Creator(table, coloumns, params);
        }

        if (request.startsWith("UPDATE VALUES ")) {
            request = request.replaceFirst("UPDATE VALUES ", "");
            request.spli
            params = request.split(",|=|where");
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].trim();
                if (params[i].startsWith("'") && params[i].endsWith("'"))
                    params[i] = params[i].substring(1,params[i].length()-1);
            }
            System.out.println(Arrays.toString(params));
            return new Creator(table, coloumns, params);
        }

        return null;
    }

}
