package com.digdes.school;

public class Filter {
    private String param;
    private String comparator;
    private String value;

    public Filter(String param, String comparator, String value) {
        this.param = param;
        this.comparator = comparator;
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "param='" + param + '\'' +
                ", comparator='" + comparator + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
