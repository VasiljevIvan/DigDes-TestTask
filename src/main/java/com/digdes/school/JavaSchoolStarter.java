package com.digdes.school;

import java.util.*;

public class JavaSchoolStarter {

    private List<Map<String, Object>> table;

    public JavaSchoolStarter() {
        this.table = new ArrayList<>();
    }

    public List<Map<String, Object>> execute(String requestString) throws Exception {
        RequestParser.parse(requestString);
//        TableManager tableManager = new TableManager(table);
//        Request request = RequestParser.parse(requestString);
//        System.out.println("request " + request);
//        System.out.println("table " + table);
//        return tableManager.processRequest(request);

        return null;
    }
}
