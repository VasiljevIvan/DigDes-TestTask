package com.digdes.school;

/**
 * Класс описывающий один из фильтров WHERE, содержит поля param, comparator и value.
 *
 * Поле param содержит название поля для которого будет применен этот фильтр.
 *
 * Поле comparator содержит строку с оператором сравнения.
 *
 * Поле value содержит строку со значением параметра.
 *
 * Дефолтного конструктора нет, используется конструктор со всеми полями.
 * */
public class Filter {

    private String paramTitle;
    private String comparator;
    private String paramValue;

    public Filter(String paramTitle, String comparator, String paramValue) {
        this.paramTitle = paramTitle;
        this.comparator = comparator;
        this.paramValue = paramValue;
    }

    public String getParamTitle() {
        return paramTitle;
    }

    public void setParamTitle(String paramTitle) {
        this.paramTitle = paramTitle;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
