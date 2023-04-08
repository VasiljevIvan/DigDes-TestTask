package com.digdes.school;

import java.util.*;

public class JavaSchoolStarter {

    List<Map<String, Object>> table;
    List<String> coloumns = new ArrayList<>();


    public JavaSchoolStarter() {
        this.table = new ArrayList<>();

        coloumns.add("id");
        coloumns.add("lastName");
        coloumns.add("age");
        coloumns.add("cost");
        coloumns.add("active");
        Map<String,Object> row1 = new HashMap<>();
        row1.put("id",1);
        row1.put("lastName","Петров");
        row1.put("age",30);
        row1.put("cost",5.4);
        row1.put("active", true);
        Map<String,Object> row2 = new HashMap<>();
        row2.put("id",2);
        row2.put("lastName","Иванов");
        row2.put("age",25);
        row2.put("cost",4.3);
        row2.put("active", false);
        table.add(row1);
        table.add(row2);
    }

    public List<Map<String, Object>> execute(String request) throws Exception {

        TableManager tableManager;
        RequestParser parser = new RequestParser(table,coloumns);
        Request re
        tableManager = parser.parse(request);


        return tableManager.action();
    }
}
