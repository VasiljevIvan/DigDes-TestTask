package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Updater implements TableManager {
    private List<Map<String, Object>> table;
    private String[] params;
    private List<String> coloumns;

    public Updater(List<Map<String, Object>> table, List<String> coloumns, String[] params) {
        this.table = table;
        this.coloumns = coloumns;
        this.params = params;
    }

    @Override
    public List<Map<String, Object>> action() {
        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Object> values = new HashMap<>();

        for (int i = 0; i < params.length; i+=2) {
            if (params[i].equals("where")) {




            }
        }

        if (params.length % 2 == 0)
            for (int i = 0; i < params.length; i += 2) {
                int idxOfColoumn = coloumns.indexOf(params[i]);
                switch (idxOfColoumn) {
                    case 0:
                    case 2: {
                        Long id = Long.parseLong(params[i + 1]);
                        values.put(params[i],id);
                        break;
                    }
                    case 1: {
                        values.put(params[i],params[i+1]);
                        break;
                    }
                    case 3: {
                        Double cost = Double.parseDouble(params[i+1]);
                        values.put(params[i],cost);
                        break;
                    }
                    case 4: {
                        Boolean value = Boolean.parseBoolean(params[i+1]);
                        values.put(params[i],value);
                        break;
                    }
                    default:{
                        if (idxOfColoumn<0)
                            try {
                                throw new Exception("Неверное значение колонки");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                    }
                }
            }


        result.add(values);
        table.add(values);


        return result;
    }
}
