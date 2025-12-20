package org.example.vechimo;

import java.util.ArrayList;
import java.util.List;

public class InterventionRecord {
    String type, job, act;
    int salary;
    InterventionRecord(String type, String job, String act,int salary){
        this.type = type;
        this.job = job;
        this.act = act;
        this.salary = salary;
    }

    //

    InterventionRecord(String type, String job, int salary){
        this.type = type;
        this.job = job;
        this.salary = salary;
        this.act = "decizie";
    }

    public String getDescription(){
        return switch (type) {
            case "inregistrare" -> "Inrgistrare Contract";
            case "majorare" -> "Majorare salariu";
            case "inspection" -> "Inspection:";
            default -> "Unknown intervention type";
        };
    }

    public void print(){
        System.out.println(type + " " + job + " " + act + " " + salary + " ;");
    }

    static void putInterventionRecordMajorare(String line, String beforeline) {
        double salary = 0;
        // extract the salary amount after "la" and before "lei"
        int laIdx = line.toLowerCase().indexOf("la");
        int leiIdx = line.toLowerCase().indexOf("lei");
        if (laIdx >= 0 && leiIdx > laIdx) {
            String salaryStr = line.substring(laIdx + 2, leiIdx).trim().replaceAll("[^0-9]", "");
            salary = Double.parseDouble(salaryStr);
        }
        InterventionRecord Record = new InterventionRecord("majorare", User.currentJob, "decizie", (int) salary);
        User.currentSalary = (int) salary;
        User.addInterventionRecord(beforeline, Record);
    }

    static void putInterventionRecordIncetare(String line, String beforeline) {
        InterventionRecord Record = new InterventionRecord("incetare", User.currentJob, "decizie", User.currentSalary); // this is alright

        User.addInterventionRecord(beforeline, Record);


    }

    static void putInterventionRecordPromovare(String line, String beforeline) {

        String startDate = null;
        java.util.regex.Matcher dateMatcher = java.util.regex.Pattern
                .compile("^\\s*([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})")
                .matcher(line);
        if (dateMatcher.find()) {
            startDate = dateMatcher.group(1).trim();
        }

        // extract job code before hyphen (e.g. 522101 from "522101-VÂNZÄTOR")
        line = line.substring(22);
        String jobValue = null;
        java.util.regex.Matcher jobMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*-").matcher(line);
        if (jobMatcher.find()) {
            jobValue = jobMatcher.group(1).trim();
        }

        if (jobValue != null && !jobValue.isEmpty()) {
            User.currentJob = jobValue;
        }

        String key = (startDate != null && !startDate.isEmpty()) ? startDate : (beforeline != null ? beforeline.trim() : "unknown_date");


        if(!User.isInitialized){
            InterventionRecord record = new InterventionRecord("inregistrare", User.currentJob, "cim ", getSalaryPresent(key));
            User.isInitialized = true;
            // this is a problem because of the way the parsing works, when we do reach this point ( the list of the promotions and the register , we already reached the "majorare" and User.currently salary is set to the latest salary
            User.addInterventionRecord(key,record);
            return;
        }



        InterventionRecord record = new InterventionRecord("promovare", User.currentJob, "decizie", getSalaryPresent(key));

        User.addInterventionRecord(key, record);
    }

    private static int getSalaryPresent(String key){
        int salary = 0;

        // this in intented to be used when we have a promotion or a registration ( most likely a registration )
        // we need to find the greatest salary up to that point in time ( assuming the salary never decreases )

        for(InterventionRecord record : User.ProgressMap.get(key)){
            if(record.salary > salary ) salary = record.salary;
        }
        if(User.ProgressMap.lowerKey(key) == null) return salary;
        for(InterventionRecord record : User.ProgressMap.get(User.ProgressMap.lowerKey(key))){
            if(record.salary > salary) salary = record.salary;

        }
        for(InterventionRecord record : User.ProgressMap.get(key)){
            if(record.salary > salary ) salary = record.salary;
        }


        return salary;
    }

}
