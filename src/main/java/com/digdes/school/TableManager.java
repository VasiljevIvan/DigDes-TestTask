package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableManager {
    private List<Map<String, Object>> table;
    private List<String> columns;

    public TableManager(List<Map<String, Object>> table, List<String> columns) {
        this.table = table;
        this.columns = columns;
    }

    public TableManager(List<Map<String, Object>> table) {
        this.table = table;
    }

    public List<Map<String, Object>> processRequest(Request request) {
        List<Map<String, Object>> result = null;
        switch (request.getAction()) {
            case "insert" -> result = create(request);
            case "update" -> result = update(request);
        }
        return result;
    }


    public List<Map<String, Object>> create(Request request) {
        table.addAll(request.getParams());
        for (Map<String, Object> cur : table)
            for (String column : columns)
                if (!cur.containsKey(column))
                    cur.put(column, null);
        return table;
    }

    private List<Map<String, Object>> update(Request request) {

        for (Map<String, Object> curr : table) {


        }

        return table;
    }
}
