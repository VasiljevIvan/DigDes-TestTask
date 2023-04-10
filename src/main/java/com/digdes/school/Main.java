package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            //Вставка строки в коллекцию
            List<Map<String, Object>> result1 = starter
                    .execute("INSERT   VALUES  'lastName' = 'Фёдоров' , 'id'= 3, 'age' =40, 'active'=true");
            //Изменение значения которое выше записывали
            List<Map<String, Object>> result2 = starter
                   .execute("UPDaTE VALUES 'lastName' = 'Фёдоров' , 'id'= 3, 'age' =40, 'cost' =56.2, 'active'=true where 'lastName' like %asd%");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            //List<Map<String, Object>> result3 = starter.execute("SELECT");

            //List<Map<String, Object>> result4 = starter.execute("SELECT WHERE 'age' >= 30 and 'lastName' ilike '%п%'");
            List<Map<String, Object>> result5 = starter.execute("UPDATE VALUES 'active'=true  where 'active'=false");
            //List<Map<String, Object>> result6 = starter.execute("DELETE WHERE 'id'=3");
            //List<Map<String, Object>> result7 = starter.execute("DELETE");


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
