package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {
    private final List<Map<String, Object>> table;
    private final TableManager tableManager;

    public JavaSchoolStarter() {
        this.table = new ArrayList<>();
        this.tableManager = new TableManager(table);
    }

    public List<Map<String, Object>> execute(String requestString) throws Exception {
        Request request = new RequestParser(requestString).parse();
        return tableManager.handleRequest(request);
    }
}
