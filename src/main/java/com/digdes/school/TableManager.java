package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableManager {
    private List<Map<String, Object>> table;

    public TableManager(List<Map<String, Object>> table) {
        this.table = table;
    }

    public List<Map<String, Object>> processRequest(Request request) {
        List<Map<String, Object>> result = null;
        switch (request.getAction()) {
            case "INSERT" -> result = create(request);
            case "UPDATE" -> result = update(request);
        }
        return result;
    }



    public List<Map<String, Object>> create(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> values = new HashMap<>();

//        if (request.getId() != null)
//            values.put("id", request.getId());
//        if (request.getLastName() != null)
//            values.put("lastName", request.getLastName());
//        if (request.getAge() != null)
//            values.put("age", request.getAge());
//        if (request.getCost() != null)
//            values.put("cost", request.getCost());
//        if (request.getActive() != null)
//            values.put("active", request.getActive());

        table.add(values);
        result.add(values);
        return result;
    }

    private List<Map<String, Object>> update(Request request) {
        List<Map<String, Object>> result = new ArrayList<>();
//        Map<String, Object> values = new HashMap<>();
//
//        for (int i = 0; i < table.size(); i++) {
//            Map<String, Object> tmp = table.get(i);
//
//        }
//
//        request.getFilters().forEach(o -> {
//            if (o.getId()!=null)
//
//        });
//        result = table.stream().filter(o -> {
//
//        }).collect(Collectors.toMap());
//
        return result;
    }
}
