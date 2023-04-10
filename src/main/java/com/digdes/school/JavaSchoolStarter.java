package com.digdes.school;

import java.util.*;

public class JavaSchoolStarter {

    private List<Map<String, Object>> table;
    private List<String> columns;

    public JavaSchoolStarter() {
        this.table = new ArrayList<>();
        this.columns = new ArrayList<>();
        columns.add("'id'");
        columns.add("'lastName'");
        columns.add("'age'");
        columns.add("'cost'");
        columns.add("'active'");
    }

    public List<Map<String, Object>> execute(String requestString) throws Exception {
        Request request = RequestParser.parse(requestString);
        TableManager tableManager = new TableManager(table,columns);
        return tableManager.processRequest(request);
    }
}
