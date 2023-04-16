package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
            //Вставка строки в коллекцию
            List<Map<String, Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true");
            //Изменение значения которое выше записывали
            List<Map<String, Object>> result2 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            List<Map<String, Object>> result3 = starter.execute("SELECT");

            test(starter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Для удобства проверки обработки
     *  запросов. Ради интереса вывожу
     *  время выполнения.
     */
    private static void test(JavaSchoolStarter starter) throws Exception {
        long start = System.currentTimeMillis();

        List<String> stringRequests = new ArrayList<>();
        stringRequests.add("INSERT VALUES 'lastName' = 'Висильев' ,  'age'=23,  'active'=true");
        stringRequests.add("INSERT VALUES 'lastName' = 'Петров' , 'id'=1, 'age'=32, 'cost'=2.3, 'active'=true");
        stringRequests.add("INSERT VALUES 'lastName' = 'Кудрявова' , 'age'=40, 'active'=false");
        stringRequests.add("INSERT VALUES  'lastName' = 'Иван where % and or , like ilike' , 'id'=3, 'cost'=2.3");
        stringRequests.add("INSERT VALUES   'lastName' = 'Семён Бобков' , 'id'=4, 'age'=6,  'active'=true");
        stringRequests.add("INSERT VALUES 'lastName' = '' , 'id'=30, 'cost'=200, 'age'=null");
        stringRequests.add("INSERT VALUES 'lastName' = 'Навальный ALEXEI' , 'id'=3, 'age'=40, 'cost'=0.15, 'active'=true");
        stringRequests.add("INSERT VALUES 'lastName'     = 'Donald Trump' , 'id'=33, 'age'=55,'cost'=33.2, 'active'=false");
        stringRequests.add("INSERT VALUES 'lastName' = 'John Smith' , 'id'=0, 'age'=0,'cost'=45.6213, 'active'=true");
        stringRequests.add("INSERT VALUES 'lastName' = 'Алёёёша     ' , 'id'=40, 'age'=3000,'cost'=999999, 'active'=false");
        stringRequests.add("INSERT VALUES 'lastName' = 'СидОров' , 'id'=40, 'age'=99999 ,'cost'=0, 'active'=true");

        stringRequests.add("UPDATE VALUES 'id'=40 where 'lastName' ilike '%идор%'");
        stringRequests.add("UPDATE VALUES 'lastName' = null, 'id'=null, 'age'=null, 'cost'=null, 'active'=null where 'cost'<0");
        stringRequests.add("UPDATE VALUES 'lastName' = 'Алёёёёёёёёна', 'active'=false, 'cost'=10.1 where 'id'=3");

        stringRequests.add("SELECT ");
        stringRequests.add("SELECT WHERE 'age'>0 and 'id'=40 and 'cost'=0 or 'id'=0");
        stringRequests.add("SELECT WHERE 'age'!=0 and 'lastName' like ''");
        stringRequests.add("   SELECT  WHERE   'age'!=0 and 'age'<45");
        stringRequests.add("SELECT WHERE 'cost'>5 and 'cost'<5");
        stringRequests.add("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0");
        stringRequests.add("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0 or 'age'=0");
        stringRequests.add("SELECT WHERE 'lastName' like '%идо%'");
        stringRequests.add("SELECT WHERE 'cost' > 33.19 and 'cost' < 33.21");

        stringRequests.add("DELETE WHERE 'cost' <100");
        stringRequests.add("DELETE");
        stringRequests.add("SELECT");

        for (String stringRequest : stringRequests)
            System.out.println("Result of request: " + starter.execute(stringRequest));

        System.out.println(System.currentTimeMillis() - start + "ms");
    }
}
