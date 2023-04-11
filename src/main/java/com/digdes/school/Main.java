package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            //
            //   ПОФИКСИТЬ ДОБАВЛЕНИЕ ФАМИЛИИ С ПРОБЕЛОМ И ЗАПЯТОЙ
            //
            //
            //Вставка строки в коллекцию
            List<Map<String, Object>> result1 = starter
                    .execute("INSERT   VALUES  'lastName' = 'FedoroWhasddorvPsina' , 'id'= 3, 'age' =41, 'active'=false");
            System.out.println(result1);
            //Изменение значения которое выше записывали
//            List<Map<String, Object>> result2 = starter
//                   .execute("UPDaTE VALUES 'lastName' = 'BABAiKA)saas' , " +
//                           "'id'= 39, 'age' =40, 'cost' =56.2, 'active'=false where 'lastName' like '%asd%' OR 'id' = 35");
//            System.out.println(result2);
//            List<Map<String, Object>> result8 = starter.execute("INSERT VALUES   'id'= 1, 'age' =30, 'active'=false");
//            System.out.println(result8);
//            List<Map<String, Object>> result9 = starter.execute("INSERT   VALUES 'lastName'='Semen' , 'id'= 3,  'active'= true");
//            System.out.println(result9);
//            List<Map<String, Object>> result10 = starter.execute("INSERT   VALUES  'lastName' = 'Ruslan' , 'id' =2, 'age' =30, 'active'=true");
//            System.out.println(result10);
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            //List<Map<String, Object>> result3 = starter.execute("SELECT");

            //List<Map<String, Object>> result4 = starter.execute("SELECT WHERE 'age' >= 30 and 'lastName' ilike '%п%'");
            List<Map<String, Object>> result5 = starter
                    .execute("UPDATE VALUES 'active'=true  where 'active'=false  ");
            System.out.println(result5);
            //List<Map<String, Object>> result6 = starter.execute("DELETE WHERE 'id'=3");
            //List<Map<String, Object>> result7 = starter.execute("DELETE");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
