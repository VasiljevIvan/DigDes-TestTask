package com.digdes.school;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
////            Вставка строки в коллекцию
//            List<Map<String,Object>> result1 = starter
//                    .execute("INSERT VALUES 'lastName' = 'Fed borov' , 'id'=3, 'age'=40, 'active'=true");
//            System.out.println(result1);
////            Изменение значения которое выше записывали
//            List<Map<String,Object>> result2 = starter
//                    .execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
//            System.out.println(result2);
////            Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
//            List<Map<String,Object>> result3 = starter
//                    .execute("SELECT");
//            System.out.println(result3);

            long start = System.currentTimeMillis();

            List<String> stringRequests = new ArrayList<>();
            stringRequests.add("INSERT VALUES 'lastName' = 'Fed bOrovj' ,  'age'=23,  'active'=true");
            stringRequests.add("INSERT VALUES 'lastName' = 'dvaaaa' , 'id'=1, 'age'=32, 'cost'=2.3, 'active'=true");
            stringRequests.add("INSERT VALUES 'lastName' = 'ilja' , 'age'=40, 'active'=false");
            stringRequests.add("INSERT VALUES  'lastName' = 'semen bobkov' , 'id'=3, 'cost'=2.3");
            stringRequests.add("INSERT VALUES   'lastName' = 'semen' , 'id'=4, 'age'=6,  'active'=true");
            stringRequests.add("INSERT VALUES 'lastName' = 'Fed borov' , 'id'=30, 'cost'=200, 'age'=null");
            stringRequests.add("INSERT VALUES 'lastName' = 'Fedorov' , 'id'=3, 'age'=40, 'cost'=0.15, 'active'=true");
            stringRequests.add("INSERT VALUES 'lastName' = 'Incestov' , 'id'=33, 'age'=55,'cost'=33.2, 'active'=false");
            stringRequests.add("INSERT VALUES 'lastName' = 'Evstigneev' , 'id'=0, 'age'=0,'cost'=45.6213, 'active'=true");
            stringRequests.add("INSERT VALUES 'lastName' = 'Incestov' , 'id'=40, 'age'=3000,'cost'=999999, 'active'=false");
            stringRequests.add("INSERT VALUES 'lastName' = 'Sidorov' , 'id'=40, 'age'=99999 ,'cost'=0, 'active'=true");


            stringRequests.add("UPDATE VALUES 'active'=false, 'age'=null 'cost'=10.1 where 'lastName' ilike 'FED borov%'");
            stringRequests.add("UPDATE VALUES 'lastName' = null, 'id'=null, 'age'=null, 'cost'=null, 'active'=null where 'cost'<0");
            stringRequests.add("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");


//            stringRequests.add("DELETE");
//            stringRequests.add("SELECT ");
//            stringRequests.add("SELECT WHERE 'age'>0 and 'id'=40 and 'cost'=0 or 'id'=0");
//            stringRequests.add("SELECT WHERE 'age'>=55 and 'lastName' like '%est%'");
//            stringRequests.add("SELECT WHERE 'age'!=0 and 'age'<45");
//            stringRequests.add("SELECT WHERE 'cost'>5 and 'cost'<5");
//            stringRequests.add("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0");
//            stringRequests.add("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0 or 'age'=0");
//            stringRequests.add("SELECT WHERE 'lastName' like '%idor%'");
//            stringRequests.add("SELECT WHERE 'cost' > 33.19 and 'cost' < 33.21");


//            stringRequests.add("DELETE WHERE 'cost' <100");
//            stringRequests.add("DELETE");
//
//            stringRequests.add("SELECT");

            for (String stringRequest : stringRequests)
                System.out.println(starter.execute(stringRequest));



            System.out.println(System.currentTimeMillis()-start);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
