package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String... args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();
        try {
//            Вставка строки в коллекцию
            List<Map<String,Object>> result1 = starter
                    .execute("INSERT VALUES 'lastName' = 'Fed borov' , 'id'=3, 'age'=40, 'active'=true");
            System.out.println(result1);
//            Изменение значения которое выше записывали
            List<Map<String,Object>> result2 = starter
                    .execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
            System.out.println(result2);
//            Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            List<Map<String,Object>> result3 = starter
                    .execute("SELECT");
            System.out.println(result3);

            long start = System.currentTimeMillis();

            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Fed bOrovj' ,  'age'=23,  'active'=true"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'dvaaaa' , 'id'=1, 'age'=32, 'cost'=2.3, 'active'=true"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'ilja' , 'age'=40, 'active'=false"));
            System.out.println("request: " + starter.execute("INSERT VALUES  'lastName' = 'semen bobkov' , 'id'=3, 'cost'=2.3"));
            System.out.println("request: " + starter.execute("DELETE WHERE 'age'<30"));
            System.out.println("request: " + starter.execute("INSERT VALUES     'lastName' = 'semen' , 'id'=4, 'age'=6,  'active'=true"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Fed borov' , 'id'=30, 'age'=40, 'cost'=200 "));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'active'=false, 'age'=null 'cost'=10.1 where 'lastName' ilike 'FED borov%'"));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'lastName' = null, 'id'=null, 'age'=null, 'cost'=null, 'active'=null where 'lastname'='ilja' or 'lastname' like '%d b%'"));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3"));
            System.out.println("request: " + starter.execute("SELECT  where 'id'=3"));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'lastName' = 'Bobkov Semen', 'id'=101, 'age'=30, 'cost'=180.0, 'active'=true where 'id'!=0"));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3"));
            System.out.println("request: " + starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3"));


            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Fedorov' , 'id'=3, 'age'=40, 'cost'=0.15, 'active'=true"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Incestov' , 'id'=33, 'age'=55,'cost'=33.2, 'active'=false"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Evstigneev' , 'id'=0, 'age'=0,'cost'=45.6213, 'active'=true"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Incestov' , 'id'=40, 'age'=3000,'cost'=999999, 'active'=false"));
            System.out.println("request: " + starter.execute("INSERT VALUES 'lastName' = 'Sidorov' , 'id'=40, 'age'=99999 ,'cost'=0, 'active'=true"));


            System.out.println("request: " + starter.execute("SELECT"));
            System.out.println("request: " + starter.execute("SELECT "));
//
            System.out.println("request: " + starter.execute("SELECT WHERE 'age'>0 and 'id'=40 and 'cost'=0 or 'id'=0"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'age'>=55 and 'lastName' like '%est%'"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'age'!=0 and 'age'<45"));

            System.out.println("request: " + starter.execute("SELECT WHERE 'cost'>5 and 'cost'<5"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'cost'>5 and 'active'=false and 'age' !=0 or 'age'=0"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'lastName' like '%idor%'"));
            System.out.println("request: " + starter.execute("SELECT WHERE 'cost' > 33.19 and 'cost' < 33.21"));
            System.out.println("request: " + starter.execute("DELETE WHERE 'cost' <1000"));
            System.out.println("request: " + starter.execute("DELETE"));
            System.out.println("request: " + starter.execute("SELECT"));

            System.out.println(System.currentTimeMillis()-start);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
