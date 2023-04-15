package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {

    private final List<Map<String, Object>> table;

    public JavaSchoolStarter() {
        this.table = new ArrayList<>();
    }

    public List<Map<String, Object>> execute(String requestString) throws Exception {
        Request request = RequestParser.parse(requestString);
        TableManager tableManager = new TableManager(table);

        List<Map<String, Object>> result = tableManager.handleRequest(request);

        System.out.println("\n\n\nTable:");
        for (Map<String, Object> currEntry : table)
                System.out.println("\t\t\t" + currEntry);
        System.out.println();

        return result;
    }
}
