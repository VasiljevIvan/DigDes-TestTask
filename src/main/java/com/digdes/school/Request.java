package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private String action;
    private List<Map<String, Object>> params;
    private List<List<Filter>> filters;

    public Request() {
//        Map<String, Object> columns = new HashMap<>();
//        columns.put("id", null);
//        columns.put("lastname", null);
//        columns.put("age", null);
//        columns.put("cost", null);
//        columns.put("active", null);
//        params = new ArrayList<>();
//        params.add(columns);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
        this.params = params;
    }

    public List<List<Filter>> getFilters() {
        return filters;
    }

    public void setFilters(List<List<Filter>> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return "Request{" +
                "action='" + action + '\'' +
                ", params=" + params +
                ", filters=" + filters +
                '}';
    }
}
