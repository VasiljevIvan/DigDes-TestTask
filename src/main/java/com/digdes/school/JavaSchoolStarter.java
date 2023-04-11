package com.digdes.school;

import java.util.*;

import static com.digdes.school.Constants.*;

public class JavaSchoolStarter {

    private List<Map<String, Object>> table;
    private List<String> columns;

    public JavaSchoolStarter() {
        this.table = new ArrayList<>();
        this.columns = new ArrayList<>();
        columns.add(ID);
        columns.add(LASTNAME);
        columns.add(AGE);
        columns.add(COST);
        columns.add(ACTIVE);
    }

    public List<Map<String, Object>> execute(String requestString) throws Exception {
        Request request = RequestParser.parse(requestString);
        TableManager tableManager = new TableManager(table,columns);
        System.out.println(table);
        return tableManager.processRequest(request);
    }
}
