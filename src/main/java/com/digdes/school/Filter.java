package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Filter extends AbstractRequest {
    private List<Map<String, Object>> params;

    public List<Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
        this.params = params;
    }
}
