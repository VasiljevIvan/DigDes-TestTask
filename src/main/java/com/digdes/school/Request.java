package com.digdes.school;

import java.util.List;
import java.util.Map;

/**
 * Класс описывающий запрос, содержит поля action, params и filters.
 *
 * Поле action описывает то, что будет делать наш запрос. "insert", "update", "delete" или "select".
 *
 * Поле params описывает пары ключ - значение, где в роли ключа выступает название столбца в таблице,
 * а в роли значения выступает значение этого столбца. Используется если action = "insert" или "update".
 *
 * Поле filters описывает фильтры переданные после оператора WHERE в запросе.
 * Каждый элемент внешнего List соответствует группе фильтров разделенных оператором OR.
 * Каждый элемент внутреннего List соответствует конкретному фильтру, которые разделяются оператором AND.
 *
 * Дефолтного конструктора нет, т.к. нет смысла от запроса с неопределенным действием.
 * Конструктор с одним полем т.к. поле action единственное обязательное к заполнению поле.
 */
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
}
