package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Request {

    private String action;
    private Map<String, Object> params;
    private List<List<Filter>> filters;

    public Request(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
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
