package org.example.vechimo;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class User { // static class to hold user data across different screens ( no abstract class since we only have one user during runtime )

    static String currentJob = "522101"; // default value if for some reason the parsing fails to initialize it
    static int currentSalary = 0;
    static Map<String , String> DataMap = new HashMap<>();
    static Map<String, InterventionRecord > ProgressMap = new TreeMap<>(new org.example.vechimo.YearComparator());

    static void resetUserData(){
        currentJob = "522101";
        DataMap.clear();
        ProgressMap.clear();
    }

    static void printProgressMap(){
        for (Map.Entry<String, InterventionRecord> entry : ProgressMap.entrySet()) {
            System.out.print(entry.getKey() + " | ");
            entry.getValue().print();
        }
    }

    static void printDataMap(){
        for (Map.Entry<String, String> entry : DataMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " | Value: " + entry.getValue());
        }
    }

    static void print(){
        printDataMap();
        System.out.println("\n");
        printProgressMap();
        System.out.println("\n");
        System.out.println("At this time, the job is : " + currentJob + "which has the salary of " + currentSalary);
    }

    static String getJob(){
        return switch (currentJob) {
                        case "522101" -> "VANZATOR";
                        case "142008" -> "Sofer";
                        case "112018" -> "DIRECTOR VANZARI";
                        case "311519" -> "TEHNICIAN MECANIC";
                        case "243103" -> "SPECIALIST MARKETING";
                        default -> "GESTIONAR DEPOZIT";
                    };
    }
}
